/*
 * C++ solution for Keyboard
 * a problem for ICPC2015
 * author: Peter Kluit
 * date  : Dovember 2014
 * too slow : time limit exceeded
 */


#include <iostream>
#include <vector>
//#include <array>
#include <queue>
#include <string>
#include <time.h>
using namespace std;

const int INF = 100000000;
const int SIZE = 50;

char layout [SIZE][SIZE];
int rows;
int cols; 
string toType;

int dist [SIZE * SIZE][SIZE * SIZE];

int cons(int x, int y){
   return SIZE * x + y;
}

int row (int z){
	return z/ SIZE;
}

int col(int z){
	return z % SIZE;
}
  int leftNext(int a){
      int r = row(a);
	  int c = col(a);
      char cc = layout[r][c];
      int k = c - 1;
      while (k >= 0 && layout[r][k] == cc)
         k--;
      if (k < 0)
        return -1;
      int answer = cons(r, k);
     // System.out.println("leftnext : " + show(answer));
      return answer;
   }

    int  rightNext(int a){
      int r = row(a);
	  int c = col(a);
      char cc = layout[r][c];
      int k = c + 1;
      while (k < cols  && layout[r][k] == cc)
         k++;
      if (k == cols )
        return -1;
      int  answer = cons(r, k);
      // System.out.println("rightNext :  " + show(answer));
      return answer;
   }

   int upNext(int  a){
      int r = row(a);
	  int c = col(a);
      char cc = layout[r][c];
      int k = r - 1;
      while (k >= 0 && layout[k][c] == cc)
         k--;
      if (k < 0)
        return -1;
      int answer = cons(k, c);
     // System.out.println("upNext " + show(answer));
      return answer;
   }

  int downNext(int a){
	  int r = row(a);
	  int c = col(a);
      char cc = layout[r][c];
      int k = r + 1;
      while (k < rows && layout[k][c] == cc)
         k++;
      if (k == rows)
        return -1;
      int answer = cons(k, c);    
      return answer;
   }

vector<vector<int>> keys (91);

void makeKeys(){
	  
	  //
      int start = 0;
      for (int r = 0; r < rows; r++)
         for (int c = 0; c < cols; c++){
             char cc = layout[r][c];            
             int adres = cons(r, c);
             int d = dist[start][adres];
             if (d < INF)
                keys[cc].push_back(adres);
         }
   }  

   void setDist(int a, int b, int d) {   
      dist[a][b] = d;
   }

  void calculateDistFrom (int row, int col){
     int a = cons (row, col);
     for (int r = 0; r < rows; r++)
         for (int c = 0; c < cols; c++)
            if (r !=  row || c != col)
               dist[a][cons(r,c)] = INF;

      queue<int> todo;
      todo.push(a);
      while (! todo.empty()){
         int present = todo.front(); todo.pop();     
         int presentd = dist[a][ present];      
         int buurd = presentd + 1;
         int buur = leftNext(present);
         if (buur != -1){
            if (buurd < dist[a][ buur]){
               setDist(a, buur, buurd);
               todo.push(buur);             
            }
         }
         buur = rightNext(present);
         if (buur != -1){
            if (buurd < dist[a][ buur]){
               setDist(a, buur, buurd);
               todo.push(buur);             
            }
         }
         buur = upNext(present);
         if (buur != -1){
            if (buurd < dist[a][ buur]){
               setDist(a,buur, buurd);
               todo.push(buur);              
            }
         }
         buur = downNext(present);
         if (buur != -1){
            if (buurd < dist[a][buur]){
               setDist(a, buur, buurd);
               todo.push(buur);            
            }
         }
      }
   }
  
void makeTable(){
   calculateDistFrom(0,0);
   for (int r = 0; r < rows; r++)
	   for(int c = 0 ; c < cols; c++){
		   if (cons(c, r) > 0){
			   if (dist [0][cons(r,c)] < INF)
				   calculateDistFrom(r,c);
		   }
	   }
}

int countStrokes(){
       int strokes = toType.size();

       vector<int> fromDist (1);
       fromDist[0] = 0;
       vector<int> fromKey(5);
       int adres = cons(0, 0);
       fromKey[0] = adres;
       for (int c = 0; c < toType.size(); c++){
          char cc = toType[c];  // 
          vector<int> key = keys[cc];
          vector<int> strokesUpto(key.size());
		 
          for (int k = 0; k < strokesUpto.size(); k++){
             strokesUpto[k] = INF;
             int  target = key[k]; //
             for (int l = 0; l < fromDist.size(); l++){
                int from = fromKey[l];
                int ldist = dist[from] [target];   // 
                int newDist = ldist + fromDist[l];
                if (newDist > INF)
                   newDist = INF;
                if (newDist < strokesUpto[k])
                    strokesUpto[k] = newDist;
             }
          }
          fromDist = strokesUpto;
          fromKey  = key;
       //   for (int k = 0; k < fromDist.length; k++)
       //      System.out.print(fromDist[k] + " " );
       //   System.out.println();
       }
       int best = INF;	 	      
       for (int l = 0; l < fromDist.size(); l++){
           if (fromDist[l] < best)
              best = fromDist[l];             
       }                 
     //  
       return strokes + best;
   }


void runSingle(){        
      cin >> rows >> cols ;                
      for (int k = 0; k < rows; k++){
          string line;
		  cin >> line;
          for (int l = 0; l < cols; l++)
               layout[k][l] = line.at(l);
       }
       cin >> toType;	
	   toType.push_back('*');
	   //cout << toType << "\n"; 
	   makeTable();
	   //cout << "table made\n";
	   makeKeys();		
	   //cout << "keys made\n";
       int strokes = countStrokes();
       cout << strokes << endl;           
    }

int main(int argc, char **argv) {
	 time_t begin = time(NULL);
      runSingle();     
	   time_t end = time(NULL);
	   //	   cout << "duur: " << difftime(end, begin);
   }
