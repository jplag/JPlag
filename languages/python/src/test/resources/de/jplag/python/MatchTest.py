def test_match(value):
    match value:
        case 1:
            return "one"
        case 2:
            return "two"
        case _:
            return "other" 
