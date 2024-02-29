package de.jplag.java_cpg;

import de.jplag.ParsingException;
import de.jplag.Token;
import de.jplag.java_cpg.transformation.GraphTransformation;
import de.jplag.java_cpg.transformation.TransformationRepository;
import org.kohsuke.MetaInfServices;

import java.io.File;
import java.util.List;
import java.util.Set;

/**
 * This class represents the frond end of the CPG module of JPlag.
 */
@MetaInfServices(de.jplag.Language.class)
public class Language implements de.jplag.Language {
    public static final int DEFAULT_MINIMUM_TOKEN_MATCH = 9;
    public static final String[] FILE_EXTENSIONS = {".java"};
    private static final String IDENTIFIER = "java-cpg";
    public static final String NAME = "Java Code Property Graph module";
    private final CpgAdapter cpgAdapter;

    public Language() {
        this.cpgAdapter = new CpgAdapter(standardTransformations());
    }

    @Override
    public String[] suffixes() {
        return FILE_EXTENSIONS;
    }

    @Override
    public String getName() {
        return NAME;
    }

    @Override
    public String getIdentifier() {
        return IDENTIFIER;
    }

    @Override
    public int minimumTokenMatch() {
        return DEFAULT_MINIMUM_TOKEN_MATCH;
    }

    @Override
    public List<Token> parse(Set<File> files) throws ParsingException {
        return cpgAdapter.adapt(files);
    }

    private GraphTransformation<?>[] standardTransformations() {
        return new GraphTransformation[]{
            // TODO: Specify set of standard transformations

            TransformationRepository.wrapThenStatement,
            TransformationRepository.wrapElseStatement,
            TransformationRepository.removeLibraryRecords,
            TransformationRepository.moveConstantToOnlyUsingClass,
            TransformationRepository.inlineSingleUseConstant,
            TransformationRepository.inlineSingleUseVariable,
            TransformationRepository.removeEmptyDeclarationStatement,
            TransformationRepository.removeImplicitStandardConstructor,
            TransformationRepository.ifWithNegatedConditionResolution,
            TransformationRepository.removeEmptyRecord
        };
    }

    /**
     * Adds the given {@link GraphTransformation}s to the list to apply to the submissions.
     * @param transformations the transformations
     */
    public void addTransformations(GraphTransformation<?>[] transformations) {
        this.cpgAdapter.addTransformations(transformations);
    }

    public void resetTransformations() {
        this.cpgAdapter.clearTransformations();
        this.cpgAdapter.addTransformations(standardTransformations());
    }
}
