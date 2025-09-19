>public class Catch {
>    void x() {
>        try {
>        } catch (Exception e) {
$          | J_CATCH_BEGIN 5
>        } catch (Throwable t) {
$        | J_CATCH_END 1
$          | J_CATCH_BEGIN 5
>        }
$        | J_CATCH_END 1
>    }
>}