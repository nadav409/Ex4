public class Ex2SheetTest {
    public static void main(String[] a) {
        Ex2Sheet test = new Ex2Sheet();
        String iftest = "=if(5>2,=if(c8>2,5,4),2";
        System.out.println(test.allCellsInIf(iftest));

    }
}
