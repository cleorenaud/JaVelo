package ch.epfl.javelo;

public final class Math2 {
    /**
     * constructeur privé de la classe Math2
     */
    private Math2(){};

    /**
     * méthode qui permet d'avoir la division x/y arrondi à l'entier supérier
     * @param x (int) : le dividende
     * @param y (int) : le diviseur
     * @return le résultat de la division
     * @throws IllegalArgumentException si y<=0 ou si x<0
     */
    public static int ceilDiv(int x, int y) throws IllegalArgumentException{
        if(x<0 || y<=0){
            throw new IllegalArgumentException();
        }
        return (x+y-1)/y;
    }

    /**
     * méthode qui retourne la coordonée y de la droite passant par (y0,0) et (y1,1) et ayant x comme coordonnée
     * @param y0 (double) : l'ordonnée au point 0
     * @param y1 (double) : l'ordonnée au point 1
     * @param x (double) : la coordonée d'abscisse
     * @return
     */
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
