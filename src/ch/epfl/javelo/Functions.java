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
        Constant(double y) {}

        @Override
        public double applyAsDouble(double y) {
            return y;
        }
    }

    /**
     * Méthode retournant une fonction obtenue pas interpolation linéaire entre les échantillons
     * @param samples (float[]) échantillons espacés régulièrement
     * @param xMax (double) valeur maximale de la plage dans laquelle on a nos échantillons
     * @return (DoubleUnaryOperator)
     */
    public static DoubleUnaryOperator sampled(float[] samples, double xMax) {
        if ((samples.length < 2) || (xMax <=0)) {
            throw new IllegalArgumentException();
        }
        return new Sampled(samples, xMax);
    }

    private static final class Sampled implements DoubleUnaryOperator {
        Sampled(float[] samples, double xMax) {}


        @Override
        public double applyAsDouble(double operand) {
            return 0;
        }
    }

}
