> class Test {
>     void test(String param) {
>         int result = switch(param) {
>             case "a" -> {
>                 yield 1;
$                 | J_YIELD 5
>             }
>             default -> {
>                 yield 2;
$                 | J_YIELD 5
>             }
>         };
>     }
> }