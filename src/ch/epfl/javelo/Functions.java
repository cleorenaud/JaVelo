package ch.epfl.javelo;

import ch.epfl.javelo.projection.SwissBounds;

import java.util.function.DoubleUnaryOperator;

/**
 * Non instanciable, contient des méthodes permettant de créer des objets représentants des fonctions mathémathiques
 * des réels vers les réels
 *
 * @author Cléo Renaud (325156)
 */
public final class Functions {
    private Functions() {
    }

    /**
     * Méthode retournant une fonction constante dont la valeur est toujours y
     *
     * @param y (double) la valeur de la fonction
     * @return (DoubleUnaryOperator) une fonction constante
     */
    public static DoubleUnaryOperator constant(double y) {
        return new Constant(y);
    }

    private static final class Constant implements DoubleUnaryOperator {
        private double constant;

        /**
         * Constructeur de la classe Constant
         *
         * @param y (double) valeur de constante retournée par la fonction
         */
        public Constant(double y) {
            this.constant = y;
        }

        /**
         * Méthode permettant d'appliquer la fonction constante au paramètre donné
         *
         * @param y (double) l'argument
         * @return (double) la constante
         */
        @Override
        public double applyAsDouble(double y) {
            return constant;
        }
    }

    /**
     * Méthode retournant une fonction obtenue pas interpolation linéaire entre les échantillons
     *
     * @param samples (float[]) échantillons espacés régulièrement
     * @param xMax    (double) valeur maximale de la plage dans laquelle on a nos échantillons
     * @return (DoubleUnaryOperator) une fonction qui permet d'interpoler
     */
    public static DoubleUnaryOperator sampled(float[] samples, double xMax) {
        if ((samples.length < 2) || (xMax <= 0)) {
            throw new IllegalArgumentException();
        }
        return new Sampled(samples, xMax);
    }

    private static final class Sampled implements DoubleUnaryOperator {
        private float[] samples;
        private double xMax;
        private int nSamples; //le nombre d'échantillons
        private double interval; // la longueur de l'intervalle entre deux échantillons

        /**
         * Constructeur de la classe Sampled
         *
         * @param samples (float[]) le tableau contenant les échantillons
         * @param xMax    (double) la valeur de x correspondant au dernier échantillon
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
         * @param x (double) le paramètre donné
         * @return (double) la valeur interpolée
         */
        @Override
        public double applyAsDouble(double x) {
            // On cherche à trouver dans quel intervalle est x
            // a*interval <= x < (a+1)*interval
            // Pour trouver a on utilise la division entière de x par interval
            // On utilise l'interpolation en prenant samples[a] et samples[a+1]
            int a = (int) (x / interval);
            double remainder = x % interval;
            // Si x est hors de l'intervalle [0 ; xMax] on retroune la valeur de 0 ou de xMax
            if (x <= 0) {
                return samples[0];
            }
            if (x >= xMax) {
                return samples[nSamples-1];
            }
            return Math2.interpolate(samples[a], samples[a + 1], remainder / interval);
        }
    }

}
