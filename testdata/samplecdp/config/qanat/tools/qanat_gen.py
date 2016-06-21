#!/usr/bin/python3 -tt

import sys, random, os

# Global Constants
max_w = 10000
max_n = 1000
inputfile_num = 40
filename_prefix = 'qanat'

# Global Variables
file_index = 1
fixed_files = [
"""8 4 1
""",
"""195 65 2
""",
"""10000 1 1000
""",
"""2 1 5
""",
"""10000 9999 1
""",
"""10000 1 1
""",
"""10000 51 500
""",
"""10000 185 500
""",
"""1000 995 2
""",
"""10000 1 9
""",
"""10000 1 10
""",
"""10000 1 11
"""
]

def write_random_input(file_path, w_lb = 2, w_ub = max_w, n_lb = 1, n_ub = max_n):
  f = open(file_path, 'w')

  w = random.randint(w_lb, w_ub)
  h = random.randint(1, 1 + w // 100)
  n = random.randint(n_lb, n_ub)
  f.write(str(w) + ' ' + str(h) + ' ' + str(n) + '\n')

  f.close()


def write_fixed_input(file_path, contents):
  f = open(file_path, 'w')
  f.write(contents)
  f.close()

def next_file_path(data_dir):
  global file_index
  str_file_index = str(file_index)
  if file_index < 10:
    str_file_index = '0' + str_file_index
  filename = filename_prefix + '-' + str_file_index + '.in'
  print("Starting to create %s" % filename)
  file_index += 1
  return os.path.join(data_dir, filename)
  

def main():
  if len(sys.argv) != 2:
    print('Usage: %s data_dir' % (sys.argv[0]))
    sys.exit(1)
  
  data_dir = os.path.abspath(sys.argv[1])
  if not os.path.exists(data_dir):
    os.makedirs(data_dir)

  for contents in fixed_files:
    write_fixed_input(next_file_path(data_dir), contents)

  for _ in range(inputfile_num // 3):  
    write_random_input(next_file_path(data_dir))

  for _ in range(inputfile_num - file_index + 1):
    write_random_input(next_file_path(data_dir), 2, max_w, max_n // 2, max_n)

if __name__ == '__main__':
  main()
