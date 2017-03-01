
/*
 * File:    isumit.c 
 * Purpose: to produce a seg fault aka RTE
 * Author:  pc2@ecs.csus.edu or http://www.ecs.csus.edu/pc2
 *
 * Thu Nov 03 13:55:33 2016
 *
 */

#include <stdio.h>

#define INFILENAME "sumit.dat"

int main (char **argv)
{
	int sum = 0;
	int i;

	char *p;

	p = 0;
	*p = 'a'; /* seg fault */

	return 22;
}

/* eof isumit.c */
