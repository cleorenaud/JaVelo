package ch.epfl.javelo;

import java.util.function.DoubleUnaryOperator;

/**
 * Classe non instantiable, contient des méthodes permettant de créer des objets représentants des fonctions mathématiques
 * des réels vers les réels
 *
 * @author Cléo Renaud (325156)
 */
public final class Functions {

    /**
     * Constructeur privé pour que la classe ne soit pas instantiable
     */
    private Functions() {}

    /**
     * Méthode retournant une fonction constante dont la valeur est toujours y
     *
     * @param y (double) : la valeur de la fonction
     * @return (DoubleUnaryOperator) : une fonction constante
     */
    public static DoubleUnaryOperator constant(double y) {
        return new Constant(y);
    }

    /**
     * Classe imbriquée qui sert à définir la fonction constante
     */
    private static final class Constant implements DoubleUnaryOperator {
        private final double CONSTANT;

        /**
         * Constructeur de la classe Constant
         *
         * @param y (double) : valeur de constante retournée par la fonction
         */
        public Constant(double y) {
            this.CONSTANT = y;
        }

        /**
         * Méthode permettant d'appliquer la fonction constante au paramètre donné
         *
         * @param y (double) : l'argument
         * @return (double) : la constante
         */
        @Override
        public double applyAsDouble(double y) {
            return CONSTANT;
        }
    }

    /**
     * Méthode retournant une fonction obtenue par interpolation linéaire entre les échantillons
     *
     * @param samples (float[]) : échantillons espacés régulièrement
     * @param xMax    (double) : valeur maximale de la plage dans laquelle on a nos échantillons
     * @return (DoubleUnaryOperator) : une fonction qui permet d'interpoler
     * @throws IllegalArgumentException si le tableau samples contient moins de deux éléments, ou si xMax est inférieur
     *                                  ou égal à 0
     */
    public static DoubleUnaryOperator sampled(float[] samples, double xMax) throws IllegalArgumentException {
        Preconditions.checkArgument((samples.length >= 2) && (xMax > 0));
        return new Sampled(samples, xMax);
    }

    /**
     * Classe imbriquée utilisée pour définir la fonction d'interpolation
     */
    private static final class Sampled implements DoubleUnaryOperator {
        private final float[]  samples; // tableau d'échantillons
        private final double xMax; // valeur du dernier x pour lequel on a un échantillon
        private final int nSamples; // le nombre d'échantillons
        private final double interval; // la longueur de l'intervalle entre deux échantillons

        /**
         * Constructeur de la classe Sampled
         *
         * @param samples (float[]) : le tableau contenant les échantillons
         * @param xMax    (double) : la valeur de x correspondant au dernier échantillon
         */
        public Sampled(float[] samples, double xMax) {
            this.samples = samples;
            this.xMax = xMax;
            nSamples = samples.length;
            interval = xMax / (nSamples - 1);
        }

        /**
         * Méthode permettant d'appliquer une interpolation linéaire à un paramètre donné
         *
         * @param x (double) : le paramètre donné
         * @return (double) : la valeur interpolée
         */
        @Override
        public double applyAsDouble(double x) {
            double remainder = x % interval;
            // Si x est hors de l'intervalle [0 ; xMax] on retourne la valeur de 0 ou de xMax
            if (x <= 0) {
                return samples[0];
            }
            if (x >= xMax) {
                return samples[nSamples - 1];
            }
            // On cherche à trouver dans quel intervalle est x
            // a*interval <= x < (a+1)*interval
            // Pour trouver a on utilise la division entière de x par interval
            // On utilise l'interpolation en prenant samples[a] et samples[a+1]
            int a = (int) (x / interval);
            return Math2.interpolate(samples[a], samples[a + 1], remainder / interval);
        }
    }

}
