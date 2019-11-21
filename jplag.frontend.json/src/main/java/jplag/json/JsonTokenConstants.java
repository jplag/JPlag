package jplag.json;

public interface JsonTokenConstants extends jplag.TokenConstants {

    final static int FILE_END = 0;
    final static int TRUE = 1;
    final static int FALSE = 2;
    final static int NULL = 3;
    int NUMBER = 4;
    int OBJECT_START = 5;
    int OBJECT_END = 6;
    int ARRAY_START = 7;
    int ARRAY_END = 8;
    int DYNAMIC_START = 9;
}
