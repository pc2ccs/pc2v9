import java.util.Scanner;

/**
 * Arrow solution. 
 * 
 * @author pc2@ecs.csus.edu
 * @version $Id$
 */

// $HeadURL$
public class Arrow {

    // M_PI 3.14159265358979323846

    double M_PI = 3.14159265358979323846;

    double height(double a, double v, double dx) {
        double vx = v * Math.cos(a);
        double vy = v * Math.sin(a);
        double t = dx / vx;
        return (vy - 4.9 * t) * t;
    }

    double find_angle(double v, double dx, double dy) {
        double prev_pos = height(0.0, v, dx);
        double prev_neg = prev_pos;
        for (double a = 0; a < 90.0; a += 0.0000001) {
            double next_pos = height(a, v, dx);
            double next_neg = height(-a, v, dx);
            if (prev_pos <= dy && next_pos >= dy) {
                return a;
            }
            if (prev_neg >= dy && next_neg <= dy) {
                return -a;
            }
            prev_pos = next_pos;
            prev_neg = next_neg;
        }
        return M_PI;
    }

    private void run() {
        double v, dx, dy, a;

        Scanner scanIn = new Scanner(System.in);
        // scanf("%lf %lf %lf", &v, &dx, &dy);
        v = scanIn.nextFloat();
        dx = scanIn.nextFloat();
        dy = scanIn.nextFloat();
        scanIn.close();

        a = find_angle(v, dx, dy);
        if (a > M_PI / 2) {
            System.out.println("None");
        } else {
            // printf("%f\n", a * 180 / M_PI);
            System.out.printf("%f\n", a * 180 / M_PI);

        }

    }

    public static void main(String[] args) {

        Arrow arrow = new Arrow();
        arrow.run();
    }
}