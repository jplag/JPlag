package de.jplag.endtoend.architecture;

import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.fields;
import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.noClasses;

import com.tngtech.archunit.junit.AnalyzeClasses;
import com.tngtech.archunit.junit.ArchTest;
import com.tngtech.archunit.lang.ArchRule;

@AnalyzeClasses(packages = "de.jplag")
public class JPlagArchitectureTest {
    @ArchTest
    public static final ArchRule enforceCorrectLogger = noClasses().should().accessClassesThat()
            .haveNameMatching(java.util.logging.Logger.class.getName());

    @ArchTest
    public static final ArchRule enforceNameOfLogger = fields().that().haveRawType(org.slf4j.Logger.class).should().haveName("logger");
}
