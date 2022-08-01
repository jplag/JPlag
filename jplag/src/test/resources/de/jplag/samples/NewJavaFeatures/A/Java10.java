import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class Java10 {
    
    List<Optional<String>> test;
    
    public Java10() {
        // Var type for local variables (JEP 286):
        test = new ArrayList<>();
        var variable = test;
        var length = variable.toString().length();
        test = variable;
        test.add(Optional.of(variable.toString()));
        System.out.println(length);
    }

}
