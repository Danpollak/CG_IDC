package edu.cg.algebra;

import edu.cg.CyclicList;

import Jama.Matrix;


public class CubicSpline{

    CyclicList<PolyVec> polys;
    final double length;

    /**
     * Constructs a Cubic-Spline object from a given collection of points.
     * Populates both the spline and meta-data regarding the mathemetical object
     * 
     *  
     * @param controlPoints a cyclic collection of {@link Point} objects.
     * 
     */
    public CubicSpline(CyclicList<Point> controlPoints){
        
        // interpolate the points via solving a LES
        int n = controlPoints.size();
        double[][] mat = new double[4*n][4*n];
        double[] xSolVec= new double[4*n];
        double[] ySolVec= new double[4*n];
        double[] zSolVec= new double[4*n];
        for(int i = 0; i < n ; i++){
            Point p_i = controlPoints.get(i);
            xSolVec[4*i] = p_i.x;
            ySolVec[4*i] = p_i.y;
            zSolVec[4*i] = p_i.z;
            
            mat[4*i][4*i + 3] = 1.0; // eq 1 di

            mat[4*i + 1][4*i] = 1.0;        // eq 2 ai
            mat[4*i + 1][4*i + 1] = 1.0;    // eq 2 bi
            mat[4*i + 1][4*i + 2] = 1.0;    // eq 2 ci
            mat[4*i + 1][4*i + 3] = 1.0;    // eq 2 di
            mat[4*i + 1][4*(i+1) + 3] = 1.0;    // eq 2 -d(i+1)

            mat[4*i + 2][4*i] = 3.0;        // eq 3 ai
            mat[4*i + 2][4*i + 1] = 2.0;    // eq 3 bi
            mat[4*i + 2][4*i + 2] = 1.0;    // eq 3 ci
            mat[4*i + 2][4*(i+1) + 2] = -1.0;    // eq 3 -ci(i+1)

            mat[4*i + 3][4*i] = 6.0;        // eq 4 ai
            mat[4*i + 3][4*i + 1] = 2.0;    // eq 4 bi
            mat[4*i + 3][4*i + 1] = -2.0;       // eq 4 -b(i+1)
        }
        Matrix matA = new Matrix(mat);
        Matrix xB = new Matrix(xSolVec,4*n);
        Matrix yB = new Matrix(ySolVec,4*n);
        Matrix zB = new Matrix(zSolVec,4*n);
        
        Matrix xAns = matA.solve(xB);
        Matrix yAns = matA.solve(yB);
        Matrix zAns = matA.solve(zB);

        // populate the resulting splines and the length of the curve
        this.polys = new CyclicList<PolyVec>();
        double sum = 0;
        for(int i = 0; i < n ; i++){
            
            PolyVec f_i = new PolyVec(
                new CubicPolynomial(
                    xAns.get(4*i,0),
                     xAns.get(4*i + 1,0),
                      xAns.get(4*i + 2,0),
                       xAns.get(4*i + 3,0)),    // x_i(t)
                new CubicPolynomial(
                    yAns.get(4*i,0),
                     yAns.get(4*i + 1,0),
                      yAns.get(4*i + 2,0),
                       yAns.get(4*i + 3,0)),    // y_i(t)
                new CubicPolynomial(
                    zAns.get(4*i,0),
                     zAns.get(4*i + 1,0),
                      zAns.get(4*i + 2,0),
                       zAns.get(4*i + 3,0)));   // z_i(t)
            polys.add(i, f_i);
            sum += f_i.length();  
        }
        
        length = sum;
        
        
    }

    // probably needs editing
    public Vec eval(double t){
        double floor = Math.floor(t);
        int i = (int) floor;
        return polys.get(i).eval(t-floor);
    }

    public Vec tangent(double t){
        double floor = Math.floor(t);
        int i = (int) floor;
        return polys.get(i).deriv().eval(t-floor);
        
    }

    public Vec normal(double t){

        // f' X f'' X f'
        double floor = Math.floor(t);
        int i = (int) floor;
        return polys.get(i).normal(t - floor);
        
    }

    
    class PolyVec{

        CubicPolynomial x;
        CubicPolynomial y;
        CubicPolynomial z;

        PolyVec(CubicPolynomial x, CubicPolynomial y, CubicPolynomial z){
            this.x=x;
            this.y=y;
            this.z=z;
        }

        PolyVec deriv(){
            return new PolyVec(x.deriv(), y.deriv(), z.deriv());
        }

        Vec eval(double t){
            return new Vec( x.eval(t), y.eval(t),z.eval(t));
        }

        double length(){
            double sum = 0;
            for(double t = 0 ; t < 2000 ; t++){
                sum += eval(t/2000).add(eval((t+1)/2000).neg()).norm();
            }
            return sum;
        }

        Vec normal(double t){
            PolyVec dt = this.deriv();
            PolyVec ddt = dt.deriv();
            Vec normal = dt.eval(t).cross(ddt.eval(t)).cross(dt.eval(t));
            return normal.normalize();

        }

        public String toString(){
            String ret = "x: " + x.toString()
             + "\ny: " + y.toString()
             + "\nz: " + z.toString() + "\n";
             return ret;
        }
    }
    
    class CubicPolynomial{
        
        // a polynomial of the sort P(t)=a*t^3+b*t^2+c*t+d
        double a;
        double b;
        double c;
        double d;

        CubicPolynomial(double a,double b,double c,double d){
            this.a=a;
            this.b=b;
            this.c=c;
            this.d=d;            
        }

        CubicPolynomial deriv(){
            return new CubicPolynomial(0,3*a,2*b,c);
        }
        double eval(double t){
            return (a*t*t*t) + (b*t*t) + (c*t) + (d); 
        }

        public String toString(){
            String ret = "P(t)= " + a  + "t^3 + "
            + b + "t^2 + " + c + "t + " + d;
            return ret; 
        }

    }
}