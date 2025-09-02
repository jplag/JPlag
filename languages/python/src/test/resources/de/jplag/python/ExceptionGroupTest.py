def test_exception_groups():
    def risky_operation():
        errors = []
        errors.append(ValueError("First error"))
        errors.append(TypeError("Second error"))
        if errors:
            raise ExceptionGroup("Multiple errors", errors)
    
    try:
        risky_operation()
    except* ValueError as eg:
        print(f"Handling ValueError group: {eg}")
    except* TypeError as eg:
        print(f"Handling TypeError group: {eg}")
    except* Exception as eg:
        print(f"Handling other exceptions: {eg}")


def test_nested_exception_groups():
    def create_nested_errors():
        inner_errors = [ValueError("Inner value error"), TypeError("Inner type error")]
        outer_errors = [RuntimeError("Outer runtime error")]
        
        try:
            raise ExceptionGroup("Inner group", inner_errors)
        except ExceptionGroup as inner_group:
            outer_errors.append(inner_group)
            raise ExceptionGroup("Outer group", outer_errors)
    
    try:
        create_nested_errors()
    except* ValueError as vg:
        print("Caught ValueError in group")
    except* TypeError as tg:
        print("Caught TypeError in group")
    except* RuntimeError as rg:
        print("Caught RuntimeError in group")
    except* ExceptionGroup as eg:
        print("Caught nested ExceptionGroup")


if __name__ == "__main__":
    test_exception_groups()
    test_nested_exception_groups() 
