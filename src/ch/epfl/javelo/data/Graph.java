/*
 * Author : Roxanne Chevalley
 * Date : 08.03.22
 */
package ch.epfl.javelo.data;

import ch.epfl.javelo.Functions;
import ch.epfl.javelo.projection.PointCh;

import java.io.*;
import java.nio.*;
import java.nio.channels.FileChannel;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.function.DoubleUnaryOperator;
import java.util.stream.Stream;

/**
 * Classe publique et immuable représentant le graphe JaVelo
 *
 * @author : Roxanne Chevalley (339716)
 */

public final class Graph {
    /**
     * Méthode permettant de charger le graphe depuis un répertoire
     *
     * @param basePath (Path) : chemin d'accès du répertoire
     * @return (Graph) : le graphe chargé grâce au répertoire
     * @throws IOException en cas d'erreur d'entrée/sortie, p. ex. si l'un des fichiers attendus n'existe pas.
     */
    public static Graph loadFrom(Path basePath) throws IOException {
        Path attributesPath = basePath.resolve("attributes.bin");
        Path edgesPath = basePath.resolve("edges.bin");
        Path nodesPath = basePath.resolve("nodes.bin");
        Path sectorsPath = basePath.resolve("sectors.bin");
        Path elevationsPath = basePath.resolve("elevations.bin");
        Path profileIdsPath = basePath.resolve("profile_ids.bin");

        IntBuffer nodesBuffer;
        ByteBuffer sectorsBuffer;
        ByteBuffer edgesBuffer;
        IntBuffer profileIds;
        ShortBuffer elevations;
        List<AttributeSet> attributeSets = new ArrayList<AttributeSet>();
        LongBuffer attReader;

        try (FileChannel channel = FileChannel.open(nodesPath)) {
            nodesBuffer = channel.map(FileChannel.MapMode.READ_ONLY, 0, channel.size()).asIntBuffer();
        }
        GraphNodes graphNodes = new GraphNodes(nodesBuffer);

        try (FileChannel channel = FileChannel.open(sectorsPath)) {
            sectorsBuffer = channel.map(FileChannel.MapMode.READ_ONLY, 0, channel.size());
        }
        GraphSectors graphSectors = new GraphSectors(sectorsBuffer);

        try (FileChannel channel = FileChannel.open(edgesPath)) {
            edgesBuffer = channel.map(FileChannel.MapMode.READ_ONLY, 0, channel.size());
        }
        try (FileChannel channel = FileChannel.open(profileIdsPath)) {
            profileIds = channel.map(FileChannel.MapMode.READ_ONLY, 0, channel.size()).asIntBuffer();
        }
        try (FileChannel channel = FileChannel.open(elevationsPath)) {
            elevations = channel.map(FileChannel.MapMode.READ_ONLY, 0, channel.size()).asShortBuffer();
        }
        GraphEdges graphedges = new GraphEdges(edgesBuffer, profileIds, elevations);

        try (FileChannel channel = FileChannel.open(attributesPath)) {
            attReader = channel.map(FileChannel.MapMode.READ_ONLY, 0, channel.size()).asLongBuffer();
        }

        for (int i = 0; i < attReader.capacity(); i++) {
            AttributeSet attributeSet = new AttributeSet(attReader.get(i));
            attributeSets.add(attributeSet);
        }

        return new Graph(graphNodes, graphSectors, graphedges, attributeSets);

    }

    private final GraphNodes nodes;
    private final GraphSectors sectors;
    private final GraphEdges edges;
    private final List<AttributeSet> attributeSets;

    /**
     * Constructeur public de Graph
     *
     * @param nodes         (GraphNodes) : représente l'ensemble de nœuds du graphe
     * @param sectors       (GraphSectors) : représente l'ensemble des secteurs du graphe
     * @param edges         (GraphEdges) : représente l'ensemble des arêtes du graphe
     * @param attributeSets (List<AttributeSet>) : représente l'ensemble des attributs du graphe
     */
    public Graph(GraphNodes nodes, GraphSectors sectors, GraphEdges edges, List<AttributeSet> attributeSets) {
        this.nodes = nodes;
        this.sectors = sectors;
        this.edges = edges;
        this.attributeSets = List.copyOf(attributeSets);
    }

    /**
     * Méthode retournant le nombre total de nœuds dans le graphe
     *
     * @return (int) : le nombre total de nœuds dans le graphe
     */
    public int nodeCount() {
        return nodes.count();
    }

    /**
     * Méthode retournant la position du nœud d'identité donnée
     *
     * @param nodeId (int) : l'identité du nœud
     * @return (PointCh) : la position du nœud d'identité donnée
     */
    public PointCh nodePoint(int nodeId) {
        return new PointCh(nodes.nodeE(nodeId), nodes.nodeN(nodeId));
    }

