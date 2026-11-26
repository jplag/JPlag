> def test(): Unit = {
>     try {
$     | TRY_BEGIN 3
>         println("Test");
>     } catch {
$       | CATCH_BEGIN 5
>         case e: IOException => println("Had an IOException trying to read that file");
>     } finally {
$     | CATCH_END 1
$       | FINALLY 7
>     }
> }