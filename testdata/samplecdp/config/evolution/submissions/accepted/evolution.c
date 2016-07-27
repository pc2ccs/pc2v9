
/**
 * World Finals 2015 problem solution: PARALLEL EVOLUTION
 * 
 * @author Martin Kacer
 */

#include <stdio.h>
#include <string.h>
#include <stdlib.h>

#define MAX_SAMPLES 5000
#define MAX_LENGTH 5000

int cnt;
char current[MAX_LENGTH+1];
char samples[MAX_SAMPLES+2][MAX_LENGTH+1];
int len[MAX_SAMPLES], idx[MAX_SAMPLES], next[MAX_SAMPLES+1];

int contains(const char* sup, const char* sub) {
	while (*sup && *sub) {
		if (*sup == *sub) ++sub;
		++sup;
	}
	return !*sub;
}

int length_comparator(const void* a, const void* b) {
	return len[*((const int*)a)] - len[*((const int*)b)];
}

int solve() {
	int i, i1 = MAX_SAMPLES, i2 = MAX_SAMPLES+1, ic = -1, icc = -1;
	for (i = 0; i < cnt; ++i) {
		if (!contains(current, samples[i])) return 0;
		len[i] = strlen(samples[i]);
		idx[i] = i;
	}
	qsort(idx, cnt, sizeof(int), &length_comparator);
	samples[MAX_SAMPLES][0] = samples[MAX_SAMPLES+1][0] = '\0';
	for (i = 0; i < cnt; ++i) {
		if (ic >= 0 ) {
			if (contains(samples[idx[i]], samples[ic])) {
				next[ic] = idx[i]; ic = idx[i];
			} else if (contains(samples[idx[i]], samples[i1])) {
				next[i1] = idx[i]; i1 = idx[i];
				next[i2] = icc; i2 = ic;
				ic = icc = -1;
			} else if (contains(samples[idx[i]], samples[i2])) {
				next[i2] = idx[i]; i2 = idx[i];
				next[i1] = icc; i1 = ic;
				ic = icc = -1;
			} else return 0;
		} else {
			if (contains(samples[idx[i]], samples[i1])) {
				if (contains(samples[idx[i]], samples[i2])) {
					ic = icc = idx[i];
				} else {
				next[i1] = idx[i]; i1 = idx[i];
				}
			} else if (contains(samples[idx[i]], samples[i2])) {
				next[i2] = idx[i]; i2 = idx[i];
			} else return 0;
		}
	}
	if (ic >= 0) {
		next[i2] = icc; i2 = ic;
	}
	ic = 0;
	for (i = MAX_SAMPLES; i != i1; i = next[i]) ++ic;
	printf("%d %d\n", ic, cnt-ic);
	for (i = MAX_SAMPLES; i != i1; i = next[i]) printf("%s\n", samples[next[i]]);
	for (i = MAX_SAMPLES+1; i != i2; i = next[i]) printf("%s\n", samples[next[i]]);
	return 1;
}

int main() {
	int i;
	scanf("%d", &cnt);
	scanf("%s", current);
	for (i = 0; i < cnt; ++i) scanf("%s", samples[i]);
	if (!solve()) printf("Impossible\n");
	return 0;
}
