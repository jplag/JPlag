package de.jplag.scxml;

import de.jplag.scxml.parser.ScxmlParserAdapter;
import de.jplag.scxml.sorting.SortingStrategy;
import de.jplag.scxml.util.AbstractScxmlVisitor;

public class ConfigurableScxmlParserAdapter extends ScxmlParserAdapter {

    public void configure(AbstractScxmlVisitor visitor, SortingStrategy sorter) {
        visitor.setSorter(sorter);
        this.visitor = visitor;
    }

    public void setSorter(SortingStrategy sortingStrategy) {
        this.visitor.setSorter(sortingStrategy);
    }
}
