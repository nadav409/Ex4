import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Scanner;

/**
 * The documentation of this class was removed as of Ex4...
 */
public class Ex2Sheet implements Sheet {
    private Cell[][] table;
    private Double[][] data;

    public Ex2Sheet(int x, int y) {
        table = new SCell[x][y];
        for (int i = 0; i < x; i = i + 1) {
            for (int j = 0; j < y; j = j + 1) {
                table[i][j] = new SCell("");
            }
        }
        eval();
    }

    public Ex2Sheet() {
        this(Ex2Utils.WIDTH, Ex2Utils.HEIGHT);
    }

    @Override
    public String value(int x, int y) {
        String ans = "";
        Cell c = get(x, y);
        ans = c.toString();
        int t = c.getType();
        if (t == Ex2Utils.ERR_CYCLE_FORM) {
            ans = Ex2Utils.ERR_CYCLE;
            c.setOrder(-1);
        } // BUG 345
        //  if(t==Ex2Utils.ERR_CYCLE_FORM) {ans = "ERR_CYCLE!";}
        if (t == Ex2Utils.FUNC_ERR_FORMAT) {
            ans = Ex2Utils.FUNC_ERR;
        }
        if (t == Ex2Utils.IF) {
            Object ifResult = evaluateIf(ans);
            if (ifResult instanceof Double) {
                return ifResult.toString();
            }
            if (ifResult instanceof String) {
                return (String) ifResult;
            }
        }
        if (t == Ex2Utils.ERR_WRONG_IF) {
            ans = Ex2Utils.ERRWRONG_IF;
        }
        if (t == Ex2Utils.NUMBER || t == Ex2Utils.FORM || t == Ex2Utils.FUNCTION) {
            ans = "" + data[x][y];
        }
        if (t == Ex2Utils.ERR_FORM_FORMAT) {
            ans = Ex2Utils.ERR_FORM;
        }
        return ans;
    }

    @Override
    public Cell get(int x, int y) {
        return table[x][y];
    }

    @Override
    public Cell get(String cords) {
        Cell ans = null;
        Index2D c = new CellEntry(cords);
        int x = c.getX(), y = c.getY();
        if (isIn(x, y)) {
            ans = table[x][y];
        }
        return ans;
    }

    @Override
    public int width() {
        return table.length;
    }

    @Override
    public int height() {
        return table[0].length;
    }

    @Override
    public void set(int x, int y, String s) {
        Cell c = new SCell(s);
        table[x][y] = c;
          eval();
    }

    ///////////////////////////////////////////////////////////

    @Override
    public void eval() {
        int[][] dd = depth();
        data = new Double[width()][height()];
        for (int x = 0; x < width(); x = x + 1) {
            for (int y = 0; y < height(); y = y + 1) {
                Cell c = table[x][y];
                if (dd[x][y] != -1 && c != null && (c.getType() != Ex2Utils.TEXT)) {
                    String res = eval(x, y);
                    Double d = getDouble(res);
                    if (d == null) {
                        if (c.getType() != Ex2Utils.FUNC_ERR_FORMAT && c.getType() != Ex2Utils.IF_ERR_FORMAT && c.getType() != Ex2Utils.IF && c.getType() != Ex2Utils.ERR_WRONG_IF) {
                            c.setType(Ex2Utils.ERR_FORM_FORMAT);
                        }
                    } else {
                        data[x][y] = d;
                    }
                }
                if (dd[x][y] == -1) {
                    c.setType(Ex2Utils.ERR_CYCLE_FORM);
                }
            }
        }
    }

    @Override
    public boolean isIn(int xx, int yy) {
        boolean ans = true;
        if (xx < 0 | yy < 0 | xx >= width() | yy >= height()) {
            ans = false;
        }
        return ans;
    }

