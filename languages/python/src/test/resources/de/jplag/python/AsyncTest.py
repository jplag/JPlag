async def async_function():
    await operation()
    yield value


async def async_loop():
    async for item in async_iterator():
        await process(item)


async def async_context():
    async with async_context() as context:
        await context.operation()
