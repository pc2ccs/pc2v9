//-------------------------------------------
// Wileman's Java solution to Winning Windows
// 2015 ACM ICPC Finals
//-------------------------------------------
import java.util.*;
import java.io.*;

public class win
{
    final static int OPEN_CMD = 1;
    final static int CLOSE_CMD = 2;
    final static int RESIZE_CMD = 3;
    final static int MOVE_CMD = 4;

    static Scanner sc;
    static int xmax, ymax;		// display dimensions
    static int cmdno;			// command number: 1, 2, ...
    static int cmd;			// current command
    static int[] args = new int[4];	// command arguments
    static int nwin;			// number of live windows
    static class w {			// a window
	int cmdorg;			// OPEN command index in input
	int[] pos;			// upper left corner's coordinates
	int[] dim;			// width, height

	w() {				// constructor
	    pos = new int[2];
	    dim = new int[2];
	}
    }
    static LinkedList <w>wlist		// all open windows, in cmd order
	= new LinkedList<w>();

    static int get_command()
    {
	String command = sc.next("[A-Z][A-Z]*");
	if (command.equals("OPEN")) {
	    args[0] = sc.nextInt();
	    args[1] = sc.nextInt();
	    args[2] = sc.nextInt();
	    args[3] = sc.nextInt();
	    sc.nextLine();
	    return OPEN_CMD;
	}
	if (command.equals("CLOSE")) {
	    args[0] = sc.nextInt();
	    args[1] = sc.nextInt();
	    sc.nextLine();
	    return CLOSE_CMD;
	}
	if (command.equals("RESIZE")) {
	    args[0] = sc.nextInt();
	    args[1] = sc.nextInt();
	    args[2] = sc.nextInt();
	    args[3] = sc.nextInt();
	    sc.nextLine();
	    return RESIZE_CMD;
	}
	if (command.equals("MOVE")) {
	    args[0] = sc.nextInt();
	    args[1] = sc.nextInt();
	    args[2] = sc.nextInt();
	    args[3] = sc.nextInt();
	    sc.nextLine();
	    return MOVE_CMD;
	}
	System.err.println("Unknown command.");
	System.exit(1);
	return 0;		// not reached
    }

    // Return the string name of a command given its number
    public static String cmd_name(int cmd)
    {
	if (cmd == 1)
	    return "OPEN";
	if (cmd == 2)
	    return "CLOSE";
	if (cmd == 3)
	    return "RESIZE";
	if (cmd == 4)
	    return "MOVE";
	return "UNKNOWN";
    }

    // Find the window, if any, that includes the pixel at
    // (x,y). Return null if no such window is found.
    public static w findwin(int x, int y)
    {
	int i;
	w p;

	for(i=0; i<wlist.size(); i++) {
	    p = wlist.get(i);
	    if (x < p.pos[0] || x >= p.pos[0] + p.dim[0])
		continue;
	    if (y < p.pos[1] || y >= p.pos[1] + p.dim[1])
		continue;
	    return p;
	}
	return null;
    }

    // Display no such window error message
    public static void err_no_win(int cmd)
    {
	System.out.println("Command " + cmdno + ": " +
	    cmd_name(cmd) + " - no window at given position");
    }

    // Display window does not fit error message
    public static void err_no_fit(int cmd)
    {
	System.out.println("Command " + cmdno + ": " +
	    cmd_name(cmd) + " - window does not fit");
    }

    // Display alternate window move distance message
    public static void err_move_less(int ax, int dx)
    {
	System.out.println("Command " + cmdno + ": MOVE" +
	    " - moved " + Math.abs(ax) + " instead of " + Math.abs(dx));
    }


    // Predicate: does the range amin..amax intersect bmin..bmax?
    public static boolean overlap(int amin, int amax, int bmin, int bmax)
    {
	return ! ((amin > bmax) || (bmin > amax));
    }

    // Predicate does a window overlap any existing window EXCEPT
    // the window identified by skip (if not null)?
    public static boolean winoverlap(int x, int y, int w, int h, w skip)
    {
	w p;
	int i;

	for(i=0;i<wlist.size();i++) {
	    p = wlist.get(i);
	    if (p == skip)
		continue;
	    if (overlap(x, x+w-1, p.pos[0], p.pos[0]+p.dim[0]-1) &&
		overlap(y, y+h-1, p.pos[1], p.pos[1]+p.dim[1]-1))
		return true;
	}
	return false;
    }

