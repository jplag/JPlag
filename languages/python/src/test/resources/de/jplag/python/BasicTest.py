import os

global_counter = 0


class MyClass:
    def __init__(self, name: str):
        self.name = name
    
    async def process(self) -> None:
        global global_counter
        
        counter = 0
        while counter < 5:
            counter += 1
            if counter == 3:
                break
        
        temp_list = [1, 2, 3]
        del temp_list[0]
        
        assert len(temp_list) == 2, "List should have 2 elements"
        
        square = lambda x: x * 2
        
        def outer_function():
            local_var = 10
            
            def inner_function():
                nonlocal local_var
                local_var += 1
                return local_var
            
            return inner_function()
        
        nested_result = outer_function()
        
        for i in range(10):
            if i % 2 == 0:
                print(f"Even: {i}")
            else:
                continue
        
        try:
            with open("file.txt") as f:
                data = f.read()
        except FileNotFoundError:
            pass
        finally:
            print("Done")
        
        match self.name:
            case "test":
                return True
            case _:
                return False

async def main():
    obj = MyClass("test")
    await obj.process()


if __name__ == "__main__":
    import asyncio
    asyncio.run(main())
