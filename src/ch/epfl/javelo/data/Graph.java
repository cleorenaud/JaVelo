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
     * @param basePath (Path) :
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
     * Méthode qui retourne le nombre total de nœuds dans le graphe
     *
     * @return (int) : le nombre total de nœuds dans le graphe
     */
    public int nodeCount() {
        return nodes.count();
    }

    /**
     * Méthode qui retourne la position du nœud d'identité donnée
     *
     * @param nodeId (int) : l'identité du nœud
     * @return la position du nœud d'identité donnée
     */
    public PointCh nodePoint(int nodeId) {
        return new PointCh(nodes.nodeE(nodeId), nodes.nodeN(nodeId));
    }

    /**
     * Méthode qui retourne le nombre d'arêtes sortant du nœud d'identité donnée
     *
     * @param nodeId (int) : l'identité du nœud
     * @return le nombre d'arêtes sortant du nœud d'identité donnée
     */
    public int nodeOutDegree(int nodeId) {
        return nodes.outDegree(nodeId);
    }

    /**
     * Méthode qui retourne l'identité de la edgeIndex-ième arête sortant du nœud d'identité nodeId,
     *
     * @param nodeId    (int) : l'identité du nœud
     * @param edgeIndex (int) : l'index de l'arrête recherchée
     * @return (int) : l'identité de la edgeIndex-ième arrête sortant du nœud d'identité nodeId
     */
    public int nodeOutEdgeId(int nodeId, int edgeIndex) {
        return nodes.edgeId(nodeId, edgeIndex);
    }

    /**
     * Méthode qui retourne l'identité du nœud se trouvant le plus proche du point donné,
     * à la distance maximale donnée (en mètres), ou -1 si aucun nœud ne correspond à ces critères,
     *
     * @param point          (PointCh) : le point autour duquel on cherche
     * @param searchDistance (double) : la distance maximale à laquelle on cherche
     * @return (int) : le noeud le plus proche du point donné (ou -1)
     */
    public int nodeClosestTo(PointCh point, double searchDistance) {
        double distanceMax = searchDistance * searchDistance;
        int identyMemory = -1;
        for (int i = 0; i < nodeCount(); i++) {
            PointCh nodePoint = nodePoint(i);
            double distance = point.squaredDistanceTo(nodePoint);
            if (distance <= distanceMax) {
                distanceMax = distance;
                identyMemory = i;
            }
        }
        return identyMemory;
    }

    /**
     * Méthode qui retourne l'identité du nœud destination de l'arête d'identité donnée
     *
     * @param edgeId (int) : l'identité de l'arête
     * @return (int) : l'identité du nœud destination de l'arête d'identité donnée
     */
    public int edgeTargetNodeId(int edgeId) {
        return edges.targetNodeId(edgeId);
    }

    /**
     * Méthode qui retourne vrai ssi l'arête d'identité donnée va dans le sens contraire
     * de la voie OSM dont elle provient
     *
     * @param edgeId (int) : l'identité de l'arête
     * @return (boolean) : vrai si l'arête d'identité donnée va dans le sens contraire de la voie OSM dont elle provient, faux sinon
     */
    public boolean edgeIsInverted(int edgeId) {
        return edges.isInverted(edgeId);
    }

    /**
     * Méthode qui retourne l'ensemble des attributs OSM attachés à l'arête d'identité donnée,
     *
     * @param edgeId (int) : l'identité de l'arête
     * @return (AttributeSet) : l'ensemble des attributs OSM attachés à l'arête d'identité donnée
     */
    public AttributeSet edgeAttributes(int edgeId) {
        return attributeSets.get(edges.attributesIndex(edgeId));
    }

    /**
     * Méthode qui retourne la longueur, en mètres, de l'arête d'identité donnée
     *
     * @param edgeId (int) : l'identité de l'arête
     * @return la longueur, en mètres, de l'arête d'identité donnée
     */
    public double edgeLength(int edgeId) {
        return edges.length(edgeId);
    }

    /**
     * Méthode qui retourne le dénivelé positif total de l'arête d'identité donnée (en mètres)
     *
     * @param edgeId (int) : l'identité de l'arête
     * @return (double) : le dénivelé positif total de l'arête d'identité donnée
     */
    public double edgeElevationGain(int edgeId) {
        return edges.elevationGain(edgeId);
    }

    /**
     * Méthode qui retourne le profil en long de l'arête d'identité donnée, sous la forme d'une fonction
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
