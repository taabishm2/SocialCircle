import numpy as np
from matching_model import MatchingModel
import torch
import pandas as pd

class Simulation:

    def __init__(self, user_csv):
        # num_users, num_attributes, num_personalies, num_user_tags, 
        self.user_table = pd.read_csv(user_csv)
        print()

        self.user_colname = 'user_idx'
        self.at_colnames = [c for c in self.user_table.columns if 'a' in c]
        self.pref_colnames = [c for c in self.user_table.columns if 'p' in c]

        self.at_mat = self.user_table[self.at_colnames].to_numpy()
        self.pref_mat = self.user_table[self.pref_colnames].to_numpy()

        self.num_users = len(self.user_table)
        self.num_attributes = np.amax(self.at_mat)
        self.num_user_tags = len(self.at_colnames)

        self.graph_mat = np.zeros((self.num_users, self.num_users))
        self.matcher = MatchingModel()

        self.time = 0

        self.last_req_time = np.zeros(self.num_users)
        self.max_req_prob = 0.8
        self.req_prob_stretch = 20

        self.timestep()

    

    def get_request_probs(self):
        x = self.time - self.last_req_time
        return self.max_req_prob \
                - (1 / ((1 / self.req_prob_stretch) * x + np.sqrt(1 / self.max_req_prob)))


    def timestep(self):
        # runs the entire simulation for one timestep
        
        # get list of all users who requested a new match
        user_idxs = self.get_match_requests()

        # for each of these users, generate a predicted match ranking
        preds = self.get_match_ranking(user_idxs)
        print(preds)

        # for each of these users (again), pick the match with the highest combined score
        # (min or product between the two user's scores) and generate the interaction timeseries

        # for each of these users (again), if it has been x timesteps, check the interaction timeseries
        # and aggregate it into a single success metric (maybe avg # of interactions and their reviews)
        # and backprop through the matcher model


        pass


    def get_match_requests(self):
        # returns the list of users that requested a match at the current timestep (could be empty)
        probs = self.get_request_probs()
        requests = np.array([np.random.binomial(n=1, p=p) for p in probs])
        return np.where(requests == 1)[0]



    def cartprod_concat(to_match, all_users):
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
        to_match = self.user_table[user_idxs]
        all_users = self.user_table[:, :self.num_user_tags]

        all_pairs = self.cartprod_concat(to_match, all_users)
        pair_tensor = torch.tensor(all_pairs)
        
        preds = self.matcher(pair_tensor).numpy()
        sort_perm = np.argsort(preds)


        return preds[sort_perm]




    
s = Simulation('users.csv')

