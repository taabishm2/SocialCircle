import torch
import torch.nn as nn

class MatchingModel(nn.Module):

    def __init__(self, num_attributes, out_dims):
        super().__init__()

        layer_list = [nn.Linear(num_attributes, out_dims[0])]
        for d_curr, d_next in zip(out_dims[:-1], out_dims[1:]):
            layer_list.append(nn.ReLU())
            layer_list.append(nn.Linear(d_curr, d_next))

        self.layers = nn.Sequential(*layer_list)
        self.sigmoid = nn.Sigmoid()

    def forward(self, vec1, vec2):
        return self.sigmoid(torch.sum(self.layers(vec1) * self.layers(vec2), dim=-1))
        