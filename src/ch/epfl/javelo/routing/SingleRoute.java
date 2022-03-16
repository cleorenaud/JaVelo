package ch.epfl.javelo.routing;

import ch.epfl.javelo.data.GraphNodes;
import ch.epfl.javelo.projection.PointCh;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Classe publique et immuable représentant un itinéraire simple, c'est-à-dire reliant un point de départ à un point
 * d'arrivée sans point de passage intermédiaire
 *
 * @author Cléo Renaud (325156)
 */
public final class SingleRoute implements Route {

    private final List<Edge> edges;

    /**
     * Constructeur retournant l'itinéraire simple composé des arêtes données
     *
     * @param edges (List<Edge>) les arêtes données
     */
    public SingleRoute(List<Edge> edges) {
        this.edges = edges;
    }

    /**
     * Méthode retournant l'index du segment de l'itinéraire contenant la position donnée,
     * qui vaut toujours 0 dans le cas d'un itinéraire simple,
     *
     * @param position (double) la position donnée (en mètres)
     * @return (int) l'index du segment de l'itinéraire contenant la position donnée
     */
    @Override
    public int indexOfSegmentAt(double position) {
        return 0; // car il s'agit d'une route simple
    }

    /**
     * Méthode retournant la longueur de l'itinéraire
     *
     * @return (double) la longueur de l'itinéraire (en mètres)
     */
    @Override
    public double length() {
        double length = 0;
        for(int i =0; i < this.edges.size(); i++) {
            length = length + edges.get(i).length();
        }
        return length;
    }

    /**
     * Méthode retournant la totalité des arêtes de l'itinéraire
     *
     * @return (List < Edge >) la liste contenant la totalité des arêtes de l'itinéraire
     */
    @Override
    public List<Edge> edges() {
        return this.edges;
    }

    /**
     * Méthode retournant la totalité des points situés aux extrémités des arêtes de l'itinéraire
     *
     * @return (List < Edge >) la liste contenant la totalité des points situés aux extrémités des arêtes de l'itinéraire
     */
    @Override
    public List<PointCh> points() {
        List<PointCh> points = new ArrayList<>();
        for(int i =0; i < this.edges.size(); i++) {
            points.add(edges.get(i).fromPoint());
            points.add(edges.get(i).toPoint());
        }
        return points;
    }

    /**
     * Méthode retournant le point se trouvant à la position donnée le long de l'itinéraire
     *
     * @param position (double) la position donnée le long de l'itinéraire
     * @return (PointCh) le point correspondant à la position donnée
     */
    @Override
    public PointCh pointAt(double position) {
        int node = Arrays.binarySearch(new List[]{this.edges}, position);
        if (node < 0) {
            // calculer la position du point fromPoint de la logne suivante ?
            this.edges.get(Math.abs(node) - 2); // donne le numéro de l'edge contenant la position passée en paramètre
            return null;
        } else {
            return (this.edges.get(node)).fromPoint();
        }
    }

    /**
     * Méthode retournant l'altitude à la position donnée le long de l'itinéraire,
     * qui peut valoir NaN si l'arête contenant cette position n'a pas de profil
     *
     * @param position (double) la position donnée le long de l'itinéraire
     * @return (double) l'altitude à la position donnée le long de l'itinéraire
     */
    @Override
    public double elevationAt(double position) {
        return 0;
    }

    /**
     * Méthode retournant l'identité du nœud appartenant à l'itinéraire et se trouvant le plus proche de la position donnée
     *
     * @param position (double) la position donnée
     * @return (int) l'identité du nœud le plus proche de la position donnée
     */
    @Override
    public int nodeClosestTo(double position) {
        return 0;
    }

    /**
     * Méthode retournant le point de l'itinéraire se trouvant le plus proche du point de référence donné
     *
     * @param point (PointCh) le point de référence
     * @return (RoutePoint) le point de l'itinéraire la plus proche du point de référence
     */
    @Override
    public RoutePoint pointClosestTo(PointCh point) {
        return null;
    }
}
