// Note: this is the solution written for DNA (as the problem was
// called then) for ICPC 2013, slightly modified for the new format. /Per
#include <algorithm>
#include <cstdio>
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
	char str[MAX_LENGTH + 1];
	
	int n;
	int caseNum = 0;
	while (scanf("%d", &n) !=  EOF)
	{
		scanf("%s", str);
		string dna(str);
		
		bool possible = true;
		vector<vector<string> > bucket(MAX_LENGTH, vector<string>());
		for (int i = 0; i < n; i++)
		{
			scanf("%s", str);
			string strtmp(str);

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
//		printf("Case %d: ", caseNum);
		if (!possible)
		{
			//cerr << "impossible" << endl;
			printf("Impossible\n");
		}
		else
		{
			reverse(seq[0].begin(), seq[0].end());
			reverse(seq[1].begin(), seq[1].end());
			printf("%d %d\n", (int)seq[0].size(), (int)seq[1].size());
			for (int i = 0; i < 2; i++)
				for (int j = 0; j < seq[i].size(); j++)
					printf("%s\n", seq[i][j].c_str());
		}
	}
	
	return 0;
}