    @Override
    public int[][] depth() {
        int[][] ans = new int[width()][height()];
        for (int x = 0; x < width(); x = x + 1) {
            for (int y = 0; y < height(); y = y + 1) {
                Cell c = this.get(x, y);
                int t = c.getType();
                if (Ex2Utils.TEXT != t) {
                    ans[x][y] = -1;
                }
            }
        }
        int count = 0, all = width() * height();
        boolean changed = true;
        while (changed && count < all) {
            changed = false;
            for (int x = 0; x < width(); x = x + 1) {
                for (int y = 0; y < height(); y = y + 1) {
                    if (ans[x][y] == -1) {
                        Cell c = this.get(x, y);
                        //   ArrayList<Coord> deps = allCells(c.toString());
                        ArrayList<Index2D> deps = allCells(c.getData());
                        int dd = canBeComputed(deps, ans);
                        if (dd != -1) {
                            ans[x][y] = dd;
                            count++;
                            changed = true;
                        }
                    }
                }
            }
        }
        return ans;
    }

    @Override
    public void load(String fileName) throws IOException {
        Ex2Sheet sp = new Ex2Sheet();
        File myObj = new File(fileName);
        Scanner myReader = new Scanner(myObj);
        String s0 = myReader.nextLine();
        if (Ex2Utils.Debug) {
            System.out.println("Loading file: " + fileName);
            System.out.println("File info (header:) " + s0);
        }
        while (myReader.hasNextLine()) {
            s0 = myReader.nextLine();
            int xPart = s0.indexOf(",");
            int yPart = s0.indexOf(",",xPart + 1);
            String functionPart = s0.substring(yPart + 1);
            String[] s1 = s0.split(",");
            try {
                int x = Ex2Sheet.getInteger(s1[0]);
                int y = Ex2Sheet.getInteger(s1[1]);
                sp.set(x, y, functionPart);
            } catch (Exception e) {
                e.printStackTrace();
                System.err.println("Line: " + data + " is in the wrong format (should be x,y,cellData)");
            }
        }
        sp.eval();
        table = sp.table;
        data = sp.data;
    }

    @Override
    public void save(String fileName) throws IOException {
        FileWriter myWriter = new FileWriter(fileName);
        myWriter.write("I2CS ArielU: SpreadSheet (Ex2) assignment - this line should be ignored in the load method\n");
        for (int x = 0; x < this.width(); x = x + 1) {
            for (int y = 0; y < this.height(); y = y + 1) {
                Cell c = get(x, y);
                if (c != null && !c.getData().equals("")) {
                    String s = x + "," + y + "," + c.getData();
                    myWriter.write(s + "\n");
                }
            }
        }
        myWriter.close();
    }

    private int canBeComputed(ArrayList<Index2D> deps, int[][] tmpTable) {
        int ans = 0;
        for (int i = 0; i < deps.size() & ans != -1; i = i + 1) {
            Index2D c = deps.get(i);
            if (!(isIn(c.getX(), c.getY()))) {
                ans = 0;
                return ans;
            }
            int v = tmpTable[c.getX()][c.getY()];
            if (v == -1) {
                ans = -1;
            } // not yet computed;
            else {
                ans = Math.max(ans, v + 1);
            }
        }
        return ans;
    }

    @Override
    public String eval(int x, int y) {
        Cell c = table[x][y];
        String line = c.getData();
        if (c == null || c.getType() == Ex2Utils.TEXT) {
            data[x][y] = null;
            return line;
        }
        int type = c.getType();
        if (type == Ex2Utils.NUMBER) {
            data[x][y] = getDouble(c.toString());
            return line;
        }
        if (type == Ex2Utils.FUNCTION || type == Ex2Utils.FUNC_ERR_FORMAT) {
            if (!Range2D.advnacedValidFunction(line, this)) {
                c.setType(Ex2Utils.FUNC_ERR_FORMAT);
            } else {
                c.setType(Ex2Utils.FUNCTION);
                Range2D range = new Range2D(Range2D.findStartAndEndValid(line));
                range.updateValue(this);
                Double dd1 = range.evaluateFunction(line);
                data[x][y] = dd1;
            }
        } else if (type == Ex2Utils.IF || type == Ex2Utils.IF_ERR_FORMAT || type == Ex2Utils.ERR_WRONG_IF) {
            if (!validIf(line)) {
                c.setType(Ex2Utils.ERR_WRONG_IF);
            } else {
                c.setType(Ex2Utils.IF);
                Object ifResult = evaluateIf(line);
                if (ifResult instanceof Double) {
                    data[x][y] = (Double) ifResult;
                }
            }
        } else if (type == Ex2Utils.FORM | type == Ex2Utils.ERR_CYCLE_FORM || type == Ex2Utils.ERR_FORM_FORMAT) {
            line = line.substring(1); // removing the first "="
            if (isForm(line)) {
                Double dd = computeForm(x, y);
                data[x][y] = dd;
                if (dd == null) {
                    c.setType(Ex2Utils.ERR_FORM_FORMAT);
                } else {
                    c.setType(Ex2Utils.FORM);
                }
            } else {
                data[x][y] = null;
            }
        }
        String ans = null;
        if (data[x][y] != null) {
            ans = data[x][y].toString();
        }
        return ans;
    }

