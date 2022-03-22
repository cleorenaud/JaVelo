/*
 * Author : Roxanne Chevalley
 * Date : 25.02.22
 */
package ch.epfl.javelo;

/**
 * Classe permettant de convertir des nombres entre la représentation Q28.4 et d'autres représentations
 *
 * @author Roxanne Chevalley (339716)
 */
public final class Q28_4 {
    private Q28_4() {
    } //constructeur privé

    /**
     * Retourne la valeur Q28.4 correspondant à l'entier donné
     *
     * @param i (int) : l'entier donné
     * @return (int) la valeur Q28.4 correspondant à l'entier donné (int)
     */
    public static int ofInt(int i) {
        return i << 4;
    }

    /**
     * Retourne la valeur de type double égale à la valeur Q28.4 donnée
     *
     * @param q28_4 (int) : la valeur Q28.4 donnée
     * @return (double) la valeur de type double égale à la valeur Q28.4 donnée (double)
     */
    public static double asDouble(int q28_4) {
        return Math.scalb((double) q28_4, -4);
    }

    /**
     * Retourne la valeur de type float correspondant à la valeur Q28.4 donnée.
     *
     * @param q28_4 (int) : la valeur Q28.4 donnée
     * @return (float) la valeur de type double égale à la valeur Q28.4 donnée (float)
     */
    public static float asFloat(int q28_4) {
        return Math.scalb((float) q28_4, -4);
    }

}
