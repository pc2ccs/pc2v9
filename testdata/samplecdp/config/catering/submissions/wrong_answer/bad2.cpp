#include<iostream>
using namespace std;

const int MAXN = 100;
const int MAXK = 100;

long table[MAXN+1][MAXN+1][MAXK+1];	// table[i][k][k] = optimal cost for first i events
									//	with one set a event j using k sets of equipment
long cost[MAXN+1][MAXN+1];

int main()
{
	int n, nSets;

	cin >> n >> nSets;
	if (nSets > n)
		nSets = n;
	for(int i=0; i<n; i++) {
		for(int j=i+1; j<=n; j++) {
			cin >> cost[i][j];
		}
	}
	table[1][1][1] = cost[0][1];
	for(int i=2; i<=n; i++) {
		table[i][i][1] = table[i-1][i-1][1] + cost[i-1][i];
	}
	for(int i=2; i<=n; i++) {
		for(int j=1; j<i; j++)
			table[i][j][1] = table[n][n][1]+1;
	}
	for(int k=2; k<=nSets; k++) {
		for(int i=2; i<=n; i++) {
			for(int j=1; j<i; j++) {
				int ans = table[i-1][j][k-1];
				table[i][i][k] = ans;
				for(int l=1; l<j; l++) {
					if (table[i][l][k] + cost[l][j] < ans)
						ans = table[i][l][k] + cost[l][k];
				}
				if (table[i-1][j][k-1] + cost[0][i] < ans)
					ans = table[i-1][j][k-1] + cost[0][i];
				table[i][j][k] = ans;
				if (ans < table[i][i][k])
					table[i][i][k] = ans;
			}
		}
	}
	cout << table[n][n][nSets] << endl;
for(int i=1; i<=n; i++)
for(int j=1; j<=i; j++)
for(int k=1; k<=nSets; k++)
cout << "i,j,k=" << i << ',' << j << ',' << k << ": " << table[i][j][k] << endl;
}
