import java.io.*;
import java.util.*;

public class pipeDK {

public static void main(String[] args) throws IOException {
  BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
  StringTokenizer st = new StringTokenizer(br.readLine());
  int T = Integer.parseInt(st.nextToken());
  for (int t = 0; t < T; t++) {
    st = new StringTokenizer(br.readLine());
    int L = Integer.parseInt(st.nextToken());
    int V1 = Integer.parseInt(st.nextToken());
    int V2 = Integer.parseInt(st.nextToken());
    int Tol = Integer.parseInt(st.nextToken());
    int S = Integer.parseInt(st.nextToken());

    int leaves = (V2-V1+Tol-1)/Tol, minv = V2-leaves*Tol, nodes = 1, depth = 0;
    while (nodes > 0 && leaves > nodes) {
      depth++;
      int maxv = L / (S*depth);
      int leaves2 = Math.min(leaves, (maxv-minv+Tol)/Tol);
      nodes -= leaves - leaves2;
      nodes *= 2;
      leaves = leaves2;
    }

    if (nodes <= 0) {
      System.out.println("impossible");
    } else {
      System.out.println(depth);
    }
  }
}

}
