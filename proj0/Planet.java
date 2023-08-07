public class Planet {
    private static double G = 6.67e-11;
    public double xxPos; //current x position
    public double yyPos; //current y position
    public double xxVel; //current velocity in the x direction
    public double yyVel; //current velocity in the y direction
    public double mass;
    public String imgFileName;
    public Planet(double xP, double yP, double xV, double yV, double m, String img){
        xxPos = xP;
        yyPos = yP;
        xxVel = xV;
        yyVel = yV;
        mass = m;
        imgFileName = img;
    }
    /** take in a Planet object and initialize an identical Planet object. */
    public Planet(Planet p){
        xxPos = p.xxPos;
        yyPos = p.yyPos;
        xxVel = p.xxVel;
        yyVel = p.yyVel;
        mass = p.mass;
        imgFileName = p.imgFileName;
    }

    /** calculates the distance between two Planets. */
    public double calcDistance(Planet p){
        double x = Math.abs(xxPos - p.xxPos);
        double y = Math.abs(yyPos - p.yyPos);
        return Math.sqrt(x*x + y*y);
    }

    /** returns a double describing the force exerted on this planet by the given planet. */
    public double calcForceExertedBy(Planet p){
        double d = this.calcDistance(p); //distance
        return G*mass*(p.mass)/(d*d);
    }

    /** describe the force exerted in the X directions. */
    public double calcForceExertedByX(Planet p){
        double force = this.calcForceExertedBy(p);
        double distance = this.calcDistance(p);
        return force*(p.xxPos - xxPos)/distance;
    }

    /** describe the force exerted in the Y directions. */
    public double calcForceExertedByY(Planet p){
        double force = this.calcForceExertedBy(p);
        double distance = this.calcDistance(p);
        return force*(p.yyPos - yyPos)/distance;
    }

    /**  take in an array of Planets and calculate the net X  force
     * exerted by all planets in that array upon the current Planet.*/
    public double calcNetForceExertedByX(Planet[] p){
        double sum = 0;
        for(Planet x: p){
            if(this.equals(x))
                continue;
            sum += this.calcForceExertedByX(x);
        }
        return sum;
    }

    /**  take in an array of Planets and calculate the net Y force
     * exerted by all planets in that array upon the current Planet.*/
    public double calcNetForceExertedByY(Planet[] p){
        double sum = 0;
        for(Planet x: p){
            if(this.equals(x))
                continue;
            sum += this.calcForceExertedByY(x);
        }
        return sum;
    }

    /** For example, samh.update(0.005, 10, 3) would adjust the velocity and position if an x
     * -force of 10 Newtons and a y-force of 3 Newtons were applied for 0.005 seconds. */
    public void update(double dt, double x_force, double y_force){
        double a_x = x_force/mass; //acceleration
        double a_y = y_force/mass;
        xxVel += dt*a_x;
        yyVel += dt*a_y;
        xxPos += dt*xxVel;
        yyPos += dt*yyVel;
    }
    public void draw(){
        StdDraw.picture(xxPos, yyPos,"images/" + imgFileName);
    }
}
