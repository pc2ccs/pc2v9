using namespace std;
#include <iostream>

bool done = false;

int explore(){
    cerr << "SUBMISSION making query\n" << flush;
	string str = "";
	cout << "up\n" << flush;
	cin >> str;
	if (str != "wall" && !done) {
		explore();
		cout << "down\n" << flush;
	} else if (str == "solved"){
		done = true;
	}
	cout << "right\n" << flush;
	cin >> str;
	if (str != "wall" && !done) {
		explore();
		cout << "left\n" << flush;
	} else if (str == "solved"){
		done = true;
	}
	cout << "down\n" << flush;
	cin >> str;
	if (str != "wall" && !done) {
		explore();
		cout << "up\n" << flush;
	} else if (str == "solved"){
		done = true;
	}
	cout << "left\n" << flush;
	if (str != "wall" && !done) {
		explore();
	} else if (str == "solved"){
		done = true;
	} else if (!done){
	}
}

int main(){
	explore();
}
