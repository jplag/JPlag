void main() {
    ADT result1 = switch (condition()) {
        case true -> new ADT.A(10);
        case false -> new ADT.B();
    };

    ADT result2 = switch (result1) {
        case ADT.A(byte _) -> new ADT.A(0);
        case ADT.A(short v) -> new ADT.A(v + 10);
        default -> new ADT.B();
    };

    switch (result2.toString()) {
        case "B[]":
            IO.println("B");
            break;
        case "A[v=0]":
            IO.println("Byte");
            break;
        default:
            break;
    }

    switch ("A") {
        case "A":
        case "B":
            break;
    }
}

boolean condition() {
    return true;
}

sealed interface ADT {
    record A(int v) implements ADT {}
    record B() implements ADT {}
}
