
/*
 * File:    isumit.c 
 * Purpose: to sum the integers from stdin
 * Author:  pc2@ecs.csus.edu or http://www.ecs.csus.edu/pc2
 *
 * Thu Nov 03 14:07:07 2016
 *
 *
 * TODO cause tle by sleeping/running for a long time
 */

#include <stdio.h>

#define INFILENAME "sumit.dat"

int main (char **argv)
{
	int sum = 0;
	int i;

	while (1==fscanf(stdin, "%d", &i))
	{
		sum += i > 0 ? i : 0;
	}

	sum += 1000;

	printf("The sum of the integers is %d\n",sum);

	return 0;
}

/* eof isumit.c */
