import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.util.ArrayList;

public class Range2DTest {
    private Range2D range;

    @BeforeEach
    void setUp() {
        Index2D start = new CellEntry(0, 0);
        Index2D end = new CellEntry(2, 2);
        range = new Range2D(start, end);
    }

    @Test
    void testGetStartEnd() {
        assertEquals(0, range.getStart().getX());
        assertEquals(0, range.getStart().getY());
        assertEquals(2, range.getEnd().getX());
        assertEquals(2, range.getEnd().getY());
    }

    @Test
    void testGetCells() {
        ArrayList<Index2D> cells = range.getcells();
        assertEquals(9, cells.size());
    }

    @Test
    void testGetCellNames() {
        ArrayList<String> cellNames = range.getCellNames();
        assertTrue(cellNames.contains("A1"));
        assertTrue(cellNames.contains("B2"));
        assertTrue(cellNames.contains("C2"));
        assertTrue(cellNames.contains("A0"));
        assertTrue(cellNames.contains("B1"));
        assertTrue(cellNames.contains("C0"));
        assertFalse(cellNames.contains("D1"));
        assertFalse(cellNames.contains("A6"));
    }

    @Test
    void testMinValue() {
        Ex2Sheet sheet = new Ex2Sheet(3, 3);
        sheet.set(0, 0, "5");
        sheet.set(1, 1, "2");
        sheet.set(2, 2, "8");
        range.updateValue(sheet);

        assertEquals("2.0", range.minValue());
    }
    @Test
    void testMultiplyValue() {
        Ex2Sheet sheet = new Ex2Sheet(3, 3);
        sheet.set(0, 0, "=2+3");
        sheet.set(1, 1, "2");
        sheet.set(2, 2, "8");
        range.updateValue(sheet);
        assertEquals("80.0", range.multiplyValue());
        sheet.set(1, 1, "-2");
        range.updateValue(sheet);
        assertEquals("-80.0", range.multiplyValue());
    }

    @Test
    void testMaxValue() {
        Ex2Sheet sheet = new Ex2Sheet(3, 3);
        sheet.set(0, 0, "5");
        sheet.set(1, 1, "2");
        sheet.set(2, 2, "8");
        range.updateValue(sheet);

        assertEquals("8.0", range.maxValue());
    }

    @Test
    void testSumValue() {
        Ex2Sheet sheet = new Ex2Sheet(3, 3);
        sheet.set(0, 0, "5");
        sheet.set(1, 1, "2");
        sheet.set(2, 2, "8");
        range.updateValue(sheet);

        assertEquals("15.0", range.sumValue(), "Sum should be 15.0");
    }

    @Test
    void testValidFunction() {
        assertTrue(Range2D.ValidFunction("=SUM(A1:B2)"));
        assertTrue(Range2D.ValidFunction("=MIN(A1:B3)"));
        assertFalse(Range2D.ValidFunction("=RANDOM(A1:B2)"));
        assertFalse(Range2D.ValidFunction("=SUM(A1 B2)"));
        assertFalse(Range2D.ValidFunction("=SUM("));
    }

    @Test
    void testFindStartAndEndValid() {
        assertEquals("A1:B2", Range2D.findStartAndEndValid("=SUM(A1:B2)"));
        assertEquals("A0:B5", Range2D.findStartAndEndValid("=SUM(A0:B5)"));
        assertEquals("a0:c0", Range2D.findStartAndEndValid("=min(a0:c0)"));
        assertEquals("A1:E8", Range2D.findStartAndEndValid("=MAX(A1:E8)"));
        assertEquals("A1:B5", Range2D.findStartAndEndValid("=average(A1:B5)"));
    }

    @Test
    void testAllCellsInRange() {
        assertEquals("[A1, A2, B1, B2]", Range2D.AllCellsInRange("=SUM(A1:B2)"));
        assertEquals("[A0, B0, C0]", Range2D.AllCellsInRange("=MAX(A0:C0)"));
        assertEquals("[A1, A2, B1, B2]", Range2D.AllCellsInRange("=MIN(A1:B2)"));
    }

    @Test
    void testEvaluateFunction() {
        Ex2Sheet sheet = new Ex2Sheet(3, 3);
        sheet.set(0, 0, "4");
        sheet.set(1, 1, "2");
        sheet.set(2, 2, "6");
        sheet.set(0, 1, "");
        range.updateValue(sheet);
        assertEquals(2.0, range.evaluateFunction("=MIN(A1:C3)"));
        assertEquals(6.0, range.evaluateFunction("=MAX(A1:C3)"));
        assertEquals(12.0, range.evaluateFunction("=SUM(A1:C3)"));
    }
}

