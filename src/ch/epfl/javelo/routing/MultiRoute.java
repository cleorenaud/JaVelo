package ch.epfl.javelo.routing;

import ch.epfl.javelo.Preconditions;
import ch.epfl.javelo.projection.PointCh;

import java.util.ArrayList;
import java.util.List;

/**
 * Classe publique et immuable représentant un itinéraire multiple, c'est-à-dire composé d'une séquence d'itinéraires
 * contigus nommés segments
 *
 * @author Cléo Renaud (325156)
 */
public class MultiRoute implements Route {

    private final List<Route> segments;
    private final double[] positionSegment;

    /**
     * Construit un itinéraire multiple composé des segments donnés
     *
     * @param segments (List<Route>) liste des segments donnés
     * @throws IllegalArgumentException si la liste des segments est nulle
     */
    public MultiRoute(List<Route> segments) throws IllegalArgumentException {
        Preconditions.checkArgument(!segments.isEmpty());
        this.segments = segments;

        //On crée un tableau contenant la position au début de la Route / du segment i dans l'index i
        double[] positionSegment = new double[segments.size() + 1];
        positionSegment[0] = 0;
        for (int i = 1; i < positionSegment.length; i++) {
            positionSegment[i] = positionSegment[i - 1] + segments.get(i - 1).length();
        }
        this.positionSegment = positionSegment;
    }

    /**
     * Méthode retournant l'index du segment de l'itinéraire contenant la position donnée
     *
     * @param position (double) la position donnée (en mètres)
     * @return (int) l'index du segment de l'itinéraire
     */
    @Override
    public int indexOfSegmentAt(double position) {
        int i = 0;
        int index = 0;
        while (positionSegment[i] < position) {
            i++;
            index = i;
        }
        return index;

    }

    /**
     * Méthode retournant la longueur de l'itinéraire
     *
     * @return (double) la longueur de l'itinéraire (en mètres)
     */
    @Override
    public double length() {
        double totalLength = 0;
        for (int i = 0; i < this.segments.size(); i++) {
            totalLength = totalLength + (this.segments.get(i)).length();
        }
        return totalLength;
    }

    /**
     * Méthode retournant la totalité des arêtes de l'itinéraire
     *
     * @return (List < Edge >) la totatlité des arêtes de l'itinéraire
     */
    @Override
    public List<Edge> edges() {
        List<Edge> edges = new ArrayList<>();
        for (int i = 0; i < this.segments.size(); i++) {
            for (int j = 0; j < this.segments.get(i).edges().size(); j++) {
                edges.add(this.segments.get(i).edges().get(j));
            }
        }
        return edges;
    }

    /**
     * Méthode retournant la totalité des points situés aux extrémités des arêtes de l'itinéraire, sans doublons
     *
     * @return (List < PointCh >) la totalité des points situés aux extrémités des arêtes de l'itinéraire
     */
    @Override
    public List<PointCh> points() {
        List<PointCh> points = new ArrayList<>();
        List<Edge> edges = this.edges();
        for (int i = 0; i < edges().size(); i++) {
            points.add(edges.get(i).fromPoint());
        }
        points.add((edges.get(edges().size() - 1)).toPoint());
        return points;
    }

    /**
     * Méthode retournant la point se trouvant à la position donnée le long de l'itinéraire
     *
     * @param position (double) la position donnée le long de l'itinéraire
     * @return (PointCh) le point à la position donnée le long de l'itinéraire
     */
    @Override
    public PointCh pointAt(double position) {
        /// Si la position est négative, elle est considérée comme étant équivalente à zéro
        if (position < 0) {
            position = 0;
        }
        // Si la position est plus grande que la longueur de l'itinéraire elle est considérée comme étant équivalente
        // à la longueur de l'itinéraire
        if (position > this.length()) {
            position = this.length();
        }

        // On isole le segment contenant la position donnée
        Route segment = this.segments.get(indexOfSegmentAt(position));
        // La position à passer en paramètre est la position donnée, moins la position du début du segment sur lequel
        // se trouve le point voulu
        return segment.pointAt(position - positionSegment[indexOfSegmentAt(position)]);
    }

    /**
     * Méthode retournant l'altitude de la position donnée le long de l'itinéraire, qui peut valoir NaN si l'arête
     * contenant cette position n'a pas de profil
     *
     * @param position (double) la position donnée le long de l'itinéraire
     * @return (double) l'altitude a la position donnée le long de l'itinéraire
     */
    @Override
    public double elevationAt(double position) {
// Si la position est négative, elle est considérée comme étant équivalente à zéro
        // Si la position est négative, elle est considérée comme étant équivalente à zéro
        if (position < 0) {
            position = 0;
        }
        // Si la position est plus grande que la longueur de l'itinéraire elle est considérée comme étant équivalente
        // à la longueur de l'itinéraire
        if (position > this.length()) {
            position = this.length();
        }

        // On isole le segment contenant la position donnée
        Route segment = this.segments.get(indexOfSegmentAt(position));
        // La position à passer en paramètre est la position donnée, moins la position du début du segment sur lequel
        // se trouve le point voulu
        return segment.elevationAt(position - positionSegment[indexOfSegmentAt(position)]);    }

    /**
     * Méthode retournant l'identité du nœud appartenant à l'itinéraire et se trouvant le plus proche de la position donnée
     *
     * @param position (double) la position donnée
     * @return (int) l'identité du nœud appartenant à l'itinéraire
     */
    @Override
    public int nodeClosestTo(double position) {
        return 0;
    }

    /**
     * Méthode retournant le point de l'itinéraire se trouvant le plus proche du point de référence donné
     *
     * @param point (PointCh) le point de référence
     * @return (RoutePoint) le point de l'itinéraire le plus proche
     */
    @Override
    public RoutePoint pointClosestTo(PointCh point) {
        return null;
    }
}
