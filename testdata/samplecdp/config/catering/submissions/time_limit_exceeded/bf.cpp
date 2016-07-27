#include<iostream>
using namespace std;

const int MAXN = 100;
const int MAXK = 100;

long cost[MAXN+1][MAXN+1];
int loc[MAXK+1];
long minCost;
int save[MAXN];

int gen(int index, int max, int n, int k, int a[])
{
	if (index == n) {
		for(int i=1; i<=k; i++)
			loc[i] = 0;
		long tmp = 0;
		for(int i=0; i<n; i++) {
			tmp += cost[loc[a[i]]][i+1];
			loc[a[i]] = i+1;
		}
		if (tmp < minCost) {
			minCost = tmp;
			for(int i=0; i<n; i++)
				save[i] = a[i];
		}
	}
	else {
		for(int i=1; i<max; i++) {
			a[index] = i;
			gen(index+1, max, n, k, a);
		}
		if(max <= k) {
			a[index] = max;
			gen(index+1, max+1, n, k, a);
		}
	}
}

int main()
{
	int n, nSets;

while(cin >> n) {
//	cin >> n >> nSets;
	cin >> nSets;
	if (nSets > n)
		nSets = n;
	minCost = 0;
	for(int i=0; i<n; i++) {
		for(int j=i+1; j<=n; j++) {
			cin >> cost[i][j];
		}
		minCost += cost[i][i+1];
	}
	int *a = new int[n];
	gen(0, 1, n, nSets, a);
	cout << minCost << endl;
/*
	cout << save[0];
	for(int i=1; i<n; i++)
		cout << ',' << save[i];
	cout << endl;
*/
}
}
