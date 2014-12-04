#include <stdio.h>
#include <math.h>

/*
 * $HeadURL$
 */ 

double height(double a, double v, double dx) {
    double vx = v * cos(a);
    double vy = v * sin(a);
    double t = dx / vx;
    return (vy - 4.9 * t) * t;
}

double find_angle(double v, double dx, double dy) {
    double prev_pos = height(0.0, v, dx);
    double prev_neg = prev_pos;
    for (double a=0; a<90.0; a+=0.0000001) {
        double next_pos = height(a,v,dx);
        double next_neg = height(-a,v,dx);
        if (prev_pos <= dy && next_pos >= dy) return a;
        if (prev_neg >= dy && next_neg <= dy) return -a;
        prev_pos = next_pos;
        prev_neg = next_neg;
    }
    return M_PI;
}

int main() {
    double v, dx, dy, a;
    scanf("%lf %lf %lf", &v, &dx, &dy);
    a = find_angle(v,dx,dy);
    if (a > M_PI/2) printf("None\n");
    else            printf("%f\n", a*180/M_PI);
    return 0;
}
