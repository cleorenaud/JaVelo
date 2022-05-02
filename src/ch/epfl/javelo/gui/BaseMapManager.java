package ch.epfl.javelo.gui;

import ch.epfl.javelo.Math2;
import javafx.application.Platform;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.geometry.Point2D;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.ScrollEvent;
import javafx.scene.layout.Pane;

import java.io.IOException;
import java.util.Map;


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

    private static final int TILE_SIZE = 256;


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

        int indexXLeftTile = (int) Math.floor(x / TILE_SIZE); // L'index x de la Tile supérieure gauche
        int indexYLeftTile = (int) Math.floor(y / TILE_SIZE); // L'index y de la Tile supérieure gauche

        int indexXRightTile = (int) Math.floor((x + canvas.getWidth()) / TILE_SIZE); // L'index x de la Tile inférieure droite
        int indexYRightTile = (int) Math.floor((y + canvas.getHeight()) / TILE_SIZE); // L'index y de la Tile inférieure droite

        double xTiles = indexXRightTile - indexXLeftTile + 1; // Le nombre de tiles horizontales après la Tile supérieure gauche
        double yTiles = indexYRightTile - indexYLeftTile + 1; // Le nombre de tiles verticales après la Tile supérieure gauche

        double xInTile = x - indexXLeftTile * TILE_SIZE; // La coordonnée x du point supérieur gauche par rapport au sommet de la Tile supérieure gauche
        double yInTile = y - indexYLeftTile * TILE_SIZE; // La coordonnée y du point supérieur gauche par rapport au sommet de la Tile supérieure gauche

        // On itère sur toutes les tiles pour récupérer leur image puis les afficher à l'écran si cette dernière existe
        for (int i = 0; i < xTiles; i++) {
            for (int j = 0; j < yTiles; j++) {
                try {
                    TileManager.TileId tileId = new TileManager.TileId(objectProperty.get().zoomLevel(), indexXLeftTile + i, indexYLeftTile + j);
                    Image image = tileManager.imageForTileAt(tileId);
                    if (!TileManager.TileId.isValid(tileId)) {
                        System.out.println("zoom " + mapViewParameters.zoomLevel());
                        System.out.println("x " + indexXLeftTile + i);
                        System.out.println("y " + indexYLeftTile + j);
                    }
                    graphicsContext.drawImage(image, TILE_SIZE * i - xInTile, TILE_SIZE * j - yInTile, TILE_SIZE, TILE_SIZE);

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
            if (mouseEvent.isStillSincePress()) {
                double x = mouseEvent.getX();
                double y = mouseEvent.getY();
                waypointsManager.addWaypoint(x,y);
                redrawOnNextPulse();
            }
        });

        pane.setOnMousePressed((MouseEvent mouseEvent) -> {
            // On crée un ObjectProperty contenant la position à laquelle se trouvait la souris au moment où elle est pressée
            this.posSouris = new SimpleObjectProperty<>(new Point2D(mouseEvent.getX(), mouseEvent.getY()));

        });

        pane.setOnMouseDragged((MouseEvent mouseEvent) -> {
            // TODO: utiliser les méthodes add et substract
            Point2D newPosSouris = new Point2D(mouseEvent.getX(), mouseEvent.getY());
            float deltaX = (float) (posSouris.get().getX() - newPosSouris.getX());
            float deltaY = (float) (posSouris.get().getY() - newPosSouris.getY());

            MapViewParameters mapViewParameters = objectProperty.get();
            objectProperty.set(mapViewParameters.withMinXY(mapViewParameters.x() + deltaX, mapViewParameters.y() + deltaY));

            posSouris.set(newPosSouris);

            redrawOnNextPulse();
        });

        pane.setOnMouseReleased((MouseEvent mouseEvent) -> {
            /*
            Point2D newPosSouris = new Point2D.Double(mouseEvent.getX(), mouseEvent.getY());
            float deltaX = (float) (posSouris.get().getX() - newPosSouris.getX());
            float deltaY = (float) (posSouris.get().getY() - newPosSouris.getY());

            MapViewParameters mapViewParameters = objectProperty.get();
            objectProperty.set(mapViewParameters.withMinXY(mapViewParameters.x() + deltaX, mapViewParameters.y() + deltaY));

            posSouris.set(newPosSouris);

            redrawOnNextPulse();
             */

        });

        pane.setOnScroll((ScrollEvent scrollEvent) -> {
            double xTranslation = scrollEvent.getX(); // La coordonnée x de la souris par rapport au coin supérieur gauche
            double yTranslation = scrollEvent.getY(); // La coordonnée y de la souris par rapport au coin supérieur gauche
            float xSouris = (float) (objectProperty.get().x() + xTranslation); // La coordonnée x de la souris
            float ySouris = (float) (objectProperty.get().y() + yTranslation); // La coordonnée y de la souris


            // On effectue une première translation pour que le point sous la souris se retrouve dans le coin
            // supérieur gauche de la fenêtre
            objectProperty.set(objectProperty.get().withMinXY(xSouris, ySouris));

            // On calcule le nouveau zoom selon le degré dont la molette à été tournée
            double delta = Math.round(scrollEvent.getDeltaY());
            int oldZoomLevel = objectProperty.get().zoomLevel();
            int newZoomLevel = (int) (oldZoomLevel + delta);
            newZoomLevel = Math2.clamp(8, newZoomLevel, 19);

            int difZoom = newZoomLevel - oldZoomLevel;
            // On effectue la deuxième translation pour que le point sous la souris se retrouve à nouveau au bon endroit
            float newX = (float) (Math.scalb(xSouris, difZoom) - xTranslation);
            float newY = (float) (Math.scalb(ySouris, difZoom) - yTranslation);

            objectProperty.setValue(new MapViewParameters(newZoomLevel, newX, newY));
            redrawOnNextPulse();

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
