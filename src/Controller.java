import javafx.scene.control.Button;
import javafx.scene.control.ToggleButton;
import javafx.scene.layout.HBox;

import java.util.ArrayList;

/**
 * Created by Hermann Grieder on 12.10.2016.
 * <p>
 * Controller class
 */
class Controller {
    private View view;
    private Model model;

    Controller(View view, Model model) {
        this.view = view;
        this.model = model;
    }

    void start() {
        view.createLayout();
        addEventHandler();
        view.show();
    }

    private void addEventHandler() {

        /* ************************************************************************** *
         *  This sections deals with 42 main Numbers. When one is clicked, it should  *
         *  be added to the list under the following condition:                       *
         *  The toggleButton went from deselected to selected and the row is not      *
         *  completely filled yet. If the row was already filled, the toggleButton    *
         *  should stay deselected. If the toggleButton was selected and now is       *
         *  deselected the number of the button should be removed from the list.      *
         *  Finally we check if the draw (6 numbers and 1 luckyNumber) is completely  *
         *  filled now, if so we advance to the next draw.                            *
         * ************************************************************************** */

        for (ToggleButton numberButton : view.getNumberButtons())
            numberButton.setOnAction(event -> {

                int currentDraw = model.getCurrentDraw();
                int number = Integer.parseInt(numberButton.getText());
                view.setActiveDrawBox(currentDraw, true);

                if (numberButton.isSelected() && drawComplete(currentDraw)) {
                    numberButton.setSelected(false);
                } else if (numberButton.isSelected() && !drawComplete(currentDraw)) {
                    model.addNumber(number);
                    view.updatePickedNumbers(model.getPickedNumbers().get(currentDraw), currentDraw);
                } else if (!numberButton.isSelected()) {
                    model.removeNumber(number);
                    view.updatePickedNumbers(model.getPickedNumbers().get(currentDraw), currentDraw);
                }

                if (drawComplete(model.getCurrentDraw())) {
                    advanceToNextDraw(currentDraw);
                }
            });

        /* ************************************************************************** *
         *  This sections deals with Lucky Numbers. When one is clicked it            *
         *  should be added to the list. If a lucky number has already been           *
         *  chosen previously, the old luck number Label should be deselected and     *
         *  the new label selected and the actual lucky number added to the list.     *
         *  Finally we check if the draw (6 numbers and 1 luckyNumber) is completely  *
         *  filled now, if so we advance to the next draw.                            *
         * ************************************************************************** */

        for (ToggleButton toggleButton : view.getLuckyNumbersButtons())
            toggleButton.setOnAction(event -> {

                int currentDraw = model.getCurrentDraw();
                int luckyNumber = Integer.parseInt(toggleButton.getText());
                int prevLuckyNumber = model.getPreviousLuckyNumber();
                view.setActiveDrawBox(currentDraw, true);

                if (prevLuckyNumber != 0 && prevLuckyNumber != luckyNumber) {
                    for (ToggleButton buttonToDeselect : view.getLuckyNumbersButtons()) {
                        if (buttonToDeselect.getText().equals(String.valueOf(prevLuckyNumber))) {
                            buttonToDeselect.setSelected(false);
                        }
                    }
                }

                model.addLuckyNumber(luckyNumber);
                view.updatePickedNumbers(luckyNumber, currentDraw);

                if (drawComplete(model.getCurrentDraw())) {
                    advanceToNextDraw(currentDraw);
                }
            });

        /* *************************************************************************** *
         *  This section deals with the event that a user wants to change an existing  *
         *  draw. Either one of the 6 numbers or the lucky number.                     *
         *  We find the index of the clicked HBox and get the numbers and the lucky    *
         *  number from the model. Then we reselect the numbers in the view.           *
         * *************************************************************************** */

        for (HBox myChoiceBox : view.getMyChoicesHBoxes()) {
            myChoiceBox.setOnMouseClicked(event -> {
                for (HBox oldActiveChoiceBox : view.getMyChoicesHBoxes()) {
                    if (oldActiveChoiceBox.getId() != null && oldActiveChoiceBox != event.getSource()) {
                        oldActiveChoiceBox.setId(null);
                        int index = view.getMyChoicesHBoxes().indexOf(oldActiveChoiceBox);
                        view.getTrashCans().get(index).setVisible(false);
                    }
                }
                if (model.getPickedNumbers() != null && model.getLuckyNumbers().size() > 0) {
                    int tempCurrentDraw = view.getMyChoicesHBoxes().indexOf(myChoiceBox);
                    int currentDraw;
                    if (model.getPickedNumbers().size() >= tempCurrentDraw) {
                        model.setCurrentDraw(tempCurrentDraw);
                        currentDraw = model.getCurrentDraw();
                    } else {
                        currentDraw = model.getPickedNumbers().size() - 1;
                    }
                    reselect(currentDraw);
                }
            });

         /* ************************************************************************** *
         *  This section deals with the "random number" button. It creates a random    *
         *  set of numbers and a lucky number for the current draw.                    *
         * *************************************************************************** */

            view.getRandomNumberBtn().setOnAction(event -> {
                int currentDraw = model.getCurrentDraw();

                ArrayList<Integer> randomNumbers = model.createRandomNumbers();

                if (model.getPickedNumbers() == null) {
                    model.addNewDraw();
                }
                if (model.getPickedNumbers().size() == 0 || model.getPickedNumbers().size() <= currentDraw) {
                    model.getPickedNumbers().add(randomNumbers);
                } else {
                    model.getPickedNumbers().set(currentDraw, randomNumbers);
                }

                int randomLuckyNumber = model.createRandomLuckyNumber();
                model.getLuckyNumbers().set(currentDraw, randomLuckyNumber);
                view.updatePickedNumbers(model.getPickedNumbers().get(currentDraw), currentDraw);
                view.updatePickedNumbers(randomLuckyNumber, currentDraw);
                if (drawComplete(model.getCurrentDraw())) {
                    advanceToNextDraw(currentDraw);
                }
            });

        /* *************************************************************************** *
         *  This section deals with the "trash can". When to show it, hide it and what *
         *  to do when the user presses it. The list in the model is deleted according *
         *  to the index of the trash can.                                             *
         * *************************************************************************** */

            myChoiceBox.setOnMouseEntered(event -> {
                if (view.isHover()) {
                    int index = view.getMyChoicesHBoxes().indexOf(myChoiceBox);
                    view.getTrashCans().get(index).setVisible(true);
                }
            });

            myChoiceBox.setOnMouseExited(event -> {
                if (view.isHover()) {
                    int index = view.getMyChoicesHBoxes().indexOf(myChoiceBox);
                    view.getTrashCans().get(index).setVisible(false);
                }
            });
        }

        for (Button trash : view.getTrashCans()) {
            trash.setOnAction(event -> {
                int currentDraw = view.getTrashCans().indexOf(trash);
                if (model.getPickedNumbers() != null
                        && model.getPickedNumbers().size() != 0
                        && model.getPickedNumbers().size() > currentDraw
                        && !model.getPickedNumbers().get(currentDraw).isEmpty()) {
                    int prevDraw = model.getCurrentDraw();
                    toggleActiveDrawBox(prevDraw, currentDraw);
                    model.setCurrentDraw(currentDraw);
                    model.getPickedNumbers().set(currentDraw, new ArrayList<>());
                    model.getLuckyNumbers().set(currentDraw, 0);
                    view.updatePickedNumbers(model.getPickedNumbers().get(currentDraw), currentDraw);
                    model.getPickedNumbers().remove(currentDraw);
                    view.clearLuckyNumber(currentDraw);
                    advanceToNextDraw(currentDraw);
                }
            });
        }

    /* *************************************************************************** *
     *  This section deals with the "play button". When the user presses it, the   *
     *  application collects all the numbers played. Then creates a random         *
     *  set of the winning numbers. Check what the user won. And show it.          *
     * *************************************************************************** */

        view.getPlayBtn().setOnAction(event -> {
            if (model.getPickedNumbers() != null && model.getPickedNumbers().size() >= model.getCurrentDraw()) {
                view.setHover(false);
                view.showLotteryPane();
                view.playDrawAnimation();
                view.updateOddsLabels(model.calculateOdds(), model.getPickedNumbers().size());
                addAnimationListener();
            }
        });
    }

