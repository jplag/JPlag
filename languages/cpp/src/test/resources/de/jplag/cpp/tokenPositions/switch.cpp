> void test(int param) {
>     switch(param) {
$     | SWITCH_BEGIN 6
>         case 1:
$         | CASE 4
>             break;
$             | BREAK 5
>         case 2:
$         | CASE 4
>             break;
>         default:
$         | DEFAULT 7
>             break;
>     }
$     | SWITCH_END 1
> }