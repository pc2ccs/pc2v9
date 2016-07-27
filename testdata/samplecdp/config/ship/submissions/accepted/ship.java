/*
Java solution of the 'Ship Traffic' problem.
ACM ICPC World Finals 2015, Morocco.
December 2014, Istanbul.
By Osman Ay and Walter Guttmann.
*/
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.StringTokenizer;


public class ship {

	double w, u, v, t1, t2;	
	double endPos,lastShipPos = 1e9 + 1;		
	ArrayList <ShipStruct> shipVector;
 
	
	void solve(){
		readData();
		double result = go();
		pw.format("%.3f", result);
		return;
	}
	
	double go()
	{
		double res=0, res2;
		double maxGap = 0;		//Maximum empty distance between two concequative ships.
		double sternPos = 0;	//Reference position to calculate the distance to the front of the current ship.
		Collections.sort(shipVector);	//By their front positions in increasing order.
		for (int i = 0; i < shipVector.size(); i++)
		{
			ShipStruct curShip = shipVector.get(i);					//The current ship.
			maxGap = Math.max(maxGap, curShip.front - sternPos);	//Calculate the new gap and check if it is bigger then maxGap.
			sternPos = Math.max(sternPos, curShip.back);			//The new reference position to calculate the distance.	
		}
		
		//Calculate the time period for the biggest distance.
		res = (maxGap/u) - (w/v);		

		//Consider the gap after the last ship in the vector.
		res2 = Math.max(res, (endPos-sternPos)/u);	//There is a ship after the time t2.
		maxGap = lastShipPos-sternPos;
		res2 = Math.min(res2, (maxGap/u) - (w/v));	//There is no more ship after the last ship in the vector.
		return Math.max(res,res2);
	}
	
	public void readData(){
		int n = sc.nextInt();					//Number of lanes
		w = sc.nextDouble();
		u = sc.nextDouble();
		v = sc.nextDouble();
		t1 = sc.nextDouble();
		t2 = sc.nextDouble();
		//cin >> n >> w >> u >> v >> t1 >> t2;
		t2 = t2 - t1;			//Consider that t1 is 0, set t2 accordingly.
		endPos = t2 * u;
		shipVector = new ArrayList<>();
		for (int i = 0; i < n; i++)	//i is the lane order.
		{
			char direction = sc.nextToken().charAt(0);
			//cin >> direction;	
			int m = sc.nextInt();				//Number of ships at the current lane.
			//cin >> m;
			
			for (int j = 0; j < m; j++)
			{
				ShipStruct newShip = new ShipStruct();
				//cin >> newShip.len >> newShip.front;
				newShip.len = sc.nextInt();
				newShip.front = sc.nextDouble();
				//Calculate the stern position of the ship.
				if (direction == 'W')
					newShip.back = newShip.front + newShip.len;
				else 
					newShip.back = newShip.front - newShip.len;
			
				//Eliminate the ships that alredy passed the crossing line.
				if (direction == 'E' && newShip.back > 0)
					continue;
				if (direction == 'W' && newShip.back < 0)
					continue;
				//Mirroring the eastbound ships to westbound.
				if (direction == 'E')
					newShip.front = Math.abs(newShip.front);		
				//Move the ship to the lane1.
				double distance = (i * w * u) / v;			
				newShip.front -= (distance + u * t1);
				newShip.back = newShip.front  + newShip.len;

				//Eliminate the ships that passed the crossing line before the time t1.
				if (newShip.back <= 0)
					continue;	
				//Eliminate the ships that passed the crossing line after the time t2.
				if (newShip.front >= endPos) 	
				{
					//Keep the position of the first ship passing the crossing line after the time t2.
					lastShipPos = Math.min(lastShipPos,newShip.front);
					continue;
				}
				//Add the new ship to the ship vector.
				shipVector.add(newShip);
			}
		}
	}
	
	public class ShipStruct implements Comparable<ShipStruct>{
		int len;
		double front, back;
		public int compareTo(ShipStruct s){
			return (new Double(front)).compareTo(s.front);
		}
	}
	public static void main(String[] args) {
        new ship().run();
    }
    class FastScanner {
        BufferedReader br;
        StringTokenizer st;
        boolean eof;
        String buf;

        public FastScanner(String fileName) throws FileNotFoundException {
            br = new BufferedReader(new FileReader(fileName));
            nextToken();
        }

        public FastScanner(InputStream stream) {
            br = new BufferedReader(new InputStreamReader(stream));
            nextToken();
        }

        String nextToken() {
            while (st == null || !st.hasMoreTokens()) {
                try {
                    st = new StringTokenizer(br.readLine());
                } catch (Exception e) {
                    eof = true;
                    break;
                }
            }
            String ret = buf;
            buf = eof ? "-1" : st.nextToken();
            return ret;
        }
        
        String nextLine() {
        	String s = null;
        	try {
        		s = br.readLine();
        	} catch(Exception e){
        		eof = true;
        	}
        	return s;
        }

        int nextInt() {
            return Integer.parseInt(nextToken());
        }

        long nextLong() {
            return Long.parseLong(nextToken());
        }

        double nextDouble() {
            return Double.parseDouble(nextToken());
        }

        void close() {
            try {
                br.close();
            } catch (Exception e) {

            }
        }

        boolean isEOF() {
            return eof;
        }
    }
	
	FastScanner sc;
    PrintWriter pw;
    public void run(){
    	try {
    		//sc = new FastScanner("ship.in");
    		sc = new FastScanner(System.in);
    		//pw = new PrintWriter(new FileWriter("ship.out"));
    		pw = new PrintWriter(System.out);
    		solve();
    		sc.close();
    		pw.close();
    	} catch (Exception e){
    		e.printStackTrace();
    		System.exit(1);
    	}
    }
}
