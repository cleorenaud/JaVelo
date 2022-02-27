/*
 * Author : Roxanne Chevalley
 * Date : 25.02.22
 */
package ch.epfl.javelo.data;

/**
 * Classe qui représente un ensemble d'attributs OpenStreetMap à l'aide d'une séquence de bits
 *
 * @author : Roxanne Chevalley (339716)
 */

public record AttributeSet(long bits) {
    public AttributeSet {
        if ((bits>>>62)!=0){
            throw new IllegalArgumentException();
        }
    }

    public static AttributeSet of(Attribute... attributes){
        long bits=0;
        for (int i = 0; i < attributes.length ; i++) {
            long mask= 1L<<attributes[i].ordinal();
            bits = bits + mask;
        }
        return new AttributeSet(bits);
    }

    public boolean contains(Attribute attribute){
        int index= attribute.ordinal();
        long newBits= bits()<<63-index;
        newBits= newBits>>>63;
        return (newBits==1);

    }
}
