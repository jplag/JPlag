package edu.kit.informatik;


public final class Main {

    private InputState state;// = InputState.PLAYER_NAMES;
    private InputState state2 = InputState.PLAYER_NAMES;
    private int result;
    private int result2;

    private Main() {
        throw new IllegalStateException();
    }

    public static void main(String[] args) {

        state = InputState.PLAYER_COUNT;

        state2 = InputState.PLAYER_COUNT;

        if (state == InputState.PLAYER_COUNT) {
            result2 = 100;
        }

        result = 400;
    }

}
