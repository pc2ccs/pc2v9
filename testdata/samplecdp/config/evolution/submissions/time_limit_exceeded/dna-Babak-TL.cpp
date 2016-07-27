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
		
		int cc = 0;
		for (int i = 0; i < test.size(); i++)
			for (int j = i + 1; j < test.size(); j++)
				if (isSubsequence(test[j], test[i]))
					cc++;
		cerr << cc << endl;
		
		//cerr << n << endl;
		//for (int i = 0; i < test.size(); i++)
			//cerr << test[i] << endl;

		vector<string> currentDna(2, dna);
		vector<string> seq[2];
		int blockStart = 0;
		while (possible && blockStart < test.size())
		{
			//dcerr << "Blockstart = " << blockStart << endl;

			int pos = blockStart;
			while (pos < test.size() - 1 && isSubsequence(test[pos + 1], test[pos]))
				pos++;
			int blockEnd = pos + 1;			
			//cerr << "Blcokend = " << blockEnd << endl;
			
			pos = blockStart;
			while (possible && pos < blockEnd)
			{
				vector<bool> isSub(2);
				for (int i = 0; i < 2; i++)
					isSub[i] = isSubsequence(test[pos], currentDna[i]);
				if (!isSub[0] && !isSub[1])
					possible = false;
				else if (isSub[0] && isSub[1])
					break;
				else
					for (int i = 0; i < 2; i++)
						if (isSub[i])
						{
							currentDna[i] = test[pos];
							seq[i].push_back(test[pos]);
							pos++;
						}
			}
			
			if (possible)
			{
				possible = false;
				for (int i = 0; i < 2 && !possible; i++)
					if (blockEnd == test.size() || isSubsequence(test[blockEnd], currentDna[i]))
					{
						for (int j = pos; j < blockEnd; j++)
							seq[(i + 1) % 2].push_back(test[j]);
						currentDna[(i + 1) % 2] = test[blockEnd - 1];
						if (blockEnd < test.size())
						{
							seq[i].push_back(test[blockEnd]);
							currentDna[i] = test[blockEnd];
						}
						
						possible = true;
						blockStart = blockEnd + 1;
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
			cout << seq[0].size() << ' ' << seq[1].size() << endl;
			for (int i = 0; i < 2; i++)
				for (int j = seq[i].size()-1; j >= 0; --j)
					cout << seq[i][j] << endl;
		}
	}
	
	return 0;
}
