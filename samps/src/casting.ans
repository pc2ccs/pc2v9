/*
 * B - B-Casting
 * ICPC 2012 Greater NY Regional
 * Solution by Lee Wittenberg
 * Problem by Lee Wittenberg
 */
#include <stdio.h>
#include <assert.h>
#include <ctype.h>

int main()
{
	int p, i, i1, b, result, c;
	scanf("%d", &p);				/* get # of data sets */
	for (i = 0; i < p; i++) {
		scanf("%d %d ", &i1, &b);	/* trailing space in fmt eats spaces */
		assert(i1 == i + 1);		/* between B and N */
		result = 0;
		while (isdigit(c = getchar())) {
			result = (result + c - '0') % (b - 1);
		}
		printf("%d %d\n", i1, result);
	}
	return 0;
}
