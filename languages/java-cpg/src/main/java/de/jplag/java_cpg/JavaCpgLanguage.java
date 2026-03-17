package de.jplag.java_cpg;

import static de.jplag.java_cpg.transformation.TransformationRepository.forStatementToWhileStatement;
import static de.jplag.java_cpg.transformation.TransformationRepository.ifWithNegatedConditionResolution;
import static de.jplag.java_cpg.transformation.TransformationRepository.inlineSingleUseConstant;
import static de.jplag.java_cpg.transformation.TransformationRepository.inlineSingleUseVariable;
import static de.jplag.java_cpg.transformation.TransformationRepository.moveConstantToOnlyUsingClass;
import static de.jplag.java_cpg.transformation.TransformationRepository.removeEmptyConstructor;
import static de.jplag.java_cpg.transformation.TransformationRepository.removeEmptyDeclarationStatement;
import static de.jplag.java_cpg.transformation.TransformationRepository.removeEmptyRecord;
import static de.jplag.java_cpg.transformation.TransformationRepository.removeGetterMethod;
import static de.jplag.java_cpg.transformation.TransformationRepository.removeImplicitStandardConstructor;
import static de.jplag.java_cpg.transformation.TransformationRepository.removeLibraryField;
import static de.jplag.java_cpg.transformation.TransformationRepository.removeLibraryRecord;
import static de.jplag.java_cpg.transformation.TransformationRepository.removeOptionalGetCall;
import static de.jplag.java_cpg.transformation.TransformationRepository.removeOptionalOfCall;
import static de.jplag.java_cpg.transformation.TransformationRepository.removeUnsupportedConstructor;
import static de.jplag.java_cpg.transformation.TransformationRepository.removeUnsupportedMethod;
import static de.jplag.java_cpg.transformation.TransformationRepository.wrapDoStatement;
import static de.jplag.java_cpg.transformation.TransformationRepository.wrapElseStatement;
import static de.jplag.java_cpg.transformation.TransformationRepository.wrapForStatement;
import static de.jplag.java_cpg.transformation.TransformationRepository.wrapThenStatement;
import static de.jplag.java_cpg.transformation.TransformationRepository.wrapWhileStatement;

import java.io.File;
import java.util.List;
import java.util.Set;

import de.jplag.Language;
import de.jplag.ParsingException;
import de.jplag.Token;
import de.jplag.java_cpg.transformation.GraphTransformation;

import com.google.auto.service.AutoService;

/**
 * This class represents the frond end of the CPG module of JPlag.
 */
@AutoService(Language.class)
public class JavaCpgLanguage implements Language {
    private static final int DEFAULT_MINIMUM_TOKEN_MATCH = 9;
    private static final List<String> FILE_EXTENSIONS = List.of(".java");
    private static final String NAME = "Java Code Property Graph module";
    private static final String IDENTIFIER = "java-cpg";
    private final CpgAdapter cpgAdapter;

    /**
     * Creates a new {@link JavaCpgLanguage}.
     */
    public JavaCpgLanguage() {
        this.cpgAdapter = new CpgAdapter(allTransformations());
    }

    /**
     * Adds the given {@link GraphTransformation} to the list to apply to the submissions.
     * @param transformation the transformation
     */
    public void addTransformation(GraphTransformation transformation) {
        this.cpgAdapter.addTransformation(transformation);
    }

    /**
     * Adds the given {@link GraphTransformation}s to the list to apply to the submissions.
     * @param transformations the transformations
     */
    public void addTransformations(GraphTransformation[] transformations) {
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
        try {
            return cpgAdapter.adapt(files, normalize);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            return List.of();
        }
    }

    @Override
    public boolean requiresCoreNormalization() {
        return false;
    }

    /**
     * Resets the set of transformations to the obligatory transformations only.
     */
    public void resetTransformations() {
        this.cpgAdapter.clearTransformations();
        this.cpgAdapter.addTransformations(this.obligatoryTransformations());
        this.cpgAdapter.addTransformations(this.standardTransformations());
    }

    /**
     * Returns the set of transformations required to ensure that the tokenization works properly.
     * @return the array of obligatory transformations
     */
    private GraphTransformation[] obligatoryTransformations() {
        return new GraphTransformation[] {wrapThenStatement, wrapElseStatement, wrapForStatement, wrapWhileStatement, wrapDoStatement};
    }

    /**
     * Returns a set of transformations suggested for use.
     * @return the array of recommended transformations
     */
    public GraphTransformation[] standardTransformations() {
        return new GraphTransformation[] {removeOptionalOfCall, removeOptionalGetCall, moveConstantToOnlyUsingClass, inlineSingleUseVariable,
                removeLibraryRecord, removeEmptyRecord,};
    }

    /**
     * Returns a set of all transformations.
     * @return the array of all transformations
     */
    public GraphTransformation[] allTransformations() {
        return new GraphTransformation[] {ifWithNegatedConditionResolution, forStatementToWhileStatement, removeOptionalOfCall, removeOptionalGetCall,
                removeGetterMethod, moveConstantToOnlyUsingClass, inlineSingleUseConstant, inlineSingleUseVariable, removeEmptyDeclarationStatement,
                removeImplicitStandardConstructor, removeLibraryRecord, removeLibraryField, removeEmptyConstructor, removeUnsupportedConstructor,
                removeUnsupportedMethod, removeEmptyRecord,};
    }

    @Override
    public List<String> fileExtensions() {
        return FILE_EXTENSIONS;
    }

    @Override
    public boolean supportsNormalization() {
        return true;
    }

    @Override
    public boolean hasPriority() {
        return false;
    }
}
