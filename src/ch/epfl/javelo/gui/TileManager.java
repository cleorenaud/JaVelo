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
     * @param x (int) : l'index X de la tuile
     * @param y (int) : l'index Y de la tuile
     */
    record TileId(int zoomLevel, int x, int y) {


        public static boolean isValid(int zoomLevel, int x, int y) {
            if(x>=0 && y>= 0 && x<=Math.pow(2,zoomLevel)-1 && y<=Math.pow(2,zoomLevel)-1){
                return true;
            }
            return false;
        }

    }
}
