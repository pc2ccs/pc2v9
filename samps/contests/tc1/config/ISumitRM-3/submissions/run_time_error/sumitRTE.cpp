/* Copyright (C) 1989-2019 PC2 Development Team: John Clevenger, Douglas Lane, Samir Ashoo, and Troy Boudreau. */

//
// Purpose: to produce a RTE
// Author:  pc2@ecs.csus.edu or http://www.ecs.csus.edu/pc2
//
// Thu Nov 03 13:56:38 2016
//
//

#include <fstream>
#include <iostream>
using namespace std;

main ()
{

	char *p;

	p = 0;
	*p = 'a'; /* seg fault */

}

// eof isumit.c $Id$
