package ch.epfl.javelo.gui;

import ch.epfl.javelo.data.Graph;
import ch.epfl.javelo.routing.*;
import javafx.application.Application;
import javafx.beans.binding.Bindings;
import javafx.beans.binding.BooleanBinding;
import javafx.geometry.Orientation;
import javafx.scene.Scene;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.control.SplitPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Path;
import java.util.function.Consumer;

/**
 * Classe publique et instantiable qui est la classe principale de l'application
 *
 * @author Roxanne Chevalley (339716)
 * @author Cléo Renaud (325156)
 */
public final class JaVelo extends Application {

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {

        //Graph graph = Graph.loadFrom(Path.of("javelo-data"));
        Graph graph = Graph.loadFrom(Path.of("ch_west")); //TODO : a supprimer plus tard, pour les tests
        //Path cacheBasePath = Path.of("osm-cache");
        Path cacheBasePath = Path.of(".");  //TODO : a supprimer plus tard, pour tester
        String tileServerHost = "https://tile.openstreetmap.org/";
        TileManager tileManager = new TileManager(cacheBasePath, tileServerHost);
        RouteBean routeBean = new RouteBean(new RouteComputer(graph, new CityBikeCF(graph)));
        ErrorManager errorManager = new ErrorManager();
        Consumer<String> errorConsumer = (s -> errorManager.displayError(s));

        AnnotatedMapManager mapPane = new AnnotatedMapManager(graph, tileManager, routeBean, errorConsumer);

        SplitPane splitPane = new SplitPane();
        splitPane.setOrientation(Orientation.VERTICAL);
        splitPane.getItems().add(mapPane.pane());

        ElevationProfileManager profileManager = new ElevationProfileManager
                (routeBean.elevationProfileProperty(), routeBean.highlightedPositionProperty());

        routeBean.elevationProfileProperty().addListener((p, oldP, newP) -> {
            if (newP == null && oldP != null) {
                splitPane.getItems().retainAll(mapPane.pane());
            }

            if (newP != null && oldP == null) {
                splitPane.getItems().add(profileManager.pane());
                SplitPane.setResizableWithParent(profileManager.pane(), false);
            }
        });

        routeBean.highlightedPositionProperty().bind(Bindings.
                when(mapPane.mousePositionOnRouteProperty().greaterThanOrEqualTo(0))
                .then(mapPane.mousePositionOnRouteProperty())
                .otherwise(profileManager.mousePositionOnProfileProperty()));

        MenuBar menuBar = new MenuBar();
        Menu menu = new Menu("Fichier");
        menuBar.getMenus().add(menu);
        MenuItem menuItem = new MenuItem("Exporter GPX");
        menu.getItems().add(menuItem);
        BooleanBinding routeIsNull = routeBean.routeProperty().isNull();
        menuItem.disableProperty().bind(routeIsNull);

        menuItem.setOnAction(e -> {
            try {
                GpxGenerator.writeGpx("javelo.gpx", routeBean.route(), routeBean.elevationProfile());
            } catch (IOException exception) {
                throw new UncheckedIOException(exception);
            }
        });

        StackPane mainPane = new StackPane(splitPane, errorManager.pane());
        BorderPane borderPane = new BorderPane();
        borderPane.setCenter(mainPane);
        borderPane.setTop(menuBar);
        primaryStage.setMinWidth(800);
        primaryStage.setMinHeight(600);
        primaryStage.setScene(new Scene(borderPane));
        primaryStage.setTitle("JaVelo");
        primaryStage.show();
    }

}
