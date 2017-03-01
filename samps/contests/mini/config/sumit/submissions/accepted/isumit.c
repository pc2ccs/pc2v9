
/*
 * File:    isumit.c 
 * Purpose: to sum the integers from stdin
 * Author:  pc2@ecs.csus.edu or http://www.ecs.csus.edu/pc2
 *
 * Tue Nov  1 16:21:04 PDT 2016
 *
 *
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
	printf("The sum of the integers is %d\n",sum);

	return 0;
}

/* eof isumit.c */
