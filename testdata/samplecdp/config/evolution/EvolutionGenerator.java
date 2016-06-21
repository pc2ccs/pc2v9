import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;

/**
 * Input file generator: EVOLUTION
 * ACM ICPC World Finals 2015
 *
 * @author Martin Kacer
 */
public class EvolutionGenerator
{
	int splitFiles;
	/** Use this writer to produce input file(s). */
	PrintWriter inp = new PrintWriter(System.out);
	int caseCounter = 0, fileCounter = 0;
	final DecimalFormat NF_00 = new DecimalFormat("00");
	
	/** Call this BEFORE any testcase. */
	void nextTestCase(String name) {
		samples.clear();
		if (splitFiles > 0)
			if (caseCounter++ % splitFiles == 0) {
				if (caseCounter > 1) inp.close();
				try {
					inp = new PrintWriter(new File(getFileName(name)));
				} catch (IOException ex) {
					throw new RuntimeException(ex);
				}
			}
	}
	/** Called once at the end. */
	void finish() {
		inp.close();
	}
	String getFileName(String name) {
		return "evolution-" + NF_00.format(++fileCounter) + "-" + name.replaceAll("[^A-Za-z0-9]", "-").replaceAll("--*", "-") + ".in";
	}
	public static void main(String[] args) {
		EvolutionGenerator inst = new EvolutionGenerator();
		inst.splitFiles = 1;
		if (args.length > 0) inst.splitFiles = Integer.parseInt(args[0]);
		inst.generateAll();
		inst.finish();
	}
	Random randGen = new Random(-8032746865856502611L);

	static final int MAX_PER_FILE = 1;
	static final int MAX_SAMPLES = 4000;
	static final int MAX_LENGTH = 4000;
	static final char[] POS_CHARS = "ACM".toCharArray();
	static {
		if (!assertPreconditions()) throw new IllegalStateException("this won't work");
	}
	static boolean assertPreconditions() {
		return MAX_LENGTH >= MAX_SAMPLES;
	}
	
	Set<String> samples = new HashSet<String>(MAX_SAMPLES * 2);

	static class Insertion {
		final int idx;
		final char ch;
		public Insertion(int idx, char ch) { this.idx = idx; this.ch = ch; }
		@Override public String toString() { return "ins-"+ch+"-"+idx; }
	}
	
	String commonContainingSequence(String s1, String s2) {
		int[][] best = new int[s1.length()+1][s2.length()+1];
		for (int i1 = 1; i1 <= s1.length(); ++i1)
			for (int i2 = 1; i2 <= s2.length(); ++i2) {
				if (s1.charAt(i1-1) == s2.charAt(i2-1))
					best[i1][i2] = best[i1-1][i2-1] + 1;
				else
					best[i1][i2] = Math.min(best[i1][i2-1], best[i1-1][i2]) + 1;
			}
		StringBuilder sb = new StringBuilder();
		int i1 = s1.length(), i2 = s2.length();
		while (i1 > 0 && i2 > 0) {
			if (s1.charAt(i1-1) == s2.charAt(i2-1)) {
				sb.append(s1.charAt(i1-1)); --i1; --i2;
				continue;
			}
			boolean b2/*;
			if (best[i1][i2-1] < best[i1-1][i2])
				b2 = true;
			else if (best[i1][i2-1] > best[i1-1][i2])
				b2 = false;
			else
				b2*/ = randGen.nextBoolean(); // randomize if there is a choice
			if (b2)
				sb.append(s2.charAt(--i2));
			else
				sb.append(s1.charAt(--i1));
		}
		while (i1 > 0) sb.append(s1.charAt(--i1));
		while (i2 > 0) sb.append(s2.charAt(--i2));
		return sb.reverse().toString();
	}
	
	String insertChars(String s, int cnt) {
		// inefficient (but who cares?)
		while (cnt-- > 0) {
			int idx = randGen.nextInt(s.length()+1);
			s = s.substring(0, idx) + randomChar() + s.substring(idx);
		}
		return s;
	}
	String insertChar(String s, Insertion ins) {
		return s.substring(0, ins.idx) + ins.ch + s.substring(ins.idx);
	}
	
