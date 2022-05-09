package ch.epfl.javelo.gui;

import ch.epfl.javelo.data.Graph;
import ch.epfl.javelo.routing.*;
import com.sun.jdi.BooleanValue;
import javafx.application.Application;
import javafx.beans.property.*;
import javafx.beans.value.ObservableBooleanValue;
import javafx.geometry.Orientation;
import javafx.scene.Scene;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.control.SplitPane;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Path;
import java.util.function.Consumer;

import static java.lang.Float.NaN;

/**
 * Classe publique et instantiable qui est la classe principale de l'application
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
        String tileServerHost = "tile.openstreetmap.org";
        TileManager tileManager =
                new TileManager(cacheBasePath, tileServerHost);
        RouteBean routeBean = new RouteBean(new RouteComputer(graph, new CityBikeCF(graph)));
        ErrorManager errorManager = new ErrorManager();
        Consumer<String> errorConsumer = (s -> errorManager.displayError(s));

        AnnotatedMapManager mapPane = new AnnotatedMapManager(graph, tileManager, routeBean, errorConsumer);



        SplitPane splitPane = new SplitPane();
        splitPane.setOrientation(Orientation.VERTICAL);
        splitPane.getItems().add(mapPane.pane());

        routeBean.highlightedPosition.bind(
                mapPane.mousePositionOnRouteProperty());

       MenuBar menuBar = new MenuBar();
        Menu menu = new Menu("Fichier");
        menuBar.getMenus().add(menu);
        MenuItem menuItem = new MenuItem("Exporter GPX");
        menu.getItems().add(menuItem);
        ObservableBooleanValue routeIsNull = new
        routeBean.route.addListener(e->{
            if(routeBean.route() != null){
                ElevationProfile profile = ElevationProfileComputer
                        .elevationProfile(routeBean.route(), 5);
                ObjectProperty<ElevationProfile> profileProperty = new SimpleObjectProperty<>(profile);
                ElevationProfileManager profileManager = new ElevationProfileManager(profileProperty, highlightProperty);
                splitPane.getChildrenUnmodifiable().add(profileManager.pane());
                SplitPane.setResizableWithParent(profileManager.pane(), false);

                menuItem.setOnAction(e -> {
                    try {
                        GpxGenerator.writeGpx("javelo.gpx", routeBean.route(), profile);
                    } catch (IOException exception) {
                        throw new UncheckedIOException(exception);
                    }
                });

                if (mapPane.mousePositionOnRouteProperty().get() >= 0) {
                    highlightProperty.bind(mapPane.mousePositionOnRouteProperty());
                } else {
                    highlightProperty.bind(profileManager.mousePositionOnProfileProperty());
                }
            }
        });
        menuItem.disableProperty().bind(routeIsNull);
        menuBar.setUseSystemMenuBar(true);




       /* if (routeBean.route() != null) {

            ElevationProfile profile = ElevationProfileComputer
                    .elevationProfile(routeBean.route(), 5);
            ObjectProperty<ElevationProfile> profileProperty = new SimpleObjectProperty<>(profile);
            ElevationProfileManager profileManager = new ElevationProfileManager(profileProperty, highlightProperty);
            splitPane.getChildrenUnmodifiable().add(profileManager.pane());
            SplitPane.setResizableWithParent(profileManager.pane(), false);

            menuItem.setOnAction(e -> {
                try {
                    GpxGenerator.writeGpx("javelo.gpx", routeBean.route(), profile);
                } catch (IOException exception) {
                    throw new UncheckedIOException(exception);
                }
            });

            if (mapPane.mousePositionOnRouteProperty().get() >= 0) {
                highlightProperty.bind(mapPane.mousePositionOnRouteProperty());
            } else {
                highlightProperty.bind(profileManager.mousePositionOnProfileProperty());
            }
        }*/


        StackPane mainPane = new StackPane(splitPane, errorManager.pane(), menuBar);
        primaryStage.setMinWidth(800);
        primaryStage.setMinHeight(600);
        primaryStage.setScene(new Scene(mainPane));
        primaryStage.setTitle("JaVelo");
        primaryStage.show();


        routeIsNull.addListener(e->{
            if(routeBean.route() != null){
                System.out.println("pas nul");
            }
        });


    }

}
