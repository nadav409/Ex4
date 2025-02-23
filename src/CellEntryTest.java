import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class CellEntryTest {
    private CellEntry cell;

    @Test
    void testGetCellEntry() {
        cell = new CellEntry("A1");
        assertEquals(0, cell.getX());
        assertEquals(1, cell.getY());
        cell = new CellEntry("Z99");
        assertEquals(25, cell.getX());
        assertEquals(99, cell.getY());
        cell = new CellEntry("A0");
        assertEquals(0, cell.getX());
        assertEquals(0, cell.getY());
        cell = new CellEntry(5, 10);
        assertEquals(5, cell.getX());
        assertEquals(10, cell.getY());
        assertEquals("F10", cell.toCell());
    }
}