    // Predicate: do windows a and b overlap?
    public static boolean winolap(w a, w b)
    {
	return overlap(a.pos[0], a.pos[0]+a.dim[0]-1,
	           b.pos[0], b.pos[0]+b.dim[0]-1) &&
	       overlap(a.pos[1], a.pos[1]+a.dim[1]-1,
	           b.pos[1], b.pos[1]+b.dim[1]-1);
    }

    // Predicate: will a window fit on the display?
    public static boolean winfit(int x, int y, int w, int h)
    {
	return (x >= 0 && y >= 0 && x+w-1 < xmax && y+h-1 < ymax);
    }

    // Setup for a move. Create the array of all possibly affected windows.
    // md[] is the desired move distance. Return the vector of windows that
    // are possibly directly moved by the movement of p.
    public static Vector<w> mset(w p, int[] md)
    {
	w M = new w();	// the move window
	Vector <w>b;	// vector of directly affected windows
	int hv;		// horizontal/x (0) or vertical/y (1) move?
	int lr;		// left/up (0) or right/down (1) move?
	int sdist;	// signed distance to move
	int adist;	// absolute distance to move
	int na;		// # of entries in the a array
	int i;

	if (md[0] == 0) hv = 1; else hv = 0;	// 1 if moving vertically
	if (md[hv] > 0) lr = 1; else lr = 0;	// 1 if moving right/down
	sdist = md[0] + md[1];
	adist = Math.abs(sdist);

	// Setup the move rectangle
	M.dim[hv] = adist;
	M.dim[1-hv] = p.dim[1-hv];
	if (md[0] < 0) {
	    M.pos[0] = p.pos[0] - adist;
	    M.pos[1] = p.pos[1];
	} else if (md[0] > 0) {
	    M.pos[0] = p.pos[0] + p.dim[0];
	    M.pos[1] = p.pos[1];
	} else if (md[1] < 0) {
	    M.pos[0] = p.pos[0];
	    M.pos[1] = p.pos[1] - adist;
	} else {
	    M.pos[0] = p.pos[0];
	    M.pos[1] = p.pos[1] + p.dim[1];
	}

	// Find all windows that intersect the move rectangle, saving
	// them in the 'a' array.
	w[] a = new w[wlist.size()];
	na = 0;
	for(i=0;i<wlist.size();i++) {
	    w r = wlist.get(i);
            if (overlap(M.pos[0], M.pos[0]+M.dim[0]-1,
                    r.pos[0], r.pos[0]+r.dim[0]-1) &&
		overlap(M.pos[1], M.pos[1]+M.dim[1]-1,
                    r.pos[1], r.pos[1]+r.dim[1]-1)) {
		a[na] = r;
		na++;
	    }
	}

	// Find all windows that might be directly moved by p, saving
	// them in the 'b' vector.
	b = new Vector<w>(na);	// allocate the vector
	for(i=0;i<na;i++) {
	    w S = new w();	// the "sweep" rectangle
	    w q = a[i];		// window identified by a[i]
	    int bp;
	    int j;
	    boolean isect;

	    if (hv == 0) {	// horizontal win between p and a[i]
		if (lr == 0) {                  // left of p
		    S.pos[0] = q.pos[0] + q.dim[0];
		    S.dim[0] = p.pos[0] - S.pos[0];
		} else {                        /* right of p */
		    S.pos[0] = p.pos[0] + p.dim[0];
		    S.dim[0] = q.pos[0] - S.pos[0];
		}
		S.pos[1] = Math.max(p.pos[1],q.pos[1]);
		bp = Math.min(p.pos[1]+p.dim[1]-1,q.pos[1]+q.dim[1]-1);
		S.dim[1] = bp - S.pos[1] + 1;
	    } else {		// vertical win between p and a[i]
		if (lr == 0) {                  // above p
		    S.pos[1] = q.pos[1] + q.dim[1];
		    S.dim[1] = p.pos[1] - S.pos[1];
		} else {                        /* below p */
		    S.pos[1] = p.pos[1] + p.dim[1];
		    S.dim[1] = q.pos[1] - S.pos[1];
		}
		S.pos[0] = Math.max(p.pos[0],q.pos[0]);
		bp = Math.min(p.pos[0]+p.dim[0]-1,q.pos[0]+q.dim[0]-1);
		S.dim[0] = bp - S.pos[0] + 1;
	    }


	    isect = false;
	    for(j=0; j<na; j++) {
		if (i == j)
		    continue;
		if (winolap(S,a[j])) {
		    isect = true;
		    break;
		}
	    }
	    if (!isect)
		b.add(a[i]);
        }
	return b;
    }

