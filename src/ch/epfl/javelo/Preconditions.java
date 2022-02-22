package ch.epfl.javelo;

public final class Preconditions {
<<<<<<< Updated upstream
    private Preconditions() {}

    void checkArgument(boolean shouldBeTrue) {
        if (!shouldBeTrue) {
            //leve l'exception IllegalArgumentException
        }
    }

}
=======
    private Preconditions() {
    }

    void checkArgument(boolean shouldBeTrue) {
        if (!shouldBeTrue) {
            throw new IllegalArgumentException();
        }
    }

}
>>>>>>> Stashed changes
