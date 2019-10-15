/* Copyright (C) 1989-2019 PC2 Development Team: John Clevenger, Douglas Lane, Samir Ashoo, and Troy Boudreau. */
//
// File:    ipractice.cpp
// Purpose: to sum from the input number to zero 
// Author:  pc2@ecs.csus.edu or http://www.ecs.csus.edu/pc2
//
// Fri Oct 11 23:29:50 PDT 2019
//

#include <fstream>
#include <iostream>
using namespace std;

main ()
{
	int num;
	int sum;
	int i;

  sum = 0;

  cin >> num;

  while( num != 0 )
  {
    sum = 0;

    if (num < 1)
    {
      for (i = 1; i >= num; i --)
             sum += i;	
    }
    else
    {
      for (i = 1; i <= num; i ++)
             sum += i;	
    }
    printf("N = %-3d    Sum = %d\n", num, sum);

    cin >> num;

  }

}

// eof ipractice.cpp
