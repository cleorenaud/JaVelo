package ch.epfl.javelo.gui;

import javafx.animation.FadeTransition;
import javafx.animation.PauseTransition;
import javafx.animation.SequentialTransition;
import javafx.animation.Transition;
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
    private final Pane errorPane;
    private final VBox errorVbox;
    private final Text errorMessage;
    private final FadeTransition enterTransition;
    private final FadeTransition leaveTransition;
    private final PauseTransition pauseTransition;
    private final SequentialTransition fullAnimation;
    /**
     * constructeur public dénué d'argument
     */
    public ErrorManager(){
        this.errorPane = new Pane();
        this.errorMessage = new Text();
        this.errorVbox = new VBox(errorMessage);
        errorVbox.setId("error.css");
        errorPane.setMouseTransparent(true);

        this.enterTransition = new FadeTransition(ENTER_DURATION, errorVbox);
        enterTransition.setFromValue(0);
        enterTransition.setToValue(0.8);

        this.leaveTransition = new FadeTransition(LEAVE_DURATION, errorVbox);
        leaveTransition.setFromValue(0.8);
        leaveTransition.setToValue(0);

        this.pauseTransition = new PauseTransition(PAUSE_DURATION);
        this.fullAnimation  = new SequentialTransition(errorVbox, enterTransition,leaveTransition, pauseTransition);
        //TODO  : faut-il laisser le errorVbox à chaque fois
    };

    /**
     * méthode retournant le panneau sur lequel les messages d'erreurs apparaissent
     * @return
     */
    public Pane pane(){
        return errorPane;
    }

    public void displayError(String message){
        java.awt.Toolkit.getDefaultToolkit().beep();
       // errorMessage.


    }

}
