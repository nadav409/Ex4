import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Scanner;

/**
 * This class represents a spreadsheet that holds a table of cells.
 * It allows setting and retrieving values, evaluating formulas, evaluating functions, and loading/saving data.
 */
public class Ex2Sheet implements Sheet {
    private Cell[][] table;
    private Double[][] data;

    /**
     * Creates a spreadsheet with given width and height.
     * @param x The number of columns.
     * @param y The number of rows.
     */
    public Ex2Sheet(int x, int y) {
        table = new SCell[x][y];
        for (int i = 0; i < x; i = i + 1) {
            for (int j = 0; j < y; j = j + 1) {
                table[i][j] = new SCell("");
            }
        }
        eval();
    }

    /**
     * Creates a spreadsheet with default width and height.
     * The dimensions are taken from Ex2Utils.
     */

    public Ex2Sheet() {
        this(Ex2Utils.WIDTH, Ex2Utils.HEIGHT);
    }

    /**
     * Gets the value stored in a specific cell.
     * @param x The column index.
     * @param y The row index.
     * @return The string representation of the cell's value.
     */
    @Override
    public String value(int x, int y) {
        String ans = "";
        Cell c = get(x, y);
        ans = c.toString();
        int t = c.getType();
        if (t == Ex2Utils.ERR_CYCLE_FORM) {
            ans = Ex2Utils.ERR_CYCLE;
            c.setOrder(-1);
        }
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

    /**
     * Gets a cell object at a specific location.
     * @param x The column index.
     * @param y The row index.
     * @return The Cell object.
     */
    @Override
    public Cell get(int x, int y) {
        return table[x][y];
    }

    /**
     * Gets a cell using its string coordinates (e.g., "A1").
     * @param cords The string representation of the cell coordinates.
     * @return The Cell object if found, otherwise null.
     */
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

    /**
     * @return The width (number of columns) of the spreadsheet.
     */
    @Override
    public int width() {
        return table.length;
    }

    /**
     * @return The height (number of rows) of the spreadsheet.
     */
    @Override
    public int height() {
        return table[0].length;
    }

    /**
     * Sets a value in the spreadsheet at the specified location.
     * @param x The column index.
     * @param y The row index.
     * @param s The value to set in the cell.
     */
    @Override
    public void set(int x, int y, String s) {
        Cell c = new SCell(s);
        table[x][y] = c;
        eval();// Recalculate all values after setting
    }

    /**
     * Evaluates all cells, recalculating formulas and updating values in the table.
     */
    @Override
    public void eval() {
        int[][] dd = depth(); // Computes the dependency depth for each cell
        data = new Double[width()][height()];
        for (int x = 0; x < width(); x = x + 1) {
            for (int y = 0; y < height(); y = y + 1) {
                Cell c = table[x][y];
                // If the cell is not a text type and is computable, evaluate its value
                if (dd[x][y] != -1 && c != null && (c.getType() != Ex2Utils.TEXT)) {
                    String res = eval(x, y);
                    Double d = getDouble(res);
                    // If the result is invalid, mark it as a formula error
                    if (d == null) {
                        if (c.getType() != Ex2Utils.FUNC_ERR_FORMAT && c.getType() != Ex2Utils.IF_ERR_FORMAT && c.getType() != Ex2Utils.IF && c.getType() != Ex2Utils.ERR_WRONG_IF) {
                            c.setType(Ex2Utils.ERR_FORM_FORMAT);
                        }
                    } else {
                        data[x][y] = d;// Stores the computed numeric value
                    }
                }
                // If the cell is part of a circular dependency
                if (dd[x][y] == -1) {
                    c.setType(Ex2Utils.ERR_CYCLE_FORM);
                }
            }
        }
    }

    /**
     * Checks whether the given cell coordinates are within the spreadsheet bounds.
     * @param xx The column index.
     * @param yy The row index.
     * @return True if inside the spreadsheet, otherwise false.
     */
    @Override
    public boolean isIn(int xx, int yy) {
        boolean ans = true;
        if (xx < 0 | yy < 0 | xx >= width() | yy >= height()) {
            ans = false;
        }
        return ans;
    }

    /**
     * Computes the dependency depth for each cell.
     * This function determines the order in which cells should be computed,
     * ensuring that dependent cells are evaluated only after their dependencies.
     * @return A 2D array representing dependency of the cells.
     */
    @Override
    public int[][] depth() {
        int[][] ans = new int[width()][height()];
        for (int x = 0; x < width(); x = x + 1) {
            for (int y = 0; y < height(); y = y + 1) {
                Cell c = this.get(x, y);
                int t = c.getType();
                if (Ex2Utils.TEXT != t) {// text cells are not computable.
                    ans[x][y] = -1;
                }
            }
        }
        int count = 0, all = width() * height();// Track computed cells
        boolean changed = true;
        // process cells until no further changes occur or the maximum depth is reached in this spreadsheet.
        while (changed && count < all) {
            changed = false;
            for (int x = 0; x < width(); x = x + 1) {
                for (int y = 0; y < height(); y = y + 1) {
                    if (ans[x][y] == -1) {// Process only uncomputed cells
                        Cell c = this.get(x, y);
                        ArrayList<Index2D> deps = allCells(c.getData());// Identify and collect the dependent cells required for computation."
                        int dd = canBeComputed(deps, ans);// Determine if computation is possible
                        if (dd != -1) {// If computation is possible, assign depth level
                            ans[x][y] = dd;
                            count++;// Increase computed cell count
                            changed = true;// Mark that a change occurred
                        }
                    }
                }
            }
        }
        return ans;
    }

    /**
     * Loads spreadsheet data from a file/
     * It reads the file line by line and extracts cell coordinates and values.
     * @param fileName The name of the file to load data from.
     * @throws IOException If an error occurs while reading the file.
     */
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
            int yPart = s0.indexOf(",", xPart + 1);
            String functionPart = s0.substring(yPart + 1);
            String[] s1 = s0.split(",");// Split the line into parts
            try {
                int x = Ex2Sheet.getInteger(s1[0]);
                int y = Ex2Sheet.getInteger(s1[1]);
                sp.set(x, y, functionPart);// Set value in the spreadsheet
            } catch (Exception e) {
                e.printStackTrace();
                System.err.println("Line: " + data + " is in the wrong format (should be x,y,cellData)");
            }
        }
        sp.eval();
        table = sp.table;
        data = sp.data;
    }

    /**
     * Saves the current spreadsheet data to a file.
     * It writes the table's content to a file line by line.
     * @param fileName The name of the file where data should be saved.
     * @throws IOException If an error occurs while writing to the file.
     */
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

    /**
     * Checks if the given dependencies allow a cell to be computed.
     * It verifies if all dependent cells have already been computed.
     "@param deps A list of cells that must be computed before this cell.
     * @param tmpTable A temporary table tracking computed cells.
     * @return The computation depth if valid, otherwise -1.
     */
    private int canBeComputed(ArrayList<Index2D> deps, int[][] tmpTable) {
        int ans = 0;
        for (int i = 0; i < deps.size() & ans != -1; i = i + 1) {
            Index2D c = deps.get(i);
            if (!(isIn(c.getX(), c.getY()))) {// Check if cell is within bounds
                ans = 0;
                return ans;
            }
            int v = tmpTable[c.getX()][c.getY()];
            if (v == -1) {
                ans = -1;
            } // not yet computed;
            else {
                ans = Math.max(ans, v + 1);// Update depth level
            }
        }
        return ans;
    }

    /**
     * Evaluates a specific cell at the given coordinates.
     * It processes the cell's value, determines its type (text, number, formula, function, ...),
     * and computes the appropriate result.
     * @param x The column index of the cell.
     * @param y The row index of the cell.
     * @return The evaluated value of the cell as a string.
     */

    @Override
    public String eval(int x, int y) {
        Cell c = table[x][y];
        String line = c.getData();// Get the data stored in the cell
        c.setData(line);
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
            if (!advancedValidIf(line)) {
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

    /**
     * Converts a string representation of a number to an Integer.
     * Returns null if the string is not a valid integer.
     * @param line The string to be converted.
     * @return The Integer value of the string, or null if invalid.
     */
    public static Integer getInteger(String line) {
        Integer ans = null;
        try {
            ans = Integer.parseInt(line);
        } catch (Exception e) {
            ;
        }
        return ans;
    }

    /**
     * Converts a string representation of a number to a Double.
     * Returns null if the string is not a valid number.
     * @param line The string to be converted.
     * @return The Double value of the string, or null if invalid.
     */
    public static Double getDouble(String line) {
        Double ans = null;
        try {
            ans = Double.parseDouble(line);
        } catch (Exception e) {
            ;
        }
        return ans;
    }

    /**
     * Removes all spaces from the input string.
     * @param s The input string.
     * @return The string without spaces.
     */
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

    /**
     * Checks if a given string is a valid formula.
     * It removes spaces and verifies the format using `isFormP`.
     * @param form The formula string to check.
     * @return True if the string is a valid formula, otherwise false.
     */
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

    /**
     * Computes the result of a formula stored in a specific cell.
     * Removes the '=' symbol and validates the formula before computation.
     * @param x The column index of the cell.
     * @param y The row index of the cell.
     * @return The computed value as a Double, or null if invalid.
     */
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

    /**
     * Checks if a given string is a properly formatted formula.
     * @param form The formula string to validate.
     * @return True if the string represents a valid formula, otherwise false.
     */
    private boolean isFormP(String form) {
        boolean ans = false;
        while (canRemoveB(form)) {
            form = removeB(form);
        }
        Index2D c = new CellEntry(form);
        if (isIn(c.getX(), c.getY())) {// Check if it's a valid cell reference
            ans = true;
        } else {
            if (isNumber(form)) {// Check if it's a valid number
                ans = true;
            } else {
                int ind = findLastOp(form);// Find the last operator position
                if (ind == 0) {  // the case of -1, or -(1+1)
                    char c1 = form.charAt(0);
                    if (c1 == '-' | c1 == '+') {
                        ans = isFormP(form.substring(1));// Recursively check if the remaining expression is a valid formula.
                    } else {
                        ans = false;
                    }
                } else {
                    String f1 = form.substring(0, ind);
                    String f2 = form.substring(ind + 1);
                    ans = isFormP(f1) && isFormP(f2);// Ensure both parts of the expression are valid
                }
            }
        }
        return ans;
    }

    /**
     * Extracts all referenced cells from a given formula.
     * @param line The formula string.
     * @return A list of Index2D objects representing all referenced cells.
     */
    public ArrayList<Index2D> allCells(String line) {
        ArrayList<Index2D> ans = new ArrayList<Index2D>();
        if (Range2D.ValidFunction(line)) {
            line = Range2D.AllCellsInRange(line);// Convert function calls to all the cells int the range.
        }
        if (validIf(line)) {
            line = allCellsInIf(line);// Convert If calls to all the cells in the If statements.
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
            if (sc3.isValid()) {// Check if a 3-character reference is valid
                ans.add(sc3);
                i += 3;
            } else {
                if (sc2.isValid()) {// Check if a 2-character reference is valid
                    ans.add(sc2);
                    i += 2;
                } else {
                    i = i + 1;
                }
            }

        }
        return ans;
    }

    /**
     * Computes the result of a mathematical formula recursively.
     * @param form The formula string to evaluate.
     * @return The computed result as a Double, or null if the formula is invalid.
     */
    private Double computeFormP(String form) {
        Double ans = null;
        while (canRemoveB(form)) {
            form = removeB(form);
        }
        CellEntry c = new CellEntry(form);
        if (c.isValid()) {
            return getDouble(eval(c.getX(), c.getY()));// Evaluate referenced cell
        } else {
            if (isNumber(form)) {
                ans = getDouble(form);// Convert number string to Double
            } else {
                int ind = findLastOp(form);// Find last operator position
                int opInd = opCode(form.substring(ind, ind + 1));// Get operation type
                if (ind == 0) {  // the case of -1, or -(1+1)
                    double d = 1;
                    if (opInd == 1) {// If the operator is '-', negate the value
                        d = -1;
                    }
                    ans = d * computeFormP(form.substring(1));// Recursively compute the remaining expression
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

    /**
     * Finds the position of the last operator in an expression.
     * Used to determine where to split for recursive computation.
     * @param form The formula string.
     * @return The index of the last operator found, or -1 if none is found.
     */
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

    /**
     * Removes surrounding parentheses.
     * @param s The input string.
     * @return The modified string without unnecessary parentheses.
     */
    private static String removeB(String s) {
        if (canRemoveB(s)) {
            s = s.substring(1, s.length() - 1);
        }
        return s;
    }

    /**
     * Determines whether parentheses can be safely removed from a string.
     * Ensures the parentheses enclose the entire expression correctly.
     * @param s The input string.
     * @return True if the parentheses can be removed, otherwise false.
     */
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

    /**
     * Checks if a given character at a specific index in a string is an operator.
     * @param line The string to check.
     * @param words The array of operator symbols.
     * @param start The index to check for an operator.
     * @return The index of the operator in words, or -1 if not found.
     */
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

    /**
     * Checks if a given string represents a valid number.
     * @param line The string to check.
     * @return True if the string can be parsed as a number, otherwise false.
     */
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

    /**
     * Extracts the condition part from an IF function.
     * @param line The IF function string.
     * @return The extracted condition as a string.
     */
    public static String ifCondition(String line) {
        if (line.length() < 12) {
            return "";
        }
        int indexEnd = line.indexOf(",");
        String condition = line.substring(4, indexEnd);
        return condition;
    }

    /**
     * Evaluates the condition of an IF function and returns the result.
     * Supports conditions such as <, >, <=, >=, ==, and !=.
     * @param line The IF function string.
     * @return True if the condition evaluates to true, otherwise false.
     */
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
    /**
     * Extracts the 'true' part of an IF function.
     * @param line The IF function string.
     * @return The string representing the true case of the IF function.
     */
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

    /**
     * Extracts the 'false' part of an IF function.
     * @param line The IF function string.
     * @return The string representing the false case of the IF function.
     */
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

    /** Evaluates an IF function by computing its condition and returning the appropriate value.
     * If the condition is true, it returns the true part; otherwise, it returns the false part.
     * The true and false branches can be numbers, formulas, functions, text, or IF statements.
     * If the condition is true, it returns the true branch; otherwise, it returns the false branch.
     * @param line The IF function string.
     * @return The computed result of the IF function as an Object (Number or String).
     */
    public Object evaluateIf(String line) {
        if (evaluateCondition(line)) {//if condition is true
            String trueCondition = ifTrue(line);
            if (isNumber(trueCondition)) {
                return Double.parseDouble(trueCondition);// Convert number string to Double
            }
            if (SCell.isFunction(trueCondition)) {
                Range2D range = new Range2D(Range2D.findStartAndEndValid(trueCondition));
                range.updateValue(this);
                return range.evaluateFunction(trueCondition);// Evaluate function in the range
            }
            if (SCell.BasicIsForm(trueCondition)) {
                return computeFormP(trueCondition.substring(1));// Compute mathematical expression
            }
            if (SCell.isIf(trueCondition)) {
                return evaluateIf(trueCondition);// Recursively evaluate IF statement
            }
            return trueCondition;// Return as a string if none of the above conditions apply
        } else {
            String falseCondition = ifFalse(line);//if the condition is false
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

    /**
     * Checks an IF function is in a correct format.
     * Checks for the correct number of commas, parentheses balance, and valid conditions.
     * @param _line The IF function string.
     * @return True if the IF function is correctly formatted, otherwise false.
     */
    public boolean validIf(String _line) {
        if (_line.isEmpty() || _line.isBlank()) {
            return false;
        }
        int count = 0;
        int open = 0;
        int close = 0;
        for (int i = 0; i < _line.length(); i++) {
            if (_line.charAt(i) == ',') {
                count++;// Count commas to ensure correct IF format
            }
            if (_line.charAt(i) == '(') {
                open++;
            }
            if (_line.charAt(i) == ')') {
                close++;
            }
        }
        if (count < 2) {
            return false;// IF function must have at least two commas
        }
        if (open != close) {
            return false;// Parentheses must be equal
        }
        if (ifTrue(_line).equals(_line) || ifFalse(_line).equals(_line)) {
            return false;// Check if the true or false branch is the same as the entire IF statement, which would cause an infinite recursive loop
        }
        if (_line.contains(" ")) {
            return false;// IF function should not contain spaces
        }
        if (!validConditionIf(ifCondition(_line))) {
            return false;// Check if the condition part of the IF statement is valid.
        }
        if (!validIfTrueAndFalse(ifTrue(_line))) {
            return false;// check if the true part is valid.
        }
        if (!validIfTrueAndFalse(ifFalse(_line))) {
            return false;// check if the false part is valid.
        }
        return true;
    }

    public Boolean advancedValidIf(String line) {
        return validIf(line) && CheckCellsInIf(line);
    }


    /**
     * Check if the condition part of an IF function is valid.
     * Ensures it contains a valid comparison operator and valid formula on both sides.
     * @param _line The condition string.
     * @return True if the condition is valid, otherwise false.
     */
    public boolean validConditionIf(String _line) {
        if (_line.isEmpty()) {
            return false;
        }
        int pass = 0;
        String oprator = null;
        for (int i = 0; i < Ex2Utils.B_OPS.length; i++) {
            if (countOccurrences(_line, Ex2Utils.B_OPS[i]) > 1) {
                return false;// Condition should contain only one operator
            }
            if (_line.contains(Ex2Utils.B_OPS[i])) {
                pass++;
                oprator = Ex2Utils.B_OPS[i];
            }
        }
        if (pass != 1 || oprator == null) {
            return false;// Must contain exactly one operator
        }
        int indexOperator = _line.indexOf(oprator);
        if (indexOperator == -1) {
            return false; // Operator not found, invalid condition
        }
        String leftFormula = _line.substring(0, indexOperator);
        String rightFormula = _line.substring(indexOperator + oprator.length());
        if (leftFormula.isEmpty()) {
            return false;// Left side of the condition must not be empty
        }
        if (leftFormula.charAt(0) == '=') {
            if (!isForm(leftFormula.substring(1))) {
                return false; // Left formula must be a valid.
            }
        } else {
            if (!isForm(leftFormula)) {
                return false;// Right side of the condition must not be empty
            }
        }
        if (rightFormula.isEmpty()) {
            return false;
        }
        if (rightFormula.charAt(0) == '=') {
            if (!isForm(rightFormula.substring(1))) {
                return false;
            }
        }
        else {
            if (!isForm(rightFormula)){
                return false;
            }
        }
        return true;// If all checks pass, the condition is valid
    }
    /**
     * Validates if the true or false parts of an IF function are valid.
     * The value can be a text,number, formula, function, or another IF statement.
     * @param line The true or false part of the IF function.
     * @return True if valid, otherwise false.
     */
    public boolean validIfTrueAndFalse(String line) {
        if (line.isEmpty()) {
            return false;
        }
        if (line.charAt(0) != '=') {
            return true;// If it's a text, it's valid
        }
        if (isForm(line.substring(1))) {
            return true;// If it's a valid formula, return true
        }
        if (isNumber(line)) {
            return true;// If it's a number, return true
        }
        if (Range2D.advnacedValidFunction(line, this)) {
            return true;// If it's a valid function, return true
        }
        if (validIf(line)) {
            return true;// If it's a valid IF statement, return true
        }
        return false;
    }
    /**
     * Counts the occurrences of a substring in a given text.
     * @param text The main text.
     * @param sub The substring to search for.
     * @return The number of times the substring appears in the text.
     */
    public static int countOccurrences(String text, String sub) {
        int count = 0;
        int index = 0;
        // Loop through the text to find all occurrences of the substring
        while ((index = text.indexOf(sub, index)) != -1) {
            count++;
            index += sub.length();// Move index forward to avoid overlapping matches
        }
        return count;
    }
    /**
     * Retrieves all referenced cells inside an IF function, including IFs and functions.
     * @param line The IF function string.
     * @return A string representation of all referenced cells.
     */
    public String allCellsInIf(String line) {
        //All referenced cells from the IF condition
        ArrayList<Index2D> cells = allCells(ifCondition(line));
        StringBuilder result = new StringBuilder("["); // Create a string representation of the referenced cells
        // Go over all collected cell references
        for (int i = 0; i < cells.size(); i++) {
            result.append(cells.get(i).toString());
            if (i < cells.size() - 1) {
                result.append(",");// Add a comma separator between cell references, except for the last one
            }
        }
        // Extract the 'true' and 'false' parts of the IF function
        String True = ifTrue(line);
        String False = ifFalse(line);

        if (SCell.isFunction(True)) {
            result.append(Range2D.AllCellsInRange(True));// If the true part is a function, retrieve all referenced cells within its range
        } else if (SCell.isIf(True)) {
            result.append(allCellsInIf(True)); // If the true part contains a nested IF, recursively process its referenced cells
        }
        if (SCell.isFunction(False)) {
            result.append(Range2D.AllCellsInRange(False));
        } else if (SCell.isIf(False)) {
            result.append(allCellsInIf(True));
        }
        result.append("]");
        return result.toString(); // Convert the collected cell references into a string format and return
    }
    /**
     * Checks if a given string represents a valid spreadsheet cell.
     * A valid cell follows the format "A0" to "Z99".
     * @param a The cell reference.
     * @return True if the cell is valid, otherwise false.
     */
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
    /**
     * Checks if a given cell reference is valid within the spreadsheet dimensions.
     * Ensures the cell exists within the defined width and height.
     * @param line The cell reference string.
     * @return True if the cell exists in the sheet, otherwise false.
     */
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

    /**
     * Extracts all the cell references used inside an IF function.
     * - Adds all cells referenced in the IF condition.
     * - If `ifTrue` or `ifFalse` contain another IF function, it recursively extracts their cells.
     * - If `ifTrue` or `ifFalse` are formulas (start with `=` but are NOT functions), it extracts their referenced cells.
     * - If `ifTrue` or `ifFalse` contain functions, they are **not** added.
     * @param line The IF function string.
     * @return An ArrayList containing all cell references in the IF condition.
     */
    public ArrayList<Index2D> allCellsIf(String line) {
        String Condition = ifCondition(line);
        String ifTrue = ifTrue(line);
        String ifFalse = ifFalse(line);
        ArrayList<Index2D> cells = allCells(Condition);
        if (SCell.isIf(ifTrue)) {
            ArrayList<Index2D> cellsInIfTrue = allCellsIf(ifTrue);
            cells.addAll(cellsInIfTrue);
        }
        else {
            if (ifTrue.startsWith("=") && !SCell.isFunction(ifTrue)){
                ArrayList<Index2D> cellsInIfTrue = allCells(ifTrue);
                cells.addAll(cellsInIfTrue);
            }
        }
        if (SCell.isIf(ifFalse)){
            ArrayList<Index2D> cellsInIfFalse = allCellsIf(ifFalse);
            cells.addAll(cellsInIfFalse);
        }
        else {
            if (ifFalse.startsWith("=") && !SCell.isFunction(ifFalse)){
                ArrayList<Index2D> cellsInIfFalse = allCells(ifFalse);
                cells.addAll(cellsInIfFalse);
            }
        }
        return cells;
    }

    /**
     * Validates all cell references inside an IF function.
     * - Ensures that all referenced cells exist and are not empty.
     * - Checks that each cell is valid using `advancedValidCell()`.
     * - Rejects any cells containing errors, text, or invalid types.
     * @param line The IF function string.
     * @return `true` if all referenced cells are valid, otherwise `false`.
     */

    public boolean CheckCellsInIf(String line){
        ArrayList<Index2D> cells = allCellsIf(line);
        if(cells.isEmpty()){
            return true;
        }
        for (int i = 0;i<cells.size();i++){
            Cell current = get(cells.get(i).toString());
            if (current.getData().isEmpty()){
                return false;
            }
            if(!advancedValidCell(cells.get(i).toString())){
                return false;
            }
            if(current.getType() == Ex2Utils.TEXT || current.getType() == Ex2Utils.FUNC_ERR_FORMAT || current.getType() == Ex2Utils.ERR_WRONG_IF || current.getType() == Ex2Utils.ERR_CYCLE_FORM || current.getType() == Ex2Utils.ERR_FORM_FORMAT ){
                return false;
            }
        }
        return true;
    }
}



