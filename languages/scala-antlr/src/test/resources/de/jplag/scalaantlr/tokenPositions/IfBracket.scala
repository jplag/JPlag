> def test(): Unit = {
>     if (true) {
$     | IF 2
$               | IF_BEGIN 1
>     } else {
$     | IF_END 1
$       | ELSE 4
$            | ELSE_BEGIN 1
>     }
$     | ELSE_END 1
> }