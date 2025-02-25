import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class SCellTest {
    private SCell cell;

    @BeforeEach
    void setUp() {
        cell = new SCell();
    }

    @Test
    void testEmptyCell() {
        assertEquals("", cell.getData());
        assertEquals(Ex2Utils.TEXT, cell.getType());
    }

    @Test
    void testSetNumber() {
        cell.setData("42");
        assertEquals("42", cell.getData());
        assertEquals(Ex2Utils.NUMBER, cell.getType());
        cell.setData("-10");
        assertEquals(Ex2Utils.NUMBER, cell.getType());
        cell.setData("3.14");
        assertEquals(Ex2Utils.NUMBER, cell.getType());
        cell.setData("42abc");
        assertEquals(Ex2Utils.TEXT, cell.getType());
    }

    @Test
    void testSetText() {
        cell.setData("Hello");
        assertEquals("Hello", cell.getData());
        assertEquals(Ex2Utils.TEXT, cell.getType());
    }

    @Test
    void testSetFormula() {
        cell.setData("=A1+A2");
        assertEquals("=A1+A2", cell.getData());
        assertEquals(Ex2Utils.FORM, cell.getType());
    }

    @Test
    void testSetFunction() {
        cell.setData("=SUM(A1:A10)");
        assertEquals("=SUM(A1:A10)", cell.getData());
        assertEquals(Ex2Utils.FUNCTION, cell.getType());
    }

    @Test
    void testIsIfFunctionValid() {
        assertTrue(SCell.isIf("=IF(A1>10,100,200)"));
    }

    @Test
    void testIsIfFunctionInvalid() {
        assertFalse(SCell.isIf("IF(A1>10,100,200)"));
        assertFalse(SCell.isIf("=SUM(A1:A10)"));
    }

    @Test
    void testSetIfStatement() {
        cell.setData("=IF(A1>10,100,200)");
        assertEquals("=IF(A1>10,100,200)", cell.getData());
        assertEquals(Ex2Utils.IF, cell.getType());
    }
    @Test
    void testToString() {
        cell.setData("=A1+A2");
        assertEquals("=A1+A2", cell.toString());
    }
    @Test
    void testSetOrder() {
        cell.setOrder(5);
        assertEquals(5, cell.getOrder());
    }
}

