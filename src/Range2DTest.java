import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.util.ArrayList;

public class Range2DTest {
    private Range2D range;

    @BeforeEach
    void setUp() {
        Index2D start = new CellEntry(0, 0); // "A0"
        Index2D end = new CellEntry(2, 2);   // "C2"
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

        assertEquals("2.0", range.minValue(), "Min value should be 2.0");
    }

    @Test
    void testMaxValue() {
        Ex2Sheet sheet = new Ex2Sheet(3, 3);
        sheet.set(0, 0, "5");
        sheet.set(1, 1, "2");
        sheet.set(2, 2, "8");
        range.updateValue(sheet);

        assertEquals("8.0", range.maxValue(), "Max value should be 8.0");
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
    void testAverageValue() {
        Ex2Sheet sheet = new Ex2Sheet(3, 3);
        sheet.set(0, 0, "4");
        sheet.set(1, 1, "2");
        sheet.set(2, 2, "6");
        range.updateValue(sheet);

        assertEquals("2.0", range.averageValue());
    }

    @Test
    void testValidFunction() {
        assertTrue(Range2D.ValidFunction("=SUM(A1:B2)"), "SUM should be valid");
        assertTrue(Range2D.ValidFunction("=MIN(A1:B3)"), "MIN should be valid");
        assertFalse(Range2D.ValidFunction("=RANDOM(A1:B2)"), "RANDOM should be invalid");
        assertFalse(Range2D.ValidFunction("=SUM(A1 B2)"), "SUM missing colon should be invalid");
    }

    @Test
    void testBasicValidFunction() {
        assertTrue(Range2D.BasicValidFunction("=SUM(A1:B2)"), "SUM should be valid");
        assertFalse(Range2D.BasicValidFunction("SUM(A1:B2)"), "Missing '=' should be invalid");
        assertFalse(Range2D.BasicValidFunction("=INVALID(A1:B2)"), "Invalid function name should be false");
    }

    @Test
    void testFindStartAndEndValid() {
        assertEquals("A1:B2", Range2D.findStartAndEndValid("=SUM(A1:B2)"), "Should extract 'A1:B2'");
    }

    @Test
    void testAllCellsInRange() {
        assertEquals("[A1, A2, B1, B2]", Range2D.AllCellsInRange("=SUM(A1:B2)"), "Should return all cells in range A1:B2");
    }

    @Test
    void testEvaluateFunction() {
        Ex2Sheet sheet = new Ex2Sheet(3, 3);
        sheet.set(0, 0, "4");
        sheet.set(1, 1, "2");
        sheet.set(2, 2, "6");
        range.updateValue(sheet);

        assertEquals(2.0, range.evaluateFunction("=MIN(A1:C3)"), "Min function should return 2");
        assertEquals(6.0, range.evaluateFunction("=MAX(A1:C3)"), "Max function should return 6");
        assertEquals(12.0, range.evaluateFunction("=SUM(A1:C3)"), "Sum function should return 12");
        assertEquals(2, range.evaluateFunction("=AVERAGE(A1:C3)"), "Average function should return 1.5");
    }
}

