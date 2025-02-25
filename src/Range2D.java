import java.util.ArrayList;
/**
 * This class represents a 2D range in a spreadsheet.
 * It stores a start and end index, and a table of values.
 * It provides functions to get the min, max, sum, and other calculations in the range.
 */
public class Range2D {
    private Index2D start;
    private Index2D end;
    private String[][] value;

    /**
     * Creates a Range2D with given start and end points.
     * @param start The starting index2D of the range.
     * @param end The ending index2D of the range.
     */
    public Range2D(Index2D start, Index2D end) {
        this.start = start;
        this.end = end;
        this.value = new String[end.getY() - start.getY() + 1][end.getX() - start.getX() + 1];// Creates a 2D array to store values for the range
    }

    /**
     * Creates a Range2D from a string like "A1:B3".
     * @param range A string that shows the range.
     */
    public Range2D(String range) {
        int index = range.indexOf(":");
        String start = range.substring(0, index);
        String end = range.substring(index + 1);
        Index2D s = new CellEntry(start);
        Index2D e = new CellEntry(end);
        this.start = s;
        this.end = e;
        this.value = new String[e.getY() - s.getY() + 1][e.getX() - s.getX() + 1];// Creates a 2D array to store values for the range
    }

    /**
     * @return The starting index2D of the range.
     */
    public Index2D getStart() {
        return start;
    }

    /**
     * @return The ending index2D of the range.
     */
    public Index2D getEnd() {return end;}

    /**
     * @return The table of values in this range.
     */
    public String[][] getValue(){
        return this.value;
    }

    /**
     * Gets all cells in the range.
     * @return list of Index2D objects for all the cells.
     */
    public ArrayList<Index2D> getcells() {
        ArrayList<Index2D> cells = new ArrayList<>();
        for (int row = start.getX(); row <= end.getX(); row++) {
            for (int col = start.getY(); col <= end.getY(); col++) {
                String cellCurrent = Ex2Utils.ABC[row] + col;// Converts to cell name
                cells.add(new CellEntry(cellCurrent));
            }
        }
        return cells;
    }

    /**
     * Gets all cell names (like "A1","B2") in the range.
     * @return A list of cell names.
     */
    public ArrayList<String> getCellNames() {
        ArrayList<String> cellNames = new ArrayList<>();
        for (int row = start.getX(); row <= end.getX(); row++) {
            for (int col = start.getY(); col <= end.getY(); col++) {
                String cellCurrent = Ex2Utils.ABC[row] + col;
                cellNames.add(cellCurrent);
            }
        }
        return cellNames;
    }

    /**
     * Updates the values in the range from a table.
     * @param table The Ex2Sheet object that has the data.
     */
    public void updateValue(Ex2Sheet table) {
        int height = end.getY() - start.getY() + 1;
        int width = end.getX() - start.getX() + 1;
        for (int i = 0, row = start.getY(); i < height; i++, row++) {
            for (int j = 0, col = start.getX(); j < width; j++, col++) {
                this.value[i][j] = table.eval(col, row);// Fills value array from table
            }
        }
    }

    /**
     * Finds the smallest number in the range.
     * @return The smallest value as a string.
     */
    public String minValue() {
        Double min = Double.MAX_VALUE;
        for (int i = 0; i < this.value.length; i++) {
            for (int j = 0; j < this.value[0].length; j++) {
                if (value[i][j].equals("")){// Skips empty values
                    continue;
                }
                double current = Double.parseDouble(value[i][j]);
                if (current < min) {
                    min = current;
                }
            }
        }
        return min.toString();
    }

    /**
     * Finds the biggest number in the range.
     * @return The biggest value as a string.
     */
    public String maxValue() {
        Double max = Double.MIN_VALUE;
        for (int i = 0; i < this.value.length; i++) {
            for (int j = 0; j < this.value[0].length; j++) {
                if (value[i][j].equals("")){
                    continue;
                }
                double current = Double.parseDouble(value[i][j]);
                if (current > max) {
                    max = current;
                }
            }
        }
        return max.toString();
    }

