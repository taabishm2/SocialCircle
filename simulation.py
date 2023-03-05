import numpy as np
from matching_model import MatchingModel
import torch
import pandas as pd
import time
import matplotlib.pyplot as plt
from graph import plot_graph
import datetime as dt

class Simulation:

    def __init__(self, user_csv):
        # num_users, num_attributes, num_personalies, num_user_tags, 
        self.user_table = pd.read_csv(user_csv)
        # self.interaction_table = {
        #     'user1': [],
        #     'user2': [],
        #     'rating': [],
        #     'notes': []
        # }
        self.interaction_table = {'row': []}
        self.interaction_tracker = {}
        self.interaction_idx = 0
        self.possible_notes = [
            'fun person!',
            'birthday in 2 months',
            'dont mention the thing',
            'that was kind of boring',
            'ask why they had to go on a trip',
        ]

        self.user_colname = 'Personality'
        self.tag_colnames = [self.user_colname] + [c for c in self.user_table.columns if c[0:2] == 'T_']
        self.pref_colnames = [c for c in self.user_table.columns if c[0:2] == 'A_']

        self.num_users = len(self.user_table)
        self.num_attributes = len(self.pref_colnames)
        self.num_user_tags = len(self.tag_colnames)

        self.tag_mat = self.user_table[self.tag_colnames].to_numpy().astype(np.int32)
        self.pref_mat = self.user_table[self.pref_colnames].to_numpy()
        # self.pref_mat = self.normalize(self.pref_mat)
        self.num_personalities = int(np.max(self.tag_mat[:, 0]))
        self.bin_tag_mat = self.create_binary_tags()
        self.bin_tag_mat_no_personality = self.bin_tag_mat[:, self.num_personalities:]

        self.graph_mat = np.zeros((self.num_users, self.num_users))
        self.curr_cons = set()
        self.con_to_start = {}
        self.con_to_pred = {}
        self.con_to_score = {}
        self.con_to_inter = {}
        self.archive_cons = set()
        self.archive_users = {}
        self.seen_users = set()
        self.max_strong_cons = 3
        self.strong_con_thresh = 0.9
        self.time_to_check = 30

        in_dim = self.num_attributes + self.num_personalities
        out_dims = [in_dim // 2, in_dim // 2, in_dim // 3]
        self.matcher = MatchingModel(in_dim, out_dims)
        # exit()
        self.lr = 0.002
        self.optimizer = torch.optim.Adam(self.matcher.parameters(), lr=self.lr)

        self.time = 0

        self.last_req_time = np.zeros(self.num_users)
        self.max_req_prob = 0.3
        self.req_prob_stretch = 500

        self.tmp_loss_list = []

        max_timesteps = 400
        for i in range(max_timesteps):
            print('timestep', i)
            self.timestep()

        plt.cla()
        plt.clf()
        plt.plot(np.arange(len(self.tmp_loss_list)), self.tmp_loss_list)
        plt.savefig('loss.png')

        print()
        print('total_edges:', len(self.con_to_score) / 2)
        print('total connected users:', len(self.seen_users))

        count_strong_cons = []
        for idx in self.seen_users:
            num_strong_cons = np.sum(
                np.where(self.graph_mat[idx] >= self.strong_con_thresh, 1, 0)
            )
            count_strong_cons.append(num_strong_cons)
        count_strong_cons = np.array(count_strong_cons)
        print(np.sum(np.where(count_strong_cons > 0, 1, 0)), ' users with >= 1 strong connections') 
        print(np.sum(np.where(count_strong_cons > self.max_strong_cons, 1, 0)), f' users with >= {self.max_strong_cons} strong connections') 

        for key in self.interaction_table:
            self.interaction_table[key] = pd.Series(self.interaction_table[key])
        
        pd.DataFrame(self.interaction_table).to_csv('interaction_table.csv')

    
    # thing that stores time, x, and y
    # 
    
    def get_request_probs(self):
        x = self.time - self.last_req_time
        return self.max_req_prob \
                - (1 / ((1 / self.req_prob_stretch) * x + np.sqrt(1 / self.max_req_prob)) ** 2)


    def standardize(self, mat):
        return (mat - np.mean(mat, axis=0, keepdims=True)) / np.std(mat, axis=0, keepdims=True)


    def normalize(self, mat):
        magnitudes = np.sqrt(np.sum(mat ** 2, axis=-1, keepdims=True))
        return mat / magnitudes


    def interaction_func(self, score, con_time):
        hump_len = 12
        t = 0.8
        user_func = score \
                    * (2 ** (1 / (-con_time))
                        + ((con_time ** 2 * (score - t - np.abs(score - t))) / hump_len)
                      )
        return np.where(user_func < 0, 0, user_func)


    def try_add_known_connection(self, user1, user2):
        if user1 == user2:
            return False
        
        user_tuple = (user1, user2)
        user_tuple_r = (user2, user1)
        # if they haven't connected in the past and aren't currently connected, connect them
        if user_tuple not in self.curr_cons and user_tuple not in self.archive_cons \
        and user1 not in self.archive_users and user2 not in self.archive_users:
            
            self.curr_cons.add(user_tuple) # add edge to known connections list
            self.curr_cons.add(user_tuple_r)
            
            self.con_to_start[user_tuple] = self.time # add start time for interaction function
            self.con_to_start[user_tuple_r] = self.time

            score = np.dot(self.bin_tag_mat_no_personality[user1], self.pref_mat[user2]) \
              * np.dot(self.bin_tag_mat_no_personality[user2], self.pref_mat[user1])
            score = 1 / (1 + np.exp(score))
            
            self.con_to_score[user_tuple] = score # add the score (invisible to predictive)
            self.con_to_score[user_tuple_r] = score

            self.archive_users[user1] = self.time
            self.archive_users[user2] = self.time

            self.seen_users.add(user1)
            self.seen_users.add(user2)
            # self.con_to_pred[user_tuple] = grad_pred # store the pred for loss calculation
            # self.con_to_pred[user_tuple_r] = grad_pred
            
            # print('added', user_tuple)
            return True
        
        return False
    

    def archive_connection(self, user1, user2):
        user_tuple = (user1, user2)
        user_tuple_r = (user2, user1)

        try:
            # add edge to archived edges set
            self.archive_cons.add(user_tuple)
            self.archive_cons.add(user_tuple_r)

            # remove edge from active connection datastructures
            self.curr_cons.remove(user_tuple)
            self.curr_cons.remove(user_tuple_r)

            del self.con_to_start[user_tuple]
            del self.con_to_start[user_tuple_r]

        except KeyError as e: 
            pass

    def try_unarchive_users(self):
        users_to_unarchive = []
        for idx in self.archive_users:
            num_strong_cons = np.sum(
                np.where(self.graph_mat[idx] >= self.strong_con_thresh, 1, 0)
            )
            # print(num_strong_cons)

            if self.time - self.archive_users[idx] > self.time_to_check \
            and num_strong_cons <= self.max_strong_cons:
                users_to_unarchive.append(idx)
        
        for idx in users_to_unarchive:
            del self.archive_users[idx]


    def timestep(self):
        # runs the entire simulation for one timestep
        
        # get list of all users who requested a new match
        user_idxs = self.get_match_requests()
        if len(user_idxs) > 0:
            # print('attempting to give matches to', len(user_idxs), 'users')
            # for each of these users, generate a predicted match ranking
            preds = self.get_match_ranking(user_idxs)
            preds = np.reshape(preds, (len(user_idxs), -1))
            
            count = 0
            # for each of these users (again), pick the match with the highest combined score
            # (min or product between the two user's scores) and generate the interaction timeseries

            # for each user's prediciton list
            for i in range(preds.shape[0]):
                sort_perm = np.argsort(preds[i]) #sort the predictions
                sort_perm = np.flip(sort_perm)
                # print(sort_perm[0])
                user1 = user_idxs[i]
                #look through the predicted scores largest to smallest
                for user2 in sort_perm:
                    if self.try_add_known_connection(user1, user2):
                        count += 1
                        break               
            
            print('matched', count, '/', len(user_idxs), 'users')
        
        # for each of these users (again), if it has been x timesteps, check the interaction timeseries
        # and aggregate it into a single success metric (maybe avg # of interactions and their reviews)
        # and backprop through the matcher model
        self.update_current_connections()

        if len(self.con_to_score) > 0 and self.time % 10 == 0:
            plt.figure(figsize=(20, 10))
            plt.tight_layout()

            plt.subplot(1, 2, 1)
            plot_graph(self.con_to_inter, self.num_users, f'fig/{self.time}.png')
            plt.title(f'Day {self.time} graph', fontdict={'fontsize': 20})

            plt.subplot(1, 2, 2)
            plt.plot(np.arange(len(self.tmp_loss_list)), self.tmp_loss_list)
            plt.title(f'Day {self.time} loss', fontdict={'fontsize': 20})
            plt.subplots_adjust(wspace=0, hspace=0)

            plt.savefig(f'fig/{self.time}.png', bbox_inches='tight')

            plt.cla()
            plt.clf()
        
        self.try_unarchive_users()
        self.time += 1


    def log_interaction(self, user_idx1, user_idx2, value):
        interac_tuple = (min(user_idx1, user_idx2), max(user_idx1, user_idx2))
        if interac_tuple not in self.interaction_tracker:
            self.interaction_tracker[interac_tuple] = value.item()
        else:
            self.interaction_tracker[interac_tuple] = self.interaction_tracker[interac_tuple] + value.item()
        if self.interaction_tracker[interac_tuple] >= 1:
            # self.interaction_table['user1'].append(min(user_idx1, user_idx2))
            # self.interaction_table['user2'].append(max(user_idx1, user_idx2))
            # self.interaction_table['rating'].append(np.random.randint(1, 10))
            # self.interaction_table['notes'].append(
            #     self.possible_notes[np.random.randint(0, len(self.possible_notes) - 1)]
            # )
            
            sim_date = dt.datetime(year=2022, month=1, day=1) + dt.timedelta(days=self.time)
            row_str = f'(0, NOW(), NOW(), {self.interaction_idx}, {interac_tuple[0]}, {interac_tuple[1]}, "{sim_date}", {np.random.randint(1, 10)}, "{self.possible_notes[np.random.randint(0, len(self.possible_notes) - 1)]}", false),'
            if self.interaction_idx % 5000 == 0:
                self.interaction_table['row'].append('insert into socialcircle_connect(version, created_at, updated_at, connect_id, source_user_id, destination_user_id, connect_time, score, notes, is_suggestion) VALUES ' + row_str)
            else:
                tmp_list = self.interaction_table['row']
                tmp_list[-1] = tmp_list[-1] + row_str
                self.interaction_table['row'] = tmp_list

            self.interaction_tracker[interac_tuple] = self.interaction_tracker[interac_tuple] \
                                                    - int(self.interaction_tracker[interac_tuple])
            self.interaction_idx += 1
            

    def update_edge(self, user_idx1, user_idx2, value):
        self.graph_mat[user_idx1, user_idx2] = value
        self.graph_mat[user_idx2, user_idx1] = value
        self.con_to_inter[(user_idx1, user_idx2)] = value.item()
        self.con_to_inter[(user_idx2, user_idx1)] = value.item()

        self.log_interaction(user_idx1, user_idx2, value)



    def update_current_connections(self):
        cons_to_archive = []

        for (user1, user2), start_time in self.con_to_score.items():
            new_edge_weight = self.interaction_func(
                self.con_to_score[(user1, user2)],
                max(self.time - start_time, 1e-4)
            )
            self.update_edge(user1, user2, new_edge_weight)

        for (user1, user2), start_time in self.con_to_start.items():
            if self.time - start_time >= self.time_to_check:
                cons_to_archive.append((user1, user2))
        
        # look up prediction value and backprop
        if len(cons_to_archive) > 0:
            print('updating model')
            self.optimizer.zero_grad()
            grad_pred = self.predict_match_with_grad(
                [edge[0] for edge in cons_to_archive],
                [edge[1] for edge in cons_to_archive]
            )
            interactions = [
                self.interaction_func(
                    self.con_to_score[(user1, user2)],
                    np.arange(1, self.time - self.con_to_start[(user1, user2)] + 1, 1)
                ) for user1, user2 in cons_to_archive
            ]
            # for ts, time_ser in enumerate(interactions):
            #     plt.clf()
            #     plt.cla()
            #     plt.plot(
            #         np.arange(1, len(time_ser) + 1, 1),
            #         time_ser
            #     )
            #     plt.title(f'score: {self.con_to_score[(user1, user2)]}')
            #     plt.savefig(f'fig/{self.time}_{ts}.png')

            interactions = torch.tensor(interactions, dtype=torch.float32)
            # print(torch.mean(interactions, dim=-1))
            # time.sleep(0.25)
            # print('interactions shape', interactions.shape)
            # print(grad_pred)
            # print(interactions)
            loss = torch.mean((grad_pred - torch.mean(interactions)) ** 2)
            print(loss)
            self.tmp_loss_list.append(loss.item())
            loss.backward()
            self.optimizer.step()

            # print(self.matcher.layers._modules['0'].weight)
            

            for user1, user2 in cons_to_archive:
                self.archive_connection(user1, user2)
            


    def get_match_requests(self):
        # returns the list of users that requested a match at the current timestep (could be empty)
        probs = self.get_request_probs()
        requests = np.array([np.random.binomial(n=1, p=p) for p in probs])
        user_idxs = np.where(requests == 1)[0]
        self.last_req_time[user_idxs] = self.time
        return user_idxs



    def create_binary_tags(self):
        feature_mat = np.zeros((len(self.tag_mat), self.num_attributes + self.num_personalities))
        for i in range(len(self.tag_mat)):
            feature_mat[i][self.tag_mat[i] + self.num_personalities] = 1
            feature_mat[i][self.tag_mat[i, 0]] = 1
        return feature_mat


    def cartprod_concat(self, to_match, all_users):
        # returns cartesian product of the rows in rows1 and rows2

        cartprod = np.zeros((len(to_match) * len(all_users), to_match.shape[1] + all_users.shape[1]))
        idx = 0
        for i in range(len(to_match)):
            for j in range(len(all_users)):
                cartprod[idx] = np.concatenate([to_match[i], all_users[j]])
                idx += 1
        
        return cartprod

    def cartprod(self, to_match, all_users):
        t1 = np.zeros((len(to_match) * len(all_users), to_match.shape[1]))
        t2 = np.zeros((len(to_match) * len(all_users), all_users.shape[1]))
        idx = 0
        for i in range(len(to_match)):
            for j in range(len(all_users)):
                t1[idx] = to_match[i]
                t2[idx] = all_users[j]
                idx += 1
        
        return t1, t2

    def predict_match_with_grad(self, user1_list, user2_list):
        t1 = torch.tensor(self.bin_tag_mat[user1_list], dtype=torch.float32)
        t2 = torch.tensor(self.bin_tag_mat[user2_list], dtype=torch.float32)
        pred = self.matcher(t1, t2)
        return pred


    def get_match_ranking(self, user_idxs):
        # returns a match for the user given by user_idx
        # meaning it returns a list of predicted match scores between all users and user_idx
        
        with torch.no_grad():
            to_match = self.bin_tag_mat[user_idxs]
            t1, t2 = self.cartprod(to_match, self.bin_tag_mat)
            t1 = torch.tensor(t1, dtype=torch.float32)
            t2 = torch.tensor(t2, dtype=torch.float32)

            # pair_tensor = torch.tensor(all_pairs, dtype=torch.float32)
            # print('pair_tensor shape', pair_tensor.shape)
            preds = self.matcher(t1, t2)
            # print('new network output shape', preds.shape)
            preds = torch.squeeze(preds, dim=-1).detach().numpy()
            
        
        # sort_perm = np.argsort(preds)
        return preds




    
s = Simulation('users.csv')

