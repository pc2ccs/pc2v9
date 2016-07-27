import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.HashSet;
import java.util.Set;
import java.util.regex.Pattern;

/**
 * Input file format validator: EVOLUTION
 * ACM ICPC World Finals 2015
 *
 * @author Martin Kacer
 */
public class EvolutionChecker {
	BufferedReader read = new BufferedReader(new InputStreamReader(System.in));
	
	static final int MAX_PER_FILE = 1;
	static final int MAX_SAMPLES = 4000;
	static final int MAX_LENGTH = 4000;
	
	static final Pattern PAT_NUM = Pattern.compile("[1-9][0-9]*");
	static final Pattern PAT_SAMPLE = Pattern.compile("[ACM]*");

	public static void main(String[] args) throws Exception {
		EvolutionChecker inst = new EvolutionChecker();
		if (!inst.checkOne())
			throw new IllegalArgumentException("empty file");
		int cnt = 1;
		while (inst.checkOne()) {
			if (++cnt > MAX_PER_FILE)
				throw new IllegalArgumentException("too much input per one file");
		}
		if (inst.read.readLine() != null)
			throw new IllegalArgumentException("excess input");
		System.exit(42);
	}

	boolean checkOne() throws Exception {
		String s = read.readLine();
		if (s == null) return false;
		if (!PAT_NUM.matcher(s).matches())
			throw new IllegalArgumentException("invalid first line format: " + s);
		int n = Integer.parseInt(s);
		if (n < 1 || n > MAX_SAMPLES)
			throw new IllegalArgumentException("invalid number of samples: " + s);
		Set<String> samples = new HashSet<String>(n*2);
		for (int i = -1; i < n; ++i) {
			s = read.readLine();
			if (!PAT_SAMPLE.matcher(s).matches())
				throw new IllegalArgumentException("invalid genetic sample format: " + s);
			if (s.length() < 1 || s.length() > MAX_LENGTH)
				throw new IllegalArgumentException("invalid genetic sample length: " + s);
			if (!samples.add(s))
				throw new IllegalArgumentException("duplicate genetic sample: " + s);
		}
		return true;
	}
}
