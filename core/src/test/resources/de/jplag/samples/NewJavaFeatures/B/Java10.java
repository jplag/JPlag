import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class Java10 {
    
    List<Optional<String>> test;
    
    public Java10() {
        // Typed variables as usual:
        test = new ArrayList<>();
        List<Optional<String>> variable = test;
        int length = variable.toString().length();
        test = variable;
        test.add(Optional.of(variable.toString()));
        System.out.println(length);
    }

}
