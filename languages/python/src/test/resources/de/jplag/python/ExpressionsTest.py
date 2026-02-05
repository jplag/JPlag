def test_lambda_expressions():
    add_function = lambda x, y: x + y
    square_function = lambda number: number ** 2
    positive_filter = lambda value: value > 0
    
    numbers = [1, 2, 3, 4, 5]
    
    squared_numbers = list(map(lambda x: x ** 2, numbers))
    even_numbers = list(filter(lambda x: x % 2 == 0, numbers))
    
    return squared_numbers, even_numbers


def test_named_expressions():
    numbers = [1, 2, 3, 4, 5, 6, 7, 8, 9, 10]
    
    if (length := len(numbers)) > 5:
        print(f"List has {length} elements")
    
    while (current_item := numbers.pop()) > 5:
        print(f"Processing {current_item}")
        if len(numbers) == 0:
            break
    
    filtered_values = [doubled for x in range(10) if (doubled := x * 2) > 5]
    
    if (lambda_result := test_lambda_expressions()):
        return lambda_result


def test_complex_expressions():
    process_data = lambda data: [x * 2 for x in data if x > 0]
    
    if (processed_data := process_data([1, -2, 3, -4, 5])):
        sum_evens = lambda number_list: sum(x for x in number_list if x % 2 == 0)
        return sum_evens(processed_data)


if __name__ == "__main__":
    test_lambda_expressions()
    test_named_expressions()
    test_complex_expressions()
