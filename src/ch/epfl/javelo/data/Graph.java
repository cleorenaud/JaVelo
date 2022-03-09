/*
 * Author : Roxanne Chevalley
 * Date : 08.03.22
 */
package ch.epfl.javelo.data;

import java.io.*;
import java.nio.*;
import java.nio.channels.FileChannel;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

/**
 * Classe publique et immuable représentant le graphe JaVelo
 * @author : Roxanne Chevalley (339716)
 */

public final class Graph {
    /**
     * méthode permettant de charger le graphe depuis un répertoire
     * @param basePath (Path) :
     * @return (Graph) : le graphe chargé grâce au répertoire
     * @throws IOException en cas d'erreur d'entrée/sortie, p. ex. si l'un des fichiers attendu n'existe pas.
     */
    public static Graph loadFrom(Path basePath) throws IOException{
        Path attributesPath =basePath.resolve("attributes.bin");
        Path edgesPath =basePath.resolve("edges.bin");
        Path nodesPath = basePath.resolve("nodes.bin");
        Path sectorsPath = basePath.resolve("sectors.bin");
        Path elevationsPath =basePath.resolve("elevations.bin");
        Path profileIdsPath= basePath.resolve("profile_ids.bin");

        IntBuffer nodesBuffer;
        ByteBuffer sectorsBuffer;
        ByteBuffer edgesBuffer;
        IntBuffer profileIds;
        ShortBuffer elevations;
        List<AttributeSet> attributeSets= new ArrayList<AttributeSet>();
        BufferedReader attReader;

        try (FileChannel channel = FileChannel.open(nodesPath)) {
            nodesBuffer = channel.map(FileChannel.MapMode.READ_ONLY, 0, channel.size()).asIntBuffer();
        }
        GraphNodes graphNodes= new GraphNodes(nodesBuffer);

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
        GraphEdges graphedges =new GraphEdges(edgesBuffer, profileIds, elevations);

        try (FileChannel channel = FileChannel.open(elevationsPath)){
           attributeSets=List.copyOf(channel.);

        }




    }

    public

}
