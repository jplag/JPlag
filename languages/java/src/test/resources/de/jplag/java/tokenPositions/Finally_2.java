> class Finally {
>     public void test() {
>         try(InputStream inputStream = new FileInputStream("/some/file.txt")) {
>             throw new RuntimeException();
>         } finally {
$                   | J_FINALLY_BEGIN 1
>             System.out.println("Cleanup");
>         }
$         | J_FINALLY_END 1
>     }
> }