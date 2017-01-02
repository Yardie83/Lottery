import javafx.animation.Timeline;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ToggleButton;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Hermann Grieder on 12.10.2016.
 * <p>
 * View class
 */
class View {
    private ArrayList<ToggleButton> luckyNumbersButtons;
    private ArrayList<ToggleButton> numberButtons;
    private ArrayList<ArrayList<Label>> myNumbers;
    private ArrayList<Label> myLuckyNumbers;
    private ArrayList<HBox> myChoicesHBoxes;
    private ArrayList<Button> trashCans;
    private Button randomNumberBtn;
    private Button backButton;
    private Button playBtn;
    private Stage stage;
    private HBox root;
    private Label totalPriceLbl;
    private Timeline animation;
    private Label nrOfTipsLbl;
    private Label tipsTextLbl;
    private HBox numbersBox;
    private boolean hover;
    private VBox leftBox;
    private ArrayList<Label> oddsLabels;
    private Label oddsTitle;

    View( Stage stage ) {

        this.stage = stage;
        this.numberButtons = new ArrayList<>();
        this.luckyNumbersButtons = new ArrayList<>();
        this.myNumbers = new ArrayList<>();
        this.myLuckyNumbers = new ArrayList<>();
        this.hover = true;

    }

    void createLayout() {

        root = new HBox();

        // ******************** ****************************** ************************* //
        //                                                                               //
        //                       Number selection / Left Side                            //
        //                                                                               //
        // ******************** ****************************** ************************* //

        leftBox = new VBox();
        leftBox.setPrefWidth( 350 );
        leftBox.setTranslateX( 400 );
        leftBox.setTranslateY( 50 );

        //********************* 42 number ToggleButtons ********************************//

        Pane numbersPane = new Pane();

        int number = 0;
        for ( int i = 1; i <= 7; i++ ) {
            for ( int j = 1; j <= 6; j++ ) {
                number++;
                ToggleButton button = new ToggleButton( String.valueOf( number ) );
                button.setLayoutX( j * 40 );
                button.setLayoutY( i * 40 );
                button.setPrefSize( 40, 40 );
                numberButtons.add( button );
                numbersPane.getChildren().add( button );
            }
        }

        //********************* 6 lucky number ToggleButtons ****************************//

        HBox luckyNumbersBox = new HBox();

        for ( int i = 1; i <= 6; i++ ) {
            ToggleButton button = new ToggleButton( String.valueOf( i ) );
            button.setPrefSize( 40, 40 );
            button.getStyleClass().add( "luckyButton" );
            luckyNumbersBox.getChildren().add( button );
            luckyNumbersButtons.add( button );
        }

        VBox luckyNumbersVBox = new VBox();
        luckyNumbersBox.setTranslateX( 40 );
        luckyNumbersBox.setTranslateY( 20 );

        Pane luckyNumbersPane = new Pane();
        luckyNumbersPane.getChildren().add( luckyNumbersBox );
        luckyNumbersVBox.getChildren().addAll( luckyNumbersPane );

        //*************************** Random Numbers Button ******************************//

        randomNumberBtn = new Button( "Random numbers" );
        randomNumberBtn.setId( "randomBtn" );
        randomNumberBtn.setTranslateX( 85 );
        randomNumberBtn.setTranslateY( 50 );

        //*************************** Play Lottery Area **********************************//

        HBox playLotteryPane = new HBox();
        playLotteryPane.setId( "playLotteryPane" );
        nrOfTipsLbl = new Label( "0" );
        nrOfTipsLbl.setId( "nrOfTipsLbl" );
        tipsTextLbl = new Label( "Tipps à CHF 2.50" );
        tipsTextLbl.setId( "tipsTextLbl" );
        totalPriceLbl = new Label( "CHF 0.00" );
        totalPriceLbl.setId( "totalPriceLbl" );
        playLotteryPane.getChildren().addAll( nrOfTipsLbl, tipsTextLbl, totalPriceLbl );

        playBtn = new Button( "Play" );
        playBtn.setId( "playBtn" );


        leftBox.getChildren().addAll( numbersPane, luckyNumbersVBox, randomNumberBtn, playLotteryPane, playBtn );

        // ******************** ****************************** ************************* //
        //                                                                               //
        //                       Picked Numbers / Right Side                             //
        //                                                                               //
        // ******************** ****************************** ************************* //

        VBox rightBox = new VBox( 10 );
        rightBox.setPrefWidth( 400 );
        rightBox.setTranslateX( 400 );
        rightBox.setTranslateY( 50 );

        myChoicesHBoxes = new ArrayList<>();
        trashCans = new ArrayList<>();

        for ( int i = 1; i <= Model.MAX_DRAWS; i++ ) {

            HBox myChoice = new HBox();
            myChoice.getStyleClass().add( "myChoiceHBox" );

            Label numberLbl = new Label( String.valueOf( i ) + "." );
            myChoice.getChildren().add( numberLbl );

            ArrayList<Label> numberLabels = new ArrayList<>();

            for ( int j = 0; j < 6; j++ ) {
                Label myNumber = new Label();
                myNumber.getStyleClass().add( "myNumberLbl" );
                numberLabels.add( myNumber );
                myChoice.getChildren().add( myNumber );
            }

            myNumbers.add( numberLabels );

            Label luckyNumber = new Label();
            luckyNumber.getStyleClass().add( "myLuckyNumberLbl" );

            myLuckyNumbers.add( luckyNumber );

            myChoice.getChildren().add( luckyNumber );

            Button trashCan = new Button( "" );
            trashCan.setVisible( false );
            trashCan.setId( "trashCan" );
            trashCans.add( trashCan );

            myChoice.getChildren().add( trashCan );

            myChoicesHBoxes.add( myChoice );

            rightBox.getChildren().add( myChoice );
        }

        //********************************* Basic JavaFX SetUp ************************************//

        root.getChildren().addAll( leftBox, rightBox );
        root.setPrefSize( 1318, 800 );
        Scene scene = new Scene( root );
        scene.getStylesheets().add( this.getClass().getResource( "styles.css" ).toExternalForm() );
        stage.setScene( scene );
        stage.setTitle( "Lottery" );
    }

