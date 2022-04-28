/*
 * Author : Roxanne Chevalley
 * Date : 08.03.22
 */
package ch.epfl.javelo.routing;

import ch.epfl.javelo.Functions;
import ch.epfl.javelo.Preconditions;

import java.util.Arrays;
import java.util.DoubleSummaryStatistics;
import java.util.List;
import java.util.function.DoubleUnaryOperator;

/**
 * Une classe publique et immuable représentant le profil en long d'un itinéraire simple ou multiple
 *
 * @author : Roxanne Chevalley (339716)
 */

public final class ElevationProfile {

    private final double length;
    private final float[] elevationsSamples;
    private final DoubleUnaryOperator function;
    private final double totalAscent;
    private final double totalDescent;
    private final double maxElevation;
    private final double minElevation;

    /**
     * Construit le profil en long d'un itinéraire de longueur length (en mètres) et dont les échantillons d'altitude,
     * répartis uniformément le long de l'itinéraire, sont contenus dans elevationSamples
     *
     * @param length           (double) : la longueur en mètres
     * @param elevationSamples (float []) : les échantillons d'altitude
     * @throws IllegalArgumentException si la longueur est négative ou nulle, ou si le tableau d'échantillons contient
     *                                  moins de deux éléments
     */
    public ElevationProfile(double length, float[] elevationSamples) throws IllegalArgumentException {
        Preconditions.checkArgument(length > 0 && elevationSamples.length >= 2);
        this.elevationsSamples = Arrays.copyOf(elevationSamples,elevationSamples.length);
        this.length = length;
        this.function = Functions.sampled(elevationsSamples, length);
        this.totalAscent=getTotalAscent();
        this.totalDescent=getTotalDescent();
        this.maxElevation=getStatistics().getMax();
        this.minElevation=getStatistics().getMin();
    }

    /**
     * Méthode retournant la longueur du profil, en mètres
     *
     * @return (double) : la longueur du profil
     */
    public double length() {
        return length;
    }

    /**
     * Méthode retournant l'altitude minimum du profil, en mètres
     *
     * @return (double) : l'altitude minimum du profil
     */
    public double minElevation() {
        return minElevation;
    }

    /**
     * Méthode retournant l'altitude maximum du profil, en mètres
     *
     * @return (double) : l'altitude maximum du profil
     */
    public double maxElevation() {
        return maxElevation;
    }

    /**
     * Méthode retournant le dénivelé positif total du profil, en mètres
     *
     * @return (double) : le dénivelé positif total du profil, en mètres
     */
    public double totalAscent() {
        return totalAscent;
    }


    /**
     * Méthode retournant le dénivelé négatif total du profil, en mètres
     *
     * @return (double) : le dénivelé négatif total du profil, en mètres
     */
    public double totalDescent() {
        return totalDescent;
    }

    /**
     * Méthode retournant l'altitude du profil à la position donnée,
     *
     * @param position (double) : la position donnée
     * @return (double) : l'altitude du profil à la position donnée
     */
    public double elevationAt(double position) {
        return function.applyAsDouble(position);

    }

    /**
     * Méthode qui nous aidera à calculer les statistiques de elevationsSamples
     *
     * @return (DoubleSummaryStatistics) : une instance de la classe DoubleSummaryStatistics avec les valeurs de
     * elevationsSamples
     */
    private DoubleSummaryStatistics getStatistics() {
        DoubleSummaryStatistics s = new DoubleSummaryStatistics();
        for (int i = 0; i < elevationsSamples.length; i++) {
            s.accept(elevationsSamples[i]);
        }
        return s;
    }

    private double getTotalAscent(){
        double totalAscent = 0;
        for (int i = 0; i < elevationsSamples.length - 1; i++) {
            double dif = elevationsSamples[i + 1] - elevationsSamples[i];
            if (dif > 0) {
                totalAscent = totalAscent + dif;
            }

        }
        return totalAscent;

    }

    private double getTotalDescent(){
        double totalDescent = 0;
        for (int i = 0; i < elevationsSamples.length - 1; i++) {
            double dif = elevationsSamples[i + 1] - elevationsSamples[i];
            if (dif < 0) {
                totalDescent = totalDescent + dif;
            }

        }
        return Math.abs(totalDescent);
    }


}
