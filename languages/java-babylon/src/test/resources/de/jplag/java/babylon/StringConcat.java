void main() {
    IO.println("Hello " + "World " + 12 + " (" + (true || false) + ")");

    String result = "";
    for (int i = 0; i < 100; i++) {
        result += "\n" + (i % 15 == 0 ? "FizzBuzz" : i % 5 == 0 ? "Buzz" : i % 3 == 0 ? "Fizz" : Integer.toString(i));
    }

    IO.println(result);
}
