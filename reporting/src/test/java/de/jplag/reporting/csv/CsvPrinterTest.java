package de.jplag.reporting.csv;

import java.io.IOException;
import java.util.List;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class CsvPrinterTest {
    private static final String EXPECTED_CSV_TEXT = "1,test1\r\n2,\"test2,\"\"x\"\"\"\r\n";
    private static final List<CsvTestItem> TEST_ITEMS = List.of(new CsvTestItem(1, "test1"), new CsvTestItem(2, "test2,\"x\""));

    @Test
    void testPrintWithReflectiveMapper() throws IOException {
        CsvDataMapper<CsvTestItem> mapper = new ReflectiveCsvDataMapper<>(CsvTestItem.class);
        CsvPrinter<CsvTestItem> printer = new CsvPrinter<>(mapper);

        printer.addRows(TEST_ITEMS);

        Assertions.assertEquals(EXPECTED_CSV_TEXT, printer.printToString());
    }

    @Test
    void testPrintWithHardcodedMapper() throws IOException {
        CsvDataMapper<CsvTestItem> mapper = new HardcodedCsvDataMapper<>(2, item -> new Object[] {item.number(), item.text()});
        CsvPrinter<CsvTestItem> printer = new CsvPrinter<>(mapper);

        printer.addRows(TEST_ITEMS);

        Assertions.assertEquals(EXPECTED_CSV_TEXT, printer.printToString());
    }

    private record CsvTestItem(@CsvValue(1) int number, @CsvValue(2) String text) {
    }
}