    /////////////////////////////////////////////////
    public static Integer getInteger(String line) {
        Integer ans = null;
        try {
            ans = Integer.parseInt(line);
        } catch (Exception e) {
            ;
        }
        return ans;
    }

    public static Double getDouble(String line) {
        Double ans = null;
        try {
            ans = Double.parseDouble(line);
        } catch (Exception e) {
            ;
        }
        return ans;
    }

    public static String removeSpaces(String s) {
        String ans = null;
        if (s != null) {
            String[] words = s.split(" ");
            ans = new String();
            for (int i = 0; i < words.length; i = i + 1) {
                ans += words[i];
            }
        }
        return ans;
    }

    public int checkType(String line) {
        line = removeSpaces(line);
        int ans = Ex2Utils.TEXT;
        double d = getDouble(line);
        if (d > Double.MIN_VALUE) {
            ans = Ex2Utils.NUMBER;
        } else {
            if (line.charAt(0) == '=') {
                ans = Ex2Utils.ERR_FORM_FORMAT;
                int type = -1;
                String s = line.substring(1);
                if (isForm(s)) {
                    ans = Ex2Utils.FORM;
                }
            }
        }
        return ans;
    }

    public boolean isForm(String form) {
        boolean ans = false;
        if (form != null) {
            form = removeSpaces(form);
            try {
                ans = isFormP(form);
            } catch (Exception e) {
                ;
            }
        }
        return ans;
    }

    private Double computeForm(int x, int y) {
        Double ans = null;
        String form = table[x][y].getData();
        form = form.substring(1);// remove the "="
        if (isForm(form)) {
            form = removeSpaces(form);
            ans = computeFormP(form);
        }
        return ans;
    }

    private boolean isFormP(String form) {
        boolean ans = false;
        while (canRemoveB(form)) {
            form = removeB(form);
        }
        Index2D c = new CellEntry(form);
        if (isIn(c.getX(), c.getY())) {
            ans = true;
        } else {
            if (isNumber(form)) {
                ans = true;
            } else {
                int ind = findLastOp(form);// bug
                if (ind == 0) {  // the case of -1, or -(1+1)
                    char c1 = form.charAt(0);
                    if (c1 == '-' | c1 == '+') {
                        ans = isFormP(form.substring(1));
                    } else {
                        ans = false;
                    }
                } else {
                    String f1 = form.substring(0, ind);
                    String f2 = form.substring(ind + 1);
                    ans = isFormP(f1) && isFormP(f2);
                }
            }
        }
        return ans;
    }

    public ArrayList<Index2D> allCells(String line) {
        ArrayList<Index2D> ans = new ArrayList<Index2D>();
        if (Range2D.ValidFunction(line)) {
            line = Range2D.AllCellsInRange(line);
        }
        if (validIf(line)) {
            line = allCellsInIf(line);
        }
        int i = 0;
        int len = line.length();
        while (i < len) {
            int m2 = Math.min(len, i + 2);
            int m3 = Math.min(len, i + 3);
            String s2 = line.substring(i, m2);
            String s3 = line.substring(i, m3);
            Index2D sc2 = new CellEntry(s2);
            Index2D sc3 = new CellEntry(s3);
            if (sc3.isValid()) {
                ans.add(sc3);
                i += 3;
            } else {
                if (sc2.isValid()) {
                    ans.add(sc2);
                    i += 2;
                } else {
                    i = i + 1;
                }
            }

        }
        return ans;
    }

