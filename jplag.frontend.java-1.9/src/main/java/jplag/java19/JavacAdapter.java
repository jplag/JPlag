package jplag.java19;

import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;

import javax.tools.Diagnostic;
import javax.tools.DiagnosticCollector;
import javax.tools.JavaCompiler;
import javax.tools.JavaFileObject;
import javax.tools.StandardJavaFileManager;
import javax.tools.ToolProvider;

import com.sun.source.tree.CompilationUnitTree;
import com.sun.source.tree.LineMap;
import com.sun.source.util.JavacTask;
import com.sun.source.util.SourcePositions;
import com.sun.source.util.Trees;

public class JavacAdapter {

    private static final JavaCompiler javac = ToolProvider.getSystemJavaCompiler();

    public int parseFiles(File dir, File[] pathedFiles, final Parser parser) {
        final StandardJavaFileManager jfm = javac.getStandardFileManager(null, null, null);
        DiagnosticCollector<? super JavaFileObject> diagListen = new DiagnosticCollector<>();
        final JavaCompiler.CompilationTask task = javac.getTask(null, jfm, diagListen, null, null, jfm.getJavaFileObjects(pathedFiles));
        Iterable<? extends CompilationUnitTree> asts = null;
        try {
            asts = ((JavacTask) task).parse();
        } catch (IOException e) {
            e.printStackTrace();
        }
        final Trees trees = Trees.instance(task);
        final SourcePositions positions = trees.getSourcePositions();
        for (final CompilationUnitTree ast : asts) {
            final String filename;
            if (dir == null)
                filename = ast.getSourceFile().getName();
            else {
                filename = Paths.get(dir.toURI()).relativize(Paths.get(ast.getSourceFile().toUri())).toString();
            }
            final LineMap map = ast.getLineMap();
            ast.accept(new TokenGeneratingTreeScanner(filename, parser, map, positions, ast), null);
            parser.add(JavaTokenConstants.FILE_END, filename, 1, -1, -1);
        }
        int errors = 0;
        for (Diagnostic<?> diagItem : diagListen.getDiagnostics()) {
            if (diagItem.getKind() == javax.tools.Diagnostic.Kind.ERROR) {
                errors++;
            }
        }
        return errors;
    }

}
