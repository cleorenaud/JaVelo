package ch.epfl.javelo.gui;

import ch.epfl.javelo.Math2;
import javafx.application.Platform;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.ScrollEvent;
import javafx.scene.layout.Pane;

import java.awt.event.ActionListener;
import java.awt.geom.Point2D;
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

    private ObjectProperty<Point2D> posSouris;


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

        installBindings();
        installHandlers();
        redrawOnNextPulse();

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

        // On cherche à obtenir le contexte graphique du canevas puis on utilise la méthode drawImage
        GraphicsContext graphicsContext = canvas.getGraphicsContext2D();

        MapViewParameters mapViewParameters = objectProperty.get();
        float x = mapViewParameters.x();
        float y = mapViewParameters.y();


        int indexXLeftTile = (int) Math.floor(x / 256); // L'index x de la Tile supérieure gauche
        int indexYLeftTile = (int) Math.floor(y / 256); // L'index y de la Tile supérieure gauche

        int indexXRightTile = (int) Math.floor((x + canvas.getWidth()) / 256); // L'index x de la Tile inférieure droite
        int indexYRightTile = (int) Math.floor((y + canvas.getHeight()) / 256); // L'index y de la Tile inférieure droite

        double xTiles = indexXRightTile - indexXLeftTile + 1; // Le nombre de tiles horizontales après la Tile supérieure gauche
        double yTiles = indexYRightTile - indexYLeftTile + 1; // Le nombre de tiles verticales après la Tile supérieure gauche

        double xInTile = x - indexXLeftTile * 256; // La coordonnée x du point supérieur gauche par rapport au sommet de la Tile supérieure gauche
        double yInTile = y - indexYLeftTile * 256; // La coordonnée y du point supérieur gauche par rapport au sommet de la Tile supérieure gauche

        // On itère sur toutes les tiles pour récupérer leur image puis les afficher à l'écran si cette dernière existe
        for (int i = 0; i < xTiles; i++) {
            for (int j = 0; j < yTiles; j++) {
                try {
                    TileManager.TileId tileId = new TileManager.TileId(objectProperty.get().zoomLevel(), indexXLeftTile + i, indexYLeftTile + j);
                    Image image = tileManager.imageForTileAt(tileId);
                    if(!TileManager.TileId.isValid(tileId)){
                        System.out.println("zoom " + mapViewParameters.zoomLevel());
                        System.out.println("x " + indexXLeftTile + i);
                        System.out.println("y " + indexYLeftTile + j);
                    }
                    graphicsContext.drawImage(image, 256 * i - xInTile, 256 * j - yInTile, 256, 256);

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

    /**
     * Méthode installant les gestionnaires d'événements
     */
    private void installHandlers() {
        // On doit installer trois gestionnaires d'événement gérant le glissement de la carte

        pane.setOnMouseClicked((MouseEvent mouseEvent) -> {
            double x = mouseEvent.getX();
            double y = mouseEvent.getY();
            //waypointsManager.addWaypoint((int) x, (int) y);
            //redrawOnNextPulse();
        });

        pane.setOnMousePressed((MouseEvent mouseEvent) -> {
            // On crée un ObjectProperty contenant la position à laquelle se trouvait la souris au moment où elle est pressée
            this.posSouris = new SimpleObjectProperty<>(new Point2D.Double(mouseEvent.getX(), mouseEvent.getY()));

        });

        pane.setOnMouseDragged((MouseEvent mouseEvent) -> {

        });

        pane.setOnMouseReleased((MouseEvent mouseEvent) -> {
            // On vérifie qu'il y a bien eu un déplacement de la souris depuis qu'elle a été pressée
            if(!mouseEvent.isStillSincePress()) {

            }

        });

        pane.setOnScroll((ScrollEvent scrollEvent) -> {
            double delta = Math.round(scrollEvent.getDeltaY());
            int zoomLevel = (int) (objectProperty.get().zoomLevel() + delta);
            zoomLevel = Math2.clamp(8, zoomLevel, 19);
            objectProperty.setValue(new MapViewParameters(zoomLevel, objectProperty.get().x(), objectProperty.get().y()));
            redrawOnNextPulse();

            // On fait des translations pour mettre le point sous la souris dans le coin haut gauche, on zoom puis on
            // remet le coin hau gauche sous la souris
        });


    }

    /**
     * Méthode créant les liens entre la taille de la fenêtre et la taille de notre Pane (et donc du Canvas également)
     */
    private void installBindings() {
        canvas.widthProperty().addListener(e -> redrawOnNextPulse());
        canvas.heightProperty().addListener(e -> redrawOnNextPulse());
    }

    /**
     * Méthode installant les auditeurs
     */
    private void installListeners() {
        //objectProperty.addListener(e -> redrawOnNextPulse());
    }

}
