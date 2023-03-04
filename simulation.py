import numpy as np
from matching_model import MatchingModel

class Simulation:

    def __init__(self, num_users, num_attributes, num_personalies):

        self.graph_mat = np.zeros((num_users, num_users))
        self.matcher = MatchingModel()


    def timestep(self):
        # runs the entire simulation for one timestep

        # get list of all users who requested a new match

        # for each of these users, generate a predicted match ranking

        # for each of these users (again), pick the match with the highest combined score
        # (min or product between the two user's scores) and generate the interaction timeseries

        # for each of these users (again), if it has been x timesteps, check the interaction timeseries
        # and aggregate it into a single success metric (maybe avg # of interactions and their reviews)
        # and backprop through the matcher model


        pass


    def get_match_requests(self):
        # returns the list of users that requested a match at the current timestep (could be empty)
        pass


    def get_match_ranking(self, user_idx):
        # returns a match for the user given by user_idx
        # meaning it returns a list of predicted match scores between all users and user_idx
        pass

    


