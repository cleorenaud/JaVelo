package ch.epfl.javelo.gui;

import ch.epfl.javelo.data.Graph;
import ch.epfl.javelo.routing.*;
import javafx.application.Application;
import javafx.beans.property.*;
import javafx.beans.value.ObservableBooleanValue;
import javafx.beans.value.ObservableValue;
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

    @Override
    public void start(Stage primaryStage) throws Exception {
        Graph graph = Graph.loadFrom(Path.of("javelo-data"));
        graph=Graph.loadFrom(Path.of("ch_west")); //TODO : a supprimer plus tard, pour les tests
        Path cacheBasePath = Path.of("osm-cache");
        cacheBasePath = Path.of(".");  //TODO : a supprimer plus tard, pour tester
        String tileServerHost = "tile.openstreetmap.org";
        TileManager tileManager =
                new TileManager(cacheBasePath, tileServerHost);
        CostFunction costFunction = new CityBikeCF(graph);
        RouteBean routeBean = new RouteBean(new RouteComputer(graph,new CityBikeCF(graph)));
        ErrorManager errorManager = new ErrorManager();
        Consumer<String> errorConsumer = (s -> errorManager.displayError(s));

        AnnotatedMapManager mapPane  = new AnnotatedMapManager(graph, tileManager, routeBean, errorConsumer);

        ElevationProfile profile = ElevationProfileComputer
                .elevationProfile(routeBean.route(), 5);
        ObjectProperty<ElevationProfile> profileProperty = new SimpleObjectProperty<>(profile);
        DoubleProperty highlightProperty = new SimpleDoubleProperty(NaN);
        ElevationProfileManager profileManager = new ElevationProfileManager(profileProperty, highlightProperty);

        SplitPane splitPane = new SplitPane();
        splitPane.setOrientation(Orientation.VERTICAL);
        splitPane.getChildrenUnmodifiable().add(mapPane.pane());

        if(routeBean.route != null){
            splitPane.getChildrenUnmodifiable().add(profileManager.pane());
            SplitPane.setResizableWithParent(profileManager.pane(), false);
        }

        MenuBar menuBar = new MenuBar();
        Menu menu = new Menu("Fichier");
        menuBar.getMenus().add(menu);
        MenuItem menuItem =new MenuItem("Exporter GPX");
        menu.getItems().add(menuItem);
        // TODO: bonne façon de faire ?
        ObservableBooleanValue routeIsNull = new ReadOnlyBooleanWrapper(routeBean.route() == null);
        menuItem.disableProperty().bind(routeIsNull);
        menuBar.setUseSystemMenuBar(true);

        menuItem.setOnAction(e -> {
            try {
                GpxGenerator.writeGpx("javelo.gpx", routeBean.route(), profile);
            } catch (IOException exception) {
                throw new UncheckedIOException(exception);
            }
        });

        if(mapPane.mousePositionOnRouteProperty().get() >=0){
            highlightProperty.bind(mapPane.mousePositionOnRouteProperty());
        }else{
            highlightProperty.bind(profileManager.mousePositionOnProfileProperty());
        }

        StackPane mainPane = new StackPane(splitPane, errorManager.pane(), menuBar);
        primaryStage.setMinWidth(800);
        primaryStage.setMinHeight(600);
        primaryStage.setScene(new Scene(mainPane));
        primaryStage.setTitle("JaVelo");
        primaryStage.show();


    }

}
