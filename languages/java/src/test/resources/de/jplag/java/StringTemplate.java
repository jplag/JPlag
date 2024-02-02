public class StringTemplate {
    void test() {
        int param1 = 1;
        String param2 = "test";

        String result = STR."prefix \{param1} infix + \{param2.length()} suffix";
    }
}