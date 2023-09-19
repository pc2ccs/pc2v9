#include <stdio.h>
#include <iostream>
#include <sstream>
#include <iomanip>

#include <algorithm>
#include <stdlib.h>
#include <string.h>
#include <string>
#include <utility>

#include <math.h>
#include <complex>

#include <assert.h>
#include <time.h>

#include <vector>
#include <map>
#include <set>
#include <queue>
#include <stack>
#include <list>
#include <bitset>
#define FI first
#define SE second
#define MP make_pair
#define PB push_back
#define endl '\n'
using namespace std;

typedef long long LL;
typedef unsigned long long ULL;

void desperate_optimization(int precision){
    ios_base::sync_with_stdio(false);
    cin.tie(0);
    cout.tie(0);
    cout.setf(ios::fixed);
    cout.setf(ios::showpoint);
    cout.precision(precision);
}

string print[] = {"up","right","down","left"};

int py[] = {1,0,-1,0};
int px[] = {0,1,0,-1};
bool sudah = false;

map<pair<int,int>, int > ms;

void DFS(int y,int x) {
    ms[MP(y,x)] = 1;
    for(int i = 0;i < 4; i++) {
        int yy = py[i] + y;
        int xx = px[i] + x;
        if(ms[MP(yy,xx)] == 1) continue;
        cout<<print[i]<<endl;
        cout<<flush;
        string tmp;
        cin>>tmp;
        if(tmp == "ok") {
            DFS(yy,xx);
            if(sudah == false) {
                cout<<print[(i + 2) % 4]<<endl;
                cout<<flush;
                cin>>tmp;
            }
        }
        else if(tmp == "solved") {
            sudah = true;
            return ;
        }
    }
}

int main(){
    desperate_optimization(10);
    clock_t CLOCK;
    CLOCK = clock();
    
    DFS(0,0);
    if(sudah == false) {
        cout<<"no way out"<<endl;
    }
    cerr<<"PROCESSED TIME "<<(clock() - CLOCK) * 1.0 / (1.0 * CLOCKS_PER_SEC)<<endl;
    return 0;
}


