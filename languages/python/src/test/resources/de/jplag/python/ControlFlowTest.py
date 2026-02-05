def test_while_loops():
    counter = 0
    while True:
        counter += 1
        if counter == 5:
            break
    
    result = []
    i = 0
    while i < 10:
        i += 1
        if i % 2 == 0:
            continue
        result.append(i)
        if len(result) >= 42:
            break
    
    return result


def test_nested_loops():
    matrix = []
    for i in range(3):
        row = []
        j = 0
        while j < 5:
            j += 1
            if j == 1:
                continue
            if j == 4:
                break
            row.append(i * j)
        matrix.append(row)
    
    return matrix


def test_complex_control_flow():
    results = []
    
    for outer_index in range(5):
        if outer_index == 0:
            continue
        
        inner_count = 0
        while inner_count < 10:
            inner_count += 1
            
            if inner_count < 3:
                continue
            
            if inner_count == 7:
                break
            
            results.append(outer_index * inner_count)
        
        if outer_index == 3:
            break
    
    return results


def test_while_else():
    count = 0
    while count < 3:
        count += 1
        if count == 5:
            break
    else:
        pass


def test_for_else():
    for i in range(3):
        if i == 10:
            break
    else:
        pass


if __name__ == "__main__":
    test_while_loops()
    test_nested_loops()
    test_complex_control_flow()
    test_while_else()
    test_for_else()
