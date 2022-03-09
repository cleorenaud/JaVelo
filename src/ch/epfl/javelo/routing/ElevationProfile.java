/*
 * Author : Roxanne Chevalley
 * Date : 08.03.22
 */
package ch.epfl.javelo.routing;

import ch.epfl.javelo.Functions;

import java.util.function.DoubleUnaryOperator;

/**
 * Une classe publique et immuable représentant le profil en long d'un itinéraire simple ou multiple
 * @author : Roxanne Chevalley (339716)
 */

public final class ElevationProfile {
    private double length;
    private float[] elevationsSamples;

    /**
     * construit le profil en long d'un itinéraire de longueur length (en mètres)
     * et dont les échantillons d'altitude, répartis uniformément le long de l'itinéraire, sont contenus dans elevationSamples
     * @param length (double) : la longueur en mètres
     * @param elevationSamples (float []) :
     * @throws IllegalArgumentException
     */
    public ElevationProfile(double length, float[] elevationSamples) throws IllegalArgumentException{
        this.elevationsSamples= elevationSamples;
        this.length= length;
        if(length<=0 || elevationSamples.length<2){
            throw new IllegalArgumentException();
        }
    }

    /**
     * méthode qui retourne la longueur du profil, en mètres
     * @return (double) : la longueur du profil
     */
    public double length(){
        return length;
    }

    /**
     * méthode qui retourne l'altitude minimum du profil, en mètres
     * @return (double) : l'altitude minimum du profil
     */
    public double minElevation(){
        double altitudeMin=elevationsSamples[0];
        for (int i = 1; i < elevationsSamples.length; i++) {
            if(elevationsSamples[i]<altitudeMin){
               altitudeMin=elevationsSamples[i];
            }
        }
        return altitudeMin;
    }

    /**
     * méthode qui retourne l'altitude maximum du profil, en mètres
     * @return (double) : l'altitude maximum du profil
     */
    public double maxElevation(){
        double altitudeMax=elevationsSamples[0];
        for (int i = 1; i < elevationsSamples.length; i++) {
            if(elevationsSamples[i]>altitudeMax){
                altitudeMax=elevationsSamples[i];
            }
        }
        return altitudeMax;
    }

    /**
     * méthode qui retourne le dénivelé positif total du profil, en mètres
     * @return (double) : le dénivelé positif total du profil, en mètres
     */
    public double totalAscent(){
        double totalAscent=0;
        for (int i = 0; i < elevationsSamples.length-1; i++) {
            double dif= elevationsSamples[i+1]-elevationsSamples[i];
            if(dif>0){
                totalAscent=totalAscent+dif;
            }

        }
        return totalAscent;
    }

    /**
     * méthode qui retourne le dénivelé négatif total du profil, en mètres
     * @return (double) : le dénivelé négatif total du profil, en mètres
     */
    public double totalDescent(){
        double totalDescent=0;
        for (int i = 0; i < elevationsSamples.length-1; i++) {
            double dif= elevationsSamples[i+1]-elevationsSamples[i];
            if(dif<0){
                totalDescent=totalDescent+dif;
            }

        }
        return totalDescent;
    }

    /**
     * méthode qui retourne l'altitude du profil à la position donnée,
     * @param position (double) : la position donnée
     * @return l'altitude du profil à la position donnée
     */
    public double elevationAt(double position){
        DoubleUnaryOperator function= Functions.sampled(elevationsSamples, length);
        return function.applyAsDouble(position);

    }







}
