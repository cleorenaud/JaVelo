package ch.epfl.javelo;

/**
 * Une classe permettant certains calculs, non instantiable
 *
 * @author Roxanne Chevalley (339716)
 */
public final class Math2 {
    /**
     * Constructeur privé de la classe Math2
     */
    private Math2() {
    }

    /**
     * Méthode qui permet d'avoir la division x/y arrondi à l'entier supérieur
     *
     * @param x (int) : le dividende
     * @param y (int) : le diviseur
     * @return (int) le résultat de la division
     * @throws IllegalArgumentException si y <= 0 ou si x < 0
     */
    public static int ceilDiv(int x, int y) throws IllegalArgumentException {
        Preconditions.checkArgument((x >= 0) && (y > 0));
        return (x + y - 1) / y;
    }

    /**
     * Méthode qui retourne la coordonnée y de la droite passant par (y0,0) et (y1,1) et ayant x comme coordonnée
     *
     * @param y0 (double) : l'ordonnée au point 0
     * @param y1 (double) : l'ordonnée au point 1
     * @param x  (double) : la coordonnée d'abscisse
     * @return y (double) : la coordonnée d'ordonnée
     */
    public static double interpolate(double y0, double y1, double x) {
        return Math.fma(y1 - y0, x, y0);
    }

    /**
     * Retourne v, si v appartient à l'intervalle (min, max), min si v est plus petit que min et max si v est plus grand
     * que max
     *
     * @param min (int) : le minimum de l'intervalle
     * @param v   (int) la variable
     * @param max (int) : le maximum de la variable
     * @return (int) : v, min ou max
     * @throws IllegalArgumentException si min est plus grand que max
     */
    public static int clamp(int min, int v, int max) throws IllegalArgumentException {
        Preconditions.checkArgument(max > min);
        if (v < min) {
            return min;
        }
        return Math.min(v, max);
    }

    /**
     * Retourne v, si v appartient à l'intervalle (min, max), min si v est plus petit que min et max si v est plus grand
     * que max
     *
     * @param min (double) : le minimum de l'intervalle
     * @param v   (double) la variable
     * @param max (double) : le maximum de l'intervalle
     * @return (double) : v, min ou max
     * @throws IllegalArgumentException si min est plus grand que max
     */
    public static double clamp(double min, double v, double max) throws IllegalArgumentException {
        Preconditions.checkArgument(max > min);
        if (v < min) {
            return min;
        }
        return Math.min(v, max);
    }

    /**
     * Fonction qui calcule le sinus hyperbolique inverse d'une variable
     *
     * @param x (double) : la variable
     * @return (double) : l'asinh de x (double)
     */
    public static double asinh(double x) {
        return Math.log(x + Math.sqrt(1 + x * x));
    }

    /**
     * Retourne le produit scalaire entre le vecteur u et v
     *
     * @param uX (double) : coordonnée x du vecteur u
     * @param uY (double) : coordonnée y du vecteur u
     * @param vX (double) : coordonnée x du vecteur v
     * @param vY (double) : coordonnée y du vecteur v
     * @return (double) le produit scalaire
     */
    public static double dotProduct(double uX, double uY, double vX, double vY) {
        return Math.fma(uX, vX, Math.fma(uY, vY, 0));
    }

    /**
     * Retourne la norme d'un vecteur u au carré
     *
     * @param uX (double) : la coordonnée X du vecteur u
     * @param uY (double) : la coordonnée y du vecteur u
     * @return (double) la norme du vecteur u au carré
     */
    public static double squaredNorm(double uX, double uY) {
        return uX * uX + uY * uY;
    }

    /**
     * Retourne la norme d'un vecteur u
     *
     * @param uX (double) : la coordonnée X du vecteur u
     * @param uY (double) : la coordonnée y du vecteur u
     * @return (double) : la norme du vecteur u
     */
    public static double norm(double uX, double uY) {
        return Math.sqrt(squaredNorm(uX, uY));
    }

    /**
     * Retourne la longueur de la projection du vecteur allant du point A au point P
     * sur le vecteur allant du point A au point B
     *
     * @param aX (double) : la coordonnée x du point A
     * @param aY (double) : la coordonnée y du point A
     * @param bX (double) : la coordonnée x du point B
     * @param bY (double) : la coordonnée y du point B
     * @param pX (double) : la coordonnée x du point P
     * @param pY (double) : la coordonnée y du point P
     * @return (double) : la longueur de ladite projection
     */
    public static double projectionLength(double aX, double aY, double bX, double bY, double pX, double pY) {
        return dotProduct(pX - aX, pY - aY, bX - aX, bY - aY) / norm(bX - aX, bY - aY);
    }

}