    private Double computeFormP(String form) {
        Double ans = null;
        while (canRemoveB(form)) {
            form = removeB(form);
        }
        CellEntry c = new CellEntry(form);
        if (c.isValid()) {

            return getDouble(eval(c.getX(), c.getY()));
        } else {
            if (isNumber(form)) {
                ans = getDouble(form);
            } else {
                int ind = findLastOp(form);
                int opInd = opCode(form.substring(ind, ind + 1));
                if (ind == 0) {  // the case of -1, or -(1+1)
                    double d = 1;
                    if (opInd == 1) {
                        d = -1;
                    }
                    ans = d * computeFormP(form.substring(1));
                } else {
                    String f1 = form.substring(0, ind);
                    String f2 = form.substring(ind + 1);

                    Double a1 = computeFormP(f1);
                    Double a2 = computeFormP(f2);
                    if (a1 == null || a2 == null) {
                        ans = null;
                    } else {
                        if (opInd == 0) {
                            ans = a1 + a2;
                        }
                        if (opInd == 1) {
                            ans = a1 - a2;
                        }
                        if (opInd == 2) {
                            ans = a1 * a2;
                        }
                        if (opInd == 3) {
                            ans = a1 / a2;
                        }
                    }
                }
            }
        }
        return ans;
    }

    private static int opCode(String op) {
        int ans = -1;
        for (int i = 0; i < Ex2Utils.M_OPS.length; i = i + 1) {
            if (op.equals(Ex2Utils.M_OPS[i])) {
                ans = i;
            }
        }
        return ans;
    }

    private static int findFirstOp(String form) {
        int ans = -1;
        int s1 = 0, max = -1;
        for (int i = 0; i < form.length(); i++) {
            char c = form.charAt(i);
            if (c == ')') {
                s1--;
            }
            if (c == '(') {
                s1++;
            }
            int op = op(form, Ex2Utils.M_OPS, i);
            if (op != -1) {
                if (s1 > max) {
                    max = s1;
                    ans = i;
                }
            }
        }
        return ans;
    }

    private static int findLastOp(String form) {
        int ans = -1;
        double s1 = 0, min = -1;
        for (int i = 0; i < form.length(); i++) {
            char c = form.charAt(i);
            if (c == ')') {
                s1--;
            }
            if (c == '(') {
                s1++;
            }
            int op = op(form, Ex2Utils.M_OPS, i);
            if (op != -1) {
                double d = s1;
                if (op > 1) {
                    d += 0.5;
                }
                if (min == -1 || d <= min) {
                    min = d;
                    ans = i;
                }
            }
        }
        return ans;
    }

    private static String removeB(String s) {
        if (canRemoveB(s)) {
            s = s.substring(1, s.length() - 1);
        }
        return s;
    }

    private static boolean canRemoveB(String s) {
        boolean ans = false;
        if (s != null && s.startsWith("(") && s.endsWith(")")) {
            ans = true;
            int s1 = 0, max = -1;
            for (int i = 0; i < s.length() - 1; i++) {
                char c = s.charAt(i);
                if (c == ')') {
                    s1--;
                }
                if (c == '(') {
                    s1++;
                }
                if (s1 < 1) {
                    ans = false;
                }
            }
        }
        return ans;
    }

    private static int op(String line, String[] words, int start) {
        int ans = -1;
        line = line.substring(start);
        for (int i = 0; i < words.length && ans == -1; i++) {
            if (line.startsWith(words[i])) {
                ans = i;
            }
        }
        return ans;
    }

    public static boolean isNumber(String line) {
        boolean ans = false;
        try {
            double v = Double.parseDouble(line);
            ans = true;
        } catch (Exception e) {
            ;
        }
        return ans;
    }

    public static boolean BasicValidIF(String line) {
        if (line.isEmpty()) {
            return false;
        }
        if (line.length() < 3) {
            return false;
        }
        if (!(line.startsWith("=if"))) {
            return false;
        }
        return true;
    }

    public static String ifCondition(String line) {
        if (line.length() < 12) {
            return "";
        }
        int indexEnd = line.indexOf(",");
        String condition = line.substring(4, indexEnd);
        return condition;
    }

