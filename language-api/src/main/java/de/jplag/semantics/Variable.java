package de.jplag.semantics;

import javax.lang.model.element.Name;

public record Variable(Name name, VariableId id) {

    public Variable(Name name) {
        this(name, new VariableId());
    }

    @Override
    public String toString() {
        return name + "[" + id + "]";
    }
}
