package edu.kit.informatik;
public class Board {
    private boolean[] occupied = new boolean[9];
    private char[] board = new char[9];
    public Board() {
        for (int i = 0; i < 9; i++) {
            board[i] = '-';
        }
    }
    public boolean isOccupied(int index) {
        if (occupied[index] == true) {
            return true;
        } else {
            return false;
        }
    }
    private boolean rowWin(char player) {
        for (int i = 0; i < 9; i += 3) {
            if (board[i] == player && board[i + 1] == player && board[i + 2] == player) {
                return true;
            }
        }
        return false;
    }
    private boolean columnWin(char player) {
        for (int i = 0; i < 3; i++) {
            if (board[i] == player && board[i + 3] == player && board[i + 6] == player) {
                return true;
            }
        }
        return false;
    }
    private boolean diagonalWin(char player) {
        int counter = 0;
        for (int i = 0; i < 9; i += 4) {
            if (board[i] == player) {
                counter++;
            }
        }
        if (counter == 3) {
            return true;
        }
        counter = 0;
        for (int i = 2; i < 7; i += 2) {
            if (board[i] == player) {
                counter++;
            }
        }
        if (counter == 3) {
            return true;
        }
        return false;
    }
    public boolean playerWon() {
        if (rowWin('x') == true || columnWin('x') == true || diagonalWin('x') == true) {
            return true;
        } else if (rowWin('o') == true || columnWin('o') == true || diagonalWin('o') == true) {
            return true;
        }
        return false;
    }
    public boolean isBoardFull() {
        for (boolean field : occupied) {
            if (field == false) {
                return false;
            }
        }
        return true;
    }
    public void placeOnBoard(char playerSymbol, int index) {
        if (occupied[index] == false) {
            board[index] = playerSymbol;
            occupied[index] = true;
        }
    }
    public String toString() {
        String output = "";
        for (int i = 0; i < 9; i++) {
            if ((i + 1) % 3 == 0 && i != 8) {
                output = output + board[i] + "\n";
            } else {
                output = output + board[i];
            }
        }
        return output;
    }
    public int winOpportunity(char player) {
        for (int i = 0; i < 9; i++) {
            if (occupied[i] == false) {
                board[i] = player;
                if (playerWon() == true) {
                    board[i] = '-';
                    return i;
                } else {
                    board[i] = '-';
                }
            }
        }
        return 9;
    }
}
