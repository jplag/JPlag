>class Cond {
>    void test() {
>        int x = condition() ? 1 + 2 : Math.abs(-1);
$                | J_COND 1
>    }
>
>    boolean condition() {
>        return true;
>    }
>}