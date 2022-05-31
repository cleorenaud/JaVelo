package ch.epfl.javelo.gui;

import javafx.animation.FadeTransition;
import javafx.animation.PauseTransition;
import javafx.animation.SequentialTransition;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.util.Duration;


/**
 * Classe publique et instantiable qui gère l'affichage de messages d'erreurs
 *
 * @author : Roxanne Chevalley (339716)
 */
public final class ErrorManager {

    private final static Duration ENTER_DURATION = new Duration(200);
    private final static Duration LEAVE_DURATION = new Duration(500);
    private final static Duration PAUSE_DURATION = new Duration(2000);
    private final static double FADE_MAX = 0.8;
    private final static double FADE_MIN = 0;

    private final VBox errorVbox;
    private final Text errorMessage;
    private final SequentialTransition fullTransition;

    /**
     * Constructeur public dénué d'argument
     */
    public ErrorManager() {
        this.errorMessage = new Text();
        this.errorVbox = new VBox(errorMessage);
        errorVbox.getStylesheets().add("error.css");
        errorVbox.setMouseTransparent(true);

        FadeTransition enterTransition = new FadeTransition(ENTER_DURATION);
        enterTransition.setFromValue(FADE_MIN);
        enterTransition.setToValue(FADE_MAX);

        FadeTransition leaveTransition = new FadeTransition(LEAVE_DURATION);
        leaveTransition.setFromValue(FADE_MAX);
        leaveTransition.setToValue(FADE_MIN);

        PauseTransition pauseTransition = new PauseTransition(PAUSE_DURATION);
        this.fullTransition = new SequentialTransition(errorVbox, enterTransition, pauseTransition, leaveTransition);
    }

    /**
     * Méthode retournant le panneau sur lequel les messages d'erreurs apparaissent
     *
     * @return Pane : le panneau
     */
    public Pane pane() {
        return errorVbox;
    }

    /**
     * Méthode publique permettant d'afficher un message d'erreur
     *
     * @param message (String) : le message à afficher
     */
    public void displayError(String message) {
        fullTransition.stop();
        java.awt.Toolkit.getDefaultToolkit().beep();
        errorMessage.setText(message);
        fullTransition.play();
    }

}
