package ch.epfl.javelo.gui;

import java.awt.*;
import java.nio.file.Path;

/**
 * Classe publique et finale représentant un gestionnaire de tuiles OSM
 */
public final class TileManager {

    /**
     * Constructeur de la classe TileManager
     * @param path (Path) : le chemin d'accès au répertoire contenant le cache disque
     * @param serverName (String) : le nom du serveur de tuile
     */
    public TileManager(Path path, String serverName) {

    }

    public Image imageForTileAt(TileId tileId) {
        return null;
    }

    /**
     * Enregistrement imbriqué
     * @param zoomLevel (int) : le niveau de zoom de la tuile
     * @param x (double) : l'index X de la tuile
     * @param y (double) : l'index Y de la tuile
     */
    record TileId(int zoomLevel, double x, double y) {

        public static isValid(int zoomLevel, double x, double y) {

        }
    }
}
