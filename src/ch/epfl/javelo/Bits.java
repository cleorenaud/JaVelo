/*
 * Author : Roxanne Chevalley
 * Date : 25.02.22
 */
package ch.epfl.javelo;

/**
 * Une classe (non-instanciable, publique et finale) permettant d'extraire des séquences de bits
 *
 * @author Roxanne Chevalley (339716)
 */
public final class Bits {
    private Bits() {
    } //constructeur privé

    /**
     * Une méthode permettant d'extraire une séquence de bits, interprété comme une valeur signée
     * en complément à deux
     *
     * @param value  (int) : la valeur d'origine
     * @param start  (int) : l'index du premier bit à extraire
     * @param length (int) : le nombre de bit à extraire
     * @return la nouvelle valeur extraite à partir de la séquence de bits (int)
     * @throws IllegalArgumentException si l'intervalle (start, start+length-1) n'est pas totalement
     *                                  incluse dans l'intervalle allant de 0 à 31 (inclus), ou si start ou length est négatif,
     *                                  ou si start est supérieur à 31
     */
    public static int extractSigned(int value, int start, int length) throws IllegalArgumentException {
        if (start + length > 32 || start > 31 || length < 0 || start < 0) {
            throw new IllegalArgumentException();
        }

        int temp = value << (32 - (length + start));
        return temp >> (32 - length);
    }

    /**
     * Une méthode permettant d'extraire une séquence de bits, interprété comme une valeur non-signée
     *
     * @param value  (int) : la valeur d'origine
     * @param start  (int) : l'index du premier bit à extraire
     * @param length (int): le nombre de bit à extraire
     * @return la nouvelle valeur extraite à partir de la séquence de bits (int)
     * @throws IllegalArgumentException si l'intervalle (start, start+length-1) n'est pas totalement
     *                                  incluse dans l'intervalle allant de 0 à 30 (inclus), ou si start ou length est négatif
     */
    public static int extractUnsigned(int value, int start, int length) throws IllegalArgumentException {
        if (start + length > 31 || length < 0 || start < 0) {
            throw new IllegalArgumentException();
        }
        int temp = value << (32 - (start + length));
        return temp >>> (32 - length);
    }
}
