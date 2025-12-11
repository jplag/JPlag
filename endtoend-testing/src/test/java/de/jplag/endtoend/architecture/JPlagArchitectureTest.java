package de.jplag.endtoend.architecture;

import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.fields;
import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.noClasses;

import com.tngtech.archunit.junit.AnalyzeClasses;
import com.tngtech.archunit.junit.ArchTest;
import com.tngtech.archunit.lang.ArchRule;

/**
 * Architecture tests to enforce logging practices in the JPlag codebase.
 */
@AnalyzeClasses(packages = "de.jplag")
public class JPlagArchitectureTest {
    /**
     * Prevent the use of java.util.logging.Logger in any class. Enforces use of a proper logging abstraction like SLF4J.
     */
    @ArchTest
    public static final ArchRule enforceCorrectLogger = noClasses().should().accessClassesThat()
            .haveNameMatching(java.util.logging.Logger.class.getName());

    /**
     * Enforce that all SLF4J loggers are named exactly "logger".
     */
    @ArchTest
    public static final ArchRule enforceNameOfLogger = fields().that().haveRawType(org.slf4j.Logger.class).should().haveName("logger");
}
