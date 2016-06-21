#include <cmath>
#include <cstdio>
#include <cstdlib>
#include <iostream>
#include <cstring>
#include <string>
#include <algorithm>
#include <queue>
#include <stack>
#include <climits>
#include <iomanip>
#include <cassert>
using namespace std;

/*#include<stdio.h>
#include<math.h>
#include<iostream>*/
const int MAX = 20;
//typedef int bool;
typedef long long LONG;
struct point {double x,y;LONG ix,iy;};
struct line { point a,b; double ang;};
struct line2
{
    LONG a,b,c;
};
point p1[MAX],s[MAX],p2[MAX],ptemp[MAX];
line ln[MAX*2],deq[MAX*2];
const double eps = 1e-9;
bool dy(double x,double y)	{	return x > y + eps;}	// x > y
bool xy(double x,double y)	{	return x < y - eps;}	// x < y
bool dyd(double x,double y)	{ 	return x > y - eps;}	// x >= y
bool xyd(double x,double y)	{	return x < y + eps;} 	// x <= y
bool dd(double x,double y) 	{	return fabs( x - y ) < eps;}  // x == y
double crossProduct(point a,point b,point c)
{
	return (c.x - a.x)*(b.y - a.y) - (b.x - a.x)*(c.y - a.y);
}
bool parallel(line u,line v)
{
	return dd( (u.a.x - u.b.x)*(v.a.y - v.b.y) - (v.a.x - v.b.x)*(u.a.y - u.b.y) , 0.0 );
}
point l2l_inst_p(line l1,line l2)
{
	point ans = l1.a;
	double t = ((l1.a.x - l2.a.x)*(l2.a.y - l2.b.y) - (l1.a.y - l2.a.y)*(l2.a.x - l2.b.x))/
			   ((l1.a.x - l1.b.x)*(l2.a.y - l2.b.y) - (l1.a.y - l1.b.y)*(l2.a.x - l2.b.x));
	ans.x += (l1.b.x - l1.a.x)*t;
	ans.y += (l1.b.y - l1.a.y)*t;
	return ans;
}
bool equal_ang(line a,line b)
{
	return dd(a.ang,b.ang);
}
bool cmphp(line a,line b)
{
	if( dd(a.ang,b.ang) ) return xy(crossProduct(b.a,b.b,a.a),0.0);
	return xy(a.ang,b.ang);
}
bool equal_p(point a,point b)
{
	return dd(a.x,b.x) && dd(a.y,b.y);
}
void makeline_hp(point a,point b,line &l)
{
	l.a = a; l.b = b;
	l.ang = atan2(a.y - b.y,a.x - b.x);
}
void inst_hp_nlogn(line *ln,int n,point *s,int &len)
{
	len = 0;
	sort(ln,ln+n,cmphp);
	n = unique(ln,ln+n,equal_ang) - ln;
	int bot = 0,top = 1;
	deq[0] = ln[0]; deq[1] = ln[1];
	for(int i=2; i<n; i++)
	{
		if( parallel(deq[top],deq[top-1]) || parallel(deq[bot],deq[bot+1]) )
			return ;
		while( bot < top && dy(crossProduct(ln[i].a,ln[i].b,
			l2l_inst_p(deq[top],deq[top-1])),0.0) )
			top--;
		while( bot < top && dy(crossProduct(ln[i].a,ln[i].b,
			l2l_inst_p(deq[bot],deq[bot+1])),0.0) )
			bot++;
		deq[++top] = ln[i];
	}
	while( bot < top && dy(crossProduct(deq[bot].a,deq[bot].b,
		l2l_inst_p(deq[top],deq[top-1])),0.0) )	top--;
	while( bot < top && dy(crossProduct(deq[top].a,deq[top].b,
		l2l_inst_p(deq[bot],deq[bot+1])),0.0) )	bot++;
	if( top <= bot + 1 ) return ;

	for(int i=bot; i<top; i++)
		s[len++] = l2l_inst_p(deq[i],deq[i+1]);
	if( bot < top + 1 ) s[len++] = l2l_inst_p(deq[bot],deq[top]);
	len = unique(s,s+len,equal_p) - s;
}
double area_polygon(point p[],int n)
{
	if( n < 3 ) return 0.0;
	double s = 0.0,t;
	for(int i=0; i<n; i++)
	{

		t= (p[(i+1)%n].y * p[i].x - p[(i+1)%n].x * p[i].y);
		s+=t;
	//	cout<<p[(i+1)%n].y<<"*"<<p[i].x<<" - "<<p[(i+1)%n].x<<"*"<<p[i].y;
	 //   cout<<"t = "<<t<<endl;
	}
	return fabs(s/2.0);
}

