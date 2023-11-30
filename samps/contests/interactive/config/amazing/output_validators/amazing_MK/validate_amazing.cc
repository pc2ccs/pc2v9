
#include "validate.h"
#include <fstream>
#include <algorithm>
#include <signal.h>

using namespace std;

// has to be higher than the real value
#define MAX 120

bool vert[MAX][MAX], horiz[MAX][MAX];
bool history[MAX][MAX][4];

int rows, cols, myr, myc;

void strtolower(string & str) {
	for (int i = 0; i < str.length(); ++i) str[i] = tolower(str[i]);
}

void read_input() {
	string onerow;
	if (!(judge_in >> rows >> cols >> myr >> myc))
		judge_error("Cannot read maze size and position\n");
	for (int r = 0; r <= rows; ++r) {
		if (!(judge_in >> onerow)) judge_error("Cannot read row #%d\n", r);
		if (onerow.length() != 2*cols+1) judge_error("Incorrect length of row #%d\n", r);
		for (int c = 0; c <= cols; ++c) {
			history[r][c][0] = history[r][c][1] = history[r][c][2] = history[r][c][3] = false;
			if (c < cols) horiz[r][c] = onerow[2*c+1] == '_';
			if (r > 0) vert[r-1][c] = onerow[2*c] == '|';
		}
	}
}

void run_solution(bool solvable) {
	string command;
	while (author_out >> command) {
		strtolower(command);
		bool step = true;
		int dir;
		bool* histpos = history[myr][myc];
		if (command == "left") {
			dir=0; if (vert[myr-1][myc-1]) step = false; else --myc; 
		} else if (command == "right") {
			dir=1; if (vert[myr-1][myc]) step = false; else ++myc; 
		} else if (command == "up") {
			dir=2; if (horiz[myr-1][myc-1]) step = false; else --myr; 
		} else if (command == "down") {
			dir=3; if (horiz[myr][myc-1]) step = false; else ++myr; 
		} else if (command == "no") {
			if (solvable)
				wrong_answer("Claiming \'no way out\' incorrectly\n");
			author_out >> command;
			strtolower(command);
			if (command != "way") wrong_answer("expected \'way\'\n");
			author_out >> command;
			strtolower(command);
			if (command != "out") wrong_answer("expected \'out\'\n");
			return;
		} else {
			wrong_answer("Unrecognized command\n");
		}
		if (histpos[dir]) {
			wrong_answer("Repeating move \'%s\', position %d,%d\n", command.c_str(), myr, myc);
		}
		histpos[dir] = true;
		if (myr < 1 || myr > rows || myc < 1 || myc > cols) {
			// found way out (this is a simplified version where the exit may be on the maze boundary only)
			if (!solvable)
				judge_error("The solution just found an exit from a maze that should be unsolvable!\n");
			return;
		}
		cout << (step ? "ok" : "wall") << endl;
		cout.flush();
	}
	wrong_answer("End of input without solving\n");
}


int main(int argc, char* argv[]) {
	signal(SIGPIPE, SIG_IGN);

	init_io(argc, argv);

	read_input();
	string solution;
	if (!(judge_ans >> solution)) judge_error("Cannot read answer file\n");
	run_solution(solution == "yes");
	cout << "solved" << endl;
	cout.flush();
	
	string excess;
	if (author_out >> excess) {
		wrong_answer("Excessive output after solving\n");
	}
	
	accept();
}
