package ch.epfl.javelo.projection;

/**
 * Non instantiable contenant des méthodes et des constantes liées aux limites de la Suisse
 *
 * @author Cléo Renaud (325156)
 */
public final class SwissBounds {

    /**
     * Constantes pour stocker les coordonnées min et max, nord et sud de la Suisse.
     */
    public static final double MIN_E = 2485000;
    public static final double MAX_E = 2834000;
    public static final double MIN_N = 1075000;
    public static final double MAX_N = 1296000;
    public static final double WIDTH = MAX_E - MIN_E;
    public static final double HEIGHT = MAX_N - MIN_N;

    /**
     * Constructeur privé pour que la classe ne soit pas instantiable
     */
    private SwissBounds() {}

    /**
     * Méthode permettant de vérifier si les coordonnées d'un point donné sont bien dans les limites de la Suisse
     *
     * @param e (double) : coordonnée est du point donné
     * @param n (double) : coordonnée nord du point donné
     * @return (boolean) : true si les coordonnées du point sont bien dans les limites de la Suisse, et false autrement
     */
    public static boolean containsEN(double e, double n) {
        return (MIN_E <= e) && (e <= MAX_E) && (MIN_N <= n) && (MAX_N >= n);

    }

}
