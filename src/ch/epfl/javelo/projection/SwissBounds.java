package ch.epfl.javelo.projection;

/**
 * Non instanciable contenant des méthodes et des constantes liées aux limites de la Suisse
 * @author Cléo Renaud (325156)
 */
public final class SwissBounds {
    public static final double MIN_E = 2485000;
    public static final double MAX_E = 2834000;
    public static final double MIN_N = 1075000;
    public static final double MAX_N = 1296000;
    public static final double WIDTH = MAX_E - MIN_E;
    public static final double HEIGHT = MAX_N - MIN_N;

    public static boolean containsEN(double e, double n) {
        if ((MIN_E <= e) && (e <= MAX_E) && (MIN_N <= n) && (MAX_N > n)) {
            return true;
        }
        return false;
    }
}
