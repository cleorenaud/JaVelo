package ch.epfl.javelo.routing;

import ch.epfl.javelo.Preconditions;

import java.util.Arrays;

/**
 * Classe représentant un calculateur de profil en long
 *
 * @author : Roxanne Chevalley (339716)
 */

public final class ElevationProfileComputer {

    private ElevationProfileComputer(){}//constructeur privé pour que la classe soit non-instanciable

    /**
     * Méthode retournant le profil en long de l'itinéraire route,
     * en garantissant un espacement maximal entre les échantillons du profil.
     *
     * @param route         (Route) : l'itinéraire sur lequel on se base pour le profil en long
     * @param maxStepLength (double) : l'espacement maximal entre les échantillons du profil
     * @return (ElevationProfile) : le profil en long de l'itinéraire route
     * @throws IllegalArgumentException si maxStepLength n'est pas strictement positif
     */
    public static ElevationProfile elevationProfile(Route route, double maxStepLength) throws IllegalArgumentException {
        Preconditions.checkArgument(maxStepLength > 0);
        int nbEch = (int) Math.ceil(route.length() / maxStepLength) + 1;
        double dis = route.length() / (double) (nbEch - 1);

        float[] elevationSamples = new float[nbEch];
        for (int i = 0; i < nbEch; i++) {
            elevationSamples[i] = (float) route.elevationAt(i * dis);
        }

        if (Float.isNaN(elevationSamples[0])) { //gère si les premières valeurs sont NaN ou si elles sont toutes NaN
            int k = 1;
            while (k < nbEch && Float.isNaN(elevationSamples[k])) {
                ++k;
                if (k == nbEch) {
                    k = nbEch - 1;
                    elevationSamples[k] = 0;
                }
            }
            Arrays.fill(elevationSamples, 0, k, elevationSamples[k]);
            if(Float.isNaN(elevationSamples[k])){
                return new ElevationProfile(route.length(), elevationSamples);
            }
        }

        if (Float.isNaN(elevationSamples[nbEch - 1])) { //gère si les dernières valeurs sont NaN
            int k = 2;
            while (k <= nbEch && Float.isNaN(elevationSamples[nbEch - k])) {
                ++k;
                if (k == nbEch) {
                    elevationSamples[nbEch - k] = 0;
                }
            }
            Arrays.fill(elevationSamples, nbEch - k + 1, nbEch, elevationSamples[nbEch - k]);
        }

        for (int i = 1; i < nbEch - 1; i++) { //gère si des valeurs intermédiaires sont NaN
            if (Float.isNaN(elevationSamples[i])) {
                float debut = elevationSamples[i - 1];
                int j = i + 1;
                while (Float.isNaN(elevationSamples[j])) {
                    ++j;
                }
                float fin = elevationSamples[j];
                double delta = (double) (fin - debut) / (j-i + 1);
                for (int k = i; k < j; k++) {
                    elevationSamples[k] = debut + (float) delta * (k - i + 1);
                }
                i = j;
            }
        }
        return new ElevationProfile(route.length(), elevationSamples);

    }

}
