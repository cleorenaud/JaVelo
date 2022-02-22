package ch.epfl.javelo;

public final class Math2 {
    private Math2(){};

    public static int ceilDiv(int x, int y) throws IllegalArgumentException{
        if(x<0 || y<=0){
            throw new IllegalArgumentException();
        }
        return (x+y-1)/y;
    }

    public static double interpolate(double y0, double y1, double x){
        return Math.fma(y1-y0,x,y0);
    }

    public static int clamp(int min, int v, int max) throws IllegalArgumentException{
        if(min>max){
            throw new IllegalArgumentException();
        }
        if (v<min){
            return min;
        }
        return Math.min(v,max);
    }

    public static double clamp(double min, double v, double max) throws IllegalArgumentException{
        if(min>max){
            throw new IllegalArgumentException();
        }
        if (v<min){
            return min;
        }
       return Math.min(v,max);
    }

    public static double asinh(double x){
        return Math.log(x + Math.sqrt(1 + x*x));
    }

    public static double dotProduct(double uX, double uY, double vX, double vY){
        return Math.fma(uX,vX,Math.fma(uY,vY,0));
    }

    public static double squaredNorm(double uX, double uY){
        return uX*uX+uY*uY;
    }

    public static double norm(double uX, double uY){
        return Math.sqrt(squaredNorm(uX,uY));
    }

    public static double projectionLength(double aX, double aY, double bX, double bY, double pX, double pY){
        return dotProduct(pX-aX,pY-aY,bX-aX,bY-aY)/norm(bX-aX,bY-aY);
    }

}