    /**
     * Méthode retournant le nombre d'arêtes sortant du nœud d'identité donnée
     *
     * @param nodeId (int) : l'identité du nœud
     * @return (int) : le nombre d'arêtes sortant du nœud d'identité donnée
     */
    public int nodeOutDegree(int nodeId) {
        return nodes.outDegree(nodeId);
    }

    /**
     * Méthode retournant l'identité de la edgeIndex-ième arête sortant du nœud d'identité nodeId,
     *
     * @param nodeId    (int) : l'identité du nœud
     * @param edgeIndex (int) : l'index de l'arrête recherchée
     * @return (int) : l'identité de la edgeIndex-ième arrête sortant du nœud d'identité nodeId
     */
    public int nodeOutEdgeId(int nodeId, int edgeIndex) {
        return nodes.edgeId(nodeId, edgeIndex);
    }

    /**
     * Méthode retournant l'identité du nœud se trouvant le plus proche du point donné,
     * à la distance maximale donnée (en mètres), ou -1 si aucun nœud ne correspond à ces critères,
     *
     * @param point          (PointCh) : le point autour duquel on cherche
     * @param searchDistance (double) : la distance maximale à laquelle on cherche
     * @return (int) : le nœud le plus proche du point donné (ou -1)
     */
    public int nodeClosestTo(PointCh point, double searchDistance) {
        double distanceMax = searchDistance * searchDistance;
        int identyMemory = -1;
        List<GraphSectors.Sector> sectorsInArea = sectors.sectorsInArea(point, searchDistance);
        for (GraphSectors.Sector secteur : sectorsInArea) {
            for (int j = secteur.startNodeId(); j < secteur.endNodeId(); j++) {
                PointCh nodePoint = nodePoint(j);
                double distance = point.squaredDistanceTo(nodePoint);
                if (distance <= distanceMax) {
                    distanceMax = distance;
                    identyMemory = j;
                }
            }
        }
        return identyMemory;
    }

    /**
     * Méthode retournant l'identité du nœud destination de l'arête d'identité donnée
     *
     * @param edgeId (int) : l'identité de l'arête
     * @return (int) : l'identité du nœud destination de l'arête d'identité donnée
     */
    public int edgeTargetNodeId(int edgeId) {
        return edges.targetNodeId(edgeId);
    }

    /**
     * Méthode retournant vrai ssi l'arête d'identité donnée va dans le sens contraire
     * de la voie OSM dont elle provient
     *
     * @param edgeId (int) : l'identité de l'arête
     * @return (boolean) : vrai si l'arête d'identité donnée va dans le sens contraire de la voie OSM dont elle provient, faux sinon
     */
    public boolean edgeIsInverted(int edgeId) {
        return edges.isInverted(edgeId);
    }

    /**
     * Méthode retournant l'ensemble des attributs OSM attachés à l'arête d'identité donnée,
     *
     * @param edgeId (int) : l'identité de l'arête
     * @return (AttributeSet) : l'ensemble des attributs OSM attachés à l'arête d'identité donnée
     */
    public AttributeSet edgeAttributes(int edgeId) {
        return attributeSets.get(edges.attributesIndex(edgeId));
    }

    /**
     * Méthode retournant la longueur, en mètres, de l'arête d'identité donnée
     *
     * @param edgeId (int) : l'identité de l'arête
     * @return (double) la longueur, en mètres, de l'arête d'identité donnée
     */
    public double edgeLength(int edgeId) {
        return edges.length(edgeId);
    }

    /**
     * Méthode retournant le dénivelé positif total de l'arête d'identité donnée (en mètres)
     *
     * @param edgeId (int) : l'identité de l'arête
     * @return (double) : le dénivelé positif total de l'arête d'identité donnée
     */
    public double edgeElevationGain(int edgeId) {
        return edges.elevationGain(edgeId);
    }

    /**
     * Méthode retournant le profil en long de l'arête d'identité donnée, sous la forme d'une fonction
     *
     * @param edgeId (int) : l'identité de l'arête
     * @return (DoubleUnaryOperator) : le profil en long de l'arête d'identité donnée, sous la forme d'une fonction
     * si l'arête ne possède pas de profil, alors cette fonction retourne Double.NaN pour n'importe quel argument.
     */
    public DoubleUnaryOperator edgeProfile(int edgeId) {
        if (!edges.hasProfile(edgeId)) {
            return Functions.constant(Double.NaN);
        }
        return Functions.sampled(edges.profileSamples(edgeId), edgeLength(edgeId));
    }
}