    public boolean evaluateCondition(String line) {
        String condition = ifCondition(line);
        if (condition.contains("<=")) {
            int split = condition.indexOf("<=");
            String form1 = condition.substring(0, split);
            String form2 = condition.substring(split + 2);
            return computeFormP(form1) <= computeFormP(form2);
        } else if (condition.contains(">=")) {
            int split = condition.indexOf(">=");
            String form1 = condition.substring(0, split);
            String form2 = condition.substring(split + 2);
            return computeFormP(form1) >= computeFormP(form2);
        } else if (condition.contains("==")) {
            int split = condition.indexOf("==");
            String form1 = condition.substring(0, split);
            String form2 = condition.substring(split + 2);
            return computeFormP(form1).equals(computeFormP(form2));
        } else if (condition.contains("!=")) {
            int split = condition.indexOf("!=");
            String form1 = condition.substring(0, split);
            String form2 = condition.substring(split + 2);
            return !computeFormP(form1).equals(computeFormP(form2));
        } else if (condition.contains(">")) {
            int split = condition.indexOf(">");
            String form1 = condition.substring(0, split);
            String form2 = condition.substring(split + 1);
            return computeFormP(form1) > computeFormP(form2);
        } else if (condition.contains("<")) {
            int split = condition.indexOf("<");
            String form1 = condition.substring(0, split);
            String form2 = condition.substring(split + 1);
            return computeFormP(form1) < computeFormP(form2);
        }
        return false;
    }

    public static String ifTrue(String line) {
        int indexEndCondition = line.indexOf(",");
        int indexIfTrueStart = indexEndCondition + 1;
        int level = 0;
        int indexIfTrueEnd = -1;
        for (int i = indexIfTrueStart; i < line.length(); i++) {
            char c = line.charAt(i);
            if (c == '(') {
                level++;
            }
            if (c == ')') {
                level--;
            }
            if (c == ',' && level == 0) {
                indexIfTrueEnd = i;
                break;
            }
        }
        return line.substring(indexIfTrueStart, indexIfTrueEnd);
    }

    public static String ifFalse(String line) {
        int indexEndCondition = line.indexOf(",");
        int indexIfTrueStart = indexEndCondition + 1;
        int level = 0;
        int indexIfTrueEnd = 0;
        for (int i = indexIfTrueStart; i < line.length(); i++) {
            char c = line.charAt(i);
            if (c == '(') level++;
            if (c == ')') level--;
            if (c == ',' && level == 0) {
                indexIfTrueEnd = i;
                break;
            }
        }
        return line.substring(indexIfTrueEnd + 1, line.length() - 1);
    }

    public Object evaluateIf(String line) {
        if (evaluateCondition(line)) {
            String trueCondition = ifTrue(line);
            if (isNumber(trueCondition)) {
                return Double.parseDouble(trueCondition);
            }
            if (SCell.isFunction(trueCondition)) {
                Range2D range = new Range2D(Range2D.findStartAndEndValid(trueCondition));
                range.updateValue(this);
                return range.evaluateFunction(trueCondition);
            }
            if (SCell.BasicIsForm(trueCondition)) {
                return computeFormP(trueCondition.substring(1));
            }
            if (SCell.isIf(trueCondition)) {
                return evaluateIf(trueCondition);
            }
            return trueCondition;
        } else {
            String falseCondition = ifFalse(line);
            if (isNumber(falseCondition)) {
                return Double.parseDouble(falseCondition);
            }
            if (SCell.isFunction(falseCondition)) {
                Range2D range = new Range2D(Range2D.findStartAndEndValid(falseCondition));
                range.updateValue(this);
                return range.evaluateFunction(falseCondition);
            }
            if (SCell.BasicIsForm(falseCondition)) {
                return computeFormP(falseCondition.substring(1));
            }
            if (SCell.isIf(falseCondition)) {
                return evaluateIf(falseCondition);
            }
            return falseCondition;
        }
    }

    public boolean validIf(String _line) {
        int count = 0;
        for (int i = 0; i < _line.length(); i++) {
            if (_line.charAt(i) == ',') {
                count++;
            }
        }
        if (count < 2) {
            return false;
        }
        if (_line.isEmpty() || _line.isBlank()) {
            return false;
        }
        if (ifTrue(_line).equals(_line) || ifFalse(_line).equals(_line)) {
            return false;
        }
        if (_line.contains(" ")) {
            return false;
        }
        if (!validConditionIf(ifCondition(_line))) {
            return false;
        }
        if (!validIfTrueAndFalse(ifTrue(_line))) {
            return false;
        }
        if (!validIfTrueAndFalse(ifFalse(_line))) {
            return false;
        }
        return true;
    }

