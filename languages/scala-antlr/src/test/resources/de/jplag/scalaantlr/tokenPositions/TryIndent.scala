> def test(): Unit = {
>     try
$     | TRY_BEGIN 3
>         println("Test");
>     catch
$     | CATCH_BEGIN 5
>         case e: IOException => println("Error");
>     finally
$     | CATCH_END 8
$     | FINALLY 7
>         println("Finally");
> }