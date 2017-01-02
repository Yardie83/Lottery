import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;

/**
 * Created by Hermann Grieder on 12.10.2016.
 * <p>
 * Model class
 */
class Model {

    static final int MAX_DRAWS = 14;
    private final int NUMS_TO_PICKS = 6;

    private ArrayList<ArrayList<Integer>> pickedNumbers;
    private ArrayList<Integer> winningNumbers;
    private ArrayList<Integer> luckyNumbers;
    private int previousLuckyNumber;
    private int previousDraw;
    private int currentDraw;


    Model() {
        currentDraw = 0;
        init();
    }

    /**
     * Sets the initial state of the pickedNumbers and luckyNumbers list.
     */
    public void init() {
        if (pickedNumbers != null && luckyNumbers != null) {
            pickedNumbers.clear();
            luckyNumbers.clear();
        } else {
            pickedNumbers = new ArrayList<>(MAX_DRAWS);
            luckyNumbers = new ArrayList<>(MAX_DRAWS);
        }

        for (int i = 0; i < MAX_DRAWS; i++) {
            luckyNumbers.add(0);
        }
    }

    /**
     * Adds the selected number to the current draw
     *
     * @param number The picked number to be added
     */
    void addNumber(int number) {

        if (pickedNumbers.size() == 0 || (pickedNumbers.size() < MAX_DRAWS && isDrawComplete(previousDraw))) {
            addNewDraw();
        }
        if (pickedNumbers.size() >= currentDraw + 1 && pickedNumbers.get(currentDraw).size() < NUMS_TO_PICKS) {
            pickedNumbers.get(currentDraw).add(number);
            Collections.sort(pickedNumbers.get(currentDraw));
        }
    }

    void removeNumber(int buttonNr) {
        pickedNumbers.get(currentDraw).remove(new Integer(buttonNr));
    }

    /**
     * Adds a new ArrayList to the pickedNumbers ArrayList in order to
     * play another row.
     */
    void addNewDraw() {
        pickedNumbers.add(new ArrayList<>(NUMS_TO_PICKS));
    }

    void addLuckyNumber(int luckyNumber) {
        previousLuckyNumber = luckyNumber;
        luckyNumbers.set(currentDraw, luckyNumber);
    }

    /**
     * Checks if a draw (a row) is completely filled out
     *
     * @return True if 6 numbers and 1 lucky number have been selected
     */
    boolean isDrawComplete(int drawNumber) {
        return pickedNumbers.size() >= drawNumber + 1
                && pickedNumbers.get(drawNumber).size() == NUMS_TO_PICKS
                && luckyNumbers.get(drawNumber) != 0;
    }

    /**
     * Finds the number of the next draw. If all rows have already been filled out the
     * next draw is the last draw worked or clicked on.
     *
     * @return The number of the next draw
     */
    int advanceToNextDraw() {
        previousDraw = currentDraw;
        int nextDraw = 0;
        boolean found = false;
        int i = 0;
        if (pickedNumbers.size() <= MAX_DRAWS) {
            while (!found && i < MAX_DRAWS - 1) {
                if (i < pickedNumbers.size() && pickedNumbers.get(i).size() == NUMS_TO_PICKS && luckyNumbers.get(i) != 0) {
                    nextDraw = ++i;
                } else {
                    found = true;
                }
            }
        } else {
            nextDraw = previousDraw;
        }
        currentDraw = nextDraw;
        return nextDraw;
    }

    /**
     * Creates 6 unique random numbers without from 1 to 42
     *
     * @return An ArrayList of 6 unique random numbers
     */
    ArrayList<Integer> createRandomNumbers() {
        ArrayList<Integer> randomNumbers = new ArrayList<>();

        Random rand = new Random();
        while (randomNumbers.size() != NUMS_TO_PICKS) {
            int randNumber = rand.nextInt(42) + 1;
            if (!randomNumbers.contains(randNumber)) {
                randomNumbers.add(randNumber);
            }
        }
        Collections.sort(randomNumbers);
        return randomNumbers;
    }

