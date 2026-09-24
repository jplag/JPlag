> void test(int param) {
>     switch(param) {
$     | SWITCH_BEGIN 5
>         case 1:
$         | CASE 3
>             break;
$             | BREAK 4
>         case 2:
$         | CASE 3
>             break;
>         default:
$         | DEFAULT 6
>             break;
>     }
$     | SWITCH_END 0
> }