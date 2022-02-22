package ch.epfl.javelo;

public final class Preconditions {
    private Preconditions() {}

    void checkArgument(boolean shouldBeTrue) {
        if (!shouldBeTrue) {
            //leve l'exception IllegalArgumentException
        }
    }

}
