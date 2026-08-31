# =========================
# decorators + async basics
# =========================
import math

@decorator1
@decorator2(arg=1)
async def async_function(a, b: int, /, c=3, *args, d=4, **kwds):
    await something(a)
    return a + b + c


# =========================
# normal function with all parameter types
# =========================

def complex_function(pos1, pos2: int, /, pos_or_kw=10, *args, kwonly1, kwonly2="x", **kwargs):
    x = pos1 + pos2
    x += pos_or_kw
    return x


# =========================
# lambda + apply cases
# =========================

f = lambda x, y: x + y

result = f(1, 2)


# =========================
# control flow: if/elif/else
# =========================

def if_test(x):
    if x > 10:
        return "big"
    elif x == 10:
        return "equal"
    else:
        return "small"


# =========================
# loops
# =========================

def loop_test():
    for i in range(5):
        if i % 2 == 0:
            continue
        else:
            break

    while False:
        pass


# =========================
# exceptions
# =========================

def exception_test():
    try:
        x = 1 / 0
    except ZeroDivisionError as e:
        print(e)
    except Exception:
        raise
    finally:
        print("done")


# =========================
# with statement
# =========================

def with_test():
    with open("file.txt") as f:
        data = f.read()
    return data


# =========================
# async constructs
# =========================

async def async_test():
    async for i in async_iter():
        await process(i)

    async with async_context() as ctx:
        await ctx.run()


# =========================
# match-case (Python 3.10+)
# =========================

def match_test(x):
    match x:
        case 1:
            return "one"
        case 2 | 3:
            return "two or three"
        case [a, b]:
            return a + b
        case {"key": value}:
            return value
        case _:
            return "default"


# =========================
# comprehensions (ARRAY-heavy)
# =========================

list_comp = [x * x for x in range(10) if x % 2 == 0]

set_comp = {x for x in range(10)}

dict_comp = {x: x * 2 for x in range(5)}

gen_comp = (x for x in range(10))


# =========================
# walrus operator (:=)
# =========================

def walrus_test():
    if (n := len([1, 2, 3])) > 2:
        return n


# =========================
# class + decorators + methods
# =========================

class MyClass(Generic[T]):
    class_var = 123

    @classmethod
    def cls_method(cls, x):
        return x

    @staticmethod
    def static_method(y):
        return y

    def instance_method(self, z):
        return z


# =========================
# type alias (3.12 feature)
# =========================

type Point = tuple[int, int]

def type_test(p: Point) -> Point:
    return p


# =========================
# nested structures stress
# =========================

#def nested():
#    return [
#        {i: (j for j in range(3))}
#        for i in range(5)
#    ]


# =========================
# starred expressions / kwargs stress
# =========================

def star_test(a, *args, **kwds):
    x = [*args]
    y = {**kwds}
    return x, y


# =========================
# chained calls (APPLY-heavy)
# =========================

def chain():
    return obj.method1().method2().method3()


# =========================
# tuple / unpacking
# =========================

def unpacking():
    a, b, *c = (1, 2, 3, 4, 5)
    return a, b, c


# =========================
# delete / assert / raise / yield
# =========================

def misc():
    x = 10
    assert x > 0

    del x

    if False:
        yield 1

    raise ValueError("error")
