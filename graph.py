from matplotlib import pyplot as plt
import networkx as nx
import random
import numpy as np
import time


# num_nodes = 600
# edge_to_weight = {(i, j) : np.random.rand() for i in range(num_nodes) for j in range(num_nodes) 
#                   if np.random.rand() < 0.05 and i != j}
# 

def plot_graph(edge_to_weight, num_nodes):
    rng = np.random.RandomState(0)
    pos = {i:(rng.rand(), rng.rand()) for i in range(num_nodes)}
    G = nx.Graph()
    G.add_weighted_edges_from([(i, j, w) for (i, j), w in edge_to_weight.items()])
    edges,weights = zip(*nx.get_edge_attributes(G,'weight').items())
    nx.draw(G, node_size = 5, node_color = '#000000', width = .1, edge_color = weights, edge_cmap=plt.cm.Blues, pos = pos)

