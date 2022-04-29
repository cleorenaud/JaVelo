package ch.epfl.javelo;

/**
 * Non instantiable
 *
 * @author Cléo Renaud (325156)
 */
public final class Preconditions {

    /**
     * Constructeur privé pour que la classe ne soit pas instantiable
     */
    private Preconditions() {
    }

    /**
     * Méthode permettant de lever l'exception IllegalArgumentException si son argument est faux
     * et ne fait rien sinon
     *
     * @param shouldBeTrue (boolean) : l'expression passée en argument qui doit être vérifiée
     * @throws IllegalArgumentException : si l'argument passé en paramètre est faux
     */
    public static void checkArgument(boolean shouldBeTrue) {
        if (!shouldBeTrue) {
            throw new IllegalArgumentException();
        }
    }

}

