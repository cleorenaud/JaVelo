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
     * Constructeur de la classe TileManager
     *
     * @param path       (Path) : le chemin d'accès au répertoire contenant le cache disque
     * @param serverName (String) : le nom du serveur de tuile
     */
    public TileManager(Path path, String serverName) {
        this.path = path;
        this.serverName = serverName;
        this.cacheMemory = new LinkedHashMap<>(100, 2, true);
    }

    public Image imageForTileAt(TileId tileId) throws IOException {
        Preconditions.checkArgument(TileId.isValid(tileId));

        if (cacheMemory.containsKey(tileId)) {
            return cacheMemory.get(tileId);
        }
        Image image;

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
                image = new Image(inputStream);
                addToCacheMemory(tileId, image);
                return image;
            }
        }
        return null;

    }

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

        public static boolean isValid(TileId tileId) {
            int zoomLevel = tileId.zoomLevel();
            int x = tileId.x();
            int y = tileId.y();
            if ((0 <= x && x <= Math.pow(2, zoomLevel) - 1) && (0 <= y && y <= Math.pow(2, zoomLevel) - 1) &&
                    (0 <= zoomLevel) && (zoomLevel <= 20)) {
                return true;
            }
            return false;
        }

    }

}