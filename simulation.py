import numpy as np
from matching_model import MatchingModel
import torch
import pandas as pd

class Simulation:

    def __init__(self, user_csv):
        # num_users, num_attributes, num_personalies, num_user_tags, 
        self.user_table = pd.read_csv(user_csv)

        self.user_colname = 'user_idx'
        self.tag_colnames = [c for c in self.user_table.columns if c[0:2] == 'T_']
        self.pref_colnames = [c for c in self.user_table.columns if c[0:2] == 'A_']

        self.num_users = len(self.user_table)
        self.num_attributes = len(self.pref_colnames)
        self.num_user_tags = len(self.tag_colnames)

        self.tag_mat = self.user_table[self.tag_colnames].to_numpy().astype(np.int32)
        self.pref_mat = self.user_table[self.pref_colnames].to_numpy()
        self.bin_tag_mat = self.create_binary(self.tag_mat)

        self.graph_mat = np.zeros((self.num_users, self.num_users))
        self.curr_cons = set()
        self.dead_cons = set()
        self.matcher = MatchingModel(self.num_attributes)

        self.time = 0

        self.last_req_time = np.zeros(self.num_users)
        self.max_req_prob = 0.8
        self.req_prob_stretch = 20

        for i in range(2):
            self.timestep()

    
    # thing that stores time, x, and y
    # 
    
    def get_request_probs(self):
        x = self.time - self.last_req_time
        return self.max_req_prob \
                - (1 / ((1 / self.req_prob_stretch) * x + np.sqrt(1 / self.max_req_prob)) ** 2)


    def timestep(self):
        # runs the entire simulation for one timestep
        
        # get list of all users who requested a new match
        user_idxs = self.get_match_requests()
        if len(user_idxs) > 0:
            # for each of these users, generate a predicted match ranking
            preds = self.get_match_ranking(user_idxs)
            print(preds.shape)
            print(np.reshape(preds, (len(user_idxs), -1)).shape)
            for i in len(preds):
                sort_perm = np.argsort(preds[i])
                user1 = user_idxs[i]
                for user2 in sort_perm:
                    if (user1, user2) not in self.curr_cons and (user1, user2) not in self.dead_cons:
                        self.curr_cons.add((user1, user2))
                        break
            

        # for each of these users (again), pick the match with the highest combined score
        # (min or product between the two user's scores) and generate the interaction timeseries

        # for each of these users (again), if it has been x timesteps, check the interaction timeseries
        # and aggregate it into a single success metric (maybe avg # of interactions and their reviews)
        # and backprop through the matcher model

        self.time += 1


    def update_edge(self, user_idx1, user_idx2, value):
        self.graph_mat[user_idx1, user_idx2] = value
        self.graph_mat[user_idx2, user_idx1] = value


    def update_current_connections(self):
        for idx1, idx2 in list(self.curr_cons):
            self.update_edge(idx1, idx2, self.graph_mat[idx1, idx2] + 0.01)


    def get_match_requests(self):
        # returns the list of users that requested a match at the current timestep (could be empty)
        probs = self.get_request_probs()
        requests = np.array([np.random.binomial(n=1, p=p) for p in probs])
        user_idxs = np.where(requests == 1)[0]
        self.last_req_time[user_idxs] = self.time
        return user_idxs



    def create_binary(self, tag_mat):
        feature_mat = np.zeros((len(tag_mat), self.num_attributes))
        for row in tag_mat:
            feature_mat[row] = 1
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


    def get_match_ranking(self, user_idxs):
        # returns a match for the user given by user_idx
        # meaning it returns a list of predicted match scores between all users and user_idx
        
        to_match = self.bin_tag_mat[user_idxs]
        all_pairs = self.cartprod_concat(to_match, self.bin_tag_mat)
        
        pair_tensor = torch.tensor(all_pairs, dtype=torch.float32)

        preds = self.matcher(pair_tensor)
        preds = torch.squeeze(preds, dim=-1)
        preds = preds.detach().numpy()
        
        # sort_perm = np.argsort(preds)
        return preds




    
s = Simulation('users.csv')

