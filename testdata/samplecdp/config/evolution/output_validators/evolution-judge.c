
/**
 * World Finals 2015 automatic judge: PARALLEL EVOLUTION
 *
 * Usage:
 * - the team's output is read from stdin
 * - the input data file name is argv[1]
 * - return value: see the constants below
 * ACCEPT: the output is correct
 * MINOR: the output format is slightly different but still ok
 * FORMAT: the output has incorrect format (will be judged as wrong answer)
 * WRONG: the format seems ok but the answer is not correct
 * ERROR: internal error in this judge or the test data
 * 
 * @author Martin Kacer
 */

#include <stdio.h>
#include <string.h>
#include <strings.h>
#include <stdlib.h>
#include <stdarg.h>
#include <ctype.h>

#define RES_ACCEPT 42
#define RES_MINOR 42
#define RES_FORMAT 43
#define RES_WRONG 43
#define RES_ERROR 1

#define FLG_SPACE_LEAD 1
#define FLG_SPACE_TRAIL 2
#define FLG_SPACE_EXTRA 4
#define FLG_CASE 8

#define FLG_LEAD_ZERO 16

#define MAX_SAMPLES 5000
#define MAX_LENGTH 5000

FILE* fin, * fout, * fmsg;
int line_nr = 0;
int ok_result = RES_ACCEPT, minor_flags = 0;

int cnt;
char current[MAX_LENGTH+1], input_token[MAX_LENGTH+10];
char samples[MAX_SAMPLES+2][MAX_LENGTH+1];
int len[MAX_SAMPLES], idx[MAX_SAMPLES];

void feedback(const char* msg, ...) {
	va_list pvar, pvar2;
	va_start(pvar, msg);
	va_copy(pvar2, pvar);
	if (msg) {
		vprintf(msg, pvar);
		if (line_nr) printf(" (line #%d)", line_nr);
		printf("\n");
		if (fmsg) {
			if (line_nr) fprintf(fmsg, "line #%d: ", line_nr);
			vfprintf(fmsg, msg, pvar2);
			fprintf(fmsg, "\n");
		}
	}
	va_end(pvar);
	va_end(pvar2);
}

void minor_format(int flag, const char* msg) {
	if (minor_flags & flag) return;
	minor_flags |= flag;
	feedback("minor formatting issue: %s", msg);
}

void result(int res, const char* msg) {
	if (fin) fclose(fin);
	if (msg)
		feedback("%s", msg);
	if (res != RES_ACCEPT && res != RES_MINOR && line_nr)
		feedback("Team output: %s", input_token);
	exit(res);
}

/* ===== SET OF SAMPLES (character tree) ===== */
int set_size, set_root;
int set_tree[MAX_SAMPLES*MAX_LENGTH][4];
char set_used[MAX_SAMPLES*MAX_LENGTH];

int char_value(char c) {
	switch (c) {
		case 'A': return 0;
		case 'C': return 1;
		case 'G': case 'M': return 2;
		case 'T': return 3;
	}
	return -1;
}

int set_alloc() {
	int i;
	for (i = 0; i < 4; ++i) set_tree[set_size][i] = -1;
	set_used[set_size] = 0;
	return set_size++;
}

void set_clear() {
	set_size = 0;
	set_root = set_alloc();
}

int set_add(const char* str) {
	int node = set_root;
	while (*str) {
		int v = char_value(*(str++));
		if (v < 0) result(RES_ERROR, "invalid character in the input data!");
		if (set_tree[node][v] < 0) set_tree[node][v] = set_alloc();
		node = set_tree[node][v];
	}
	if (set_used[node]) return 0;
	set_used[node] = 1;
	return 1;
}

int set_remove(const char* str) {
	int node = set_root;
	while (*str) {
		int v = char_value(*(str++));
		if (v < 0) result(RES_FORMAT, "invalid character in a DNA sample");
		if (set_tree[node][v] < 0) return 0;
		node = set_tree[node][v];
	}
	if (!set_used[node]) return 0;
	set_used[node] = 0;
	return 1;
}