double common_area(point lp1[], point lp2[], int n, int m)
{
    double a1, a2, area;
    int len,i;
	a1 = area_polygon(lp1,n);
	for(i=0; i<n; i++)
		makeline_hp(lp1[i+1],lp1[i],ln[i]);
	a2 = area_polygon(lp2,m);
	for(int i=0; i<m; i++)
		makeline_hp(lp2[i+1],lp2[i],ln[i+n]);
	n += m;
	inst_hp_nlogn(ln,n,s,len);
	area = area_polygon(s,len);
	//for(i=0;i<m;i++)
	  // cout<<lp2[i].x<<" "<<lp2[i].y<<endl;
//	cout<<"a1 = "<<a1<<" a2 = "<<a2<<endl;
    return area;
}
double common_area_at_t(double t,int n, int m, int vx, int vy)
{
     int i;
     double ans;
     for(i=0;i<=m;i++)
     {
         ptemp[i].x=p2[i].x+vx*t;
         ptemp[i].y=p2[i].y+vy*t;
     }
     ans=common_area(p1,ptemp,n,m);
     return ans;
}
void make_line(LONG p, LONG q, LONG dx, LONG dy, LONG *a, LONG *b, LONG *c)
{
    *a=dy;
    *b=-dx;
    *c=q*dx-p*dy;
}
void make_line2(LONG x1, LONG y1, LONG x2, LONG y2, LONG *a, LONG *b, LONG *c)
{
    *a=y1-y2;
    *b=x2-x1;
    *c=x1*y2-x2*y1;
}
LONG squaredist(LONG x1, LONG y1, LONG x2, LONG y2)
{
    LONG ans;
    ans=(x1-x2)*(x1-x2)+(y1-y2)*(y1-y2);
    return ans;
}
void l_inter(LONG a1, LONG b1, LONG c1, LONG a2, LONG b2, LONG c2, LONG *numex, LONG *denox,
	     LONG *numey, LONG *denoy)
{
    *numex=b1*c2-b2*c1;
    *denox=a1*b2-a2*b1;
    *numey=c1*a2-a1*c2;
    *denoy=a1*b2-a2*b1;
}
int l_ls_inter(LONG a, LONG b, LONG c, LONG x1, LONG y1, LONG x2, LONG y2)
{
    LONG sign1, sign2;
    sign1=a*x1+b*y1+c;
    sign2=a*x2+b*y2+c;
    if(sign1*sign2<=0)
       return 1;
    return 0;
}
double dist(double x1, double y1, double x2, double y2)//finds distance between (x1,y1) (x2,y2)
{
     double ans;
     ans=(x1-x2)*(x1-x2)+(y1-y2)*(y1-y2);
     return sqrt(ans);
}
int main(void)
{
	int n,m,i,mult,j, found=1;
	int vx1, vy1, vx2, vy2, vx, vy;
	LONG a, b, c, a2, b2, c2, numex, denox, numey, denoy, sign, p, q,sign2;
	double side, xx, yy, maxd, mind,d,tlow,thigh,tmid, areabefore, areaafter, areamid, areamax;

	//double x,y;
 //   freopen("asteroids.in","r",stdin);
	while ( cin>>n/*scanf("%d",&n) && n*/ )
	{
	 //   printf("n = %d\n",n);
		for(i=0; i<n; i++)
		{
		    cin>>p1[i].ix>>p1[i].iy;
		    p1[i].x=p1[i].ix;
		    p1[i].y=p1[i].iy;
		}
		p1[n] = p1[0];
		for(i=0;i<n;i++)
		{
		    side=dist(p1[i].x, p1[i].y, p1[(i+1) % n].x, p1[(i+1) % n].y);
		    assert(side<=500.0);
		}
	    cin>>vx1>>vy1;
//		scanf("%d",&m);
		cin>>m;
	//	printf("m = %d\n",m);

		for(i=0; i<m; i++)
		{
		//	scanf("%lf%lf",&p[i].x,&p[i].y);
			//	scanf("%lf%lf",&x,&y);
		    cin>>p2[i].ix>>p2[i].iy;
	    	p2[i].x=p2[i].ix;
		    p2[i].y=p2[i].iy;

		   // printf("%lf %lf\n",x,y);
		  // cout<<p[i].x<<" "<<p[i].y<<endl;
		}
		p2[m] = p2[0];
		for(i=0;i<m;i++)
		{
		    side=dist(p2[i].x, p2[i].y, p2[(i+1) % m].x, p2[(i+1) % m].y);
		    assert(side<=500.0);
		}
		mult=1;
		/*for(mult=1;;mult++)
		   if(mult % n==0 && mult % m==0)
		       break;*/
    /*    cx1=cy1=0;
		for(i=0;i<n;i++)
		{
		    p1[i].ix*=mult;
		    p1[i].iy*=mult;
		    p1[i].x*=mult;
		    p1[i].y*=mult;
		    cx1+=p1[i].ix;
		    cy1+=p1[i].iy;
		}
		assert(cx1 % n==0);
		assert(cy1 % n==0);
		cx1/=n;
		cy1/=n;
		cx2=cy2=0;
		for(i=0;i<m;i++)
		{
		    p2[i].x*=mult;
		    p2[i].y*=mult;
		    p2[i].ix*=mult;
		    p2[i].iy*=mult;
		    cx2+=p2[i].ix;
		    cy2+=p2[i].iy;
		}
		assert(cx2 % m==0);
		assert(cy2 % m==0);
		cx2/=m;
		cy2/=m;*/
		cin>>vx2>>vy2;
		vx=vx2-vx1;
		vy=vy2-vy1;
		maxd=0;
		mind=100000000.0;
		found=0;
		//cout<<"vx = "<<vx<<"vy = "<<vy<<endl;
		for(i=0;i<n;i++)
		{
		    p=p1[i].ix;
		    q=p1[i].iy;
		    make_line(p1[i].ix,p1[i].iy,-vx,-vy,&a,&b,&c);
//int l_ls_inter(LONG a, LONG b, LONG c, LONG x1, LONG y1, LONG x2, LONG y2)
		    for(j=0;j<m;j++)
		    {
//void l_inter(LONG a1, LONG b1, LONG c1, LONG a2, LONG b2, LONG c2, LONG *numex, LONG *denox,
  //           LONG *numey, LONG *denoy)
    			if(l_ls_inter( a, b, c, p2[j].ix, p2[j].iy,p2[j+1].ix,p2[j+1].iy))
	    		{
		            make_line2(p2[j].ix,p2[j].iy,p2[j+1].ix,p2[j+1].iy,&a2,&b2,&c2);
    		        l_inter(a,b,c,a2,b2,c2,&numex,&denox,&numey,&denoy);
	        	    if(denox==0 || denoy==0) continue;
	        	    if(-vx*denox>=0)
	            		sign=1;
	        	    else
	            		sign=-1;
	     	        if(-vy*denoy>=0)
	        	    	sign2=1;
	    	        else
	            		sign2=-1;
		            if((numex-p*denox)*sign>=0 && (numey-q*denoy)*sign2>=0)
		            {
	    		        xx=numex*1.0/denox;
	    	        	yy=numey*1.0/denoy;
	    	        	found=1;
	    	        //	cout<<"Hehe "<<p1[i].ix<<" "<<p1[i].iy<<endl;
		            	d=dist(p1[i].x,p1[i].y,xx,yy);
		             	if(d<mind)
			                mind=d;
		            	if(d>maxd)
		     	            maxd=d;
		            }
			    }
		    }

		}
		for(i=0;i<m;i++)
		{
		    p=p2[i].x;
		    q=p2[i].y;
		    make_line(p2[i].ix,p2[i].iy,vx,vy,&a,&b,&c);
//int l_ls_inter(LONG a, LONG b, LONG c, LONG x1, LONG y1, LONG x2, LONG y2)
		    for(j=0;j<n;j++)
		    {
//void l_inter(LONG a1, LONG b1, LONG c1, LONG a2, LONG b2, LONG c2, LONG *numex, LONG *denox,
  //           LONG *numey, LONG *denoy)
			    if(l_ls_inter( a, b, c, p1[j].ix, p1[j].iy, p1[j+1].ix, p1[j+1].iy))
			    {
    		        make_line2(p1[j].ix,p1[j].iy,p1[j+1].ix,p1[j+1].iy,&a2,&b2,&c2);
                    l_inter(a,b,c,a2,b2,c2,&numex,&denox,&numey,&denoy);
                    if(denox==0 || denoy==0) continue;
                    if(vx*denox>=0)
                        sign=1;
                    else
                    sign=-1;
                    if(vy*denoy>=0)
                        sign2=1;
                    else
                    sign2=-1;
                    if((numex-p*denox)*sign>=0 && (numey-q*denoy)*sign2>=0)
                    {
                        found=1;
                        xx=numex*1.0/denox;
                        yy=numey*1.0/denoy;
                   //     cout<<"Hehe2 "<<p2[i].ix<<" "<<p2[i].iy<<endl;
                        d=dist(p2[i].x,p2[i].y,xx,yy);
                        if(d<mind)
                            mind=d;
                        if(d>maxd)
                            maxd=d;
                    }
                }
		    }
		}
		if(found==0)
		{
		    cout<<"never"<<endl;
		    continue;
		}
		if(fabs(maxd-mind)<eps)
        {
            assert(found);
            tmid=mind/sqrt(vx*vx+vy*vy);
            cout << fixed << setprecision(3);
            cout<<tmid<<endl;
            continue;
        }
//double common_area_at_t(double t,int m, int vx, int vy)

//		cout<<"mind = "<<mind<<"maxd = "<<maxd<<endl;
		tlow=mind/sqrt(vx*vx+vy*vy);
		thigh=maxd/sqrt(vx*vx+vy*vy);
  //      cout<<"tlow = "<<tlow<<"thigh = "<<thigh<<endl;
        while(tlow<=thigh)
        {
            tmid=(tlow+thigh)/2.0;
          //  printf("Hehe\n");
            areamid=common_area_at_t(tmid, n, m, vx, vy);
            areabefore=common_area_at_t(tmid-0.0001, n, m, vx, vy);
            areaafter=common_area_at_t(tmid+0.0001, n, m, vx, vy);
      //     cout << fixed << setprecision(12);
    //        cout<<"areabefore = "<<areabefore<<" areamid = "<<areamid<<" areaafter = "<<areaafter<<endl;
            if(areamid>=areabefore && areamid>=areaafter)
                break;
            if(areamid<=areabefore && areamid>=areaafter)
                thigh=tmid;
            else if(areamid>=areabefore && areamid<=areaafter)
                tlow=tmid;
            else
            {
                assert(1==2);
            }
        }
        //cout<<"tmid = "<<tmid<<endl;
        areamax=areamid;
		tlow=mind/sqrt(vx*vx+vy*vy);
		thigh=tmid;
        //cout<<"tlowb = "<<tlow<<"thighb = "<<thigh<<endl;
		if(fabs(areamid)<=eps*10)
		{
            assert(found);
            tmid=mind/sqrt(vx*vx+vy*vy);
            cout << fixed << setprecision(3);
            cout<<tmid<<endl;
            continue;
		}
		while(tlow<=thigh)
        {
            tmid=(tlow+thigh)/2.0;
            areamid=common_area_at_t(tmid, n, m, vx, vy);
            areabefore=common_area_at_t(tmid-0.0001, n, m, vx, vy);
            areaafter=common_area_at_t(tmid+0.0001, n, m, vx, vy);
          // cout << fixed << setprecision(12);
         //   cout<<"tmid = "<<tmid<<endl;
          //  cout<<"areabefore = "<<areabefore<<" areamid = "<<areamid<<" areaafter = "<<areaafter<<endl;
            if(fabs(areamid-areamax)<eps && (areamid>=areabefore+eps*10 || fabs(tmid-tlow)<eps))
                break;
            if(areamid>=areabefore+eps && areamid>=areaafter+eps)
                break;
            if(fabs(areamid-areamax)<eps)
                thigh=tmid;
            else if(areamid>areabefore && areamid<areaafter)
                tlow=tmid;
            else
            {
                assert(1==2);
            }
        }
        cout << fixed << setprecision(6);
        cout<<tmid<<endl;

		//printf("%8.2lf",area);
      //		cout << fixed << setprecision(2);
    //		cout<<common_area(p1,p2,n,m)<<endl;
	}
//	areamid=common_area_at_t(185.065, n, m, vx, vy);
  //  cout<<"HEHE "<<areamid<<endl;
	printf("\n");
return 0;
}
