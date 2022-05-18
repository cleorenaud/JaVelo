package ch.epfl.javelo.gui;

import ch.epfl.javelo.Math2;
import javafx.application.Platform;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleLongProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.geometry.Point2D;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.ScrollEvent;
import javafx.scene.layout.Pane;

import java.io.IOException;

/**
 * Classe publique et finale qui gère l'affichage et interaction avec le fond de carte
 *
 * @author Cléo Renaud (325156)
 */
public final class BaseMapManager {

    private final static int TILE_SIZE = 256; // la taille d'une tuile OSM (en unités JavaFX)
    private final static int MIN_ZOOM = 8; // le niveau de zoom minimal autorisé
    private final static int MAX_ZOOM = 19; // le niveau de zoom maximal autorisé
    private final static int DELTA_TIME_ZOOM = 200; // durée minimale (en ms) entre deux changements de niveau de zoom

    private final TileManager tileManager;
    private final WaypointsManager waypointsManager;
    private final ObjectProperty<MapViewParameters> mapViewParametersProperty;
    private final Pane mapBackground;
    private final Canvas canvas;

    private boolean redrawNeeded;

    /**
     * Constructeur public de la classe
     *
     * @param tileManager               (TileManager) : le gestionnaire de tuiles à utiliser pour obtenir les tuiles de la carte
     * @param waypointsManager          (WaypointsManager) : le gestionnaire des points de passage
     * @param mapViewParametersProperty (ObjectProperty) : une propriété JavaFX contenant les paramètres de la carte affichée
     */
    public BaseMapManager(TileManager tileManager, WaypointsManager waypointsManager,
                          ObjectProperty<MapViewParameters> mapViewParametersProperty) {
        this.tileManager = tileManager;
        this.waypointsManager = waypointsManager;
        this.mapViewParametersProperty = mapViewParametersProperty;
        this.mapBackground = new Pane();
        this.canvas = new Canvas();
        mapBackground.getChildren().add(canvas);

        installListeners();
        installHandlers();
        installBindings();

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
        return mapBackground;
    }

    /**
     * Méthode privée effectuant le re-dessin si et seulement si l'attribut redrewNeeded est vrai
     */
    private void redrawIfNeeded() {
        if (!redrawNeeded) return;
        redrawNeeded = false;

        // On cherche à obtenir le contexte graphique du canevas puis on utilise la méthode drawImage
        GraphicsContext graphicsContext = canvas.getGraphicsContext2D();

        MapViewParameters mapViewParameters = this.mapViewParametersProperty.get();
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
                    TileManager.TileId tileId = new TileManager.TileId(this.mapViewParametersProperty.get().zoomLevel(), indexXLeftTile + i, indexYLeftTile + j);
                    Image image = tileManager.imageForTileAt(tileId);
                    graphicsContext.drawImage(image, TILE_SIZE * i - xInTile, TILE_SIZE * j - yInTile, TILE_SIZE, TILE_SIZE);
                } catch (IOException e) {
                    // ignore
                }
            }
        }

    }

    /**
     * Méthode privée permettant de demander un re-dessin au prochain battement
     */
    private void redrawOnNextPulse() {
        redrawNeeded = true;
        Platform.requestNextPulse();
    }

    /**
     * Méthode privée installant les gestionnaires d'événements
     */
    private void installHandlers() {
        ObjectProperty<Point2D> mousePosition = new SimpleObjectProperty<>();
        mapBackground.setOnMouseClicked((MouseEvent mouseEvent) -> {
            if (mouseEvent.isStillSincePress()) {
                double x = mouseEvent.getX();
                double y = mouseEvent.getY();
                waypointsManager.addWaypoint(x, y);
            }
        });

        mapBackground.setOnMousePressed((MouseEvent mouseEvent) -> {
            // On crée un ObjectProperty contenant la position à laquelle se trouvait la souris au moment où elle est pressée
            mousePosition.set(new Point2D(mouseEvent.getX(), mouseEvent.getY()));
        });

        mapBackground.setOnMouseDragged((MouseEvent mouseEvent) -> {
            Point2D newPosSouris = new Point2D(mouseEvent.getX(), mouseEvent.getY());
            float deltaX = (float) (mousePosition.get().getX() - newPosSouris.getX());
            float deltaY = (float) (mousePosition.get().getY() - newPosSouris.getY());

            MapViewParameters mapViewParameters = this.mapViewParametersProperty.get();
            this.mapViewParametersProperty.set(mapViewParameters.withMinXY(mapViewParameters.x() + deltaX, mapViewParameters.y() + deltaY));

            mousePosition.set(newPosSouris);
        });

        SimpleLongProperty minScrollTime = new SimpleLongProperty();
        mapBackground.setOnScroll((ScrollEvent scrollEvent) -> {
            if (scrollEvent.getDeltaY() == 0d) return;
            long currentTime = System.currentTimeMillis();
            if (currentTime < minScrollTime.get()) return;
            minScrollTime.set(currentTime + DELTA_TIME_ZOOM);
            int zoomDelta = (int) Math.signum(scrollEvent.getDeltaY());

            double xTranslation = scrollEvent.getX(); // La coordonnée x de la souris par rapport au coin supérieur gauche
            double yTranslation = scrollEvent.getY(); // La coordonnée y de la souris par rapport au coin supérieur gauche
            float xSouris = (float) (mapViewParametersProperty.get().x() + xTranslation); // La coordonnée x de la souris
            float ySouris = (float) (mapViewParametersProperty.get().y() + yTranslation); // La coordonnée y de la souris

            // On effectue une première translation pour que le point sous la souris se retrouve dans le coin
            // supérieur gauche de la fenêtre
            mapViewParametersProperty.set(mapViewParametersProperty.get().withMinXY(xSouris, ySouris));

            int oldZoomLevel = mapViewParametersProperty.get().zoomLevel();
            int newZoomLevel = Math2.clamp(MIN_ZOOM, oldZoomLevel + zoomDelta, MAX_ZOOM);;
            int difZoom = newZoomLevel - oldZoomLevel;

            // On effectue la deuxième translation pour que le point sous la souris se retrouve à nouveau au bon endroit
            float newX = (float) (Math.scalb(xSouris, difZoom) - xTranslation);
            float newY = (float) (Math.scalb(ySouris, difZoom) - yTranslation);

            mapViewParametersProperty.setValue(new MapViewParameters(newZoomLevel, newX, newY));
        });

    }

    /**
     * Méthode privée créant les liens entre la taille de la fenêtre et la taille de notre Pane (et donc du Canvas également)
     */
    private void installBindings() {
        canvas.widthProperty().bind(mapBackground.widthProperty());
        canvas.heightProperty().bind(mapBackground.heightProperty());
    }

    /**
     * Méthode privée installant un auditeur sur l'ObjectProperty contenant nos MapViewParameters
     */
    private void installListeners() {
        mapViewParametersProperty.addListener(e -> redrawOnNextPulse());
        canvas.widthProperty().addListener(e -> redrawOnNextPulse());
        canvas.heightProperty().addListener(e -> redrawOnNextPulse());
    }

}
