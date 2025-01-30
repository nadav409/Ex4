import java.util.ArrayList;

public class Range2D {
    private Index2D start;
    private Index2D end;
    private String[][] value;

    public Range2D(Index2D start, Index2D end) {
        this.start = start;
        this.end = end;
        this.value = new String [end.getX() - start.getX()][end.getY() - end.getX()];
    }

    public Range2D(String range) {
        int index = range.indexOf(":");
        String start = range.substring(0, index);
        String end = range.substring(index + 1);
        Index2D s = new CellEntry(start);
        Index2D e = new CellEntry(end);
        this.start = s;
        this.end = e;
        this.value = new String [e.getX() - s.getX()][e.getY() - s.getX()];
    }

    public Index2D getStart() {
        return start;
    }

    public Index2D getEnd() {
        return end;
    }

    public ArrayList<Index2D> getcells() {
        ArrayList<Index2D> cells = new ArrayList<>();
        for (int row = start.getX(); row <= end.getX(); row++) {
            for (int col = start.getY(); col <= end.getY(); col++) {
                String cellCurrent = Ex2Utils.ABC[row] + col;
                cells.add(new CellEntry(cellCurrent));
            }
        }
        return cells;
    }
    public void updateValue(Ex2Sheet table){
        int i = 0;
        int j = 0;
        for (int row = start.getX(); row <= end.getX(); row++) {
            for (int col = start.getY(); col <= end.getY(); col++) {
               this.value[i][j] = table.value(row,col);
               j++;
            }
            i++;
        }
    }
    public String minValue(){
        Double min = Double.MAX_VALUE;
        for(int i = 0; i<this.value.length; i++){
            for (int j = 0; j<this.value[i].length; i++){
                double current = Double.parseDouble(value[i][j]);
                if (current < min){
                    min = current;
                }
            }
        }
        return min.toString();
    }

}
