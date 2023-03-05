from matplotlib import pyplot as plt
import networkx as nx
#from simulation import Simulation

# =============================================================================
# s = Simulation('users.csv')
# edges = s.curr_cons
# =============================================================================


edges = {(0, 1), (0, 2), (1, 2)}
edge_to_weight = {(0, 1) : .8, (0, 2) : .1, (1, 2) : .4}

def plot_graph(edges, edge_to_weight):
    G = nx.Graph(edges)
    for u,v,d in G.edges(data=True):
        d['weight'] = edge_to_weight[(u, v)]
    edges,weights = zip(*nx.get_edge_attributes(G,'weight').items())
    nx.draw(G, edge_color = weights, width = 8, edgelist=edges,edge_cmap=plt.cm.Blues)
    plt.show()
    
plot_graph(edges, edge_to_weight)

# =============================================================================
# def self.plot_graph():
#     G = nx.Graph(self.edges)
#     for u,v,d in G.edges(data=True):
#         d['weight'] = self.con_to_score[(u, v)]
#     edges,weights = zip(*nx.get_edge_attributes(G,'weight').items())
#     nx.draw(G, edge_color = weights, width = 8, edgelist=edges,edge_cmap=plt.cm.Blues)
#     plt.show()
# 
#     
# =============================================================================
