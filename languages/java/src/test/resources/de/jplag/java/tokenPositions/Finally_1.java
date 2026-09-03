> class Finally {
>     public void test() {
>         try {
>             throw new RuntimeException();
>         } catch (Exception e) {
>             return;
>         } finally {
$                   | J_FINALLY_BEGIN 1
>             System.out.println("Cleanup");
>         }
$         | J_FINALLY_END 1
>     }
> }