import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;

class Ex2SheetTest {
    private Ex2Sheet sheet;

    @BeforeEach
    void setUp() {
        sheet = new Ex2Sheet(9, 17); // Initialize a 9x17 spreadsheet
    }

    /**
     * ✅ BASIC FUNCTIONALITY TESTS
     **/

    @Test
    void testSetAndGetCell() {
        sheet.set(0, 0, "5");
        assertEquals("5.0", sheet.value(0, 0));
        sheet.set(1, 1, "Hello");
        assertEquals("Hello", sheet.value(1, 1));
        assertEquals("", sheet.value(2, 2));
        sheet.set(0, 0, "=5+3");
        assertEquals("8.0", sheet.value(0, 0));
        sheet.set(0, 1, "=(5+3)*2");
        assertEquals("16.0", sheet.value(0, 1));
        sheet.set(0, 2, "=(5+3)*2");
        assertEquals("16.0", sheet.value(0, 2));
        sheet.set(0, 3, "=-5+10");
        assertEquals("5.0", sheet.value(0, 3));
        sheet.set(0,4,"=a3*5");
        assertEquals("25.0", sheet.value(0, 4));
    }

    /**
     * ✅ IF FUNCTION TESTS
     **/

    @Test
    void testIfCondition() {
        sheet.set(0, 0, "=if(5>2,10,20)");
        assertEquals("10.0", sheet.value(0, 0));
        sheet.set(0, 1, "=if(10>5,=if(2<3,100,200),300)");
        assertEquals("100.0", sheet.value(0, 1));
        sheet.set(1, 0, "=if(5==5,true,false)");
        assertEquals("true", sheet.value(1, 0));
        sheet.set(1, 1, "=if(10!=10,1,2)");
        assertEquals("2.0", sheet.value(1, 1));
    }

    @Test
    void testInvalidIfCondition() {
        sheet.set(0, 0, "=if(abc, 1, 2)");
        assertEquals(Ex2Utils.ERR_WRONG_IF, sheet.value(0, 0));
    }

    /**
     * ✅ CELL VALIDATION TESTS
     **/

    @Test
    void testValidCell() {
        assertTrue(sheet.validCell("A1"));
        assertTrue(sheet.validCell("I17"));
    }

    @Test
    void testInvalidCell() {
        assertFalse(sheet.validCell("A100")); // Out of bounds
        assertFalse(sheet.validCell("1A"));   // Wrong format
        assertFalse(sheet.validCell(""));     // Empty input
    }

    @Test
    void testAdvancedValidCellWithinBounds() {
        assertTrue(sheet.advancedValidCell("A1"));
        assertTrue(sheet.advancedValidCell("H17"));
    }

    @Test
    void testAdvancedValidCellOutOfBounds() {
        assertFalse(sheet.advancedValidCell("J17")); // Out of width range
        assertFalse(sheet.advancedValidCell("A18")); // Out of height range
        assertFalse(sheet.advancedValidCell("AA1")); // Only single letter supported
    }

    /**
     * ✅ FILE I/O TESTS
     **/

    @Test
    void testSaveAndLoad() throws IOException {
        sheet.set(0, 0, "10");
        sheet.set(1, 1, "=5+5");

        sheet.save("test_sheet.txt");
        Ex2Sheet loadedSheet = new Ex2Sheet();
        loadedSheet.load("test_sheet.txt");

        assertEquals("10", loadedSheet.value(0, 0));
        assertEquals("10.0", loadedSheet.value(1, 1));
    }

    @Test
    void testLoadCorruptedFile() {
        assertThrows(IOException.class, () -> {
            sheet.load("corrupt_file.txt");
        });
    }

    @Test
    void testLoadEmptyFile() {
        assertThrows(IOException.class, () -> {
            sheet.load("empty_file.txt");
        });
    }

    /**
     * ✅ CIRCULAR REFERENCE TESTS
     **/

    @Test
    void testCircularReference() {
        sheet.set(0, 0, "=B1");
        sheet.set(1, 0, "=A1");
        sheet.eval();

        assertEquals(Ex2Utils.ERR_CYCLE, sheet.value(0, 0));
        assertEquals(Ex2Utils.ERR_CYCLE, sheet.value(1, 0));
    }

    /**
     * ✅ FUNCTION TESTS
     **/

    @Test
    void testSumFunction() {
        sheet.set(1, 0, "10");
        sheet.set(2, 0, "20");
        sheet.set(3, 0, "=sum(B1:B2)");

        assertEquals("30.0", sheet.value(3, 0));
    }

    @Test
    void testMaxFunction() {
        sheet.set(1, 0, "10");
        sheet.set(2, 0, "20");
        sheet.set(3, 0, "=max(B1:B2)");

        assertEquals("20.0", sheet.value(3, 0));
    }

    @Test
    void testInvalidFunction() {
        sheet.set(0, 0, "=unknownFunction(A1)");
        assertEquals(Ex2Utils.FUNC_ERR, sheet.value(0, 0));
    }

    /**
     * ✅ STRESS TEST
     **/

    @Test
    void testLargeSheetPerformance() {
        Ex2Sheet largeSheet = new Ex2Sheet(9, 17);
        largeSheet.set(8, 16, "42");
        assertEquals("42", largeSheet.value(8, 16));
    }
}
