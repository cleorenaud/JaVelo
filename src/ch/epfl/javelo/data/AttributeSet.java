/*
 * Author : Roxanne Chevalley
 * Date : 25.02.22
 */
package ch.epfl.javelo.data;

import java.util.StringJoiner;

/**
 * Classe qui représente un ensemble d'attributs OpenStreetMap à l'aide d'une séquence de bits
 *
 * @author : Roxanne Chevalley (339716)
 */

public record AttributeSet(long bits) {

    /**
     * le constructeur
     * @param bits la séquence de bits représentant un ensemble d'attributs OpenStreetMap
     * @throws IllegalArgumentException si la valeur passée au constructeur contient un bit à 1 qui ne correspond à aucun attribut valide
     */
    public AttributeSet{//constructeur compact
        if ((bits>>>62)!=0){
            throw new IllegalArgumentException();
        }
    }

    /**
     * Méthode de construction qui retourne un ensemble contenant uniquement les attributs donnés en argument.
     * @param attributes (Attribute) : les attributs contenus par l'ensemble
     * @return un nouvel ensemble d'attributs OpenStreetMap (AttributeSet)
     */
    public static AttributeSet of(Attribute... attributes){
        long bits=0;
        for (int i = 0; i < attributes.length ; i++) {
            long mask= 1L<<attributes[i].ordinal();
            bits = bits + mask;
        }
        return new AttributeSet(bits);
    }

    /**
     * méthode qui retourne vrai si et seulement si l'ensemble récepteur (this) contient l'attribut donné
     * @param attribute (Attribute) : l'attribut "cherché"
     * @return vrai si l'ensemble récepteur (this) contient l'attribut donné et faux sinon (boolean)
     */
    public boolean contains(Attribute attribute){
        int index= attribute.ordinal();
        long newBits= bits()<<63-index;
        newBits= newBits>>>63;
        return (newBits==1);
    }

    /**
     * méthode qui retourne vrai ssi l'intersection de l'ensemble récepteur (this)
     * avec celui passé en argument (that) n'est pas vide.
     * @param that (AttributeSet) : un autre ensemble d'attributs
     * @return vrai si l'intersection de l'ensemble récepteur (this)
     * avec celui passé en argument (that) n'est pas vide, faux sinon
     */
    public boolean intersects(AttributeSet that){
        for (int i=0; i<62 ; ++i){
            long newThis= bits<<(63-i);
            newThis= newThis>>>63;
            long newThat = that.bits()<<(63-i);
            newThat=newThat>>>63;
            if(newThis==newThat){
                return true;
            }
        }
        return false;
    }


    /**
     * Cette méthode redéfinit la méthode toString afin qu'elle retourne
     * une chaîne composée de la représentation textuelle des éléments de l'ensemble
     * @return une chaîne composée de la représentation textuelle des éléments de l'ensemble
     */
    @Override
    public String toString(){
        StringJoiner j = new StringJoiner(",", "{", "}");
        for (int i= 0; i<62; ++i){
            if (this.contains(Attribute.ALL.get(i))){
               String message= Attribute.ALL.get(i).toString();
               j.add(message);
            }
        }
        return j.toString();
    }
}
