
/** This class represents a cell in the spreadsheet.
* It stores the location of a cell as either a reference string ("A1") or as numerical coordinates (x,y).
 */
public class CellEntry  implements Index2D {
    private String _data;
    private int x, y;

    /**
     * Constructor that initializes a cell entry using (x, y) coordinates.
     * If the coordinates are out of bounds, it stores an error message.
     * @param x The column index.
     * @param y The row index.
     */
    public CellEntry(int x, int y) {
        if(x<0 | y<0 | x>= Ex2Utils.ABC.length) {_data = "ERROR!";}
        else {_data = Ex2Utils.ABC[x]+y;}// Convert (x, y) to cell reference
        init();
    }

    public String toString() {return _data;}

    /**
     * Constructor that initializes a cell entry using a cell reference string.
     * @param c The cell reference string (e.g., "A1").
     */
    public CellEntry(String c) {
        _data = c;
        init();
    }

    /**
     * Converts the reference string into numerical coordinates.
     * If the conversion fails, the cell is invalid.
     */
    private void init() {
        x = -1; y= -1;
       if(_data!=null && _data.length()>=2) {
           _data = _data.toUpperCase();
            String s1 = _data.substring(0,1);
            String s2 = _data.substring(1);
            Integer yy = Ex2Sheet.getInteger(s2);
            if(yy!=null) {y=yy;}
            if(y>=0) {
                x = s1.charAt(0) - 'A';// Convert letter to index
                if(x<0 | x>25) {x=-1;}// Invalid column
          }
       }
       if(x==-1) {_data=null; y=-1;}// invalid
    }

    /**
     * Converts the stored coordinates back into a cell reference string.
     * @return The cell reference ("A1"), or null if invalid.
     */
    public String toCell() {
        String ans = null;
        if(x>=0 && y>=0) {
            ans = Ex2Utils.ABC[x]+y;
        }
        return ans;
    }

    /**
     * Converts a cell reference string into numerical coordinates.
     * @param cell The cell reference string.
     */
    private void cell2coord(String cell) {
        int x = -1, y=-1;
        if(cell!=null && cell.length()>=2) {
            cell = cell.toUpperCase();
            String s1 = cell.substring(0,1);
            String s2 = cell.substring(1);
            y = Ex2Sheet.getInteger(s2);
            x = s1.charAt(0) - 'A';
        }
    }

    /**
     * Checks if the cell is within the valid range of the given spreadsheet.
     * @param t The spreadsheet instance.
     * @return True if the cell exists in the spreadsheet, otherwise false.
     */
    public boolean isIn(Sheet t) {
        return t!=null && t.isIn(x,y);
    }

    /**
     * Checks if the cell reference is valid.
     * @return True if the cell is valid, otherwise false.
     */
    @Override
    public boolean isValid() {
        return _data!=null;// Valid if _data is not null
    }

    /**
     * Gets the column index of the cell.
     * @return The column index.
     */
    @Override
    public int getX() {return x;}

    /**
     * Gets the row index of the cell.
     * @return The row index.
     */
    @Override
    public int getY() {return y;}
}