	String randomString(int len) {
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < len; ++i)
			sb.append(randomChar());
		return sb.toString();
	}
	char randomChar() {
		return POS_CHARS[randGen.nextInt(POS_CHARS.length)];
	}
	
	void printFromSamples(String last) {
		inp.println(samples.size());
		inp.println(last);
		for (String x : samples) // the hash set is sort-of randomized by itself
			inp.println(x);
	}
	
	// random test cases; almost certainly unsolvable
	void genAllRandom(int cnt, int len1, int lenInc, int lenLast) {
		nextTestCase("random0-" + cnt + "-" + len1 + "-" + lenInc);
		inp.println(cnt);
		String s = randomString(lenLast);
		samples.add(s);
		inp.println(s);
		for (int i = 0; i < cnt; ++i) {
			do {
				s = randomString(len1 + i * lenInc);
			} while (!samples.add(s));
			inp.println(s);
		}
	}
	
	/** Generate a chain of samples derived one from another. */
	String genChain(int cnt, int len, int inc, int prob) {
		String s = randomString(len);
		samples.add(s);
		for (int i = 1; i < cnt; ++i) {
			s = insertChars(s, randGen.nextInt(inc) + 1);
			if (prob > 0 && randGen.nextInt(prob) == 0) {
				int idx = randGen.nextInt(s.length());
				char c = randomChar();
				//System.out.println(" - #" + caseCounter + ": replacing " + s.charAt(idx) + " with " + c);
				s = s.substring(0, idx) + c + s.substring(idx+1);
			}
			samples.add(s);
		}
		return s;
	}
	
	/**
	 * Random test case: generate two lines and then perturb the data a little.
	 * @param cnt1 number of samples in the first line.
	 * @param cnt2 number of samples in the second line (<0 to use cnt1).
	 * @param len the length of the first two samples.
	 * @param inc maximal length increment in one step.
	 * @param lenLast desired length of the current (longest) DNA sample.
	 * @param prob probability in each step of one letter being replaced (1/prob).
	 */
	void genRandom(int cnt1, int cnt2, int len, int inc, int lenLast, int prob) {
		nextTestCase("random-" + cnt1 + "-" + inc + "-" + lenLast);
		if (cnt2 < 0) cnt2 = cnt1;
		String s1 = genChain(cnt1, len, inc,prob), s2 = genChain(cnt2, len, inc, prob);
		String s = commonContainingSequence(s1, s2);
		if (s.length() < lenLast)
			s = insertChars(s, lenLast - s.length());
		printFromSamples(s);
	}
	
	/**
	 * Shuffle the order of insertions but make them generate the same result.
	 * Inefficient but should be enough for us.
	 */
	void shuffleInsertions(List<Insertion> modif) {
		for (int i = 0; i < modif.size(); ++i) {
			int idx = modif.get(i).idx;
			for (int j = i+1; j < modif.size(); ++j)
				if (modif.get(j).idx <= idx) ++idx;
			modif.set(i, new Insertion(idx, modif.get(i).ch));
		}
		if (modif.size() > 2)
			Collections.shuffle(modif, randGen);
		else
			Collections.reverse(modif);
		for (int i = 0; i < modif.size(); ++i) {
			int idx = modif.get(i).idx;
			for (int j = i+1; j < modif.size(); ++j)
				if (modif.get(j).idx <= idx) --idx;
			modif.set(i, new Insertion(idx, modif.get(i).ch));
		}
	}
	
	/**
	 * Generate two chains that share a common sample from time to time.
	 * The approximate total number of samples is steps*(steplen*2-1).
	 * Inspired by Onufry's test case.
	 * @param len length of the shortest sample
	 * @param steps number of steps, each of them consisting of two branches
	 *   resulting in the same (common) sequence
	 * @param steplen number of insertions in one step
	 * @param closeEnds true if both chains should start and end with a common DNA sample.
	 *   False to start and end with their own sample, different from the other line.
	 */
	void genInterleaved(int len, int steps, int steplen, boolean closeEnds) {
		if (steplen < 2) throw new IllegalArgumentException("steplen must be at least 2, it is " + steplen);
		nextTestCase("interleaved-" + steps + "-" + steplen + (closeEnds ? "-close" : "-open"));
		String s = randomString(len);
		if (closeEnds) samples.add(s);
		while (steps-- > 0) {
			String nxt = s;
			List<Insertion> modif = new ArrayList<Insertion>();
			for (int i = 0; i < steplen; ++i) {
				Insertion ins = new Insertion(randGen.nextInt(nxt.length()+1), randomChar());
				nxt = insertChar(nxt, ins);
				samples.add(nxt);
				modif.add(ins);
			}
			List<Insertion> copy = new ArrayList<Insertion>(modif);
			shuffleInsertions(copy);
			for (Insertion ins : copy) samples.add(s = insertChar(s, ins));
		}
		if (closeEnds) s = insertChars(s, steplen);
		else samples.remove(s);
		printFromSamples(s);
	}
	
	String genLongCommmon(String s, int cnt) {
		samples.add(s);
		for (int i = 0; i < cnt; ++i)
			samples.add(s = s + randomChar());
		return s;
	}

	/**
	 * Necessary to remember a long common sequence with two candidates.
	 * For algorithms going from short samples to long ones.
	 * @param cnt number of samples in the common chain
	 * @param second decides which candidate should continue after the common chain.
	 */
	void genLongCommonUp(int cnt, boolean second) {
		nextTestCase("common-" + cnt + "-up" + (second?"2":"1"));
		String s;
		samples.add("A");
		samples.add("C");
		s = "ACM";
		s = genLongCommmon(s, cnt);
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < cnt+5; ++i) sb.append(second ? 'C' : 'A');
		samples.add(sb.toString());
		s = insertChars(s, 10);
		samples.add(s);
		s = insertChars(s, 1);
		printFromSamples(commonContainingSequence(s, sb.toString()));
	}
	/**
	 * Necessary to remember a long common sequence with two candidates.
	 * For algorithms going from long samples to short ones.
	 * @param cnt number of samples in the common chain
	 * @param second decides which candidate should continue after the common chain.
	 */
	void genLongCommonDown(int cnt, boolean second) {
		nextTestCase("common-" + cnt + "-down" + (second?"2":"1"));
		String s;
		samples.add("ACM");
		samples.add(second ? "CCCCCCC" : "AAAAAAA");
		s = "ACMACMACM";
		s = genLongCommmon(s, cnt);
		samples.add("CCCCCCCCCM" + s);
		samples.add("AAAAAAAAAM" + s);
		printFromSamples("CCCCCCCCCMAAAAAAAAAM" + s);
	}

	void generateAll() {
		String s1, s2;
		
		// sample input first
		nextTestCase("sample-input-1");
		inp.println("5");
		inp.println("AACCMMAA");
		inp.println("ACA");
		inp.println("MM");
		inp.println("ACMA");
		inp.println("AA");
		inp.println("A");
		
		nextTestCase("sample-input-2");
		inp.println("3");
		inp.println("ACMA");
		inp.println("ACM");
		inp.println("ACA");
		inp.println("AMA");
		
		nextTestCase("sample-input-3");
		inp.println("1");
		inp.println("AM");
		inp.println("MA");

		// single-letter DNAs
		nextTestCase("one-letter-norm");
		inp.println("2");
		inp.println("MC");
		inp.println("M");
		inp.println("C");

		// single-letter DNAs (reversed order)
		nextTestCase("one-letter-rev");
		inp.println("2");
		inp.println("MC");
		inp.println("C");
		inp.println("M");

		// two-letter DNAs - impossible
		nextTestCase("two-letter");
		inp.println("2");
		inp.println("AMC");
		inp.println("MA");
		inp.println("AM");

		// two-letter DNAs - possible
		nextTestCase("two-letter");
		inp.println("2");
		inp.println("AMA");
		inp.println("MA");
		inp.println("AM");
		
		// one letter in a long sample
		nextTestCase("needle-in-a-haystack");
		inp.println("1");
		inp.println("CCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCACCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCMCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCC");
		inp.println("A");
		
		// two letters in a long sample
		nextTestCase("needles-in-a-haystack");
		inp.println("2");
		inp.println("CCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCACCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCMCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCC");
		inp.println("A");
		inp.println("M");
		
		// one letter that does not occur
		nextTestCase("no-needle-here");
		inp.println("1");
		inp.println("CCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCMCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCC");
		inp.println("A");
		
		// common sequence at both ends
		nextTestCase("common-endings");
		samples.add("A");
		samples.add("AC");
		samples.add("ACM");
		samples.add("ACAM");
		samples.add("AMCAM");
		samples.add("ACMCAM");
		
		samples.add("ACMCAMA");
		samples.add("ACMCAMMA");
		samples.add("ACMCAMMAC");
		
		samples.add("ACAMCAM");
		samples.add("ACAMACAM");
		samples.add("ACAMACAMA");
		
		samples.add("ACMCAMACAMA");
		samples.add("ACMCAMMACAMA");
		samples.add("ACMCAMMACMAMA");
		samples.add("ACMACAMMACMAMA");
		samples.add("ACMAACAMMACMAMA");
		printFromSamples("ACMAACAMMACAMAMA");
		// ---------------------------------------
		
		genAllRandom(10, 6, 2, 100);

		genRandom(10, -1, 3, 3, 100, 0);
		genRandom(30, -1, 3, 5, 1000, 0);
		genRandom(20, -1, 5, 100, 2000, 11);
		genRandom(20, -1, 5, 100, 2000, 12);
		genRandom(20, -1, 5, 100, 2000, 13);

		// the same letter repeated many times (check the efficiency)
		nextTestCase("almost-all-same");
		s1 = "M";
		samples.add(s1);
		for (int i = 2; i < MAX_SAMPLES; i+=2) {
			s1 = randGen.nextBoolean() ? s1 + "A" : "A" + s1;
			samples.add(s1);
		}
		s2 = "A";
		samples.add(s2);
		for (int i = 3; i < MAX_SAMPLES; i+=2) {
			s2 = randGen.nextBoolean() ? s2 + "C" : "C" + s2;
			samples.add(s2);
		}
		printFromSamples(commonContainingSequence(s1, s2));
		
		// random of maximal size
		genRandom(1000, MAX_SAMPLES-1000, 1, 1, MAX_SAMPLES, 1000);
		genRandom(1000, MAX_SAMPLES-1000, 1, 1, MAX_SAMPLES, 1000);
		
		genInterleaved(6, 5, 2, true);
		genInterleaved(6, 5, 2, false);
		genInterleaved(2, 600, 3, true);
		genInterleaved(2, 600, 3, false);
		
		genLongCommonUp(15, false);
		genLongCommonUp(15, true);
		genLongCommonDown(15, false);
		genLongCommonDown(15, true);
		
		genLongCommonUp(2000, false);
		genLongCommonUp(2000, true);
		genLongCommonDown(2000, false);
		genLongCommonDown(2000, true);
		
		finish();
	}
}
