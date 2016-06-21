/*
C++ solution of the 'Ship Traffic' problem.
ACM ICPC World Finals 2015, Morocco.
December 2014, Istanbul.
By Osman Ay and Walter Guttmann.
*/
#include <iostream>
//#include <fstream>
#include <vector>
#include <algorithm>

using namespace std;
//---------------------------------------
//ifstream cin("secret/ship-001.in");
//ofstream cout("ships.out");
//---------------------------------------
struct Ship
{
	int len;					//Length of the ship.
	double front, back;			//Front and back positions at time 0.
};
//Comparison criteria for sorting the ships.
bool compareShips(const Ship &ship1, const Ship &ship2)
{
	return (ship1.front < ship2.front);
}
//---------------------------------------
/*
Width w of each lane, the speed u of ships, speed v of the ferry, the
earliest start time t1 and the latest start time t2.
*/
double w, u, v, t1, t2;	
double endPos,lastShipPos = 1e9 + 1;		
vector <Ship> shipVector;
//---------------------------------------
void readData();
double go();
//---------------------------------------
int main()
{
	readData();				//Read ships and transfer them to lane1 setting all ships in one direction.
	double result = go();	//Calculate the result when all ships are in lane1 and traveling in the same direction.
	cout.precision(3);
	cout<<fixed<<result<<endl;
	return 0;
}
//---------------------------------------
//Read data, transfers all ships to the first lane.
//Mirror the negative positioned ships.
//Eliminate the ships that don't affect the result.
void readData()
{
	int n;					//Number of lanes
	cin >> n >> w >> u >> v >> t1 >> t2;
	t2 = t2 - t1;			//Consider that t1 is 0, set t2 accordingly.
	endPos = t2*u;			
	for (int i=0; i<n; i++)	//i is the lane order.
	{
		char direction;
		cin >> direction;	
		int m;				//Number of ships at the current lane.
		cin >> m;
		for (int j=0; j<m; j++)
		{
			Ship newShip;
			cin >> newShip.len >> newShip.front;
			
			//Calculate the stern position of the ship.
			if (direction == 'W')
				newShip.back = newShip.front + newShip.len;
			else
				newShip.back = newShip.front - newShip.len;
		
			//Eliminate the ships that already passed the crossing line.
			if (direction == 'E' && newShip.back > 0)
				continue;
			if (direction == 'W' && newShip.back < 0)
				continue;
			//Mirroring the eastbound ships to westbound.
			if (direction == 'E')
			{
				if (newShip.front < 0)
					newShip.front = -newShip.front;	
			}
			//Move the ship to the lane1.
			double distance = (i*w*u)/v;			
			newShip.front -= (distance + u*t1);
			newShip.back = newShip.front  + newShip.len;

			//Eliminate the ships that passed the crossing line before the time t1.
			if (newShip.back <= 0)
				continue;	
			//Eliminate the ships that passed the crossing line after the time t2.
			if (newShip.front >= endPos) 	
			{
				//Keep the position of the first ship passing the crossing line after the time t2.
				lastShipPos = min(lastShipPos,newShip.front);
				continue;
			}
			//Add the new ship to the ship vector.
			shipVector.push_back(newShip);
		}	
	}
}
//---------------------------------------
double go()
{
	double res=0, res2;
	double maxGap = 0;		//Maximum empty distance between two consecutive ships.
	double sternPos = 0;	//Reference position to calculate the distance to the front of the current ship.
	sort(shipVector.begin(), shipVector.end(), compareShips);	//By their front positions in increasing order.
	for (unsigned int i=0; i<shipVector.size(); i++)
	{
		Ship curShip = shipVector[i];					//The current ship.
		maxGap = max(maxGap, curShip.front - sternPos);	//Calculate the new gap and check if it is bigger then maxGap.
		sternPos = max(sternPos, curShip.back);			//The new reference position to calculate the distance.	
	}
	
	//Calculate the time period for the biggest distance.
	res = (maxGap/u) - (w/v);		

	//Consider the gap after the last ship in the vector.
	res2 = max(res, (endPos-sternPos)/u);	//There is a ship after the time t2.
	maxGap = lastShipPos-sternPos;
	res2 = min(res2, (maxGap/u) - (w/v));	//There is no more ship after the last ship in the vector.
	return max(res,res2);
}