    /**
     * Calculates the sum of all the numbers in the range.
     * @return The sum as a string.
     */
    public String sumValue() {
        Double sum = 0.0;
        for (int i = 0; i < this.value.length; i++) {
            for (int j = 0; j < this.value[0].length; j++) {
                if (value[i][j].equals("")){
                    continue;
                }
                double current = Double.parseDouble(value[i][j]);
                sum += current;
            }
        }
        return sum.toString();
    }

    /**
     * Multiplies all the numbers in the range.
     * @return The product as a string.
     */
    public String multiplyValue() {
        Double multiply = 1.0;
        for (int i = 0; i < this.value.length; i++) {
            for (int j = 0; j < this.value[0].length; j++) {
                if (value[i][j].equals("")){
                    continue;
                }
                double current = Double.parseDouble(value[i][j]);
                multiply *= current;
            }
        }
        if (multiply == -0.0){
            multiply = 0.0;
        }
        return multiply.toString();
    }

    /**
     * Finds the average of the numbers in the range.
     * @return The average as a string.
     */
    public String averageValue() {
        String sum = this.sumValue();
        Double sum1 = Double.parseDouble(sum);
        ArrayList<String> cells = this.getCellNames();
        Double average = sum1 / cells.size();
        return average.toString();
    }

    /**
     * Gets the start and end values from a given string.
     * @param line The input string containing range information.
     * @return A substring containing the extracted range.
     */
    public static String findStartAndEndValid(String line) {
        int indexStart = line.indexOf("(");
        int indexEnd = line.indexOf(")");
        return line.substring(indexStart + 1, indexEnd);
    }

    /**
     * Gets all the cell names within a given range.
     * @param line The input string containing range information.
     * @return A list of all cell names within the range as a string.
     */
    public static String AllCellsInRange(String line) {
        int indexStart = line.indexOf("(");
        int indexEnd = line.indexOf(")");
        String range = line.substring(indexStart + 1, indexEnd);
        Range2D current = new Range2D(range);
        ArrayList<String> CellsInRange = current.getCellNames();
        return CellsInRange.toString();
    }

    /**
     * Checks if a given formula line is a basic valid function.
     * @param line The formula string.
     * @return True if valid, otherwise false.
     */
    public static boolean BasicValidFunction(String line){
        line = line.toLowerCase();
        if (line.charAt(0) != '='){
            return false;
        }
        int indexEnd = line.indexOf("(");
        if(indexEnd == -1){
            return false;
        }
        String functionName = line.substring(1,indexEnd);
        for (int i = 0;i<Ex2Utils.FUNCTIONS.length;i++){
            if(functionName.equals(Ex2Utils.FUNCTIONS[i])){
                return true;
            }
        }
        return false;
    }

    /**
     * Checks if a function is valid.
     * @param line The input function string.
     * @return True if the function format is valid, otherwise false.
     */
    public static boolean ValidFunction(String line) {
        int space = line.indexOf(" ");
        if (space != -1) {
            return false;
        }
        if (!BasicValidFunction(line)){
            return false;
        }
        if (line.charAt(line.length() - 1) != ')') {
            return false;
        }
        int indexStart = line.indexOf("(");
        int indexMiddle = line.indexOf(":");
        int indexEnd = line.length() - 1;
        if (indexStart == -1 || indexMiddle == -1) {
            return false;
        }
        String startRange = line.substring(indexStart + 1, indexMiddle);
        String endRange = line.substring(indexMiddle + 1, indexEnd);
        CellEntry firstCell = new CellEntry(startRange);
        CellEntry lastCell = new CellEntry(endRange);
        if (lastCell.getX() < firstCell.getX() || lastCell.getY() < firstCell.getY()) {
            return false;
        }
        if (!Ex2Sheet.validCell(startRange) || !Ex2Sheet.validCell(endRange) ) {
            return false;
        }
        return true;
    }

