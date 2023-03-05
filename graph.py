from matplotlib import pyplot as plt
import networkx as nx
import random
import numpy as np
import time


# # =============================================================================
# num_nodes = 20
# s = time.time()
# edges = np.random.choice([0, 1], size=(num_nodes, num_nodes))
# edge_to_weight = {(i, j) : np.random.rand() for i in range(num_nodes) for j in range(num_nodes) 
#                   if np.random.rand() < 0.05 and i != j}

# =============================================================================
def plot_graph(edge_to_weight, num_nodes, figname):
    rng = np.random.RandomState(0)
    pos = {i:(rng.rand(), rng.rand()) for i in range(num_nodes)}
    G = nx.Graph()
    G.add_weighted_edges_from([(i, j, w) for (i, j), w in edge_to_weight.items()])
    
    cmap = plt.cm.Blues
    edges,weights = zip(*nx.get_edge_attributes(G,'weight').items())
    nx.draw(G, node_size = 20, node_color = '#000000', width = 5, edge_color = weights, edge_cmap= cmap, pos = pos)
    sm = plt.cm.ScalarMappable(cmap=cmap, norm=plt.Normalize(vmin = 0, vmax=1))
    sm._A = []
    plt.colorbar(sm)

num_nodes = 10
edge_to_weight = {(i, j) : 5*np.random.rand() for i in range(num_nodes) for j in range(num_nodes) 
                   if np.random.rand() < 0.05 and i != j}
print(edge_to_weight)
plot_graph(edge_to_weight, num_nodes, 'test.png')
