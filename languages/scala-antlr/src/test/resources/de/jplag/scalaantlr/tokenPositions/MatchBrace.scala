> def test(): Unit = {
>     t match {
$       | MATCH_BEGIN 5
>         case 1 => test();
$         | CASE_STATEMENT 4
$                   | CASE_BEGIN 4
$                         | CASE_END 1
>         case 2 => {
$         | CASE_STATEMENT 4
$                   | CASE_BEGIN 1
>             println("Test");
>         }
$          | CASE_END 1
>         case _ =>
$         | CASE_STATEMENT 4
$                | CASE_BEGIN 2
>     }
$     | CASE_END 1
$     | MATCH_END 1
> }