//
// File:    practice.cpp
// Purpose: to sum from the input number to zero 
// Author:  pc2@ecs.csus.edu or http://www.ecs.csus.edu/pc2
//
// caveat - this is not nice code, copy at own risk ;)
//
// $Id$
//
// $Header$
//
//

#include <fstream>
#include <iostream>
using namespace std;

main ()
{
	int num;
	int sum;
	int i;
	ifstream filein("practice.dat");

	if (filein.fail())
	{
		cerr << "Could not read from file practice.dat " << endl;
	}
	else
	{

		sum = 0;

		while(!filein.eof())
		{
			filein >> num;
			sum = 0;

			if (num == 0)
				exit (0);

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
			// cout << "N = " << num << " Sum = " << sum << endl;
			// requires formatting
		  printf("N = %-3d    Sum = %d\n", num, sum);

		}

		cout << "Did not find trailing zero " << endl;
	}
}

// eof $Id$ practice.cpp
