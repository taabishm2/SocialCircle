import numpy as np
import pandas as pd
from binarytree import tree
import random


random.seed(9)
class user():
    def __init__(self, P, attributes):
        self.tree = decisionTree(P, 4)
        self.attributes = attributes
    
class decisionTree():
    def __init__(self, P, h):
        self.P = P
        self.h = h
        self.root = self.createTree(P, h)
        
    
    def createTree(self, P, h):
        root = tree(height=h, is_perfect=False)
        print(root)
        return root
    
    def attributeScore(self, attributes):
        curr = self.tree
        for i in range(self.h):
            attribute = attributes.index(curr)
            
        print(attribute)
    

sn = user("A", "B")