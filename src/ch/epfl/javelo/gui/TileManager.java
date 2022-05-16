package ch.epfl.javelo.gui;

import java.io.*;
import java.net.URL;
import java.net.URLConnection;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.LinkedHashMap;
import java.util.Map;

import ch.epfl.javelo.Preconditions;
import javafx.scene.image.Image;

/**
 * Classe publique et finale représentant un gestionnaire de tuiles OSM
 *
 * @author Roxanne Chevalley (339716)
 */
public final class TileManager {

    private final Path path;
    private final String serverName;

    private LinkedHashMap<TileId, Image> cacheMemory;
    private final int CAPACITY = 100;

    /**
     * Constructeur public de la classe TileManager
     *
     * @param path       (Path) : le chemin d'accès au répertoire contenant le cache disque
     * @param serverName (String) : le nom du serveur de tuile
     */
    public TileManager(Path path, String serverName) {
        this.path = path;
        this.serverName = serverName;
        this.cacheMemory = new LinkedHashMap<>(100, 2, true);
    }

    /**
     * Méthode retournant l'image correspondant à l'identité de la tuile passée en argument
     *
     * @param tileId (TileId) : l'identité de la tuile
     * @return (Image) : l'image correspondante
     * @throws IOException en cas d'erreur d'entrée/sortie
     */
    public Image imageForTileAt(TileId tileId) throws IOException {
        Preconditions.checkArgument(TileId.isValid(tileId.zoomLevel, tileId.x(), tileId.y()));

        if (cacheMemory.containsKey(tileId)) {
            return cacheMemory.get(tileId);
        }

        Image tileImage;
        Path dir = path.resolve(String.valueOf(tileId.zoomLevel()));
        dir = dir.resolve(String.valueOf(tileId.x()));
        Path tilePath = dir.resolve(String.valueOf(tileId.y()) + ".png");
        if (!Files.exists(tilePath)) {
            Files.createDirectories(dir);
            URL u = new URL(serverName + tileId.zoomLevel() + "/" + tileId.x + "/" + tileId.y + ".png");
            URLConnection c = u.openConnection();
            c.setRequestProperty("User-Agent", "JaVelo");
            try (InputStream i = c.getInputStream()) {
                FileOutputStream file = new FileOutputStream(tilePath.toString());
                i.transferTo(file);
            }
        }
        if (Files.exists(tilePath)) {
            File file = tilePath.toFile();
            try (InputStream inputStream = new FileInputStream(file)) {
                tileImage = new Image(inputStream);
                addToCacheMemory(tileId, tileImage);
                return tileImage;
            }
        }
        return null;

    }

    /**
     * Méthode privée permettant d'ajouter l'image d'une tuile au cache mémoire
     *
     * @param tileId (TileId) : l'identité de la tuile
     * @param image  (Image) : l'image de la tuile
     */
    private void addToCacheMemory(TileId tileId, Image image) {
        if (cacheMemory.size() == CAPACITY) {
            TileId key = null;
            for (Map.Entry<TileId, Image> entry : cacheMemory.entrySet()) {
                key = entry.getKey();
                continue;
            }
            cacheMemory.remove(key);
        }
        cacheMemory.put(tileId, image);

    }

    /**
     * Enregistrement imbriqué représentant l'identité d'une tuile OSM
     *
     * @param zoomLevel (int) : le niveau de zoom de la tuile
     * @param x         (int) : l'index X de la tuile
     * @param y         (int) : l'index Y de la tuile
     */
    record TileId(int zoomLevel, int x, int y) {

        /**
         * Méthode publique et statique permettant de déterminer si les paramètres passés en arguments constituent une
         * identité de tuile valide
         * @param zoomLevel (int) : le niveau de zoom
         * @param x (int) : l'index x de la tuile
         * @param y (int) : l'index y de la tuile
         * @return (boolean) : true si les paramètres constituent une identité de tuile valide et false autrement
         */
        public static boolean isValid(int zoomLevel, int x, int y) {
            if ((0 <= x && x <= Math.pow(2, zoomLevel) - 1) && (0 <= y && y <= Math.pow(2, zoomLevel) - 1) &&
                    (0 <= zoomLevel) && (zoomLevel <= 20)) {
                return true;
            }
            return false;
        }

    }

}