/* ===== partial problem solution (detects whether it is solvable) ===== */

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
				ic = idx[i];
			} else if (contains(samples[idx[i]], samples[i1])) {
				i1 = idx[i];
				i2 = ic;
				ic = icc = -1;
			} else if (contains(samples[idx[i]], samples[i2])) {
				i2 = idx[i];
				i1 = ic;
				ic = icc = -1;
			} else return 0;
		} else {
			if (contains(samples[idx[i]], samples[i1])) {
				if (contains(samples[idx[i]], samples[i2])) {
					ic = icc = idx[i];
				} else {
				i1 = idx[i];
				}
			} else if (contains(samples[idx[i]], samples[i2])) {
				i2 = idx[i];
			} else return 0;
		}
	}
	return 1;
}

/* ===== validation ===== */

int read_char() {
	if (!line_nr) line_nr = 1;
	int c = fgetc(fout);
	if (c == '\n') ++line_nr;
	return c;
}

char* read_opt_token() {
	int len = 0;
	int c = read_char();
	while (isspace(c)) c = read_char();
	while (!isspace(c) && c != EOF && len <= MAX_LENGTH) {
		input_token[len++] = c;
		c = read_char();
	}
	input_token[len] = '\0'; // must be there BEFORE we call "result"
	if (len > MAX_LENGTH) result(RES_FORMAT, "line is too long");
	return input_token;
}

char* read_token() {
	read_opt_token();
	if (!input_token[0]) result(RES_FORMAT, "premature EOF when a line is expected");
	return input_token;
}

int check_one_number(char* str) {
	char *s = str;
	while (*s >= '0' && *s <= '9') ++s;
	if (s == str) result(RES_FORMAT, "number does not start with a digit");
	if (s > str + 6) result(RES_FORMAT, "number too long");
	if (*s != '\0') result(RES_FORMAT, "additional characters following a number");
	if (*str == '0' && s - str > 1) minor_format(FLG_LEAD_ZERO, "unnecessary leading zero in a number");
	return atoi(str);
}

void check_sequence(int c) {
	char s[MAX_LENGTH+10];
	s[0] = '\0';
	while (c-- > 0) {
		if (!set_remove(read_token())) result(RES_WRONG, "a sample does not occur in the input or is duplicated in the output");
		if (!contains(input_token, s)) result(RES_WRONG, "a sample is not derived from the previous one");
		strcpy(s, input_token);
	}
}

void check_solvable() {
	int c1, c2;
	char* str = read_token();
	if (strcasecmp(str, "impossible") == 0) result(RES_WRONG, "a solvable test case claimed impossible");
	c1 = check_one_number(str);
	c2 = check_one_number(read_token());
	if (c1 + c2 != cnt) result(RES_WRONG, "the size of two evolution lines does not match the number of samples");
	check_sequence(c1);
	check_sequence(c2);
}

int main(int argc, char* argv[]) {
	int i;
	if (argc < 2) { fprintf(stderr, "Command-line argument missing\n"); return RES_ERROR; }
	if (!(fin = fopen(argv[1], "r"))) { perror("opening input file"); return RES_ERROR; }
	fout = stdin;
	if (argc > 3) {
		char *nmsg = (char*) malloc(strlen(argv[3]) + 40);
		sprintf(nmsg, "%s/judgemessage.txt", argv[3]);
		fmsg = fopen(nmsg, "w");
		free(nmsg);
	}
	
	if (fscanf(fin, "%d", &cnt) != 1) result(RES_ERROR, "error reading number");
	if (fscanf(fin, "%s", current) != 1) result(RES_ERROR, "error reading the first line of input");
	set_clear();
	for (i = 0; i < cnt; ++i) {
		if (fscanf(fin, "%s", samples[i]) != 1) result(RES_ERROR, "error reading input");
		if (!set_add(samples[i])) result(RES_ERROR, "duplicate sample in the input!");
	}
	
	if (!solve()) {
		if (strcmp(read_token(), "impossible")) {
			if (strcasecmp(input_token, "impossible"))
				result(RES_WRONG, "wrong answer for an impossible test case");
			else
				minor_format(FLG_CASE, "uppercase character found");
		}
	} else {
		check_solvable();
	}
	if (read_opt_token()[0]) result(RES_FORMAT, "no EOF after a correct output");
	result(ok_result, "the output is correct");
	return RES_ERROR; // should not get here
}
