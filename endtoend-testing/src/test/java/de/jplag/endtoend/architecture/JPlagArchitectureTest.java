package de.jplag.endtoend.architecture;

import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.noClasses;

import com.tngtech.archunit.junit.AnalyzeClasses;
import com.tngtech.archunit.junit.ArchTest;
import com.tngtech.archunit.lang.ArchRule;

@AnalyzeClasses(packages = "de.jplag")
public class JPlagArchitectureTest {
    @ArchTest
    public static final ArchRule enforceCorrectLogger = noClasses().should().accessClassesThat()
            .haveNameMatching(java.util.logging.Logger.class.getName());
}
