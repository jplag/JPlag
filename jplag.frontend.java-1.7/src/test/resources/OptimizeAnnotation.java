package foo;
import static java.lang.annotation.ElementType.CONSTRUCTOR;
import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.ElementType.LOCAL_VARIABLE;
import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.ElementType.PACKAGE;
import static java.lang.annotation.ElementType.PARAMETER;
import static java.lang.annotation.ElementType.TYPE;

import java.lang.annotation.Target;

public @interface OptimizeAnnotation {
	String value();

	String assignedTo();

	String assignedToDefault() default "";

	String[] assignedToMany();

	String[] assignedToNone();

	int priority();

	enum Priority {
		LOW, NORM, HIGH;
	}

	Priority prio();

}