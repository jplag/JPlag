> def test(): Unit = {
>     t match
$       | MATCH_BEGIN 5
>         case 1 => {
$         | CASE_STATEMENT 4
$                   | CASE_BEGIN 1
>         }
$          | CASE_END 1
>         case 2 =>
$         | CASE_STATEMENT 4
>             println("Test");
$             | CASE_BEGIN 8
> }
$ | CASE_END 8
$ | MATCH_END 8