import java.io.*;
import java.util.*;

public class pipe {

  static StreamTokenizer st = new StreamTokenizer(new BufferedReader(new InputStreamReader(System.in)));
  static int readi() throws IOException {
    st.nextToken();
    return new Integer(st.sval).intValue();
  }

  // is there a binary tree of maximal depth maxd whose leaves have depths <= ds, in this order, where ds is an increasing sequence?
  static boolean check(int maxd, Vector<Integer> ds) {
    Vector<Integer> ix = new Vector<Integer>(); // 0-1 sequence: path to current leaf, 0 = left, 1 = right
    for (Integer e : ds) {
      while (ix.size() < Math.min(maxd, e))
        ix.add(0);
      ix.setSize(Math.min(maxd, e)); // go up to level *it
      while (!ix.isEmpty() && ix.lastElement() != 0) // go to next node at that level: increment binary counter
        ix.remove(ix.size()-1);
      if (ix.isEmpty()) // tree is full
        return false;
      ix.setElementAt(1, ix.size()-1);
    }
    return true;
  }

  public static void main(String[] args) throws Exception {
    st.ordinaryChars('-', '9');
    st.wordChars('-', '9');
    int t = readi();
    for (int k=0 ; k<t ; k++) {
      int l = readi(), v1 = readi(), v2 = readi(), tol = readi(), s = readi();
      Vector<Integer> ds = new Vector<Integer>(); // maximal depths of the intervals, which are the leaves of a tree
      for (int v=v2-tol ; v>v1 ; v-=tol) // backwards to make the boundaries as small as possible
        ds.add((l / s) / v);
      // v = velocity that decides between two intervals
      // v * s = location of tapping
      // l / (v * s) = maximal number of tappings until this tapping takes place
      int maxtap = ds.size(); // an upper bound on the depth of the tree
      // binary search for the minimal depth of the tree
      int lo = 0;
      int hi = maxtap + 1; // add 1 to see if it cannot be done at all
      while (lo < hi) {
        int med = (lo + hi) / 2;
        if (check(med, ds))
          hi = med;
        else
          lo = med + 1;
      }
      if (lo > maxtap)
        System.out.println("impossible");
      else
        System.out.println(lo);
    }
  }

}