    // ******************** ****************************** ************************* //
    //                                                                               //
    //                                  Methods                                      //
    //                                                                               //
    // ******************** ****************************** ************************* //


    void updatePickedNumbers( ArrayList<Integer> pickedNumbers, int currentDraw ) {

        for ( Label myPicks : myNumbers.get( currentDraw ) ) {
            myPicks.setText( "" );
        }

        for ( int i = 0; i < pickedNumbers.size(); i++ ) {
            myNumbers.get( currentDraw ).get( i ).setText( pickedNumbers.get( i ).toString() );
        }
    }

    void updatePickedNumbers( int luckyNumber, int currentDraw ) {
        myLuckyNumbers.get( currentDraw ).setText( String.valueOf( luckyNumber ) );
    }

    void updateCostToPlay( ArrayList<ArrayList<Integer>> pickedNumbers, ArrayList<Integer> luckyNumbers ) {

        int nrOfGamesPlayed = 0;
        if ( pickedNumbers != null ) {
            for ( int i = 0; i < pickedNumbers.size(); i++ ) {
                if ( pickedNumbers.get( i ).size() == 6 && luckyNumbers.get( i ) != 0 ) {
                    nrOfGamesPlayed++;
                }
            }
        }

        nrOfTipsLbl.setText( String.valueOf( nrOfGamesPlayed ) );

        if ( nrOfGamesPlayed == 1 ) {
            tipsTextLbl.setText( "Tipp à CHF 2.50" );
        } else {
            tipsTextLbl.setText( "Tipps à CHF 2.50" );
        }
        double totalPrice = nrOfGamesPlayed * 2.50;
        totalPriceLbl.setText( "CHF " + String.valueOf( totalPrice ) + "0" );
    }

