#include<cstdio>
#include<iostream>
#include<cstdlib>
#include<cstring>
#include<algorithm>
#include<cmath>
#include<set>
#include<string>


using namespace std;
typedef pair<int,int> pII;
set<pII> vis;
string dir[4] = {"up", "right", "down", "left"};
bool work(int x,int y, string from) {
	string reply;
	
	vis.insert(pII(x,y));
	
	for(int i=0;i<4;i++) {
		if(from == "up"&&dir[i]=="down") continue;
		else if (from == "right"&&dir[i]=="left") continue;
		else if (from == "down"&&dir[i]=="up") continue;
		else if (from == "left"&&dir[i]=="right") continue;
		cout<<dir[i]<<endl;
		cin>>reply;
		if(reply == "wall") {}
		else if(reply == "solved") return true;
		else if (reply == "ok") {
			if(i==0 && vis.count(pII(x,y+1)) == 0 && work(x,y+1,dir[i])) return true; 
			if(i==1 && vis.count(pII(x+1,y)) == 0 && work(x+1,y,dir[i])) return true;
			if(i==2 && vis.count(pII(x,y-1)) == 0 && work(x,y-1,dir[i])) return true;
			if(i==3 && vis.count(pII(x-1,y)) == 0 && work(x-1,y,dir[i])) return true;
		}
	}
	if(from != "none") {
		if(from == "up") cout<<"down"<<endl;
		else if (from == "right") cout<<"left"<<endl;
		else if (from == "down") cout<<"up"<<endl;
		else cout<<"right"<<endl;
		cin >> reply;
	}
	return false;
	
}
int main() {
  while(true) {
  	bool ans = false;
		vis.clear();
		
		if(work(0,0,"none")) continue;
		puts("no way out");
  }
	
}
