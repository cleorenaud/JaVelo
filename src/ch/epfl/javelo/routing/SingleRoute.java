package ch.epfl.javelo.routing;

import ch.epfl.javelo.Math2;
import ch.epfl.javelo.Preconditions;
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

    private final List<Edge> edges; // liste des arêtes dela route
    private final double[] tableau; // tableau contenant la position au debut de la node i dans l'index i

    /**
     * Constructeur retournant l'itinéraire simple composé des arêtes données
     *
     * @param edges (List<Edge>) les arêtes données
     * @throws IllegalArgumentException si la liste d'arêtes est vide
     */
    public SingleRoute(List<Edge> edges) throws IllegalArgumentException{
        Preconditions.checkArgument(!edges.isEmpty());
        this.edges = List.copyOf(edges);

        // On crée un tableau contenant la position au debut de la node i dans l'index i
        double[] tableau = new double[edges.size() + 1];
        tableau[0] = 0; // le noeud 0 est a une position 0
        for (int i = 1; i < tableau.length; i++) {
            tableau[i] = tableau[i - 1] + edges.get(i - 1).length();
        }
        this.tableau = tableau;
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
        for (int i = 0; i < this.edges.size(); i++) {
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
        return List.copyOf(edges);
    }

    /**
     * Méthode retournant la totalité des points situés aux extrémités des arêtes de l'itinéraire
     *
     * @return (List < Edge >) la liste contenant la totalité des points situés aux extrémités des arêtes de l'itinéraire
     */
    @Override
    public List<PointCh> points() {
        List<PointCh> points = new ArrayList<>();
        points.add(edges.get(0).fromPoint());
        for (int i = 0; i < this.edges.size(); i++) {
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
        // Une position supérieure à la longueur de l'itinéraire est considérée comme équivalente à cette longueur
        // Une position inférieure à la longueur de l'itinéraire est considérée comme équivalente à zéro
        position = Math2.clamp(0, position, this.length());

        int node = Arrays.binarySearch(tableau, position);

        if (node < 0) {
            // Valeur négative donc = -(point d'insertion) -1
            int newNode = Math.abs(node) - 2;
            double posOnEdge = position - tableau[newNode];
            return this.edges.get(newNode).pointAt(posOnEdge);
        } else if (node == edges.size()) {
            return (this.edges.get(node - 1)).toPoint();
        } else {
            // Valeur positive donc la position correspond à un point
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
        // Une position supérieure à la longueur de l'itinéraire est considérée comme équivalente à cette longueur
        // Une position inférieure à la longueur de l'itinéraire est considérée comme équivalente à zéro
        position = Math2.clamp(0, position, this.length());

        int node = Arrays.binarySearch(tableau, position);

        if (node < 0) {
            // Valeur négative donc = -(point d'insertion) -1
            int newNode = Math.abs(node) - 2;
            double posOnEdge = position - tableau[newNode];
            return this.edges.get(newNode).elevationAt(posOnEdge);

            //les conditions ci-dessous permettent à un nombre valide de toujours être prioritaire par rapport à un NaN
        } else if (node != 0 && (node == edges.size() || Float.isNaN((float) (this.edges.get(node)).elevationAt(0)))) {
            Edge tempEdge = this.edges.get(node - 1);
            return (tempEdge).elevationAt(tempEdge.length());

        } else {
            return (this.edges.get(node)).elevationAt(0);
        }
    }

    /**
     * Méthode retournant l'identité du nœud appartenant à l'itinéraire et se trouvant le plus proche de la position donnée
     *
     * @param position (double) la position donnée
     * @return (int) l'identité du nœud le plus proche de la position donnée
     */
    @Override
    public int nodeClosestTo(double position) {
        position = Math2.clamp(0, position, this.length());

        int node = Arrays.binarySearch(tableau, position);

        if (node < 0) {
            int newNode = Math.abs(node) - 2;
            double posOnEdge = position - tableau[newNode];
            
            if (posOnEdge <= (edges.get(newNode).length()) / 2) {
                return edges.get(newNode).fromNodeId();
            } else {
                return edges.get(newNode).toNodeId();
            }

        } else if (node == edges.size()) {
            return this.edges.get(node - 1).toNodeId();

        } else {
            return this.edges.get(node).fromNodeId();
        }
    }

    /**
     * Méthode retournant le point de l'itinéraire se trouvant le plus proche du point de référence donné
     *
     * @param point (PointCh) le point de référence
     * @return (RoutePoint) le point de l'itinéraire la plus proche du point de référence
     */
    @Override
    public RoutePoint pointClosestTo(PointCh point) {
        double position = this.edges.get(0).positionClosestTo(point);
        double lengthBefore = 0;
        position = Math2.clamp(0,position, this.edges.get(0).length());
        double positionGen = position;
        // On initialise la distance comme étant la différence de point, et de sa projection sur l'arête 0
        double distance = point.distanceTo(this.edges.get(0).pointAt(position));
        // On initialise le numéro de l'arrête contenant le point le plus proche de point
        int edgeCloser = 0;
        // On parcourt la liste des arêtes restantes, et pour chacune d'entre elles on projette point
        // Si la distance entre point et sa projection est plus petite que l'ancienne valeur de distance, alors on stocke
        // la nouvelle valeur de distance et on stocke la position sur l'arrête de la projection de point dans position
        // edgeCloser permet de garder en mémoire l'indice de l'arrête contenant la meilleure projection de point
        for (int i = 1; i < this.edges.size(); i++) {
            lengthBefore = lengthBefore + edges.get(i - 1).length();
            double position2 = this.edges.get(i).positionClosestTo(point);
            position2 = Math2.clamp(0, position2, this.edges.get(i).length());
            double distance2 = point.distanceTo(this.edges.get(i).pointAt(position2));
            if (distance2 < distance) {
                distance = distance2;
                position = position2;
                edgeCloser = i;
                positionGen = position + lengthBefore;
            }
        }
        return new RoutePoint(this.edges.get(edgeCloser).pointAt(position), positionGen, distance);
    }
}