    public static void move2(w p, int md[])
    {
if(false) {
System.out.println("Invoked move2.");
System.out.println("r.x = " + p.pos[0]);
System.out.println("r.y = " + p.pos[1]);
System.out.println("r.w = " + p.dim[0]);
System.out.println("r.h = " + p.dim[1]);
System.out.println("md[0] = " + md[0]);
System.out.println("md[1] = " + md[1]);
System.exit(0);
}
	Vector <w>b = mset(p, md);
	int nb = b.size();
	int[] mvdist = new int[2];
	int gapdist;

	for(int i=0;i<nb;i++) {
	    w r = b.get(i);
	    mvdist[0] = mvdist[1] = 0;
	    if (md[0] < 0) {                /* horizontal left */
		gapdist = p.pos[0] - (r.pos[0] + r.dim[0]);
		if (gapdist >= Math.abs(md[0]))
		    continue;
		else
		    mvdist[0] = md[0] + gapdist;
	    } else if (md[0] > 0) { /* horizontal right */
		gapdist = r.pos[0] - (p.pos[0] + p.dim[0]);
		if (gapdist >= md[0])
		    continue;
		else
		    mvdist[0] = md[0] - gapdist;
	    } else if (md[1] < 0) { /* vertical up */
		gapdist = p.pos[1] - (r.pos[1] + r.dim[1]);
		if (gapdist >= Math.abs(md[1]))
		    continue;
		else
		    mvdist[1] = md[1] + gapdist;
	    } else {                        /* vertical down */
		gapdist = r.pos[1] - (p.pos[1] + p.dim[1]);
		if (gapdist >= md[1])
		    continue;
		else
		    mvdist[1] = md[1] - gapdist;
	    }

	    if (mvdist[0] + mvdist[1] == 0)
		continue;

	    move2(r,mvdist);
	}
	p.pos[0] += md[0];
	p.pos[1] += md[1];
    }

    // Return the ABSOLUTE VALUE of the distance (in the appropriate
    // direction that the window 'p' can move. md[] contains the 
    // signed values of the desired move distances.
    public static int move1(w p, int md[], boolean domove)
    {
	Vector <w>b = mset(p, md);
	int i, mindist, nb;

	// If there were directly encountered windows in b,
	// the distance p can be moved is determined by computing, for
	// each entry r in b, the sum of the distance between p and r,
	// and the maximum distance r can move. The maximum distance p
	// can move is the minimum of these distances.
	nb = b.size();
	if (nb > 0) {
	    int maxbdist;   // minimum of max dist entries in b can move

	    mindist = 0;
	    for(i=0;i<nb;i++) {
		int gapdist;        // absolute gap distance between p and r
		int[] mvdist = new int[2];  // signed distance r needs to move

		w r = b.get(i);
		if (md[0] < 0) {            // horizontal left
		    gapdist = p.pos[0] - (r.pos[0] + r.dim[0]);
		    mvdist[0] = md[0] + gapdist;
		    mvdist[1] = 0;
		} else if (md[0] > 0) {     // horizontal right
		    gapdist = r.pos[0] - (p.pos[0] + p.dim[0]);
		    mvdist[0] = md[0] - gapdist;
		    mvdist[1] = 0;
		} else if (md[1] < 0) {     // vertical up
		    gapdist = p.pos[1] - (r.pos[1] + r.dim[1]);
		    mvdist[0] = 0;
		    mvdist[1] = md[1] + gapdist;
		} else {                    // vertical down
		    gapdist = r.pos[1] - (p.pos[1] + p.dim[1]);
		    mvdist[0] = 0;
		    mvdist[1] = md[1] - gapdist;
		}
		maxbdist = move1(r, mvdist, false);
		maxbdist += gapdist;
		if (i == 0 || maxbdist < mindist)
		    mindist = maxbdist;
	    }
	} else {	// nothing to push; distance to display edge is limit
	    if (md[0] < 0) {
		mindist = Math.min(-md[0], p.pos[0]);
	    } else if (md[0] > 0) {
		mindist = Math.min(md[0], xmax - (p.pos[0] + p.dim[0]));
	    } else if (md[1] < 0) {
		mindist = Math.min(-md[1], p.pos[1]);
	    } else {
		mindist = Math.min(md[1], ymax - (p.pos[1] + p.dim[1]));
	    }
	}

	// Now we push directly encountered windows by the appropriate
	// distance, if necessary. Obviously if the gap between p and
	// a DE window is of distance mindist or more, the DE window
	// does not move.
	if (domove) for(i=0;i<nb;i++) {
	    int gapdist;          	  // abs gap distance between p and r
	    int[] mvdist = new int[2];    // distance r needs to move

	    w r = b.get(i);
	    mvdist[0] = mvdist[1] = 0;
	    if (md[0] < 0) {		// horizontal left
		gapdist = p.pos[0] - (r.pos[0] + r.dim[0]);
		if (gapdist < mindist)
		    mvdist[0] = -(mindist - gapdist);
	    } else if (md[0] > 0) {	 // horizontal right
		gapdist = r.pos[0] - (p.pos[0] + p.dim[0]);
		if (gapdist < mindist)
		    mvdist[0] = mindist - gapdist;
	    } else if (md[1] < 0) {	// vertical up
		gapdist = p.pos[1] - (r.pos[1] + r.dim[1]);
		if (gapdist < mindist)
		    mvdist[1] = -(mindist - gapdist);
	    } else {			// vertical down
		gapdist = r.pos[1] - (p.pos[1] + p.dim[1]);
		if (gapdist < mindist)
		    mvdist[1] = mindist - gapdist;
	    }

	    if (mvdist[0] + mvdist[1] == 0)
		continue;

	    move2(r,mvdist);
	}

	return mindist;
    }


