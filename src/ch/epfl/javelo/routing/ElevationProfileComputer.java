package ch.epfl.javelo.routing;

import java.util.Arrays;
import java.util.List;

/**
 * Classe représentant un calculateur de profil en long
 *
 * @author : Roxanne Chevalley (339716)
 */

public final class ElevationProfileComputer {
    /**
     * Méthode qui retourne le profil en long de l'itinéraire route,
     * en garantissant un espacement maximal entre les échantillons du profil.
     *
     * @param route         (Route) : l'itinéraire sur lequel on se base pour le profil en long
     * @param maxStepLength (double) : l'espacement maximal entre les échantillons du profil
     * @return le profil en long de l'itinéraire route
     * @throws IllegalArgumentException si maxStepLength n'est pas strictement positif
     */
    public static ElevationProfile elevationProfile(Route route, double maxStepLength) throws IllegalArgumentException {
        if (maxStepLength <= 0) {
            throw new IllegalArgumentException();
        }
        List<Edge> edges = route.edges();
        int nbEch = (int) Math.ceil(route.length() / maxStepLength) + 1;
        double dis = route.length() / (double) nbEch;

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
            Arrays.fill(elevationSamples, 0, k - 1, elevationSamples[k]);
        }

        if (Float.isNaN(elevationSamples[nbEch - 1])) { //gère si les dernières valeurs sont NaN
            int k = 2;
            while (k <= nbEch && Float.isNaN(elevationSamples[nbEch - k])) {
                ++k;
                if (k == nbEch) {
                    elevationSamples[nbEch - k] = 0;
                }
            }
            Arrays.fill(elevationSamples, nbEch - k + 1, nbEch - 1, elevationSamples[nbEch - k]);
        }

        for (int i = 1; i < nbEch - 1; i++) { //gère si des valeurs intermédiaires sont NaN
            if (Float.isNaN(elevationSamples[i])) {
                float debut = elevationSamples[i - 1];
                int j = i + 1;
                int compteur = 1;
                while (Float.isNaN(elevationSamples[j])) {
                    ++j;
                    ++compteur;
                }
                float fin = elevationSamples[j];
                double delta = (double) (fin - debut) / (compteur + 1);
                for (int k = i; k < j; k++) {
                    elevationSamples[i] = debut + (float) delta * k;
                }
                i = j + 1;
            }
        }
        return new ElevationProfile(route.length(), elevationSamples);

    }
}
