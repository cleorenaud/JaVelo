package ch.epfl.javelo.projection;

/**
 * Non instanciable, offre des méthodes permettant de convertir entre les coordonnées WGS 84 et les coordonnées Suisse
 * @author Cléo Renaud (325156)
 */
public final class Ch1903 {
    public static double e(double lon, double lat) {
        lon = Math.toDegrees(lon);
        lat = Math.toDegrees(lat);
        lon = Math.pow(10, -4) * (3600 * lon - 26782.5);
        lat = Math.pow(10, -4) * (3600 * lat - 169028.66);
        double eastCoordinate = 2600072.37 + 211455.93 * lon - 10938.51 * lat * lon - 0.36 * lon * Math.pow(lat, 2) - 44.54 * Math.pow(lon, 3);
        return eastCoordinate;
    }

    public static double n(double lon, double lat) {
        lon = Math.toDegrees(lon);
        lat = Math.toDegrees(lat);
        lon = Math.pow(10, -4) * (3600 * lon - 26782.5);
        lat = Math.pow(10, -4) * (3600 * lat - 169028.66);
        double northCoordinate = 1200147.07 + 308807.95 * lat + 3745.25 * Math.pow(lon, 2) + 76.63 * Math.pow(lat, 2) - 194.56 * lat * Math.pow(lon, 2) + 119.79 * Math.pow(lat, 3);
        return northCoordinate;
    }

    public static double lon(double e, double n) {
        e = Math.pow(10, -6) * (e - 2600000);
        n = Math.pow(10, -6) * (n - 1200000);
        double longitude = 2.6779094 + 4.728982 * e + 0.791484 * e * n + 0.1306 * e * Math.pow(n, 2) - 0.0436 * Math.pow(e, 3);
        longitude = longitude * 100 / 36;
        longitude = Math.toRadians(longitude);
        return longitude;
    }

    public static double lat(double e, double n) {
        e = Math.pow(10, -6) * (e - 2600000);
        n = Math.pow(10, -6) * (n - 1200000);
        double latitude = 16.9023892 + 3.238272 * n - 0.270978 * Math.pow(e, 2) - 0.002528 * Math.pow(n, 2) - 0.0447 * Math.pow(e, 2) * n - 0.0140 * Math.pow(n, 3);
        latitude = latitude * 100 / 36;
        latitude = Math.toRadians(latitude);
         return latitude;
    }
}
