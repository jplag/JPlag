from typing import List

type StringList = List[str]
type NumberPair = tuple[int, int]


def test_type_aliases_usage():
    names: StringList = ["Alice", "Bob", "Charlie"]
    coordinates: NumberPair = (10, 20)
    
    def process_names(name_list: StringList) -> None:
        for name in name_list:
            print(f"Processing: {name}")
    
    process_names(names)
    return coordinates


def test_generic_type_aliases():
    type Matrix[T] = List[List[T]]
    type Callback[T, R] = callable[[T], R]
    
    int_matrix: Matrix[int] = [[1, 2], [3, 4]]
    str_callback: Callback[str, int] = lambda s: len(s)
    
    return int_matrix, str_callback


if __name__ == "__main__":
    test_type_aliases_usage()
    test_generic_type_aliases() 
