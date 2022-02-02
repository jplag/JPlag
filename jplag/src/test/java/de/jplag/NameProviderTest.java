package de.jplag;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;

public class NameProviderTest {
    private static final Integer zeroElement = 0;
    private static final Integer oneElement = 1;
    private static final Integer twoElement = 2;

    private NameProvider<Integer> nameProvider;

    @Before
    public void setup() {
        this.nameProvider = new NameProvider<>();
    }

    @Test
    public void emptyTreeIsEmpty() {
        assertTrue(nameProvider.collectNamedElements().isEmpty());
    }

    @Test
    public void testNonClashing() {
        String[] zeroParts = new String[] {"zero"};
        String[] oneParts = new String[] {"number", "one"};
        nameProvider.storeElement(zeroElement, Arrays.asList(zeroParts));
        nameProvider.storeElement(oneElement, Arrays.asList(oneParts));

        Map<Integer, List<String>> namedElements = nameProvider.collectNamedElements();
        assertEquals(2, namedElements.size());

        assertEquals(1, namedElements.get(zeroElement).size());
        assertEquals(zeroParts[0], namedElements.get(zeroElement).get(0));

        assertEquals(1, namedElements.get(oneElement).size());
        String oneName = namedElements.get(oneElement).get(0);
        assertTrue(oneName.equals(oneParts[0]) || oneName.equals(oneParts[1]));
    }

    @Test
    public void testPrefixPartlyClashing() {
        String[] zeroParts = new String[] {"number", "zero"};
        String[] oneParts = new String[] {"number", "one"};
        nameProvider.storeElement(zeroElement, Arrays.asList(zeroParts));
        nameProvider.storeElement(oneElement, Arrays.asList(oneParts));

        Map<Integer, List<String>> namedElements = nameProvider.collectNamedElements();
        assertEquals(2, namedElements.size());

        assertEquals(1, namedElements.get(zeroElement).size());
        assertEquals(zeroParts[1], namedElements.get(zeroElement).get(0));

        assertEquals(1, namedElements.get(oneElement).size());
        assertEquals(oneParts[1], namedElements.get(oneElement).get(0));
    }

    @Test
    public void testSuffixPartlyClashing() {
        String[] zeroParts = new String[] {"zero", "number"};
        String[] oneParts = new String[] {"one", "number"};
        nameProvider.storeElement(zeroElement, Arrays.asList(zeroParts));
        nameProvider.storeElement(oneElement, Arrays.asList(oneParts));

        Map<Integer, List<String>> namedElements = nameProvider.collectNamedElements();
        assertEquals(2, namedElements.size());

        assertEquals(1, namedElements.get(zeroElement).size());
        assertEquals(zeroParts[0], namedElements.get(zeroElement).get(0));

        assertEquals(1, namedElements.get(oneElement).size());
        assertEquals(oneParts[0], namedElements.get(oneElement).get(0));
    }

    @Test
    public void testSuffixPartlyClashingAtDifferentPartOffsets() {
        String[] zeroParts = new String[] {"number", "zero"};
        String[] oneParts = new String[] {"one", "number"};
        nameProvider.storeElement(zeroElement, Arrays.asList(zeroParts));
        nameProvider.storeElement(oneElement, Arrays.asList(oneParts));

        Map<Integer, List<String>> namedElements = nameProvider.collectNamedElements();
        assertEquals(2, namedElements.size());

        assertEquals(1, namedElements.get(zeroElement).size());
        assertEquals(zeroParts[1], namedElements.get(zeroElement).get(0));

        assertEquals(1, namedElements.get(oneElement).size());
        assertEquals(oneParts[0], namedElements.get(oneElement).get(0));
    }

    @Test
    public void testDuplicateNames() {
        String[] zeroParts = new String[] {"zero"};
        String[] duplicateParts = new String[] {"duplicate"};
        nameProvider.storeElement(zeroElement, Arrays.asList(zeroParts));
        nameProvider.storeElement(oneElement, Arrays.asList(duplicateParts));
        nameProvider.storeElement(twoElement, Arrays.asList(duplicateParts));

        Map<Integer, List<String>> namedElements = nameProvider.collectNamedElements();
        assertEquals(1, namedElements.size());
        assertTrue(namedElements.containsKey(zeroElement));

        List<Integer> unnamedElements = nameProvider.collectUnnamedElements();
        assertEquals(2, unnamedElements.size());
    }

    @Test
    public void testFullyCovered() {
        String[] zeroParts = new String[] {"first-duplicate", "zero"};
        String[] oneParts = new String[] {"one", "second-duplicate"};
        String[] twoParts = new String[] {"first-duplicate", "second-duplicate"}; // Full string is the only solution.
        nameProvider.storeElement(zeroElement, Arrays.asList(zeroParts));
        nameProvider.storeElement(oneElement, Arrays.asList(oneParts));
        nameProvider.storeElement(twoElement, Arrays.asList(twoParts));

        Map<Integer, List<String>> namedElements = nameProvider.collectNamedElements();
        assertEquals(3, namedElements.size());

        assertEquals(1, namedElements.get(zeroElement).size());
        assertEquals(zeroParts[1], namedElements.get(zeroElement).get(0));

        assertEquals(1, namedElements.get(oneElement).size());
        assertEquals(oneParts[0], namedElements.get(oneElement).get(0));

        assertEquals(2, namedElements.get(twoElement).size());
    }
}
