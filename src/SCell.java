/**
 * The documentation of this class was removed as of Ex4...
 */
public class SCell implements Cell {
    private String _line;
    private int order =0;
    int type = Ex2Utils.TEXT;
    public SCell() {this("");}
    public SCell(String s) {setData(s);}

    @Override
    public int getOrder() {
        return order;
    }

    //@Override
    @Override
    public String toString() {
        return getData();
    }

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
    @Override
    public String getData() {
        return _line;
    }

    @Override
    public int getType() {
        return type;
    }

    @Override
    public void setType(int t) {
        type = t;
    }

    @Override
    public void setOrder(int t) {
        this.order = t;
    }
    public static boolean isNumber(String line) {
        boolean ans = false;
        try {
            double v = Double.parseDouble(line);
            ans = true;
        }
        catch (Exception e) {;}
        return ans;
    }
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
                if (func.equals("=if")){
                    return false;
                }
                if (func.equals(current)) {
                    return true;
                }
            }
        }
        return false;
    }
    public static boolean isIf(String line){
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
                String func = line.substring(1, current.length());
                if (func.equals("=if")){
                    return true;
                }
            }
        }
        return false;
    }
}
