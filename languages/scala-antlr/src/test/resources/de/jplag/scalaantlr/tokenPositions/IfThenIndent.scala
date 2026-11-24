> def test(): Unit = {
>     if
$     | IF 2
>         true
>     then
>         println("true");
$         | IF_BEGIN 8
>     else
$     | IF_END 8
$     | ELSE 4
>         println("false");
$         | ELSE_BEGIN 8
> }
$ | ELSE_END 8
