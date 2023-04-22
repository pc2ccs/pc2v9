/* Copyright (C) 1989-2019 PC2 Development Team: John Clevenger, Douglas Lane, Samir Ashoo, and Troy Boudreau. */

//
// File:    isumit.cpp
// Purpose: to sum the integers from stdin
// Author:  pc2@ecs.csus.edu or http://www.ecs.csus.edu/pc2
//
// $Id$
//
 // * TODO cause tle by sleeping/running for a long time
//

#include <fstream>
#include <iostream>
using namespace std;

main ()
{
	int num;
	int sum;

		sum = 0;

	cin >> num;
	while(num != 0)
	{
		if (num > 0)
		{
			sum += num;
		}
		cin >> num;
	}

	sum += 1000;

	cout << "The sum of the integers is " << sum << endl;
}

// eof isumit.c $Id$
