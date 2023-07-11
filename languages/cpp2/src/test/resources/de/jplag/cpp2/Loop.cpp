void f() {
    do {
        goto a;
    } while (true);

    a:
    while (true) {
        break;
    }

    for (;;) {
        continue;
    }
    return;
}