"""
Regression test file for Python 3.6 compatibility.
This file only uses features supported by Python 3.6 to ensure
the new Tree-sitter-based module produces the same tokens as
the old ANTLR-based module.
"""

import os
import sys
from typing import Dict, List, Optional


class Calculator:
    """A simple calculator class to test class definitions."""
    
    def __init__(self, name: str):
        self.name = name
        self.history = []
    
    def add(self, a: float, b: float) -> float:
        """Add two numbers and store in history."""
        result = a + b
        self.history.append(("add", a, b, result))
        return result
    
    def subtract(self, a: float, b: float) -> float:
        """Subtract two numbers and store in history."""
        result = a - b
        self.history.append(("subtract", a, b, result))
        return result
    
    def get_history(self) -> List[tuple]:
        """Get calculation history."""
        return self.history


def test_imports_and_assignments():
    """Test basic imports and assignments."""
    import math
    import random as rand
    
    pi = math.pi
    random_number = rand.randint(1, 100)
    return pi, random_number


def test_control_flow():
    """Test various control flow constructs."""
    numbers = [1, 2, 3, 4, 5]
    result = 0
    
    # Test for loop
    for num in numbers:
        if num % 2 == 0:
            result += num
        else:
            continue
    
    # Test while loop
    counter = 0
    while counter < 10:
        if counter == 5:
            break
        counter += 1
    
    return result, counter


def test_functions_and_lambdas():
    """Test function definitions and lambda expressions."""
    def square(x: float) -> float:
        return x * x
    
    def power(base: float, exponent: float) -> float:
        if exponent == 0:
            return 1
        elif exponent < 0:
            return 1 / power(base, -exponent)
        else:
            return base * power(base, exponent - 1)
    
    # Test lambda expressions
    double = lambda x: x * 2
    add = lambda x, y: x + y
    
    return square(4), power(2, 3), double(5), add(3, 7)


def test_exception_handling():
    """Test exception handling constructs."""
    try:
        result = 10 / 0
    except ZeroDivisionError:
        result = 0
    except Exception as e:
        result = -1
    finally:
        cleanup = True
    
    return result, cleanup


def test_with_statements():
    """Test with statements for resource management."""
    with open("temp.txt", "w") as f:
        f.write("Hello, World!")
    
    try:
        with open("temp.txt", "r") as f:
            content = f.read()
    except FileNotFoundError:
        content = "File not found"
    
    return content


def test_async_await():
    """Test async/await syntax (Python 3.5+)."""
    import asyncio
    
    async def async_function():
        await asyncio.sleep(0.1)
        return "async result"
    
    async def main():
        result = await async_function()
        return result
    
    return main


def test_list_comprehensions():
    """Test list comprehensions."""
    numbers = [1, 2, 3, 4, 5, 6, 7, 8, 9, 10]
    
    # Basic list comprehension
    squares = [x * x for x in numbers]
    
    # List comprehension with condition
    even_squares = [x * x for x in numbers if x % 2 == 0]
    
    # Nested list comprehension
    matrix = [[i + j for j in range(3)] for i in range(3)]
    
    return squares, even_squares, matrix


def test_dict_operations():
    """Test dictionary operations."""
    person = {
        "name": "John",
        "age": 30,
        "city": "New York"
    }
    
    # Dictionary comprehension
    squares_dict = {x: x * x for x in range(5)}
    
    # Dictionary operations
    person["email"] = "john@example.com"
    age = person.get("age", 0)
    
    return person, squares_dict, age


def test_generators():
    """Test generator functions."""
    def fibonacci(n: int):
        a, b = 0, 1
        for _ in range(n):
            yield a
            a, b = b, a + b
    
    def count_up_to(n: int):
        i = 0
        while i < n:
            yield i
            i += 1
    
    fib_list = list(fibonacci(10))
    count_list = list(count_up_to(5))
    
    return fib_list, count_list


def main():
    """Main function to run all tests."""
    # Test basic functionality
    calc = Calculator("Test Calculator")
    result1 = calc.add(5, 3)
    result2 = calc.subtract(10, 4)
    
    # Test imports and assignments
    pi, rand_num = test_imports_and_assignments()
    
    # Test control flow
    sum_result, counter = test_control_flow()
    
    # Test functions and lambdas
    square_result, power_result, double_result, add_result = test_functions_and_lambdas()
    
    # Test exception handling
    div_result, cleanup_flag = test_exception_handling()
    
    # Test with statements
    file_content = test_with_statements()
    
    # Test async function
    async_func = test_async_await()
    
    # Test comprehensions
    squares, even_squares, matrix = test_list_comprehensions()
    
    # Test dictionaries
    person, squares_dict, age = test_dict_operations()
    
    # Test generators
    fib_sequence, count_sequence = test_generators()
    
    # Return all results
    return {
        "calculator": calc,
        "results": [result1, result2],
        "pi": pi,
        "random": rand_num,
        "sum": sum_result,
        "counter": counter,
        "square": square_result,
        "power": power_result,
        "double": double_result,
        "add": add_result,
        "division": div_result,
        "cleanup": cleanup_flag,
        "content": file_content,
        "async": async_func,
        "squares": squares,
        "even_squares": even_squares,
        "matrix": matrix,
        "person": person,
        "squares_dict": squares_dict,
        "age": age,
        "fibonacci": fib_sequence,
        "count": count_sequence
    }


if __name__ == "__main__":
    results = main()
    print("All tests completed successfully!")
    print(f"Results: {results}") 
