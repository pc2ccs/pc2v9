/*
 * C++ solution for Keyboard
 * a problem for ICPC2015
 * author: Peter Kluit
 * date  : Januari 2015
 */

#include <iostream>
#include <vector>
#include <sstream>
#include <queue>
#include <string>
#include <time.h>
using namespace std;

const int INF = 100000000;
const int SIZE = 50;
class Node;
class Fact;

int rows;
int cols; 
string target;
int answer;
bool done = false;

char layout [SIZE][SIZE];
Node * nodes [SIZE][SIZE];
queue<Fact*> tobedone;

int cons(int x, int y){
   return SIZE * x + y;
}

int getrow (int z){
	return z/ SIZE;
}

int getcol(int z){
	return z % SIZE;
}

int leftNext (int r, int c);
int rightNext(int r, int c);
int upNext   (int r, int c);
int downNext (int r, int c);

class Node{
public:
      int row, col;
      char character;
      Fact * fact;
      int typed ;
      Node * neighbours [4];

public :
    	void update(int strokesIn, int typedIn);
	    void link();
	    string toString(){
		      stringstream ss;
		      ss << "node (" <<  row << ", " << col << ", "  << character << ")";
		      return ss.str();
      }
	    Node(int y, int x){
          row = y;
          col = x;
          character = layout[row][col];
		      fact = NULL;
		      typed = -1;
		      for (int k = 0; k < 4; k++)
			        neighbours[k] = NULL;
      }
	    Node(){}
		void setLink(int buur, int where);
};

class Fact{
public:
      Node* position;
      int strokes;
      int typed;
	    bool free;
public:  
      void  visit();
      Fact(int pr, int pc, int s, int t);
};

void Node::update(int strokesIn, int typedIn){	    
         if (typedIn <= typed)
            return;
         if (typedIn == target.length()){
            answer = strokesIn;
            done = true;
            return;
         }
         typed = typedIn;
		     if (fact == NULL)
			       fact = new Fact(row, col, strokesIn, typedIn);
		     else{   //fact != null
             if (strokesIn == fact->strokes){
	              fact->typed = typedIn;
		         }
			       else{
				         if (fact->free){ // reuse object for speed-up!!
                    fact->strokes = strokesIn;
                    fact->typed = typedIn;
                    tobedone.push(fact);
		                fact->free = false;
				         }
			           else{ // fact is still on the queue
				            fact->free = true;
					          fact = new Fact(row, col, strokesIn, typedIn);
				         }
			       }
		     }
/*       // equivalent, but slower
         if (fact != NULL && strokesIn == fact->strokes){
	         fact->typed = typedIn;
		 }
         else{
			 if (fact != NULL){
		     	 if (fact->free)
			    	 delete fact;
			     else
				    fact->free = true;
			 }
	         fact = new Fact(row, col, strokesIn, typedIn);
		 }
*/

}

void Node::setLink(int buur, int wher){
     int r = getrow(buur);
	 int c = getcol(buur);
     if (buur != -1){
         if (nodes[r][c] == NULL){
             nodes[r][c] = new Node(r,c);
             nodes[r][c]->link();
         }
         neighbours[wher] = nodes[r][c];
     }
	 else
         neighbours[wher] = NULL;
}

void Node::link(){     
	 setLink( leftNext(row,col), 0);
	 setLink(rightNext(row,col), 1);
	 setLink(   upNext(row,col), 2);
	 setLink( downNext(row,col), 3);	 	          
}

Fact::Fact(int pr, int pc, int s, int t){
	   position = nodes[pr][pc];
     strokes = s;
     typed = t;
     tobedone.push(this);
		 free = false;
}

void Fact::visit(){
	   if (target.at(typed) == position->character)
         position->update(strokes + 1, typed + 1);
     else
         for (int k = 0; k < 4; k++){
	           Node * buur = position->neighbours[k];
	           if (buur != NULL)
	                buur->update(strokes + 1, typed);
	   }
}

int leftNext(int r, int c){
     char cc = layout[r][c];
     int k = c - 1;
     while (k >= 0 && layout[r][k] == cc)
         k--;
     if (k < 0)
        return -1;
     return cons(r, k);
}

int rightNext(int r, int c){
     char cc = layout[r][c];
     int k = c + 1;
     while (k < cols  && layout[r][k] == cc)
        k++;
     if (k == cols )
        return -1;
     return cons(r, k);
}

int upNext(int r, int c){
     char cc = layout[r][c];
     int k = r - 1;
     while (k >= 0 && layout[k][c] == cc)
         k--;
     if (k < 0)
        return -1;
     return cons(k, c);
}

int downNext(int r, int c){
    char cc = layout[r][c];
    int k = r + 1;
    while (k < rows && layout[k][c] == cc)
        k++;
    if (k == rows)
        return -1;
    return cons(k, c);
}

int countStrokes(){
     done = false;
     nodes[0][0]->update(0, 0);
     while (!done){
         Fact* present = tobedone.front();
 		     tobedone.pop();
	       present->visit();
		     if (present->free)
		        delete present;
		     else
		        present->free = true;
     }
     return answer;
}

void init(){
  	for (int r = 0; r < rows; r++)
	  	for (int c = 0; c < cols; c++)
			  nodes[r][c] = NULL;
    nodes[0][0] = new Node(0,0);
    nodes[0][0]->link();
	  answer = -37;
}

void runSingle(){
    cin >> rows >> cols ;
    for (int k = 0; k < rows; k++){
       string line;
		   cin >> line;
       for (int l = 0; l < cols; l++)
            layout[k][l] = line.at(l);
    }
    cin >> target;
	  target.push_back('*');
    init();
    int strokes = countStrokes();
    cout << strokes << endl;
}

int main(int argc, char **argv) {
    runSingle();
}
