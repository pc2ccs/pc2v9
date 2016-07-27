"""
I'm sure kattis does this already, but hardcoding my own for convenience.
"""
import os
import subprocess
import sys

datadir = '../../data/secret'
execdir = '../submissions/accepted/'
IN = '.in'

os.chdir(execdir)

if len(sys.argv) == 1:
    ANS = '.ans'
    cmd = './weather'
else:
    ANS = '.alt'
    cmd = 'java Weather'

for f in os.listdir(datadir):
    if f.endswith(IN):
        name = f[:-len(IN)]
        full = (cmd + ' < ' + datadir + '/' + name + IN + ' > ' +
                datadir + '/' + name + ANS)
        print full
        subprocess.call(full, shell=True)
