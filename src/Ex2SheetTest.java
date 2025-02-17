public class Ex2SheetTest {
    public static void main(String[] a) {
        Ex2Sheet test = new Ex2Sheet();
        String iftest = "=if((2+3)*10>=14,4,5)";
        System.out.println(test.evaluateCondition(iftest));

    }
}