    private void addAnimationListener() {
        view.getAnimation().setOnFinished(event -> {
            model.createWinningNumbers();
            view.highlightCorrectNumber(model.getWinningNumbers(), model.getPickedNumbers());
            view.showWinningNumbers(model.getWinningNumbers());
            view.getBackButton().setVisible(true);
        });

        view.getBackButton().setOnAction(event -> {
            view.setHover(true);
            model.init();
            advanceToNextDraw(model.getCurrentDraw());
            model.setPreviousDraw(model.getCurrentDraw());
            view.reset();
        });
    }

    // ******************** ****************************** ************************* //
    //                                                                               //
    //                                  Methods                                      //
    //                                                                               //
    // ******************** ****************************** ************************* //

    /**
     * Re-selects the numbers of a previous draw and the draw the user clicked on.
     *
     * @param draw The draw index which should be reselected
     */
    private void reselect(int draw) {
        view.setActiveDrawBox(draw, true);
        view.reselect(model.getPickedNumbers().get(draw), model.getLuckyNumbers().get(draw));
        view.getTrashCans().get(draw).setVisible(true);
    }

    /**
     * Checks if the current draw is completely filled out.
     *
     * @return True if all numbers and one lucky number have been selected
     */
    private boolean drawComplete(int draw) {
        return model.isDrawComplete(draw);
    }

    /**
     * Advances the currentDraw to the next empty or partially filled draw.
     * Toggles the current draw box off and the next draw box on.
     * Deselects all the numbers and the lucky number.
     *
     * @return Returns the index of the next draw
     */
    private void advanceToNextDraw(int currentDraw) {
        int nextDraw = model.advanceToNextDraw();
        toggleActiveDrawBox(currentDraw, nextDraw);
        view.updateCostToPlay(model.getPickedNumbers(), model.getLuckyNumbers());
        view.deselectAll();
    }

    /**
     * Toggles the active draws box visually.
     *
     * @param currentDraw Index of draw box to toggle off
     * @param nextDraw    Index of draw box to toggle on
     */
    private void toggleActiveDrawBox(int currentDraw, int nextDraw) {
        view.setActiveDrawBox(currentDraw, false);
        view.setActiveDrawBox(nextDraw, true);
    }

}