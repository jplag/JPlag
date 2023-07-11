import de.jplag.ParsingException;
import de.jplag.Token;
import de.jplag.typescript.TypeScriptLanguage;

import java.io.File;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class Test {
    @org.junit.jupiter.api.Test
    public void test() throws ParsingException {
        File f = new File("C:\\Users\\alexa\\Projects\\JPlagBranches\\TsLang\\languages\\typescript\\src\\test\\resources\\de\\jplag\\typescript\\methods.ts");
        TypeScriptLanguage l = new TypeScriptLanguage();
        Set<File> h = new HashSet<>();
        h.add(f);
        List<Token> ts = l.parse(h);
        System.out.println(ts);
    }
}
