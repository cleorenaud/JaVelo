/*
 * Author : Roxanne Chevalley
 * Date : 08.03.22
 */
package ch.epfl.javelo.routing;

import ch.epfl.javelo.Functions;

import java.util.DoubleSummaryStatistics;
import java.util.function.DoubleUnaryOperator;

/**
 * Une classe publique et immuable représentant le profil en long d'un itinéraire simple ou multiple
 *
 * @author : Roxanne Chevalley (339716)
 */

public final class ElevationProfile {
    private final double length;
    private final float[] elevationsSamples;

    /**
     * Construit le profil en long d'un itinéraire de longueur length (en mètres)
     * et dont les échantillons d'altitude, répartis uniformément le long de l'itinéraire, sont contenus dans
     * elevationSamples
     *
     * @param length           (double) : la longueur en mètres
     * @param elevationSamples (float []) : les échantillons d'altitude
     * @throws IllegalArgumentException when length <= 0 or elevationSample has less than 2 values
     */
    public ElevationProfile(double length, float[] elevationSamples) throws IllegalArgumentException {
        this.elevationsSamples = elevationSamples;
        this.length = length;
        if (length <= 0 || elevationSamples.length < 2) {
            throw new IllegalArgumentException();
        }
    }

    /**
     * Méthode qui retourne la longueur du profil, en mètres
     *
     * @return (double) : la longueur du profil
     */
    public double length() {
        return length;
    }

    /**
     * Méthode qui nous nous aidera à calculer les statistiques de elevationsSamples
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

    /**
     * Méthode qui retourne l'altitude minimum du profil, en mètres
     *
     * @return (double) : l'altitude minimum du profil
     */
    public double minElevation() {
        return getStatistics().getMin();
    }

    /**
     * Méthode qui retourne l'altitude maximum du profil, en mètres
     *
     * @return (double) : l'altitude maximum du profil
     */
    public double maxElevation() {
        return getStatistics().getMax();
    }

    /**
     * Méthode qui retourne le dénivelé positif total du profil, en mètres
     *
     * @return (double) : le dénivelé positif total du profil, en mètres
     */
    public double totalAscent() {
        double totalAscent = 0;
        for (int i = 0; i < elevationsSamples.length - 1; i++) {
            double dif = elevationsSamples[i + 1] - elevationsSamples[i];
            if (dif > 0) {
                totalAscent = totalAscent + dif;
            }

        }
        return totalAscent;
    }


    /**
     * Méthode qui retourne le dénivelé négatif total du profil, en mètres
     *
     * @return (double) : le dénivelé négatif total du profil, en mètres
     */
    public double totalDescent() {
        double totalDescent = 0;
        for (int i = 0; i < elevationsSamples.length - 1; i++) {
            double dif = elevationsSamples[i + 1] - elevationsSamples[i];
            if (dif < 0) {
                totalDescent = totalDescent + dif;
            }

        }
        return Math.abs(totalDescent);
    }

    /**
     * Méthode qui retourne l'altitude du profil à la position donnée,
     *
     * @param position (double) : la position donnée
     * @return l'altitude du profil à la position donnée
     */
    public double elevationAt(double position) {
        DoubleUnaryOperator function = Functions.sampled(elevationsSamples, length);
        return function.applyAsDouble(position);

    }

}
