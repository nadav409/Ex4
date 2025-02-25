/**
 * This class represents a single cell in the spreadsheet.
 * Handles different data types such as text,numbers, formulas, functions, and IF conditions.
 */
public class SCell implements Cell {
    private String _line;// Cell's data
    private int order =0;// Defines the computation order for formula evaluation
    int type = Ex2Utils.TEXT;// Type of data contained in the cell

    /**
     * Default constructor setting an empty cell.
     */
    public SCell() {
        this("");
    }
    /**
     * Constructor that sets the cell with data.
     * @param s The value to store in the cell.
     */
    public SCell(String s) {
        setData(s);
    }
    /**
     * Gets the computation order of the cell.
     * @return The order in which this cell should be evaluated.
     */
    @Override
    public int getOrder() {
        return order;
    }


    @Override
    public String toString() {
        return getData();
    }

    /**
     * Sets the data of the cell and determines its type.
     * @param s The new value to be stored in the cell.
     */
    @Override
    public void setData(String s) {
        if(s!=null) {
        type = Ex2Utils.TEXT;
        if (isNumber(s)) {
            type = Ex2Utils.NUMBER;
        }
        else if (isFunction(s)){
            type = Ex2Utils.FUNCTION;
        }
        else if(isIf(s)){
            type = Ex2Utils.IF;
        }
        else if(s.startsWith("=")) {
            type = Ex2Utils.FORM;
        }
        _line = s;
      }
    }
    /**
     * Gets the data in the cell.
     * @return The cell's value as a string.
     */
    @Override
    public String getData() {
        return _line;
    }
    /**
     * Gets the type of the data stored in the cell.
     * @return The type of data (TEXT, NUMBER, FUNCTION, IF, FORM).
     */
    @Override
    public int getType() {
        return type;
    }

    /**
     * Sets the type of the data in the cell.
     * @param t The new type to assign to the cell.
     */
    @Override
    public void setType(int t) {
        type = t;
    }

    /**
     * Sets the computation order for formula evaluation.
     * @param t The computation order value.
     */
    @Override
    public void setOrder(int t) {
        this.order = t;
    }

    /**
     * Checks if the given string represents a number.
     * @param line The string to check.
     * @return True if the string is a valid number, otherwise false.
     */
    public static boolean isNumber(String line) {
        boolean ans = false;
        try {
            double v = Double.parseDouble(line);
            ans = true;
        }
        catch (Exception e) {;}
        return ans;
    }

    /**
     * Checks if the given string represents a function.
     * A function starts with '=' followed by a recognized function name.
     * @param line The string to check.
     * @return True if the string is a function, otherwise false.
     */
    public static boolean isFunction(String line){
        if (line == null || line.isEmpty()){
            return false;
        }
        line = line.toLowerCase();
        if(line.charAt(0) != '='){
            return false;
        }
        for (int i = 0; i < Ex2Utils.FUNCTIONS.length; i++){
            String current = Ex2Utils.FUNCTIONS[i];
            if (line.length() > current.length()) {
                String func = line.substring(1, current.length()+1);
                if (func.equals("if")){
                    return false;// 'if' is handled separately
                }
                if (func.equals(current)) {
                    return true;
                }
            }
        }
        return false;
    }
    /**
     * Checks if the given string represents an IF function.
     * An IF function starts with "=if".
     * @param line The string to check.
     * @return True if the string is an IF function, otherwise false.
     */
    public static boolean isIf(String line){
        return line != null && line.length() >= 3 && line.toLowerCase().startsWith("=if");
    }

    /**
     * Checks if the given string represents a basic formula.
     * A formula starts with '=' but is not a function or an IF statement.
     * @param _line The string to check.
     * @return True if the string is a basic formula, otherwise false.
     */
    public static boolean BasicIsForm(String _line){
        return _line.charAt(0) == '=' && !isFunction(_line) && !isIf(_line);
    }
}
