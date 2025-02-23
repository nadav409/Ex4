public class Ex2SheetTest {
    public static void main(String[] a) {
        Ex2Sheet test = new Ex2Sheet();
        test.set(0,0,"1");
        test.set(0,1,"=min(a0:b23)");
        Range2D test1 = new Range2D("a0:b23");
        String iftest = "=min(a0:b23)";
        System.out.println(Range2D.advnacedValidFunction(iftest,test));

    }
}
