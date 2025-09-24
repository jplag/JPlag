import sys
from typing import Dict, List, Optional


class Calculator:
    """A simple calculator class demonstrating basic Python features."""
    
    def __init__(self, name: str):
        self.name = name
        self.history: List[float] = []
    
    def add(self, a: float, b: float) -> float:
        """Add two numbers and store in history."""
        result = a + b
        self.history.append(result)
        return result
    
    def multiply(self, a: float, b: float) -> float:
        """Multiply two numbers and store in history."""
        result = a * b
        self.history.append(result)
        return result
    
    def get_average(self) -> Optional[float]:
        """Calculate average of all results in history."""
        if not self.history:
            return None
        return sum(self.history) / len(self.history)


def process_numbers(numbers: List[int]) -> Dict[str, int]:
    """Process a list of numbers and return statistics."""
    if not numbers:
        return {"count": 0, "sum": 0, "max": 0}
    
    total = 0
    maximum = numbers[0]
    
    for num in numbers:
        total += num
        if num > maximum:
            maximum = num
    
    return {
        "count": len(numbers)
    }


def fibonacci(n: int) -> List[int]:
    """Generate Fibonacci sequence up to n terms."""
    if n <= 0:
        return []
    elif n == 1:
        return [0]
    
    sequence = [0, 1]
    while len(sequence) < n:
        sequence.append(sequence[-1] + sequence[-2])
    
    return sequence


def validate_input(value: str) -> bool:
    """Validate if input can be converted to integer."""
    try:
        int(value)
        return True
    except ValueError:
        return False


def main():
    """Main function demonstrating various Python features."""
    # Basic operations
    calc = Calculator("MyCalculator")
    
    # Function calls and assignments
    result1 = calc.add(10, 5)
    result2 = calc.multiply(4, 3)
    
    # List operations
    numbers = [1, 2, 3, 4, 5]
    stats = process_numbers(numbers)
    
    # Control flow
    if stats["count"] > 0:
        print(f"Processed {stats['count']} numbers")
        print(f"Sum: {stats['sum']}, Max: {stats['max']}")
    
    # Loop with break and continue
    for i in range(10):
        if i % 2 == 0:
            continue
        if i > 7:
            break
        print(f"Odd number: {i}")
    
    # Named expression
    if (count := len(numbers)) > 3:
        print(f"Large list with {count} elements")
    
    # Test else branch with smaller list
    small_numbers = [1, 2]
    if (small_count := len(small_numbers)) > 3:
        print(f"Large list with {small_count} elements")
    else:
        print(f"Small list with {small_count} elements")
    
    # Exception handling
    try:
        sequence = fibonacci(8)
        print(f"Fibonacci: {sequence}")
    except Exception as e:
        print(f"Error: {e}")
    
    # Lambda and function calls
    square = lambda x: x ** 2
    squared_numbers = [square(n) for n in numbers]
    
    # Return statement
    return calc.get_average()


if __name__ == "__main__":
    average = main()
    print(f"Final average: {average}")