    void clearLuckyNumber( int currentDraw ) {
        myLuckyNumbers.get( currentDraw ).setText( "" );
    }

    void deselectAll() {
        for ( ToggleButton numberButton : numberButtons ) {
            numberButton.setSelected( false );
        }

        for ( ToggleButton luckyNumberButton : luckyNumbersButtons ) {
            luckyNumberButton.setSelected( false );
        }
    }

    void reselect( ArrayList<Integer> pickedNumbers, int luckyNumber ) {
        for ( ToggleButton numberButton : numberButtons ) {
            numberButton.setSelected( false );
            for ( int pickedNumber : pickedNumbers ) {
                if ( numberButton.getText().equals( String.valueOf( pickedNumber ) ) ) {
                    numberButton.setSelected( true );
                }
            }
        }

        for ( ToggleButton luckyNumberButton : luckyNumbersButtons ) {
            luckyNumberButton.setSelected( false );
            if ( luckyNumberButton.getText().equals( String.valueOf( luckyNumber ) ) ) {
                luckyNumberButton.setSelected( true );
            }
        }
    }

    void showLotteryPane() {
        //***************** LotteryBox Left Side shown on Play Pressed *************************//

        VBox lotteryBox = new VBox( 20 );
        lotteryBox.setPrefWidth( 350 );
        lotteryBox.setTranslateX( 400 );
        lotteryBox.setTranslateY( 50 );

        //********************* 6 number Labels and 1 Lucky Number Label ***********************//

        // numberPane is where the 6 numbers and 1 lucky number drawn will be shown
        VBox numbersPane = new VBox();

        numbersBox = new HBox( 5 );
        for ( int i = 1; i <= 7; i++ ) {
            Label number = new Label( String.valueOf( i ) );
            if ( i <= 6 ) {
                number.setId( "drawNumber" );
                number.getStyleClass().add( "drawLabels" );
            } else {
                number.setId( "drawLuckyNumber" );
                number.getStyleClass().add( "drawLabels" );
            }
            numbersBox.getChildren().add( number );
        }
        numbersPane.getChildren().add( numbersBox );

        lotteryBox.getChildren().add( numbersPane );

        // oddsBox is where the odds of winning are shown depending on how many draws where played
        VBox oddsBox = new VBox();
        oddsBox.setPrefWidth( 350 );
        oddsTitle = new Label( "Odds of winning" );
        oddsTitle.setId( "oddsTitleLbl" );
        oddsBox.getChildren().add( oddsTitle );
        oddsLabels = new ArrayList<>();
        for ( int i = 6; i > 2; i-- ) {
            Label oddsJokerLbl = new Label( i + " + Joker: " );
            oddsLabels.add( oddsJokerLbl );
            oddsJokerLbl.getStyleClass().add( "oddsLabel" );
            Label oddsNoJokerLbl = new Label( i + ": " );
            oddsLabels.add( oddsNoJokerLbl );
            oddsNoJokerLbl.getStyleClass().add( "oddsLabel" );
            oddsBox.getChildren().addAll( oddsJokerLbl, oddsNoJokerLbl );
        }

        lotteryBox.getChildren().add( oddsBox );

        backButton = new Button( "Replay" );
        backButton.setId( "backButton" );
        backButton.setVisible( false );

        lotteryBox.getChildren().add( backButton );

        root.getChildren().set( 0, lotteryBox );
    }

    void showWinningNumbers( ArrayList<Integer> winningNumbers ) {
        for ( int i = 0; i < numbersBox.getChildren().size(); i++ ) {
            Label label = (Label) numbersBox.getChildren().get( i );
            label.textProperty().set( String.valueOf( winningNumbers.get( i ) ) );
        }
    }

