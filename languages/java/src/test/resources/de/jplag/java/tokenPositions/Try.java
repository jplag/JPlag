> class Test {
>     void test() {
>         try {
$         | J_TRY_BEGIN 3
>         } catch (Exception e) {
>             throw new RuntimeException(e);
>         }
$         | J_TRY_END 1
>     }
> }