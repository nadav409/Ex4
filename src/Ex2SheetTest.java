import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;

class Ex2SheetTest {
    private Ex2Sheet sheet;

    @BeforeEach
    void setUp() {
        sheet = new Ex2Sheet(9, 17);
    }

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
        sheet.set(0, 4, "=a3*5");
        assertEquals("25.0", sheet.value(0, 4));
        sheet.set(1, 2, "=multiply(a0:a1)");
        assertEquals("128.0", sheet.value(1, 2));
        sheet.set(1, 3, "=max(a0,a1)");
        assertEquals(Ex2Utils.FUNC_ERR, sheet.value(1, 3));

    }

    @Test
    void testValidCell() {
        assertTrue(sheet.validCell("A1"));
        assertTrue(sheet.validCell("a1"));
        assertTrue(sheet.validCell("I17"));
        assertTrue(sheet.validCell("A98"));
        assertTrue(sheet.validCell("Z64"));
        assertFalse(sheet.validCell("A100"));
        assertFalse(sheet.validCell("1A"));
        assertFalse(sheet.validCell(""));
        assertFalse(sheet.validCell(null));
        assertFalse(sheet.validCell("naa34"));
    }

    @Test
    void testAdvancedValidCell() {
        assertTrue(sheet.advancedValidCell("A1"));
        assertTrue(sheet.advancedValidCell("H16"));
        assertFalse(sheet.advancedValidCell("J17"));
        assertFalse(sheet.advancedValidCell("A18"));
        assertFalse(sheet.advancedValidCell("AA1"));
    }

    @Test
    void testSaveAndLoad() throws IOException {
        sheet.set(0, 0, "10");
        sheet.set(1, 1, "=5+5");
        sheet.set(2, 2, "nadav");
        sheet.set(2, 3, "=if(a0>5,=sum(a0:b1),40)");


        sheet.save("test_sheet.txt");
        Ex2Sheet loadedSheet = new Ex2Sheet();
        loadedSheet.load("test_sheet.txt");

        assertEquals("10.0", loadedSheet.value(0, 0));
        assertEquals("10.0", loadedSheet.value(1, 1));
        assertEquals("nadav", loadedSheet.value(2, 2));
        assertEquals("20.0", loadedSheet.value(2, 3));
    }

    @Test
    void testCircularReference() {
        sheet.set(0, 0, "=B0");
        sheet.set(1, 0, "=A0");
        sheet.eval();
        assertEquals(Ex2Utils.ERR_CYCLE, sheet.value(0, 0));
        assertEquals(Ex2Utils.ERR_CYCLE, sheet.value(1, 0));
    }

    @Test
    void testSumFunction() {
        sheet.set(1, 0, "10");
        sheet.set(2, 0, "20");
        sheet.set(3, 0, "=sum(B0:C0)");
        assertEquals("30.0", sheet.value(3, 0));
        sheet.set(3, 0, "string");
        sheet.set(4, 0, "=sum(B0:D0)");
        assertEquals(Ex2Utils.FUNC_ERR, sheet.value(4, 0));
        sheet.set(3, 0, "");
        assertEquals("30.0", sheet.value(4, 0));
        sheet.set(0, 0, "=sum(A1,b2)");
        assertEquals(Ex2Utils.FUNC_ERR, sheet.value(0, 0));
    }

    @Test
    void testMaxFunction() {
        sheet.set(1, 0, "10");
        sheet.set(2, 0, "20");
        sheet.set(3, 0, "=Max(B0:C0)");
        assertEquals("20.0", sheet.value(3, 0));
        sheet.set(3, 0, "string");
        sheet.set(4, 0, "=max(B0:D0)");
        assertEquals(Ex2Utils.FUNC_ERR, sheet.value(4, 0));
        sheet.set(3, 0, "");
        assertEquals("20.0", sheet.value(4, 0));
        sheet.set(0, 0, "=MAX(A1)");
        assertEquals(Ex2Utils.FUNC_ERR, sheet.value(0, 0));
    }

    @Test
    void testMinFunction() {
        assertEquals("", sheet.value(3, 0));
        sheet.set(1, 0, "10");
        sheet.set(2, 0, "5");
        sheet.set(3, 0, "=min(B0:C0)");
        assertEquals("5.0", sheet.value(3, 0));
        sheet.set(1, 0, "2");
        assertEquals("2.0", sheet.value(3, 0));
        sheet.set(1, 1, "=d0");
        assertEquals("2.0", sheet.value(1, 1));
        sheet.set(1, 0, "1");
        assertEquals("1.0", sheet.value(3, 0));
        assertEquals("1.0", sheet.value(1, 1));
    }

    @Test
    void testAverageFunction() {
        sheet.set(0, 0, "10");
        sheet.set(1, 0, "20");
        sheet.set(3, 0, "=average(A0:B0)");
        assertEquals("15.0", sheet.value(3, 0));
        sheet.set(0, 0, "");
        assertEquals("10.0", sheet.value(3, 0));
    }

    @Test
    void testEval() {
        sheet.set(0, 0, "5");
        assertEquals("5", sheet.eval(0, 0));
        sheet.set(1, 1, "Hello");
        assertEquals("Hello", sheet.eval(1, 1));
        sheet.set(2, 2, "=5+3");
        assertEquals("8.0", sheet.eval(2, 2));
        assertEquals("", sheet.eval(3, 3));
        sheet.set(4, 4, "=5++3");
        assertEquals(null, sheet.eval(4, 4));
    }

    @Test
    void testIsForm() {
        assertTrue(sheet.isForm("5+3"));
        assertTrue(sheet.isForm("A1*2"));
        assertTrue(sheet.isForm("(5+3)/2"));
        assertTrue(sheet.isForm("5+3"));
        assertTrue(sheet.isForm("A1*2"));
        assertTrue(sheet.isForm("(5+3)/2"));
        assertFalse(sheet.isForm("Hello"));
        assertFalse(sheet.isForm("==5"));
        assertFalse(sheet.isForm("Hello+3"));
        assertFalse(sheet.isForm(""));
        assertFalse(sheet.isForm(null));
    }

    @Test
    void testDepth() {
        sheet.set(0, 0, "5");
        sheet.set(1, 0, "=A0+3");
        sheet.set(2, 0, "=B0*2");
        int[][] depth1 = sheet.depth();
        assertEquals(0, depth1[0][0]);
        assertEquals(1, depth1[1][0]);
        assertEquals(2, depth1[2][0]);
        sheet.set(0, 0, "=B0");
        sheet.set(1, 0, "=C0");
        sheet.set(2, 0, "=A0");
        int[][] depth2 = sheet.depth();
        assertEquals(-1, depth2[0][0]);
        assertEquals(-1, depth2[1][0]);
        assertEquals(-1, depth2[2][0]);
        sheet.set(0, 0, "10");
        sheet.set(1, 0, "=A0 + 5");
        sheet.set(2, 0, "=B0 * 2");
        sheet.set(3, 0, "=A0 + C0");
        sheet.set(4, 0, "=max(a0:d0)");
        sheet.set(5, 0, "=if(a0>5,=min(A0:E2),40)");
        int[][] depth3 = sheet.depth();
        assertEquals(0, depth3[0][0]);
        assertEquals(1, depth3[1][0]);
        assertEquals(2, depth3[2][0]);
        assertEquals(3, depth3[3][0]);
        assertEquals(4, depth3[4][0]);
        assertEquals(5, depth3[5][0]);
    }

    @Test
    void testifCondition() {
        String formula = "=if(A1>5, 10, 20)";
        assertEquals("A1>5", sheet.ifCondition(formula));
        String formula1 = "=if(A1==5, 10, 20)";
        assertEquals("A1==5", sheet.ifCondition(formula1));
        String formula2 = "=if((2+3)*8+a0>20, 10, 20)";
        assertEquals("(2+3)*8+a0>20", sheet.ifCondition(formula2));
    }
    @Test
    void testIfTrue() {
        String formula = "=if(A1>5,B1+3,C1-4)";
        assertEquals("B1+3", sheet.ifTrue(formula));
        String formula1 = "=if(A1>5,=if(B1<3,7,9),=C1-4)";
        assertEquals("=if(B1<3,7,9)", sheet.ifTrue(formula1));
    }
    @Test
    void testIfFalse() {
        String formula = "=if(A1>5,10,20)";
        assertEquals("20", sheet.ifFalse(formula));
        String formula1 = "=if(A1>5,B1+3,C1-4)";
        assertEquals("C1-4", sheet.ifFalse(formula1));
        String formula2 = "=if(A1>5,40,=if(B1<3,7,9))";
        assertEquals("=if(B1<3,7,9)", sheet.ifFalse(formula2));
    }
    @Test
    void testEvaluateCondition() {
        assertTrue(sheet.evaluateCondition("=if(5>2,10,20)"));
        assertFalse(sheet.evaluateCondition("=if(5<2,10,20)"));
        assertTrue(sheet.evaluateCondition("=if(5!=3,5,20)"));
        assertFalse(sheet.evaluateCondition("=if(5!=5,5,20)"));
        sheet.set(0, 0, "10");
        sheet.set(1, 0, "20");
        assertTrue(sheet.evaluateCondition("=if(a0<b0,5,20)"));
        assertFalse(sheet.evaluateCondition("=if(A0>B0,5,20)"));
        sheet.set(0, 0, "10");
        sheet.set(1, 0, "20");
        assertTrue(sheet.evaluateCondition("=if(A0+5<B0,5,20)"));
        assertFalse(sheet.evaluateCondition("=if(A0*3>B0*5,5,20)"));
    }
    @Test
    void testIfFunction() {
        sheet.set(0, 0, "=if(5>2,100,200)");
        assertEquals("100.0", sheet.value(0, 0));
        sheet.set(1, 0, "10");
        sheet.set(2, 0, "20");
        sheet.set(0, 0, "=if(B0>C0,100,200)");
        assertEquals("200.0", sheet.value(0, 0));
        sheet.set(0, 0, "=if(10>5,=if(2<3,100,200),300)");
        assertEquals("100.0", sheet.value(0, 0));
        sheet.set(0, 0, "=if(abc,1,2)");
        assertEquals(Ex2Utils.ERRWRONG_IF, sheet.value(0, 0));
        sheet.set(1, 1, "-20");
        sheet.set(0, 0, "=if(5>2,=min(b0:c1),2)");
        assertEquals("-20.0", sheet.value(0, 0));
        sheet.set(0, 0, "=if(5>2,=max(b0:c1),2)");
        assertEquals("20.0", sheet.value(0, 0));
        sheet.set(0, 0, "=if(b0>5,=multiply(b0:c1),2)");
        assertEquals("-4000.0", sheet.value(0, 0));

    }
    @Test
    void testValidIfCorrect() {
        sheet.set(0, 1, "10");
        sheet.set(1, 1, "1");
        sheet.set(2, 1, "1");
        assertTrue(sheet.validIf("=if(A1>5,10,20)"));
        assertTrue(sheet.validIf("=if(B1==C1,yes,no)"));
        assertFalse(sheet.validIf("=IF(A1>5, 10)"));
        assertFalse(sheet.validIf("=if(A1>5,,20)"));
        assertFalse(sheet.validIf("=if(B1==C1,yes ,no)"));
        assertFalse(sheet.validIf("=if(B1==C1,=2>5,no)"));
        assertFalse(sheet.validIf("=if(d1>2,2,no)"));
        assertFalse(sheet.validIf("=if(3>2,=max(,5)"));
        assertTrue(sheet.validIf("=if(3>2,=min(a0:b1),5)"));

    }
    @Test
    void testCountOccurrencesBasic() {
        assertEquals(2, sheet.countOccurrences("=if(A1>5, 10, 20), if(A2<5, 30, 40)", "if"));
        assertEquals(0, sheet.countOccurrences("=A1+B1", "if"));
        assertEquals(3, sheet.countOccurrences("if if if", "if"));
    }
}
