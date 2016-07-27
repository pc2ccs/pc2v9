// Note: this is the solution written for DNA (as the problem was
// called then) for ICPC 2013, slightly modified for the new format. /Per
import java.util.ArrayList;
import java.util.Scanner;

public class evolution_babak_AC3 {

	static Boolean isSubsequence(String a, String b)
	{
		int posb = 0;
		for (int i = 0; i < a.length(); i++)
		{
			Boolean found = false;
			for (int j = posb; j < b.length() && !found; j++)
				if (a.charAt(i) == b.charAt(j))
				{
					found = true;
					posb = j + 1;
				}
			if (!found)
				return false;
		}
		return true;
	}

	public static void main(String[] args) {
		Scanner in = new Scanner(System.in);

		int caseNum = 0;
		while (in.hasNextInt())
		{
			int n = in.nextInt();
			String dna = in.next();
			
			Boolean possible = true;
			ArrayList<ArrayList<String>> bucket = new ArrayList<ArrayList<String>>();
			for (int i = 0; i < 4000; i++)
			{
				ArrayList<String> altmp = new ArrayList<String>();
				bucket.add(altmp);
			}
			for (int i = 0; i < n; i++)
			{
				String strtmp = in.next();
				if (bucket.get(4000 - strtmp.length()).size() < 2)
					bucket.get(4000 - strtmp.length()).add(strtmp);
				else
					possible = false;
			}
			
			ArrayList<String> test = new ArrayList<String>();
			for (int i = 0; i < bucket.size(); i++)
				for (int j = 0; j < bucket.get(i).size(); j++)
					test.add(bucket.get(i).get(j));
			
			ArrayList<String> currentDna = new ArrayList<String>(2);
			currentDna.add(dna);
			currentDna.add(dna);

			ArrayList<ArrayList<String>> seq = new ArrayList<ArrayList<String>>();			
			ArrayList<String> altmp1 = new ArrayList<String>();
			seq.add(altmp1);
			ArrayList<String> altmp2 = new ArrayList<String>();
			seq.add(altmp2);
			
			int blockStart = 0;
			while (possible && blockStart < test.size())
			{
				//System.err.println("Block Start = " + blockStart);

				int pos = blockStart;
				while (pos < test.size() - 1 && isSubsequence(test.get(pos + 1), test.get(pos)))
					pos++;
				int blockEnd = pos + 1;			
				//System.err.println("Block End = " + blockEnd);
				
				pos = blockStart;
				while (possible && pos < blockEnd)
				{
					ArrayList<Boolean> isSub = new ArrayList<Boolean>();
					for (int i = 0; i < 2; i++)
						isSub.add(isSubsequence(test.get(pos), currentDna.get(i)));
					if (!isSub.get(0) && !isSub.get(1))
						possible = false;
					else if (isSub.get(0) && isSub.get(1))
						break;
					else
						for (int i = 0; i < 2; i++)
							if (isSub.get(i))
							{
								currentDna.set(i, test.get(pos));
								seq.get(i).add(test.get(pos));
								pos++;
							}
				}
				
				if (possible)
				{
					possible = false;
					for (int i = 0; i < 2 && !possible; i++)
						if (blockEnd == test.size() || isSubsequence(test.get(blockEnd), 
							currentDna.get(i)))
						{
							for (int j = pos; j < blockEnd; j++)
								seq.get((i + 1) % 2).add(test.get(j));
							currentDna.set((i + 1) % 2, test.get(blockEnd - 1));
							if (blockEnd < test.size())
							{
								seq.get(i).add(test.get(blockEnd));
								currentDna.set(i, test.get(blockEnd));
							}
							
							possible = true;
							blockStart = blockEnd + 1;
						}
				}
			}
						
			caseNum++;
//			System.out.print("Case " + caseNum + ": ");
			if (!possible)
			{
				//System.err.println("impossible");
				System.out.println("Impossible");
			}
			else
			{
				//System.err.println("possible");
				System.out.println(seq.get(0).size() + " " + seq.get(1).size());
				for (int i = 0; i < 2; i++)
					for (int j = seq.get(i).size()-1; j >= 0 ; --j)
						System.out.println(seq.get(i).get(j));
			}		
		}
		
		in.close();
	}
}
