#include <cstdio>
#include <vector>
#include <iostream>
#include <string>
using namespace std;

typedef vector<int> vi;
typedef vector<vi> vvi;

const int LIM = 200201;
int dr[4] = {1, 0, 0, -1};
int dc[4] = {0, -1, 1, 0};
string moves[4] = {"up","left","right","down"};

bool visited[200201];
bool exitFound = false;

int getMap(int i, int j) { return (100-i)*100+(100-j); }

void explore(int i, int j)
{
	visited[getMap(i,j)] = true;
	for (int k = 0; k < 4; k++)
	{
	    int ni = i+dc[k], nj = j+dr[k];
	    if (!visited[getMap(ni,nj)])
	    {
	        cout << moves[k] << endl;
	        cout << flush;
	        string response;
	        getline(cin,response);
	        if (response.compare("ok") == 0)
	        {
	            explore(ni,nj);
	            if (exitFound) return;
	            cout << moves[3-k] << endl;
	            cout << flush;
	            getline(cin,response);
	        }
	        else if (response.compare("solve") == 0)
	        {
	            exitFound = true; return;
	        }
	    }
	}
}

int main()
{
	for (int i = 0; i < LIM; i++) 
		visited[i] = false;
	explore(0, 0);
	if (!exitFound) 
	{
		cout << "no way out" << endl;
		cout << flush;
		string response;
		getline(cin,response);
		return 0;
	}
	return 0;
}