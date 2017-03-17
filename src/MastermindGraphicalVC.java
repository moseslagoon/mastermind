import javafx.application.Application;
import javafx.beans.Observable;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.*;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.*;
import javafx.scene.paint.*;
import javafx.scene.shape.Circle;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.stage.Stage;
//import sun.nio.ch.sctp.SctpNet;

import java.awt.*;
import java.awt.Color;
import java.awt.Paint;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Observer;

/**
 * The MastermindGraphicalVC class. GUI for Mastermind. Drives execution of a
 * graphical Mastermind game.
 *
 * @author Moses Lagoon
 * 26 April 2016
 *
 */
public class MastermindGraphicalVC extends Application implements Observer {

    private MastermindModel model;          //The underlying model

    //The colorsArray to toggle between colors as specified in the array.
    private String[] colorsArray = new String[] {"lightgrey","red","blue",
            "green", "Purple", "lightgreen", "Violet"};

    //ArrayList of buttons to keep track of the buttons that are being clicked.
    ArrayList<Button> btnArray = new ArrayList<>();
    //The label that goes in the top
    private Label label1;
    //ArrayList to keep track of shape circles on the left grid pane for clues
    ArrayList<Circle> shapeArray = new ArrayList<>();

    /**
     * Constructs a MastermindGraphicalVC  that instantiates a model object to be
     * used for event handling later on and where this class is set as an observer
     * to the model class implementation.
     */
    public MastermindGraphicalVC() {
        this.model = new MastermindModel();
        this.model.addObserver(this);
    }

    /**
     * The start constructs the layout for the game using a BorderPane. The cons-
     * truction order is left, top, bottom, and center.
     *
     * @param primaryStage container(window) in which to render the UI.
     */

