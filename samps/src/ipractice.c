/* Copyright (C) 1989-2019 PC2 Development Team: John Clevenger, Douglas Lane, Samir Ashoo, and Troy Boudreau. */

#include <stdio.h>
#include <stdlib.h>

/*
 * File:    ipractice.c
 * Purpose: sum of positive integers practice problem
 * Author:  pc2@ecs.csus.edu
 *
 * Returns 4 if there is an error, 0 on successful solution.
 *
 */

/* Fri Oct 11 22:24:12 2019 */

int main (int argc, char **argv)
{
	int sum = 0;
	int i = 0;
	char line[128];

		while ( fgets (line, sizeof(line), stdin) != NULL)
		{
			long inval = atol (line);
			sum = 0;

			if (inval == 0)
				exit (0);

			if (inval < 1)
			{
				for (i = 1; i >= inval; i --)
				       sum += i;	
			}
			else
			{
				for (i = 1; i <= inval; i ++)
				       sum += i;	
			}
			printf("N = %-3d    Sum = %d\n", inval, sum);
		}

	exit (0);
}

/* eof ipractice.c $Id$ */