    /**
     * Checks if a given function is valid.
     * It verifies the function format and checks if the referenced cells are valid.
     * @param line The function string.
     * @param t The Ex2Sheet object for validation.
     * @return True if valid, otherwise false.
     */
    public static boolean advnacedValidFunction(String line,Ex2Sheet t){
        if (ValidFunction(line)){
            int indexStart = line.indexOf("(");
            int indexMiddle = line.indexOf(":");
            int indexEnd = line.length() - 1;
            String startRange = line.substring(indexStart + 1, indexMiddle);
            String endRange = line.substring(indexMiddle + 1, indexEnd);
            CellEntry firstCell = new CellEntry(startRange);
            CellEntry lastCell = new CellEntry(endRange);
            if(!t.advancedValidCell(startRange) || !t.advancedValidCell(endRange)){
                return false;
            }
            if (checkValidCellTypes(t,firstCell,lastCell)){
                return true;
            }
        }
        return false;
    }

    /**
     * Checks if the given function is a MIN function.
     * @param line The function string.
     * @return True if it is a MIN function, otherwise false.
     */
    public static boolean MinFunction(String line) {
        line = line.toLowerCase();
        int indexEnd = line.indexOf("(");
        return line.substring(1, indexEnd).equals("min");
    }

    /**
     * Checks if the given function is a MAX function.
     * @param line The function string.
     * @return True if it is a MAX function, otherwise false.
     */
   public static boolean MaxFunction(String line) {
        line = line.toLowerCase();
        int indexEnd = line.indexOf("(");
        return line.substring(1, indexEnd).equals("max");
    }

    /**
     *
     * Checks if the given function is a SUM function.
     * @param line The function string.
     * @return True if it is a SUM function, otherwise false.
     */
   public static boolean SumFunction(String line) {
        line = line.toLowerCase();
        int indexEnd = line.indexOf("(");
        return line.substring(1, indexEnd).equals("sum");
    }

    /**
     * Checks if the given function is a MULTIPLY function.
     * @param line The function string.
     * @return True if it is a MULTIPLY function, otherwise false.
     */
    public static boolean MultiplyFunction(String line) {
        line = line.toLowerCase();
        int indexEnd = line.indexOf("(");
        return line.substring(1, indexEnd).equals("multiply");
    }

   public static boolean AverageFunction(String line) {
        line = line.toLowerCase();
        int indexEnd = line.indexOf("(");
        return line.substring(1, indexEnd).equals("average");
    }
    public Double evaluateFunction(String line){
        if (Range2D.MinFunction(line)) {
            Double dd = Double.parseDouble(this.minValue());
            return dd;
        }
        else if (Range2D.MaxFunction(line)) {
            Double dd = Double.parseDouble(this.maxValue());
            return dd;
        }
        else if(Range2D.SumFunction(line)){
            Double dd = Double.parseDouble(this.sumValue());
            return dd;
        }
        else if (Range2D.MultiplyFunction(line)){
            Double dd = Double.parseDouble(this.multiplyValue());
            return dd;
        }
        else {
            Double dd = Double.parseDouble(this.averageValue());
            return dd;
        }
    }
    public static boolean checkValidCellTypes(Ex2Sheet t,CellEntry start,CellEntry end) {
        int height = end.getY() - start.getY() + 1;
        int width = end.getX() - start.getX() + 1;
        String empty = "";
        for (int i = 0, row = start.getY(); i < height; i++, row++) {
            for (int j = 0, col = start.getX(); j < width; j++, col++) {
                if (t.get(col, row).getData().equals(empty)){
                    continue;
                }
                if(t.get(col,row).getType() == Ex2Utils.TEXT){
                    return false;
                }
            }
        }
        return true;
    }

}
