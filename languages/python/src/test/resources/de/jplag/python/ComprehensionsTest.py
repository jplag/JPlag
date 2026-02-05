def test_list_comprehension_basic():
    numbers = [1, 2, 3, 4, 5]
    squares = [x**2 for x in numbers]
    return squares

def test_list_comprehension_with_condition():
    numbers = [1, 2, 3, 4, 5, 6, 7, 8, 9, 10]
    even_squares = [x**2 for x in numbers if x % 2 == 0]
    return even_squares

def test_nested_list_comprehension():
    matrix = [[1, 2, 3], [4, 5, 6], [7, 8, 9]]
    flattened = [item for row in matrix for item in row]
    return flattened

def test_set_comprehension_basic():
    numbers = [1, 2, 2, 3, 3, 4, 5, 5]
    unique_squares = {x**2 for x in numbers}
    return unique_squares

def test_set_comprehension_with_condition():
    numbers = [1, 2, 3, 4, 5, 6, 7, 8, 9, 10]
    even_unique_squares = {x**2 for x in numbers if x % 2 == 0}
    return even_unique_squares

def test_dict_comprehension_basic():
    countries = ['USA', 'Canada', 'Germany', 'France', 'Japan']
    capitals = ['Washington', 'Ottawa', 'Berlin', 'Paris', 'Tokyo']
    
    country_capitals = {country: capital for country, capital in zip(countries, capitals)}
    return country_capitals

def test_dict_comprehension_with_condition():
    students = ['Alice', 'Bob', 'Charlie', 'David', 'Eve', 'Frank', 'Grace', 'Henry']
    grades = [85, 92, 78, 95, 88, 91, 87, 93]
    
    high_achievers = {name: grade for name, grade in zip(students, grades) if grade >= 90}
    return high_achievers
