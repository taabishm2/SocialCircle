import numpy as np
from matching_model import MatchingModel
import torch

class Simulation:

    def __init__(self, num_users, num_attributes, num_personalies, num_user_tags):
        self.num_users = num_users
        self.num_attributes = num_attributes
        self.num_personalities = num_personalies
        self.num_user_tags = num_user_tags

        self.graph_mat = np.zeros((num_users, num_users))
        self.matcher = MatchingModel()

        self.time = 0

        self.last_req_time = np.zeros(num_users)
        self.max_req_prob = 0.8
        self.req_prob_stretch = 20

        self.user_table = np.zeros((num_users, num_user_tags + num_attributes))

    

    def get_request_probs(self):
        x = self.time - self.last_req_time
        return self.max_req_prob \
                - (1 / ((1 / self.req_prob_stretch) * x + np.sqrt(1 / self.max_req_prob)))


    def timestep(self):
        # runs the entire simulation for one timestep
        
        # get list of all users who requested a new match
        user_idxs = self.get_match_requests()

        # for each of these users, generate a predicted match ranking
        self.get_match_ranking(user_idxs)    



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



    def cartprod_concat(rows1, rows2):
        # returns cartesian product of the rows in rows1 and rows2

        cartprod = np.zeros((len(rows1) * len(rows2), rows1.shape[1] + rows2.shape[1]))
        idx = 0
        for i in range(len(rows1)):
            for j in range(len(rows2)):
                cartprod[idx] = np.concatenate([rows1[i], rows2[j]])
        return cartprod

    def get_match_ranking(self, user_idxs):
        # returns a match for the user given by user_idx
        # meaning it returns a list of predicted match scores between all users and user_idx
        to_match = self.user_table[user_idxs]
        all_users = self.user_table[:, :self.num_user_tags]

        all_pairs = cartprod_concat(to_match, all_users)
        pair_tensor = torch.tensor(all_pairs)

        
        preds = self.matcher(pair_tensor)
        return preds




    