    // Move a window.
    public static void move(w p, int x, int y)
    {
	int ax;				// actual distance window can move
	int[] md = new int[2];		// move distance
	int rd;				// requested distance

	if (x == 0 && y == 0)		// is any movement required?
	    return;
	md[0] = x;
	md[1] = y;

	ax = move1(p, md, true);
	rd = md[0] + md[1];
	if (rd < 0)
	    ax = -ax;

	if (Math.abs(ax) < Math.abs(md[0]) + Math.abs(md[1]))
	    err_move_less(ax,md[0]+md[1]);

	if (x == 0)
	    p.pos[1] += ax;
	else
	    p.pos[0] += ax;
    }

    public static void main(String argv[])
    {
	if (argv.length > 1) {
	    System.err.println("Usage: java win [inputfilename]");
	    System.exit(1);
	}
	if (argv.length == 1)
	    try {
		sc = new Scanner(new File(argv[0]));
	    } catch (Exception e) {
		System.err.println("Caught exception " + 3);
		System.exit(1);
	    }
	else
	    sc = new Scanner(System.in);

	xmax = sc.nextInt();
	ymax = sc.nextInt();
	sc.nextLine();

	for(cmdno=1;;cmdno++) {
	    if (!sc.hasNextLine())	// exit loop if no more commands
		break;
	    cmd = get_command();

	    if (cmd == OPEN_CMD) {
		if (!winfit(args[0],args[1],args[2],args[3])) {
		    err_no_fit(OPEN_CMD);
		    continue;
		}
		if (winoverlap(args[0],args[1],args[2],args[3],null)) {
		    err_no_fit(OPEN_CMD);
		    continue;
		}

		w newwin = new w();
		newwin.cmdorg = cmdno;
		newwin.pos[0] = args[0];
		newwin.pos[1] = args[1];
		newwin.dim[0] = args[2];
		newwin.dim[1] = args[3];
		wlist.add(newwin);

	    } else if (cmd == CLOSE_CMD) {
		w p = findwin(args[0],args[1]);
		if (p == null)
		    err_no_win(CLOSE_CMD);
		else
		    wlist.remove(p);

	    } else if (cmd == RESIZE_CMD) {
		w p = findwin(args[0],args[1]);
		if (p == null) {
		    err_no_win(RESIZE_CMD);
		    continue;
		}
		if (!winfit(p.pos[0],p.pos[1],args[2],args[3])) {
		    err_no_fit(RESIZE_CMD);
		    continue;
		}
		if (winoverlap(p.pos[0],p.pos[1],args[2],args[3],p)) {
		    err_no_fit(RESIZE_CMD);
		    continue;
		}
		p.dim[0] = args[2];
		p.dim[1] = args[3];

	    } else if (cmd == MOVE_CMD) {
		w p = findwin(args[0],args[1]);
		if (p == null) {
		    err_no_win(MOVE_CMD);
		    continue;
		}
		move(p,args[2],args[3]);

	    }
	}

	// Display remaining open window info
	System.out.println(wlist.size() + " window(s):");
	for (int i = 0; i<wlist.size(); i++) {
	    w p = wlist.get(i);
	    System.out.println(p.pos[0] + " " + p.pos[1] + " " +
		p.dim[0] + " " + p.dim[1]);
	}
    }
}
