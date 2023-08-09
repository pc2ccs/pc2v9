
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.HashSet;
import java.util.Set;

/**
 * ICPC World Finals 2019 Dress Rehearsal
 * Problem solution: A Mazing!
 * 
 * @author Martin Kacer
 */
public class AMazingMK {
	BufferedReader input = new BufferedReader(new InputStreamReader(System.in));
	public static void main(String[] args) throws Exception {
		new AMazingMK().solve();
		System.out.println("no way out");
	}
	
	Set<String> visited = new HashSet<>();
	{ visited.add("0,0"); }
	int r = 0, c = 0;
	
	void go(String dir, String back, int dr, int dc) throws Exception {
		String pos = (r+dr)+","+(c+dc);
		if (visited.contains(pos)) return;
		System.out.println(dir);
		String ans = input.readLine();
		if ("solved".equals(ans)) System.exit(0);
		if (!"ok".equals(ans)) return;
		r+=dr; c+=dc;
		visited.add(pos);
		solve();
		System.out.println(back);
		input.readLine();
		r-=dr; c-=dc;
	}
	
	void solve() throws Exception {
		go("up", "down", -1, 0);
		go("down", "up", 1, 0);
		go("left", "right", 0, -1);
		go("right", "left", 0, 1);
	}
}
