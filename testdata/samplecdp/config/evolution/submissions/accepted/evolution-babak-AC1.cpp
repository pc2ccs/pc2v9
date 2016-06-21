// Note: this is the solution written for DNA (as the problem was
// called then) for ICPC 2013, slightly modified for the new format. /Per
#include <algorithm>
#include <iostream>
#include <vector>
#include <string>

using namespace std;

const int MAX_LENGTH = 4000;

bool isSubsequence(string a, string b)
{
	int posb = 0;
	for (int i = 0; i < a.length(); i++)
	{
		bool found = false;
		for (int j = posb; j < b.length() && !found; j++)
			if (a[i] == b[j])
			{
				found = true;
				posb = j + 1;
			}
		if (!found)
			return false;
	}
	return true;
}

int main()
{
	int n;
	int caseNum = 0;
	while (cin >> n)
	{		
		string dna;
		cin >> dna;
		
		bool possible = true;
		vector<vector<string> > bucket(MAX_LENGTH, vector<string>());
		for (int i = 0; i < n; i++)
		{
			string strtmp;
			cin >> strtmp;
			if (bucket[MAX_LENGTH - strtmp.length()].size() < 2)
				bucket[MAX_LENGTH - strtmp.length()].push_back(strtmp);
			else
				possible = false;
		}
		
		vector<string> test;
		for (int i = 0; i < bucket.size(); i++)
			for (int j = 0; j < bucket[i].size(); j++)
				test.push_back(bucket[i][j]);
		
		//cerr << n << endl;
		//for (int i = 0; i < test.size(); i++)
			//cerr << test[i] << endl;

		vector<string> currentDna(2, dna);
		vector<string> seq[2];
		int blockStart = 0;
		while (possible && blockStart < test.size())
		{
			//cerr << "Blockstart = " << blockStart << endl;
			int pos = blockStart;
			while (pos < test.size() - 1 && isSubsequence(test[pos + 1], test[pos]))
				pos++;
			int blockEnd = pos + 1;
			
			//cerr << "Blcokend = " << blockEnd << endl;
			
			int blockSplit = blockEnd - 2;
			while (blockSplit >= blockStart && (blockEnd == test.size() || !isSubsequence(test[blockEnd], test[blockSplit])))
				blockSplit--;
			
			//cerr << "Blocksplit = " << blockSplit << endl;
			
			if (blockSplit >= blockStart)
			{
				possible = false;
				for (int i = 0; i < 2 && !possible; i++)
					if (isSubsequence(test[blockStart], currentDna[i]))
					{
						for (int j = blockStart; j <= blockSplit; j++)
							seq[i].push_back(test[j]);
						possible = true;
					}
			}

			if (possible)
			{
				possible = false;
				for (int i = 0; i < 2 && !possible; i++)
				{
					//cerr << isSubsequence(test[blockSplit + 1], currentDna[i]) << endl;
					//cerr << (blockEnd == test.size() || isSubsequence(test[blockEnd], currentDna[(i + 1) % 2])) << endl;
					if (isSubsequence(test[blockSplit + 1], currentDna[i]) && 
					(blockEnd == test.size() || isSubsequence(test[blockEnd], currentDna[(i + 1) % 2])))
					{
						for (int j = blockSplit + 1; j < blockEnd; j++)
							seq[i].push_back(test[j]);
						if (blockEnd < test.size())
						{
							seq[(i + 1) % 2].push_back(test[blockEnd]);
							currentDna[i] = test[blockEnd - 1];
							currentDna[(i + 1) % 2] = test[blockEnd];
							//cerr << currentDna[0] << ' ' << currentDna[1] << endl;
						}
						
						possible = true;
						blockStart = blockEnd + 1;
					}
				}
			}
		}
		
		caseNum++;
//		cout << "Case " << caseNum << ": ";
		if (!possible)
		{
			//cerr << "impossible" << endl;
			cout << "Impossible" << endl;
		}
		else
		{
			reverse(seq[0].begin(), seq[0].end());
			reverse(seq[1].begin(), seq[1].end());
			cout << seq[0].size() << ' ' << seq[1].size() << endl;
			for (int i = 0; i < 2; i++)
				for (int j = 0; j < seq[i].size(); j++)
					cout << seq[i][j] << endl;
		}
	}
	
	return 0;
}
