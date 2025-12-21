package edu.kit.informatik;
public class GameBoard {
    private String[] board = { "-", "-", "-", "-", "-", "-", "-", "-", "-" };
    public GameBoard() {}
    public boolean checkHorizontal() {
        int counter = 0;
        for (int begin = 0; begin <= 6; begin = begin + 3) {
            counter = 0;
            for (int i = begin; i < begin + 3; i++) {
                if (board[i] == "o") {
                    counter++;
                } else if (board[i] == "x") {
                    counter--;
                }
            }
            if (counter == 3 || counter == -3) {
                return true;
            }
        }
        return false;
    }
    public boolean checkVertical() {
        int counter = 0;
        for (int begin = 0; begin <= 2; begin++) {
            counter = 0;
            for (int i = begin; i <= begin + 6; i = i + 3) {
                if (board[i] == "o") {
                    counter++;
                } else if (board[i] == "x") {
                    counter--;
                }
            }
            if (counter == 3 || counter == -3) {
                return true;
            }
        }
        return false;
    }
    public boolean checkDiagonals(int start) {
        int add = 0;
        int limit = 0;
        int counter = 0;
        if (start == 0) {
            add = 4;
            limit = 8;
        } else if (start == 2) {
            add = 2;
            limit = 6;
        }
        for (int begin = start; begin <= limit; begin = begin + add) {
            if (board[begin] == "o") {
                counter++;
            } else if (board[begin] == "x") {
                counter--;
            }
        }
        return (counter == 3 || counter == -3);
    }
    public int freeFieldNumber(int start, int add) {
        for (int i = start; i < 9; i = i + add) {
            if (isFree(i)) {
                return i;
            }
        }
        return -1;
    }
    public boolean isFree(int number) {
        return (board[number] == "-");
    }
    public boolean hasWon() {
        return (checkHorizontal() || checkVertical() || checkDiagonals(0) || checkDiagonals(2));
    }
    public String[] placeSymbol(String player, int number) {
        board[number] = player;
        return board;
    }
    @Override public String toString() {
        String output = "";
        for (int j = 0; j < 9; j++) {
            if (j == 2 || j == 5) {
                output = output + board[j] + "\n";
            } else {
                output = output + board[j];
            }
        }
        return output;
    }
    public int winCondition(String player) {
        for (int i = 0; i < 9; i++) {
            if (board[i] == "-") {
                board[i] = player;
                if (hasWon() == true) {
                    board[i] = "-";
                    return i;
                } else {
                    board[i] = "-";
                }
            }
        }
        return -1;
    }
}