    @Override
    public void start(Stage primaryStage) throws Exception {
        primaryStage.setTitle("Mastermind");          //Sets the stage TITLE
        BorderPane mainPane = new BorderPane();       //Creates a BorderPane
        Scene scene = new Scene(mainPane, 500,710);   //Creates a new Scene Object

        //TOP of the Border Pane
        label1 = new Label();            //Creating the label that goes in top
        int fontsize = 12;               //Font size of text in the label
        label1.setFont(new Font("Calibri", fontsize));
        label1.setPadding(new Insets(20));
        label1.setAlignment(Pos.TOP_LEFT);
        //Prints out the number of guesses remaining, (LABEL Is used to do so).
        this.label1.setText("You have " + model.getRemainingGuesses() +
                            " guesses remaining");
        mainPane.setTop(label1);

        //CENTER of the border pane (makes a grid of buttons).
        mainPane.setCenter(this.makeGridPane());

        //LEFT of the BorderPane is created here for the clues grid
        VBox left = new VBox();
        left.setPadding(new Insets(65,0,0,0));
        left.getChildren().add(this.leftButtonPane());
        mainPane.setLeft(left);          //Setting the left of the border pane

        //RIGHT of the BorderPane to display the necessary buttons.
        VBox right = new VBox();
        right.setAlignment(Pos.TOP_LEFT);
        right.setPadding(new Insets(35,30,0,0)); //Sets spaces for right VBox
        Button peekBtn = new Button();           //Creating a Peek Button
        Button newBtn = new Button("New Game");  //Creating a NewGame button
        newBtn.setOnAction(event1 -> {           //Setting new game btn on Action
            model.reset();                       //calls the underlying model's
                                                 //reset method to reset the game
            peekBtn.setText("Peek");
        });
        peekBtn.setText("Peek");                 //PeekBtn;s text is set here
        // Setting Peek button on Action. Calls the underlying model's peek meth-
        // od to general the peek solution to be displayed upon update
        peekBtn.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                model.peek();
                if(peekBtn.getText().equals("Peek")){
                    peekBtn.setText("(Un)Peek");
                }
                else{
                    peekBtn.setText("Peek");
                }
            }
        });

        Button guessBtn = new Button("Guess");              //GUESS button.
        guessBtn.setOnAction(event ->  model.makeGuess());  //set on action

        right.getChildren().addAll(newBtn,peekBtn,guessBtn);
        right.setSpacing(12);               //setting the spacings between btns.
        mainPane.setRight(right);

        //SETTING the scene and displaying the guy in un-resizable mode.
        primaryStage.setScene(scene);
        primaryStage.setResizable(false);
        primaryStage.show();

    }

    /**
     * The makeGridPane method creates and returns a grid layout of buttons. These
     * buttons are the ones in the center for the user to fill in their guesses.
     *
     * @return grid pane that can be added to a region
     */
    private GridPane makeGridPane() {
        GridPane grid = new GridPane();
        int gap = 5;
        grid.setVgap(gap);      //Gap between grid cells
        grid.setHgap(gap);
        grid.setPadding(new Insets(30,15,15,0));
        int row;
        int col;
        for(row = 1; row<=11; row++) {
            for (col =1; col<=4; col++){
                Button btn = new Button();
                btn.setMinSize(50.0,50.0);
                btn.setStyle("-fx-background-color : lightgrey");
                grid.add(btn,col,12-row); //adding buttons to the grid
                btnArray.add(btn);        //Adding buttons to btn array
                final int r = row;
                final int c = col;
                btn.setOnAction(event -> model.choose(r, c));   //EventSetOnAction
            }
        }
        grid.setGridLinesVisible(false);  //For grid layout
        return grid;
    }


    /**
     * The leftButtonPane method creates and returns a grid layout of buttons. These
     * buttons are the ones  in the left region of the buttonPane to display the
     * clues generated to the user
     *
     * @return grid pane that can be added to a region
     */
    private GridPane leftButtonPane() {
        GridPane grid = new GridPane();
        double gap = 1.1;           //gap between the shape cells.
        grid.setVgap(gap);
        grid.setHgap(gap);
        grid.setPadding(new Insets(25));

        int row;
        int col;
        for(row = 1; row<=20; row++) {
            for (col =1; col<=2; col++){
                Circle circle = new Circle(13);
                circle.setFill(javafx.scene.paint.Color.LIGHTGREY); //Shape color
                grid.add(circle,col,21 - row);
                shapeArray.add(circle);         //Shapes Array
            }
        }
        grid.setGridLinesVisible(false);
        return grid;

    }

    /**
     * The update method updates the UI when the method calls update. The update
     * may change the appearance of the displayed message, clues, guesses and
     * solution.
     *
     * The update makes calls to the public interface of the model components to
     * determine the new values to display.
     *
     * @param o   the observable object.
     * @param arg an argument passed to the <code>notifyObservers</code> - not used
     */
    @Override
    public void update(java.util.Observable o, Object arg) {
        // Checking status for the label text message, condition are if the
        // game has been won, lost and the guesses remaining is equal to zero
        if (model.getVictoryStatus()) {
            this.label1.setText("You cracked the code!");
        //if there are not remaining guesses
        } else if (model.getRemainingGuesses()== 0){
            this.label1.setText("You ran out guesses!");
        //on the process just display the remaining guesses!
        }else{
            this.label1.setText("You have " + model.getRemainingGuesses() +
                    " guesses remaining");
        }

        //ArrayList of guessData integers.
        ArrayList<Integer> guessArray = model.getGuessData();
        for (int i = 0; i < guessArray.size(); i++) {
            int guessColor = guessArray.get(i);
            Button btn = btnArray.get(i);
            btn.setStyle("-fx-background-color :" + colorsArray[guessColor]+";");

        }

        // SOLUTION: Displaying the solution in the very top of the grid as
        // generated by the model's getSolution method
        ArrayList<Integer> solutionArray = model.getSolution();
        for (int i = 0; i < solutionArray.size(); i++){
            int solution = solutionArray.get(i);
            Button btn = btnArray.get(40 + i);
            btn.setStyle("-fx-background-color :" + colorsArray[solution]+";");


        // CLUE DATA: Getting and Displaying the clue data on the left grid of the
        // pane as generated by the model's getClueData method.
        }
        ArrayList<Character> clueArray = model.getClueData();
        for (char i = 0; i< clueArray.size(); i++) {
            char clueColor = clueArray.get(i);
            Circle circle = shapeArray.get(i);
            switch(clueColor) {
                case 'B':
                    circle.setFill(javafx.scene.paint.Color.BLACK);
                    break;
                case 'W':
                    circle.setFill(javafx.scene.paint.Color.WHITE);
                    break;
                default:
                    circle.setFill(javafx.scene.paint.Color.LIGHTGREY);
            }

        }

    }

    /**
     * The main entry point that launches the JavaFX GUI.
     * @param args not used
     */
    public static void main(String[] args){
        Application.launch(args);
    }
}
