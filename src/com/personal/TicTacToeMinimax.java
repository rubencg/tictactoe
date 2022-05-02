package com.personal;

import java.util.*;
import java.util.function.Function;

public class TicTacToeMinimax {
    char[][] board;
    Scanner scanner;
    Random random;
    private String player1;
    private String player2;
    private String initCommand;

    public TicTacToeMinimax(String initialState) {
        random = new Random();
        this.scanner = new Scanner(System.in);
        board = new char[3][3];
        int counter = 0;
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                char c = initialState.charAt(counter++);
                board[i][j] = c == '_' ? ' ' : c;
            }
        }
    }

    public void printBoard() {
        System.out.println("---------");
        for (int i = 0; i < 3; i++) {
            System.out.print("| ");
            for (int j = 0; j < 3; j++) {
                System.out.print(board[i][j] + " ");
            }
            System.out.println("|");
        }
        System.out.println("---------");
    }

    private boolean winning(char[][] board, char player){
        return getWinner(board) == player;
    }

    // Get winner player
    private char getWinner(char[][] board){
        char winner = ' ';
        // horizontals
        // XXX______
        // ___XXX___
        // ______XXX
        for (int i = 0; i < 3; i++) {
            if (board[i][0] != ' ' && board[i][0] == board[i][1] && board[i][1] == board[i][2]) {
                winner = board[i][0];
                break;
            }
        }

        // verticals
        // X__X__X__
        // _X__X__X_
        // __X__X__X
        for (int i = 0; i < 3; i++) {
            if (board[0][i] != ' ' && board[0][i] == board[1][i] && board[1][i] == board[2][i]) {
                winner = board[0][i];
                break;
            }
        }

        // diagonals
        // X___X___X
        // __X_X_X__
        if (board[1][1] != ' ' && ((board[1][1] == board[0][0] && board[1][1] == board[2][2])
                || ((board[1][1] == board[0][2] && board[1][1] == board[2][0])))) {
            winner = board[1][1];
        }

        return winner;
    }

    private boolean isGameFinished() {
        boolean isGameFinished = false;

        char winner = getWinner(this.board);

        if(winner != ' '){
            isGameFinished = true;
        }
        if (isGameFinished) {
            System.out.println(winner + " wins");
            return true;
        }

        boolean areThereEmptySpaces = areThereEmptySpaces();
        if(!areThereEmptySpaces){
            System.out.println("Draw");
        }

        return !areThereEmptySpaces;
    }

    private boolean areThereEmptySpaces(){
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                if(board[i][j] == ' '){
                    return true;
                }
            }
        }
        return false;
    }

    private int[] getValuesFromInput(String userInput) {
        String[] values = userInput.split(" ");
        return new int[]{
                Integer.parseInt(values[0]),
                Integer.parseInt(values[1])
        };
    }

    private Integer parseIntOrNull(String value) {
        try {
            return Integer.parseInt(value);
        } catch (NumberFormatException e) {
            return null;
        }
    }

    private boolean validateUserInput(String userInput) {
        boolean isValid = false;
        if (!userInput.isEmpty()) {
            String[] values = userInput.split(" ");
            if (values.length == 2 &&
                    parseIntOrNull(values[0]) != null &&
                    parseIntOrNull(values[1]) != null
            ) {
                isValid = true;
            }
        }

        if (!isValid) {
            System.out.println("You should enter numbers!");
        }

        return isValid;
    }

    public void start() {
        setDifficultLevel();

        if(this.initCommand.equals("exit")){
            return;
        }

        printBoard();

        do {
            boolean isGameFinishedAfterMoveOne =
                    this.player1.equals("user") ? getUserInput() : getCpInput();
            if (isGameFinishedAfterMoveOne) break;
            boolean isGameFinishedAfterMoveTwo =
                    this.player2.equals("user") ? getUserInput() : getCpInput();
            if (isGameFinishedAfterMoveTwo) break;
        }while (!isGameFinished());
    }

    private boolean getCpInput() {
        String level = this.getCurrentPlayerName();
        System.out.println(String.format("Making move level \"%s\"", level));

        switch (level){
            case "easy":
                setToken(this.getRandomCoordinates());
                break;
            case "medium":
                setToken(this.getMediumCoordinates());
                break;
            case "hard":
                setToken(this.getHardCoordinates());
                break;
        }

        this.printBoard();
        if(isGameFinished()){
            return true;
        }
        return false;
    }

    private boolean getUserInput() {
        String userInput;
        boolean validateUserInputResult = false;
        boolean coordsAreInRangeResult = false;
        boolean coordsAreNotOccupiedResult = false;

        do{
            System.out.println("Enter the coordinates: ");
            userInput = this.scanner.nextLine();
            if(validateUserInputResult = validateUserInput(userInput)){
                int[] coordinates = getValuesFromInput(userInput);
                if((coordsAreInRangeResult = coordsAreInRange(coordinates))
                        && (coordsAreNotOccupiedResult = isAvailableCoords(coordinates, this.board))){
                    setToken(coordinates);
                    this.printBoard();
                    if(isGameFinished()){
                        return true;
                    }
                }
            }
        }while(!validateUserInputResult || !coordsAreInRangeResult || !coordsAreNotOccupiedResult);

        return false;
    }

    private void setDifficultLevel(){
        System.out.print("Input command: ");
        String input = "";
        do{
            input = this.scanner.nextLine();
        }while(!isValidDifficultInput(input));
    }

    private boolean returnInvalidInput(){
        System.out.println("Bad parameters!");
        System.out.print("Input command: ");
        return false;
    }

    String[] validPlayers = new String[]{"user", "easy", "medium", "hard"};
    private boolean isValidDifficultInput(String input){
        String[] args = input.split(" ");

        // Set init command
        if(args.length > 0 && (args[0].equals("start") || args[0].equals("exit"))){
            this.initCommand = args[0];
        } else {
            return returnInvalidInput();
        }
        // If exit command, return to terminate program
        if(this.initCommand.equals("exit")){
            return true;
        }

        if(args.length == 3 && Arrays.stream(validPlayers).anyMatch(c -> c.equals(args[1])) && Arrays.stream(validPlayers).anyMatch(c -> c.equals(args[2]))){
            this.player1 = args[1];
            this.player2 = args[2];
            return true;
        }
        return returnInvalidInput();
    }

    private int[] getRandomCoordinates(){
        int x = random.nextInt(3) + 1;
        int y = random.nextInt(3) + 1;
        int[] coords = new int[]{x, y};
        return isAvailableCoords(coords, this.board) ? coords : getRandomCoordinates();
    }

    private int[] getMediumCoordinates(){
        int[] coordinatesToWin = getOneMoveWinOrBlock(getPlayer());
        if(coordinatesToWin.length > 0) return coordinatesToWin;

        char opponent = getOpponent();
        int[] coordinatesToBlock = getOneMoveWinOrBlock(opponent);
        if(coordinatesToBlock.length > 0) return coordinatesToBlock;

        // If opponent can't be blocked or Pc can't win, return random coordinate
        return getRandomCoordinates();
    }

    private char[][] duplicateBoard(){
        char[][] dupBoard = new char[this.board.length][this.board[0].length];

        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                dupBoard[i][j] = this.board[i][j];
            }
        }

        return dupBoard;
    }

    private int[] getHardCoordinates(){
        int bestScore = Integer.MIN_VALUE;
        int[] move = new int[]{};
        char[][] minimaxBoard = duplicateBoard();

        List<int[]> availableSpots = getAvailableSpots(minimaxBoard);

        for (int[] spot: availableSpots) {
            minimaxBoard[spot[0]][spot[1]] = getPlayer(); // AI
            int score = getMinimaxScore(minimaxBoard, false);
            if(score > bestScore){
                move = spot;
                bestScore = score;
            }
            minimaxBoard[spot[0]][spot[1]] = ' '; // AI
        }

        return new int[]{move[0] + 1, move[1] +1};
    }

    private int getMinimaxScore(char[][] minimaxBoard, boolean isMaximizing){
        if(winning(minimaxBoard, getOpponent())){
            return -1;
        } else if(winning(minimaxBoard, getPlayer())){
            return 1;
        } else if (!areThereAvailableSpots(minimaxBoard)){
            return 0;
        }

        List<int[]> availableSpots = getAvailableSpots(minimaxBoard);

        int bestScore;
        if(isMaximizing){
            bestScore = Integer.MIN_VALUE;
            for (int[] spot: availableSpots) {
                minimaxBoard[spot[0]][spot[1]] = getPlayer(); // AI
                int score = getMinimaxScore(minimaxBoard, false);
                bestScore = Math.max(score, bestScore);
                minimaxBoard[spot[0]][spot[1]] = ' '; // AI
            }
        } else {
            bestScore = Integer.MAX_VALUE;
            for (int[] spot: availableSpots) {
                minimaxBoard[spot[0]][spot[1]] = getOpponent(); // human
                int score = getMinimaxScore(minimaxBoard, true);
                bestScore = Math.min(score, bestScore);
                minimaxBoard[spot[0]][spot[1]] = ' '; // human
            }
        }
        return bestScore;
    }

    private List<int[]> getAvailableSpots(char[][] board){
        List<int[]> spots = new ArrayList<>();

        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                if(board[i][j] == ' '){
                    spots.add(new int[]{i, j}); // TODO: Maybe sum
                }
            }
        }

        return spots;
    }


    private int[] getOneMoveWinOrBlock(char player){
        // Are there two in a row for horizontal
        for (int i = 0; i < 3; i++) {
            if(board[i][0] == player && board[i][1] == player){
                int[] cell = new int[]{i + 1, 2 + 1};
                if(isAvailableCoords(cell, this.board)){
                    return cell;
                }
            } else if(board[i][1] == player && board[i][2] == player){
                int[] cell = new int[]{i + 1, 1};
                if(isAvailableCoords(cell, this.board)){
                    return cell;
                }
            } else if(board[i][0] == player && board[i][2] == player){
                int[] cell = new int[]{i + 1, 1 + 1};
                if(isAvailableCoords(cell, this.board)){
                    return cell;
                }
            }
        }

        // Are there two in a row for vertical
        for (int i = 0; i < 3; i++) {
            if(board[0][i] == player && board[1][i] == player){
                int[] cell = new int[]{2 + 1, i + 1};
                if(isAvailableCoords(cell, this.board)){
                    return cell;
                }
            } else if(board[1][i] == player && board[2][i] == player){
                int[] cell = new int[]{1, i + 1};
                if(isAvailableCoords(cell, this.board)){
                    return cell;
                }
            } else if(board[0][i] == player && board[2][i] == player){
                int[] cell = new int[]{1 + 1, i + 1};
                if(isAvailableCoords(cell, this.board)){
                    return cell;
                }
            }
        }

        // Are there two in a row for diagonal
        int[][] possibleDiagonalScenario = new int[][]{
                {0, 0}, {0, 2}, {2, 0}, {2, 2}
        };
        Function<Integer, Integer> getOppositeCoordinate = (value) -> value == 0 ? 2 : 0;
        if(board[1][1] == player){
            for (int i = 0; i < possibleDiagonalScenario.length; i++) {
                if(board[possibleDiagonalScenario[i][0]][possibleDiagonalScenario[i][1]] == player){
                    int[] cell = new int[]{
                            getOppositeCoordinate.apply(possibleDiagonalScenario[i][0]) + 1,
                            getOppositeCoordinate.apply(possibleDiagonalScenario[i][1]) + 1
                    };
                    if(isAvailableCoords(cell, this.board)){
                        return cell;
                    }
                }
            }
        } else {
            for (int i = 0; i < possibleDiagonalScenario.length; i++) {
                if(board[possibleDiagonalScenario[i][0]][possibleDiagonalScenario[i][1]] == player &&
                        board[getOppositeCoordinate.apply(possibleDiagonalScenario[i][0])][getOppositeCoordinate.apply(possibleDiagonalScenario[i][1])] == player){
                    int[] cell = new int[]{ 1 + 1, 1 + 1 };
                    if(isAvailableCoords(cell, this.board)){
                        return cell;
                    }
                }
            }
        }

        return new int[]{};
    }

    private void setToken(int[] coordinates){
        this.board[coordinates[0]-1][coordinates[1]-1] = getPlayer();
    }

    private char getPlayer(){
        int xCounter = 0;
        int oCounter = 0;

        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                if(board[i][j] == 'X'){
                    xCounter++;
                }else if(board[i][j] == 'O'){
                    oCounter++;
                }
            }
        }
        return xCounter <= oCounter ? 'X' : 'O';
    }

    private boolean coordsAreInRange(int[] coordinates) {
        if(coordinates[0] < 1 || coordinates[0] > 3 ||
                coordinates[1] < 1 || coordinates[1] > 3){
            System.out.println("Coordinates should be from 1 to 3!");
            return false;
        }
        return true;
    }

    private boolean isAvailableCoords(int[] coordinates, char[][] board) {
        if(board[coordinates[0] - 1][coordinates[1] - 1] != ' '){
            if(isCurrentMoveAPlayerMove()){
                System.out.println("This cell is occupied! Choose another one!");
            }
            return false;
        }
        return true;
    }

    private boolean areThereAvailableSpots(char[][] board){
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                if(board[i][j] == ' '){
                    return true;
                }
            }
        }

        return false;
    }

    private boolean isCurrentMoveAPlayerMove(){
        return (getPlayer() == 'O' && this.player2.equals("user")) ||
                (getPlayer() == 'X' && this.player1.equals("user"));
    }

    private char getOpponent() {
        return getPlayer() == 'O' ? 'X' : 'O';
    }

    private String getCurrentPlayerName(){
        return getPlayer() == 'O' ? this.player2 : this.player1;
    }
}
