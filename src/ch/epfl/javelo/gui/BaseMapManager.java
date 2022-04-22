package ch.epfl.javelo.gui;

import ch.epfl.javelo.projection.PointWebMercator;
import com.sun.javafx.geom.Point2D;
import javafx.application.Platform;
import javafx.beans.property.ObjectProperty;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;

import java.io.IOException;


/**
 * Classe publique et finale qui gère l'affichage et interaction avec le fond de carte
 *
 * @author Cléo Renaud (325156)
 */
public final class BaseMapManager {

    private final TileManager tileManager;
    private final WaypointsManager waypointsManager;
    private final ObjectProperty<MapViewParameters> objectProperty;
    private boolean redrawNeeded;
    private Pane pane;
    private Canvas canvas;


    /**
     * Constructeur public de la classe
     *
     * @param tileManager      (TileManager) : le gestionnaire de tuiles à utiliser pour obtenir les tuiles de la carte
     * @param waypointsManager (WaypointsManager) : le gestionnaire des points de passage
     * @param objectProperty   (ObjectProperty) : une propriété JavaFX contenant les paramètres de la carte affichée
     */
    public BaseMapManager(TileManager tileManager, WaypointsManager waypointsManager, ObjectProperty<MapViewParameters> objectProperty) {
        this.tileManager = tileManager;
        this.waypointsManager = waypointsManager;
        this.objectProperty = objectProperty;

        this.pane = new Pane();
        this.canvas = new Canvas();
        pane.getChildren().add(canvas);

        // Le Canvas n'était pas redimensionné automatiquement, contrairement au Pane, on utilise des liens JavaFX
        // pour que la hauteur et la largeur de notre Canvas soient toujours égales à celle de notre Pane
        canvas.widthProperty().bind(pane.widthProperty());
        canvas.heightProperty().bind(pane.heightProperty());

        redrawNeeded = true;

        // On s'assure que JavaFX appelle bien la méthode redrawIfNeeded() à chaque battement
        canvas.sceneProperty().addListener((p, oldS, newS) -> {
            assert oldS == null;
            newS.addPreLayoutPulseListener(this::redrawIfNeeded);
        });

    }

    /**
     * Méthode retournant le panneau JavaFX affichant le fond de carte
     *
     * @return (Pane) : le panneau JavaFX affichant le fond de carte
     */
    public Pane pane() {
        return pane;
    }

    /**
     * Méthode effectuant le redessin si et seulement si l'attribut redrewNeeded est vrai
     */
    private void redrawIfNeeded() {
        if (!redrawNeeded) return;
        redrawNeeded = false;
        //this.pane.setPrefSize(this.pane.sceneProperty().get().getWidth(), this.pane.sceneProperty().get().getHeight());

        // On cherche à obtenir le contexte graphique du canevas puis on utilise la méthode drawImage
        GraphicsContext graphicsContext = canvas.getGraphicsContext2D();

        //double xTiles = Math.ceil(canvas.getWidth() / 256); // Le nombre de tuiles horizontales
        //double yTiles = Math.ceil(canvas.getHeight() / 256); // Le nombre de tuiles verticales
        MapViewParameters mapViewParameters = objectProperty.get();
        int zoomLevel = mapViewParameters.zoomLevel();
        float x = mapViewParameters.x();
        float y = mapViewParameters.y();

        int indexTileX = (int) Math.floor(x / 256);
        int indexTileY = (int) Math.floor(y / 256);

        double xInTile = x - indexTileX * 256;
        double yInTile = y - indexTileY * 256;

        double xTiles = Math.ceil((canvas.getWidth() - (256 - xInTile)) / 256) + 1;
        double yTiles = Math.ceil((canvas.getHeight() - (256 - yInTile)) / 256) + 1;

        for (int i = 0; i <= xTiles; i++) {
            for (int j = 0; j <= yTiles; j++) {
                try {
                    // Utilisation de la méthode tileManager.imageForTileAt() utile pour déterminer quelles Tiles afficher à l'écran
                    TileManager.TileId tileId = new TileManager.TileId(zoomLevel, indexTileX + i, indexTileY + j);
                    Image image = tileManager.imageForTileAt(tileId);
                    graphicsContext.drawImage(image, 256 * i - xInTile, 256 * j - yInTile, 256, 256);
                    System.out.println("Tuile (" + i  + ", " + j + ")");

                } catch (IOException e) {
                    // ignore
                }

            }
        }


    }

    /**
     * Méthode permettant de demander un redessin au prochain battement
     */
    private void redrawOnNextPulse() {
        redrawNeeded = true;
        Platform.requestNextPulse();
    }

}
