import torch
import torch.nn as nn

class MatchingModel(nn.Module):

    def __init__(self, num_attributes):
        super().__init__()

        self.layers = nn.Sequential(*[
            nn.Linear(num_attributes * 2, 1),
            nn.Sigmoid()
        ])

    def forward(self, x):
        return self.layers(x)
        