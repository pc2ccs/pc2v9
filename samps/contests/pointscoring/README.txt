This folder contains the configuration for a PC2 sample "point scoring" contest. 

The contest has a "problemset.yaml" file containing descriptions of three problems:  'a', 'b', and 'c'. 
The intent is to (eventually) configure a variety of different problems, each with different "scoring properties".
Currently however, problem folders 'b' and 'c' contain duplicates of problem 'a' (except with the appropriate problem names changed).

The intent is to expand the contest by modifying the scoring attributes of problems 'b' and 'c' so that they test various other
  "point scoring" attribute combinations. (Eventually of course there may be additional combinations, requiring addition of new problems 'd', 'e', etc.)
  
All problems are intended to be based on the PC2 "Sumit" problem ("read input integers and print the sum of the positive integers"). 
This allows for easy testing, since there already exist correct and incorrect solutions to Sumit (in several languages) in the PC2 "samps/src" folder.

Problem 'a' has the following attributes:
  - a problem_statement folder with a "problem.tex" file describing the PC2 "Sumit" problem.
  - a problem.yaml file stating that the problem named "a" is "type:scoring".
  - a submissions folder containing accepted, "partially accepted" and "wrong answer" solutions for the Sumit problem.
      (The "partially accepted" submission prints the sum of ALL integers in the input; i.e., it fails to ignore negative integers.  
       This could be used for example to give "partial credit" for such a solution.)
  - both "sample" and "secret" data folders, but only with test data (.in/.ans pairs) at the root level (i.e., no scoring subgroups)
  - no "testdata.yaml" files at any level -- hence, tests the premise that if there is no testdata.yaml file, then a "testdata.yaml"
      file is implicitly added to the root ("data") group (see https://www.kattis.com/problem-package-format/spec/legacy.html#test-data-groups)
      with default values, which are:
        - on_reject: break
        - grading: default
        - grader_flags: ""  (i.e. the empty string)
        - input_validator_flags: ""
        - output_validator_flags: ""
        - accept_score: 1.0
        - reject_score: 0.0
        - range: -inf +inf
  - Since "grader_flags" defaults to the empty string, the Grader will implicitly use the following default values
      (see https://www.kattis.com/problem-package-format/spec/legacy.html#default-grader-specification):
      - verdict mode: worst_error
      - scoring mode: sum
      
Problems 'b' and 'c' are currently just duplicates of problem 'a'; they need to be modified to test other point-scoring combinations
 (for example, the use of scoring subgroups; the use of different grader flags, etc.)
 
 
        