    public boolean validConditionIf(String _line) {
        if (_line.isEmpty()) {
            return false;
        }
        int pass = 0;
        String oprator = null;
        for (int i = 0; i < Ex2Utils.B_OPS.length; i++) {
            if (countOccurrences(_line, Ex2Utils.B_OPS[i]) > 1) {
                return false;
            }
            if (_line.contains(Ex2Utils.B_OPS[i])) {
                pass++;
                oprator = Ex2Utils.B_OPS[i];
            }
        }
        if (pass != 1 || oprator == null) {
            return false;
        }
        int indexOperator = _line.indexOf(oprator);
        if (indexOperator == -1) {
            return false;
        }
        String leftFormula = _line.substring(0, indexOperator);
        String rightFormula = _line.substring(indexOperator + oprator.length());
        if (leftFormula.isEmpty()) {
            return false;
        }
        if (leftFormula.charAt(0) == '=') {
            if (!isForm(leftFormula.substring(1))) {
                return false;
            }
        } else {
            if (!isFormP(leftFormula)) {
                return false;
            }
        }
        if (rightFormula.isEmpty()) {
            return false;
        }
        if (rightFormula.charAt(0) == '=') {
            if (!isForm(rightFormula.substring(1))) {
                return false;
            } else {
                if (!isFormP(rightFormula)) {
                    return false;
                }
            }
        }
        return true;
    }

    public boolean validIfTrueAndFalse(String line) {
        if (line.isEmpty()) {
            return false;
        }
        if (line.charAt(0) != '=') {
            return true;
        }
        if (isForm(line.substring(1))) {
            return true;
        }
        if (isNumber(line)) {
            return true;
        }
        if (Range2D.advnacedValidFunction(line, this)) {
            return true;
        }
        if (validIf(line)) {
            return true;
        }
        return false;
    }

    public static int countOccurrences(String text, String sub) {
        int count = 0;
        int index = 0;
        while ((index = text.indexOf(sub, index)) != -1) {
            count++;
            index += sub.length();
        }
        return count;
    }

    public String allCellsInIf(String line) {
        ArrayList<Index2D> cells = allCells(ifCondition(line));
        StringBuilder result = new StringBuilder("[");

        for (int i = 0; i < cells.size(); i++) {
            result.append(cells.get(i).toString());
            if (i < cells.size() - 1) {
                result.append(",");
            }
        }
        String True = ifTrue(line);
        String False = ifFalse(line);

        if (SCell.isFunction(True)) {
            result.append(Range2D.AllCellsInRange(True));
        } else if (SCell.isIf(True)) {
            result.append(allCellsInIf(True)); // Recursively handle nested IF
        }
        if (SCell.isFunction(False)) {
            result.append(Range2D.AllCellsInRange(False));
        } else if (SCell.isIf(False)) {
            result.append(allCellsInIf(True));
        }
        result.append("]");
        return result.toString();
    }

    public static boolean validCell(String a) {
        if (a == null || a.isEmpty()) {
            return false;
        }
        a = a.toLowerCase();
        if (a.length() == 2) {
            if (a.charAt(0) >= 'a' && a.charAt(0) <= 'z' && a.charAt(1) >= '0' && a.charAt(1) <= '9') {
                return true;
            }
        }
        if (a.length() == 3) {
            if (a.charAt(0) >= 'a' && a.charAt(0) <= 'z') {
                String numPart = a.substring(1);
                try {
                    int num = Integer.parseInt(numPart);
                    return num >= 0 && num <= 99;
                } catch (NumberFormatException e) {
                    return false;
                }
            }
        }
        return false;
    }
    public boolean advancedValidCell(String line) {
        if (!validCell(line)) {
            return false;
        }
        line = line.toUpperCase();
        char col = line.charAt(0);  // First character is the column (A-Z)
        String rowPart = line.substring(1);  // Extract numeric part

        try {
            int row = Integer.parseInt(rowPart);  // Convert the row part to an integer

            // Ensure column and row are within valid spreadsheet dimensions
            if (col - 'A' >= 0 && col - 'A' < this.width() && row >= 0 && row < this.height()) {
                return true;
            }
        } catch (NumberFormatException e) {
            return false;  // If parsing fails, it's not a valid cell
        }

        return false;
    }
}