    /**
     * Creates the actual lucky number for the lottery
     *
     * @return A random number between 1 and 6
     */
    int createRandomLuckyNumber() {
        Random rand = new Random();
        return rand.nextInt(6) + 1;
    }

    //**************************** Lottery Overlay Methods **********************************//

    /**
     * Creates the winning numbers and the lucky number
     */
    void createWinningNumbers() {
        Random rand = new Random();
        winningNumbers = new ArrayList<>(7);

        while (winningNumbers.size() < NUMS_TO_PICKS) {
            int randomNumber = rand.nextInt(42) + 1;
            if (!winningNumbers.contains(randomNumber)) {
                // Add 6 random winning number
                winningNumbers.add(randomNumber);
            }
        }
        Collections.sort(winningNumbers);
        // Add 1 random lucky number
        winningNumbers.add(rand.nextInt(6) + 1);
    }

    /**
     * @return
     */
    ArrayList<BigInteger> calculateOdds() {
        ArrayList<BigInteger> odds = new ArrayList<>();
        for (int i = 6; i > 2; i--) {
            for (int j = 0; j < 2; j++) {
                Boolean withLuckyNumber = (j == 0);
                BigInteger odd = calculateChance(i, withLuckyNumber);
                odds.add(odd);
            }
        }
        return odds;
    }

    private BigInteger calculateChance(int n, boolean withLuckyNumber) {

        BigInteger correctGuesses = BigInteger.valueOf(n);
        BigInteger luckyNumbers = new BigInteger("6");
        BigInteger outOf = new BigInteger("6");
        BigInteger pool = new BigInteger("42");

        BigInteger combinationsNumbersTotal = combinations(pool, outOf);

        BigInteger combinationsNumbersFirst = combinations(outOf, correctGuesses);
        BigInteger combinationsNumbersSecond = factorial(pool.subtract(outOf)).divide((factorial((pool
                .subtract(outOf)).subtract(outOf.subtract(correctGuesses))).multiply(factorial(outOf
                .subtract(correctGuesses)))));

        BigInteger combinationsNumberMult = combinationsNumbersFirst.multiply(combinationsNumbersSecond);
        if (withLuckyNumber) {
            return (((combinationsNumbersTotal.multiply(luckyNumbers))).divide((combinationsNumberMult
                    .multiply(BigInteger.valueOf(pickedNumbers.size())))));
        } else {
            combinationsNumberMult = combinationsNumberMult.multiply(luckyNumbers.subtract(BigInteger.ONE));
            return (((combinationsNumbersTotal.multiply(luckyNumbers)).divide((combinationsNumberMult
                    .multiply(BigInteger.valueOf(pickedNumbers.size()))))));
        }
    }

    private BigInteger combinations(BigInteger n, BigInteger c) {
        return factorial(n).divide(factorial(c).multiply(factorial(
                n.subtract(c))));
    }

    private static BigInteger factorial(BigInteger number) {
        if (number.equals(BigInteger.ZERO)) {
            return BigInteger.ONE;
        }
        if (!(number.equals(BigInteger.ONE))) {
            number = number.multiply(factorial(number.subtract(BigInteger.ONE)));
        }
        return number;
    }

    //******************************** Getter & Setters *************************************//

    void setCurrentDraw(int currentDraw) {
        this.currentDraw = currentDraw;
    }

    int getPreviousLuckyNumber() {
        return previousLuckyNumber;
    }

    int getCurrentDraw() {
        return currentDraw;
    }

    ArrayList<ArrayList<Integer>> getPickedNumbers() {
        if (pickedNumbers.size() == 0) {
            return null;
        }
        return pickedNumbers;
    }

    ArrayList<Integer> getLuckyNumbers() {
        return luckyNumbers;
    }

    ArrayList<Integer> getWinningNumbers() {
        return winningNumbers;
    }

    void setPreviousDraw(int previousDraw) {
        this.previousDraw = previousDraw;
    }
}
