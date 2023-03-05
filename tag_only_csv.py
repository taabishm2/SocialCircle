import pandas as pd


orig_csv = pd.read_csv('users.csv')
tag_colnames = [c for c in orig_csv.columns if c[0:2] == 'T_']

orig_csv[['Personality'] + tag_colnames].to_csv('users_tags_only.csv')

