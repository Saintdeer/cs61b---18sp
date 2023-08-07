public class NBody {
    public static double readRadius(String txt){
        In in = new In(txt);
        in.readInt();
        return in.readDouble();
    }
    public static Planet[] readPlanets(String txt){
        In in = new In(txt);
        int n = in.readInt();
        Planet[] p = new Planet[n];
        in.readDouble();
        for(int i = 0; i < n; i++){
            double xxPos = in.readDouble();
            double yyPos = in.readDouble();
            double xxVel = in.readDouble();
            double yyVel = in.readDouble();
            double mass = in.readDouble();
            String ImgFileName = in.readString();
            p[i] = new Planet(xxPos, yyPos, xxVel, yyVel, mass, ImgFileName);
        }
        return p;
    }
    public static void main(String[] args){
        double T = Double.parseDouble(args[0]);
        double dt = Double.parseDouble(args[1]);
        String filename = args[2];
        Planet[] planets = readPlanets(filename);
        double radius = readRadius(filename);
        StdDraw.setScale(-radius, radius);
        StdDraw.clear();
        StdDraw.picture(0, 0, "images/starfield.jpg");
        for(Planet p: planets){
            p.draw();
        }
        StdDraw.enableDoubleBuffering();
        double time = 0;
        while(time < T){
            int num = planets.length;
            double xForces[] = new double[num];
            double yForces[] = new double[num];
            for(int i=0; i<num; i++){
                xForces[i] = planets[i].calcNetForceExertedByX(planets);
                yForces[i] = planets[i].calcNetForceExertedByY(planets);
            }
            for(int i=0; i<num; i++){
                planets[i].update(dt, xForces[i], yForces[i]);
            }
            StdDraw.picture(0, 0, "images/starfield.jpg");
            for(Planet p: planets){
                p.draw();
            }
            StdDraw.show();
            StdDraw.pause(10);
            time += dt;
        }
        StdOut.printf("%d\n", planets.length);
        StdOut.printf("%.2e\n", radius);
        for (int i = 0; i < planets.length; i++) {
            StdOut.printf("%11.4e %11.4e %11.4e %11.4e %11.4e %12s\n",
                    planets[i].xxPos, planets[i].yyPos, planets[i].xxVel,
                    planets[i].yyVel, planets[i].mass, planets[i].imgFileName);
        }
    }
}
