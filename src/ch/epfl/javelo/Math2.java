package ch.epfl.javelo;

/**
 * Classe offrant des méthodes statiques permettant d'effectuer certains calculs mathématiques
 *
 * @author Roxanne Chevalley (339716)
 */
public final class Math2 {
    /**
     * Constructeur privé pour que la classe ne soit pas instantiable
     */
    private Math2() {
    }

    /**
     * Méthode retournant la partie entière par excès de la division de x par y
     *
     * @param x (int) : le dividende
     * @param y (int) : le diviseur
     * @return (int) la partie entière de la division
     * @throws IllegalArgumentException si y <= 0 ou si x < 0
     */
    public static int ceilDiv(int x, int y) throws IllegalArgumentException {
        Preconditions.checkArgument((x >= 0) && (y > 0));
        return (x + y - 1) / y;
    }

    /**
     * Méthode retournant la coordonnée y du point se trouvant sur la droite passant par (0, y0) et (1, y1),
     * et de coordonnée x donnée
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
     * Méthode retournant v si v appartient à l'intervalle (min, max), min si v est plus petit que min
     * et max si v est plus grand que max
     *
     * @param min (int) : le minimum de l'intervalle
     * @param v   (int) la variable
     * @param max (int) : le maximum de la variable
     * @return (int) : v, min ou max
     * @throws IllegalArgumentException si min est (strictement) supérieur à max
     */
    public static int clamp(int min, int v, int max) throws IllegalArgumentException {
        Preconditions.checkArgument(max > min);
        if (v < min) {
            return min;
        }
        return Math.min(v, max);
    }

    /**
     * Méthode retournant v, si v appartient à l'intervalle (min, max), min si v est plus petit que min et max si v est plus grand
     * que max
     *
     * @param min (double) : le minimum de l'intervalle
     * @param v   (double) la variable
     * @param max (double) : le maximum de l'intervalle
     * @return (double) : v, min ou max
     * @throws IllegalArgumentException si min est (strictement) plus grand que max
     */
    public static double clamp(double min, double v, double max) throws IllegalArgumentException {
        Preconditions.checkArgument(max > min);
        if (v < min) {
            return min;
        }
        return Math.min(v, max);
    }

    /**
     * Méthode retournant le sinus hyperbolique inverse de son argument x
     *
     * @param x (double) : la variable
     * @return (double) : le sinon hyperbolique de x
     */
    public static double asinh(double x) {
        return Math.log(x + Math.sqrt(1 + x * x));
    }

    /**
     * Méthode retournant le produit scalaire entre le vecteur u et v
     *
     * @param uX (double) : composante x du vecteur u
     * @param uY (double) : composante y du vecteur u
     * @param vX (double) : composante x du vecteur v
     * @param vY (double) : composante y du vecteur v
     * @return (double) le produit scalaire
     */
    public static double dotProduct(double uX, double uY, double vX, double vY) {
        return Math.fma(uX, vX, Math.fma(uY, vY, 0));
    }

    /**
     * Méthode retournant la norme d'un vecteur u au carré
     *
     * @param uX (double) : la composante x du vecteur u
     * @param uY (double) : la composante y du vecteur u
     * @return (double) la norme du vecteur u au carré
     */
    public static double squaredNorm(double uX, double uY) {
        return uX * uX + uY * uY;
    }

    /**
     * Méthode retournant la norme d'un vecteur u
     *
     * @param uX (double) : la composante X du vecteur u
     * @param uY (double) : la composante y du vecteur u
     * @return (double) : la norme du vecteur u
     */
    public static double norm(double uX, double uY) {
        return Math.sqrt(squaredNorm(uX, uY));
    }

    /**
     * Méthode retournant la longueur de la projection du vecteur allant du point A au point P
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
