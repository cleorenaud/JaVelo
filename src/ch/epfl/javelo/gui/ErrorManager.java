package ch.epfl.javelo.gui;

import javafx.animation.FadeTransition;
import javafx.animation.PauseTransition;
import javafx.animation.SequentialTransition;
import javafx.animation.Transition;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.util.Duration;


/**
 * Classe publique et instantiable qui gère l'affichage de messages d'erreurs
 * @author : Roxanne Chevalley (339716)
 */
public final class ErrorManager {
    private final static Duration ENTER_DURATION = new Duration(200);
    private final static Duration LEAVE_DURATION = new Duration (500);
    private final static Duration PAUSE_DURATION = new Duration (2000);
    private final static double FADE_MAX =0.8;
    private final static double FADE_MIN = 0;
    private final BorderPane errorPane;
    private final VBox errorVbox;
    private final Text errorMessage;
    private final FadeTransition enterTransition;
    private final FadeTransition leaveTransition;
    private final PauseTransition pauseTransition;
    private final SequentialTransition fullTransition;

    /**
     * constructeur public dénué d'argument
     */
    public ErrorManager(){
        this.errorMessage = new Text();
        this.errorVbox = new VBox(errorMessage);
        errorVbox.getStylesheets().add("error.css");
        this.errorPane = new BorderPane(errorVbox);
        errorPane.setMouseTransparent(true);

        this.enterTransition = new FadeTransition(ENTER_DURATION);
        enterTransition.setFromValue(FADE_MIN);
        enterTransition.setToValue(FADE_MAX);

        this.leaveTransition = new FadeTransition(LEAVE_DURATION);
        leaveTransition.setFromValue(FADE_MAX);
        leaveTransition.setToValue(FADE_MIN);

        this.pauseTransition = new PauseTransition(PAUSE_DURATION);
        this.fullTransition  = new SequentialTransition(errorVbox, enterTransition, pauseTransition, leaveTransition);
        //TODO  : faut-il laisser le errorVbox à chaque fois ?
    };

    /**
     * méthode retournant le panneau sur lequel les messages d'erreurs apparaissent
     * @return BorderPane : le panneau
     */
    public BorderPane pane(){
        return errorPane;
    }

    /**
     * méthode publique permettant d'afficher un message d'erreur
     * @param message (String) : le message à afficher
     */
    public void displayError(String message){
        fullTransition.stop();
        java.awt.Toolkit.getDefaultToolkit().beep();
        errorMessage.setText(message);
        fullTransition.play();
    }

}
