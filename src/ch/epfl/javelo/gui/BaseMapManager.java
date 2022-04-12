package ch.epfl.javelo.gui;

import javafx.beans.property.ObjectProperty;
import javafx.scene.layout.Pane;

/**
 * Classe publique et finale qui gère l'affichage et interaction avec le fond de carte
 */
public final class BaseMapManager {

    /**
     * Constructeur public de la classe
     *
     * @param tileManager (TileManager) : le gestionnaire de tuiles à utiliser pour obtenir les tuiles de la carte
     * @param waypointsManager (WaypointsManager) : le gestionnaire des points de passage
     * @param objectProperty (ObjectProperty) : une propriété JavaFX contenant les paramètres de la carte affichée
     */
    public Pane BaseMapManager(TileManager tileManager, WaypointsManager waypointsManager, ObjectProperty objectProperty) {
        
    }


}
