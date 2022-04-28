/*
 * Author : Roxanne Chevalley
 * Date : 25.02.22
 */
package ch.epfl.javelo;

/**
 * Classe non-instantiable, publique et finale permettant d'extraire des séquences de bits
 *
 * @author Roxanne Chevalley (339716)
 */
public final class Bits {

    /**
     * Constructeur privé pour que la classe ne soit pas instantiable
     */
    private Bits() {
    }

    /**
     * Méthode permettant d'extraire du vecteur de 32 bits la plage de length bits commençant au bit d'index start,
     * qu'elle interprète comme une valeur signée en complément à deux
     *
     * @param value  (int) : le vecteur de 32 bits
     * @param start  (int) : l'index du premier bit à extraire
     * @param length (int) : le nombre de bit à extraire
     * @return (int) : la nouvelle valeur extraite à partir de la séquence de bits interprétée comme une valeur signée
     * @throws IllegalArgumentException si l'intervalle (start, start+length-1) n'est pas totalement inclus dans
     * l'intervalle allant de 0 à 31 (inclus), ou si start ou length est négatif, ou si start est supérieur à 31
     */
    public static int extractSigned(int value, int start, int length) throws IllegalArgumentException {
        Preconditions.checkArgument((start + length <= Integer.SIZE) && (start <= Integer.SIZE-1)
                && (length >= 0) && (start >= 0));

        int temp = value << (Integer.SIZE - (length + start));
        return temp >> (Integer.SIZE - length);
    }

    /**
     * Méthode permettant d'extraire d'un vecteur de 32 bits la plage de length bits commençant au bit d'index start,
     * qu'elle interprète comme une valeur non signée
     *
     * @param value  (int) : le vecteur de 32 bits
     * @param start  (int) : l'index du premier bit à extraire
     * @param length (int) : le nombre de bit à extraire
     * @return (int) : la nouvelle valeur extraite à partir de la séquence de bits
     * @throws IllegalArgumentException si l'intervalle (start, start+length-1) n'est pas totalement incluse dans
     * l'intervalle allant de 0 à 30 (inclus), ou si start ou length est négatif
     */
    public static int extractUnsigned(int value, int start, int length) throws IllegalArgumentException {
        Preconditions.checkArgument((start <= Integer.SIZE-1) && (start >= 0) && (length >= 0) &&
                (length <= Integer.SIZE-1) && (start + length <= Integer.SIZE));
        int temp = value << (Integer.SIZE - (start + length));
        return temp >>> (Integer.SIZE - length);
    }

}