    void highlightCorrectNumber( ArrayList<Integer> winningNumbers, ArrayList<ArrayList<Integer>> pickedNumbers ) {
        List<Integer> numbers = winningNumbers.subList( 0, 6 );
        int luckyNumber = winningNumbers.get( 6 );
        for ( int number : numbers ) {
            for ( int i = 0; i < pickedNumbers.size(); i++ ) {
                for ( int j = 0; j < myNumbers.get( i ).size(); j++ ) {
                    Label label = myNumbers.get( i ).get( j );
                    if ( Integer.parseInt( label.getText() ) == number ) {
                        label.setStyle( "-fx-background-color: lightskyblue" );
                    }
                }
                Label luckyNumberLabel = myLuckyNumbers.get( i );
                if ( Integer.parseInt( luckyNumberLabel.getText() ) == luckyNumber ) {
                    luckyNumberLabel.setStyle( "-fx-background-color: lightskyblue" );
                }
            }
        }
    }

    void updateOddsLabels( ArrayList<BigInteger> oddsList, int nrOfDraws ) {
        oddsTitle.setText( "Odds of Winning with " + nrOfDraws + " tickets" );
        for ( int i = 0; i < oddsList.size(); i++ ) {
            BigInteger odd = oddsList.get( i );
            String currentText = oddsLabels.get( i ).getText();
            String newText = currentText + "\n1 in " + String.valueOf( odd );
            oddsLabels.get( i ).setText( newText );
        }
    }

    void playDrawAnimation() {
        DrawAnimation drawAnimation = new DrawAnimation( numbersBox );
        animation = drawAnimation.getTimeLine();
        animation.play();
    }

    void reset() {
        root.getChildren().set( 0, leftBox );

        for ( ArrayList<Label> numberLabels : myNumbers ) {
            for ( Label numberLabel : numberLabels ) {
                numberLabel.setText( "" );
                numberLabel.setStyle( "-fx-background-color: antiquewhite" );
            }
        }

        for ( Label luckyNumberLabel : myLuckyNumbers ) {
            luckyNumberLabel.setText( "" );
            luckyNumberLabel.setStyle( "-fx-background-color: -fx-myColor2" );
        }

    }

    void show() {
        stage.show();
    }

    // ******************** ****************************** ************************* //
    //                                                                               //
    //                             Getter & Setters                                  //
    //                                                                               //
    // ******************** ****************************** ************************* //

    /**
     *
     * @param index Index of the draw box to set active or inactive
     * @param active True sets the box to active, false sets it to inactive
     */
    void setActiveDrawBox( int index, boolean active ) {
        if ( active ) {
            myChoicesHBoxes.get( index ).setId( "myChoiceHBox-active" );
        } else {
            myChoicesHBoxes.get( index ).setId( null );
        }
    }

    ArrayList<ToggleButton> getNumberButtons() {
        return numberButtons;
    }

    ArrayList<ToggleButton> getLuckyNumbersButtons() {
        return luckyNumbersButtons;
    }

    ArrayList<HBox> getMyChoicesHBoxes() {
        return myChoicesHBoxes;
    }

    ArrayList<Button> getTrashCans() {
        return trashCans;
    }

    Button getRandomNumberBtn() {
        return randomNumberBtn;
    }

    Button getPlayBtn() {
        return playBtn;
    }

    Button getBackButton() {
        return backButton;
    }

    Timeline getAnimation() {
        return animation;
    }

    void setHover( boolean hover ) {
        this.hover = hover;
        for ( HBox myChoiceBox : myChoicesHBoxes ) {
            if ( hover ) {
                myChoiceBox.getStyleClass().clear();
                myChoiceBox.getStyleClass().add( "myChoiceHBox" );
            } else {
                myChoiceBox.getStyleClass().clear();
                myChoiceBox.getStyleClass().add( "myChoiceHBox-inactive" );
            }
        }
    }

    boolean isHover() {
        return hover;
    }
}
