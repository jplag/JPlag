void a(int a, int b, int x, int y) {
    if (a < b) {
        x = 5;
    } else if (a > b) {
        {
            y = 10;
        }
        x = y + b;
    } else {
        y = -20;
    }
}