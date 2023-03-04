import numpy as np
import pandas as pd
from binarytree import build
import random


random.seed(1)
class user():
    def __init__(self, P, attributes):
        self.tree = tree(P)
        self.attributes = attributes
    
class tree():
    def __init__(self, P):
        self.P = P
        self.root = self.createTree(P, 10)
        
    
    def createTree(self, P, size):
        values = random.sample(range(size), size)
        root = build(values)
        print(root)
        return root
    
    def attributeScore(self, attributes):
        pass

