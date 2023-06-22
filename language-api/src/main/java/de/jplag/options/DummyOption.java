package de.jplag.options;

class DummyOption<T> implements LanguageOption<T> {
    private final T value;

    public DummyOption(T value) {
        this.value = value;
    }

    @Override
    public OptionType<T> getType() {
        return null;
    }

    @Override
    public String getName() {
        return null;
    }

    @Override
    public T getValue() {
        return this.value;
    }

    @Override
    public String getDescription() {
        return null;
    }

    @Override
    public void setValue(T value) {
    }

    @Override
    public boolean hasValue() {
        return true;
    }
}
