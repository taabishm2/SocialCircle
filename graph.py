from matplotlib import pyplot as plt
import networkx as nx
import random
import numpy as np
import time


# # =============================================================================

def threshold(x, t):
    if x > t:
        return 1
    else:
        return 0
    
# =============================================================================
def plot_graph(edge_to_weight, num_nodes, figname):
    rng = np.random.RandomState(0)
    pos = {i:(rng.rand(), rng.rand()) for i in range(num_nodes)}
    G = nx.Graph()
    G.add_weighted_edges_from([(i, j, w) for (i, j), w in edge_to_weight.items()])
    edges,weights = zip(*nx.get_edge_attributes(G,'weight').items())
    # weights = [threshold(w, 0.3) for w in list(weights)]
    nx.draw(G, node_size = 3, node_color = '#000000', width = .1, edge_color = weights, edge_cmap=plt.cm.Blues, pos = pos)
    # plt.savefig(figname, dpi=200)



# num_nodes = 20
# s = time.time()
# edges = np.random.choice([0, 1], size=(num_nodes, num_nodes))
# edge_to_weight = {(i, j) : np.random.rand() for i in range(num_nodes) for j in range(num_nodes) 
#                   if np.random.rand() < 0.05 and i != j}

# plot_graph(edge_to_weight, num_nodes, 'test.png')
