import numpy as np
import pandas as pd
import random

def create_csv(num_user, num_att, num_per, num_tag, max_att, max_err):
    data = np.zeros((num_user, 1 + num_tag + num_att))
    
    # add personalities
    data.T[0] = np.random.randint(low = 0, high = num_per, size=num_user)
    
    # personality vectors
    prefs = {i: np.random.RandomState(i).uniform(-max_att,max_att, num_att) for i in range(num_per)}
    for col in data:
        # set tags
        col[1:num_tag+1] = np.random.choice(num_att, num_tag, replace=False,)
        
        # set preferences
        error = np.random.uniform(-max_err,max_err, num_att)
        col[num_tag+1:num_tag+num_att+1] = np.around(error + prefs[int(col[0])], 3)
    
    # export csv
    df = pd.DataFrame(data)
    df.reset_index(drop=True, inplace=True)
    pers = ["Personality"]
    tags = [f"T_{i}" for i in range(num_tag)]
    prefs = [f"A_{i}" for i in range(num_att)]
    cols = pers + tags + prefs
    df.columns = cols
    df.to_csv('users.csv', index = False)
    
create_csv(1000, 20, 8, 5, 1, 0)