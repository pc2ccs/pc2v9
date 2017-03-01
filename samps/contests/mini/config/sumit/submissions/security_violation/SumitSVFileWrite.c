
/*
 * File:    isumit.c 
 * Purpose: to sum the integers from stdin
 * Author:  pc2@ecs.csus.edu or http://www.ecs.csus.edu/pc2
 *
 * Thu Nov  3 17:03:27 PDT 2016
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

	sum += 1000;

	printf("The sum of the integers is %d\n",sum);

	FILE *fpo = fopen ("sumitCoutputfile.txt", "w");
	fprintf(fpo, "The sum of the integers is %d\n",sum);
	fclose (fpo);

	return 0;
}

/* eof isumit.c */
