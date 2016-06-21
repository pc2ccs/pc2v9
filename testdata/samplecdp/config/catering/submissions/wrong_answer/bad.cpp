#include<iostream>
using namespace std;

const int MAXN = 100;
const int MAXK = 100;

long table[MAXN+1][MAXK+1];	// table[i][j] = optimal cost for first i events using j sets of equipment
long cost[MAXN+1][MAXN+1];

int main()
{
	int n, k;

	cin >> n >> k;
	if (k > n)
		k = n;
	for(int i=0; i<n; i++) {
		for(int j=i+1; j<=n; j++) {
			cin >> cost[i][j];
		}
	}
//cout << "1:";
	table[1][1] = cost[0][1];
//cout << ' ' << table[1][1];
	for(int i=2; i<=n; i++) {
		table[i][1] = table[i-1][1] + cost[i-1][i];
//cout << " " << table[i][1];
	}
//cout << endl;
	for(int j=2; j<=k; j++) {
//cout << j << ":";
		table[1][j] = table[1][j-1];
//cout << ' ' << table[1][j];
		for(int i=2; i<=n; i++) {
			int val = table[i-1][j-1] + cost[0][i];
			for(int l = 1; l<i; l++) {
				if (table[i-1][j] + cost[l][i] < val)
					val = table[i-1][j] + cost[l][i];
			}
			table[i][j] = val;
//cout << ' ' << table[i][j];
		}
	}
//cout << endl;
	cout << table[n][k] << endl;
}
