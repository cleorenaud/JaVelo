package ch.epfl.javelo;

/**
 * Non instanciable
 * @author Cléo Renaud (325156)
 */
public final class Preconditions {
    private Preconditions() {
    }

    public static void checkArgument(boolean shouldBeTrue) {
        if (!shouldBeTrue) {
            throw new IllegalArgumentException();
        }
    }

}

