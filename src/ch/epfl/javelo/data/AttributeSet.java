/*
 * Author : Roxanne Chevalley
 * Date : 25.02.22
 */
package ch.epfl.javelo.data;

import ch.epfl.javelo.Preconditions;
import org.w3c.dom.Attr;

import java.util.StringJoiner;

/**
 * Enregistrement qui représente un ensemble d'attributs OpenStreetMap à l'aide d'une séquence de bits
 *
 * @param bits (long) la séquence de bits représentant un ensemble d'attributs OpenStreetMap
 * @author : Roxanne Chevalley (339716)
 */

public record AttributeSet(long bits) {

    /**
     * Le constructeur
     *
     * @param bits la séquence de bits représentant un ensemble d'attributs OpenStreetMap
     * @throws IllegalArgumentException si la valeur passée au constructeur contient un bit à 1 qui ne correspond à
     *                                  aucun attribut valide
     */
    public AttributeSet { //constructeur compact
        Preconditions.checkArgument((bits >>> Attribute.COUNT) == 0);
    }

    /**
     * Méthode de construction qui retourne un ensemble contenant uniquement les attributs donnés en argument.
     *
     * @param attributes (Attribute) : les attributs contenus par l'ensemble
     * @return (AttributeSet) : un nouvel ensemble d'attributs OpenStreetMap
     */
    public static AttributeSet of(Attribute... attributes) {
        long bits = 0;
        for ( Attribute i : attributes) {
            long mask = 1L << i.ordinal();
            bits = bits + mask;
        }
        return new AttributeSet(bits);
    }

    /**
     * Méthode qui retourne vrai si et seulement si l'ensemble récepteur (this) contient l'attribut donné
     *
     * @param attribute (Attribute) : l'attribut "cherché"
     * @return (boolean) vrai si l'ensemble récepteur (this) contient l'attribut donné et faux sinon
     */
    public boolean contains(Attribute attribute) {
        int decalage = Attribute.COUNT + 1;
        int index = attribute.ordinal();
        long newBits = bits() << decalage - index;
        newBits = newBits >>> decalage;
        return (newBits == 1);
    }

    /**
     * Méthode qui retourne vrai ssi l'intersection de l'ensemble récepteur (this)
     * avec celui passé en argument (that) n'est pas vide.
     *
     * @param that (AttributeSet) : un autre ensemble d'attributs
     * @return (boolean) vrai si l'intersection de l'ensemble récepteur (this)
     * avec celui passé en argument (that) n'est pas vide, faux sinon
     */
    public boolean intersects(AttributeSet that) {
        for (int i = 0; i < Attribute.COUNT ; ++i) {
            int decalage= Attribute.COUNT + 1;
            long newThis = bits << (decalage - i);
            newThis = newThis >>> decalage;
            long newThat = that.bits() << (decalage - i);
            newThat = newThat >>> decalage;
            if (newThis == 1 && newThat == 1) {
                return true;
            }
        }
        return false;
    }


    /**
     * Cette méthode redéfinit la méthode toString afin qu'elle retourne
     * une chaîne composée de la représentation textuelle des éléments de l'ensemble
     *
     * @return (String) une chaîne composée de la représentation textuelle des éléments de l'ensemble
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
