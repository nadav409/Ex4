public class Ex2SheetTest {
    public static void main(String[] a) {
        Ex2Sheet test = new Ex2Sheet();
        String formtest = "=(a2:a5)";
        System.out.println(SCell.BasicIsForm(formtest));

    }
}
