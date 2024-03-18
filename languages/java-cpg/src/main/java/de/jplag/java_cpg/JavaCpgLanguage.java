package de.jplag.java_cpg;

import static de.jplag.java_cpg.transformation.TransformationRepository.*;

import java.io.File;
import java.util.List;
import java.util.Set;

import org.kohsuke.MetaInfServices;

import de.jplag.Language;
import de.jplag.ParsingException;
import de.jplag.Token;
import de.jplag.java_cpg.transformation.GraphTransformation;

/**
 * This class represents the frond end of the CPG module of JPlag.
 */
@MetaInfServices(de.jplag.Language.class)
public class JavaCpgLanguage implements Language {
    public static final int DEFAULT_MINIMUM_TOKEN_MATCH = 9;
    public static final String[] FILE_EXTENSIONS = {".java"};
    public static final String NAME = "Java Code Property Graph module";
    private static final String IDENTIFIER = "java-cpg";
    private final CpgAdapter cpgAdapter;

    public JavaCpgLanguage() {
        this.cpgAdapter = new CpgAdapter(allTransformations());
    }

    /**
     * Adds the given {@link GraphTransformation} to the list to apply to the submissions.
     * @param transformation the transformation
     */
    public void addTransformation(GraphTransformation<?> transformation) {
        this.cpgAdapter.addTransformation(transformation);
    }

    /**
     * Adds the given {@link GraphTransformation}s to the list to apply to the submissions.
     * @param transformations the transformations
     */
    public void addTransformations(GraphTransformation<?>[] transformations) {
        this.cpgAdapter.addTransformations(transformations);
    }

    @Override
    public String getIdentifier() {
        return IDENTIFIER;
    }

    @Override
    public String getName() {
        return NAME;
    }

    @Override
    public int minimumTokenMatch() {
        return DEFAULT_MINIMUM_TOKEN_MATCH;
    }

    @Override
    public List<Token> parse(Set<File> files, boolean normalize) throws ParsingException {
        return cpgAdapter.adapt(files, normalize);
    }

    @Override
    public boolean requiresCoreNormalization() {
        return false;
    }

    public void resetTransformations() {
        this.cpgAdapter.clearTransformations();
        this.cpgAdapter.addTransformations(this.obligatoryTransformations());
    }

    /**
     * Returns the set of transformations required to ensure that the tokenization works properly.
     * @return the array of obligatory transformations
     */
    private GraphTransformation<?>[] obligatoryTransformations() {
        return new GraphTransformation[] {wrapThenStatement, wrapElseStatement, wrapForStatement, wrapWhileStatement, wrapDoStatement};
    }

    /**
     * Returns a set of transformations suggested for use.
     * @return the array of recommended transformations
     */
    public GraphTransformation<?>[] standardTransformations() {
        return new GraphTransformation[] {removeOptionalOfCall,               // 3
                removeOptionalGetCall,              // 4
                moveConstantToOnlyUsingClass,       // 6
                inlineSingleUseVariable,            // 8
                removeLibraryRecord,                // 11
                removeEmptyRecord,                  // 16
        };
    }

    /**
     * Returns a set of all transformations.
     * @return the array of all transformations
     */
    public GraphTransformation<?>[] allTransformations() {
        return new GraphTransformation[] {ifWithNegatedConditionResolution,   // 1
                forStatementToWhileStatement,       // 2
                removeOptionalOfCall,               // 3
                removeOptionalGetCall,              // 4
                removeGetterMethod,                 // 5
                moveConstantToOnlyUsingClass,       // 6
                inlineSingleUseConstant,            // 7
                inlineSingleUseVariable,            // 8
                removeEmptyDeclarationStatement,    // 9
                removeImplicitStandardConstructor,  // 10
                removeLibraryRecord,                // 11
                removeLibraryField,                 // 12
                removeEmptyConstructor,             // 13
                removeUnsupportedConstructor,       // 14
                removeUnsupportedMethod,            // 15
                removeEmptyRecord,                  // 16
        };
    }

    @Override
    public String[] suffixes() {
        return FILE_EXTENSIONS;
    }

    @Override
    public boolean supportsNormalization() {
        return true;
    }
}
