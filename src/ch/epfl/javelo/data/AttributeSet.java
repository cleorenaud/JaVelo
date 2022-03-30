/*
 * Author : Roxanne Chevalley
 * Date : 25.02.22
 */
package ch.epfl.javelo.data;

import ch.epfl.javelo.Preconditions;

import java.util.StringJoiner;

/**
 * Enregistrement représentant un ensemble d'attributs OpenStreetMap à l'aide d'une séquence de bits
 *
 * @param bits (long) : la séquence de bits représentant un ensemble d'attributs OpenStreetMap
 * @author : Roxanne Chevalley (339716)
 */

public record AttributeSet(long bits) {

    /**
     * Constructeur de la classe
     *
     * @param bits (long) : la séquence de bits représentant un ensemble d'attributs OpenStreetMap
     * @throws IllegalArgumentException si la valeur passée au constructeur contient un bit à 1 qui ne correspond à
     *                                  aucun attribut valide
     */
    public AttributeSet {
        Preconditions.checkArgument((bits >>> Attribute.COUNT) == 0);
    }

    /**
     * Méthode de construction retournant un ensemble contenant uniquement les attributs donnés en argument
     *
     * @param attributes (Attribute) : les attributs contenus par l'ensemble
     * @return (AttributeSet) : un nouvel ensemble d'attributs OpenStreetMap
     */
    public static AttributeSet of(Attribute... attributes) {
        long bits = 0;
        for ( Attribute i : attributes) {
            long mask = 1L << i.ordinal();
            bits = bits | mask;
        }
        return new AttributeSet(bits);
    }

    /**
     * Méthode retournant vrai si et seulement si l'ensemble récepteur (this) contient l'attribut donné
     *
     * @param attribute (Attribute) : l'attribut "cherché"
     * @return (boolean) : vrai si l'ensemble récepteur (this) contient l'attribut donné et faux sinon
     */
    public boolean contains(Attribute attribute) {
        long bits2= 1L << attribute.ordinal();
        return ((bits() & bits2) != 0);
    }

    /**
     * Méthode retournant vrai ssi l'intersection de l'ensemble récepteur (this) avec celui passé en argument (that)
     * n'est pas vide.
     *
     * @param that (AttributeSet) : un autre ensemble d'attributs
     * @return (boolean) : vrai si l'intersection de l'ensemble récepteur (this) avec celui passé en argument (that)
     * n'est pas vide, faux sinon
     */
    public boolean intersects(AttributeSet that) {
        return ((this.bits() & that.bits()) != 0);
    }


    /**
     * Redéfinition de la méthode toString afin qu'elle retourne une chaîne composée de la représentation textuelle
     * des éléments de l'ensemble entourés d'accolades et séparées de virgules
     *
     * @return (String) : une chaîne composée de la représentation textuelle des éléments de l'ensemble
     */
    @Override
    public String toString() {
        StringJoiner j = new StringJoiner(",", "{", "}");
        for (int i = 0; i < Attribute.COUNT; ++i) {
            if (this.contains(Attribute.ALL.get(i))) {
                String message = Attribute.ALL.get(i).toString();
                j.add(message);
            }
        }
        return j.toString();
    }
}
