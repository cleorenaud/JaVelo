package ch.epfl.javelo.gui;

import javafx.application.Platform;
import javafx.beans.property.ObjectProperty;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.layout.Pane;

/**
 * Classe publique et finale qui gère l'affichage et interaction avec le fond de carte
 *
 * @author Cléo Renaud (325156)
 */
public final class BaseMapManager {
    private final TileManager tileManager;
    private final WaypointsManager waypointsManager;
    private final ObjectProperty objectProperty;
    private boolean redrawNeeded;


    /**
     * Constructeur public de la classe
     *
     * @param tileManager      (TileManager) : le gestionnaire de tuiles à utiliser pour obtenir les tuiles de la carte
     * @param waypointsManager (WaypointsManager) : le gestionnaire des points de passage
     * @param objectProperty   (ObjectProperty) : une propriété JavaFX contenant les paramètres de la carte affichée
     */
    public BaseMapManager(TileManager tileManager, WaypointsManager waypointsManager, ObjectProperty objectProperty) {
        this.tileManager = tileManager;
        this.waypointsManager = waypointsManager;
        this.objectProperty = objectProperty;

        /*
        // On s'assure que JavaFX appelle bien la méthode redrawIfNeeded() à chaque battement
        canvas.sceneProperty().addListener((p, oldS, newS) -> {
            assert oldS == null;
            newS.addPreLayoutPulseListener(this::redrawIfNeeded);
        });
         */
    }

    /**
     * Méthode retournant le panneau JavaFX affichant le fond de carte
     *
     * @return (Pane) : le panneau JavaFX affichant le fond de carte
     */
    public Pane pane() {
        Pane pane = new Pane();
        // On crée une instance de Canvas et on fait en sorte que les dimensions de canvas soient identiques à celle de
        // notre instance de Pane
        Canvas canvas = new Canvas();
        canvas.widthProperty().bind(pane.widthProperty());

        // On cherche à obtenir le contexte graphique du canevas
        GraphicsContext graphicsContext = canvas.getGraphicsContext2D();
        // Utilisation de la méthode tileManager.imageForTileAt() utile pour déterminer quelles Tiles afficher à l'écran
        //graphicsContext.drawImage(tileManager.imageForTileAt(), );

        return pane;
    }

    /**
     * Méthode effectuant le redessin si et seulement si l'attribut redrewNeeded est vrai
     */
    private void redrawIfNeeded() {
        if (!redrawNeeded) return;
        redrawNeeded = false;

        // … à faire : dessin de la carte
    }

    /**
     * Méthode permettant de demander un redessin au prochain battement
     */
    private void redrawOnNextPulse() {
        redrawNeeded = true;
        Platform.requestNextPulse();
    }

}
