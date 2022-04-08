package ch.epfl.javelo.gui;

import java.awt.*;
import java.io.*;
import java.net.URL;
import java.net.URLConnection;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.LinkedHashMap;
import java.util.Map;

import ch.epfl.javelo.Preconditions;
import javafx.scene.image.Image;

/**
 * Classe publique et finale représentant un gestionnaire de tuiles OSM
 */
public final class TileManager {
    private final Path path;
    private final String serverName;
    private LinkedHashMap2<TileId, Image> cacheMemoir;
    private InputStream inputStream;
    private OutputStream outputStream;
    private final int CAPACITY = 100;

    /**
     * Constructeur de la classe TileManager
     *
     * @param path       (Path) : le chemin d'accès au répertoire contenant le cache disque
     * @param serverName (String) : le nom du serveur de tuile
     */
    public TileManager(Path path, String serverName) {
        this.path = path;
        this.serverName = serverName;
        this.cacheMemoir = new LinkedHashMap2<>(100, 2, true); //TO DO
    }

    public Image imageForTileAt(TileId tileId) throws IOException {
        Preconditions.checkArgument(TileId.isValid(tileId));
        Image image = cacheMemoir.get(tileId);
        if (image != null) {
            return image;
        }

        Path tilePath = path.resolve(tileId.zoomLevel() + "/" + tileId.x + "/" + tileId.y + ".png");
        if (Files.exists(tilePath)) {
            File file = tilePath.toFile();
            inputStream = new FileInputStream(file);
            image = new Image(inputStream);


            return image;
        }


            /*URL u = new URL("https://tile.openstreetmap.org/" + tileId.zoomLevel() +rem
                    "/" + tileId.x  +  "/" + tileId.y + ".png");
            URLConnection c = u.openConnection();
            c.setRequestProperty("User-Agent", "JaVelo");
            InputStream i = c.getInputStream();
             */

        return null;
    }

    private void addToCacheMemoir(int tileId, Image image) {
        if (cacheMemoir.size() == CAPACITY) {


        }

    }

    /**
     * Enregistrement imbriqué
     *
     * @param zoomLevel (int) : le niveau de zoom de la tuile
     * @param x         (int) : l'index X de la tuile
     * @param y         (int) : l'index Y de la tuile
     */
    record TileId(int zoomLevel, int x, int y) {


        public static boolean isValid(TileId tileId) {
            int zoomLevel = tileId.zoomLevel();
            int x = tileId.x();
            int y = tileId.y();
            if (x >= 0 && y >= 0 && x <= Math.pow(2, zoomLevel) - 1 && y <= Math.pow(2, zoomLevel) - 1 && zoomLevel >= 0) {
                return true;
            }
            return false;
        }

    }

    /**
     * On crée une classe imbriquée nous permettant d'override la méthode removeEldestEntry de la classe LinkedHashMap
     * Grace à cette nouvelle méthode, à chaque fois que la LinkedHashMap sera complète (ses 100 entrées étant occupées)
     * et qu'on ajoutera un élément a notre LinkedHashMap, l'élément le plus anciennement accédé sera supprimé et
     * remplacé par le nouvel élément
     */
    private class LinkedHashMap2<K, V> extends LinkedHashMap<K, V> {

        public LinkedHashMap2(int initialCapacity, float loadFactor, boolean accessOrder) {
            super(initialCapacity, loadFactor, accessOrder);
        }

        @Override
        protected boolean removeEldestEntry(Map.Entry eldest) {
            return size() > CAPACITY;
        }
    }
}
