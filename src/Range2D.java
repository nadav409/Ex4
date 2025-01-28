import java.util.ArrayList;

public class Range2D {
    private Index2D start;
    private Index2D end;

    public Range2D(Index2D start,Index2D end){
        this.start = start;
        this.end = end;
    }
    public Range2D(String range) {
        for (int i = 0; i<range.length();i++){
            if (range.charAt(i) == ':'){
                String start = range.substring(0,i);
                String end = range.substring(i+1);
                Index2D s  = new CellEntry(start);
                Index2D e  = new CellEntry(end);
                this.start = s;
                this.end = e;
            }
        }
    }
    public Index2D getStart(){
        return start;
    }
    public Index2D getEnd(){
        return end;
    }
    public ArrayList<Index2D> getcells(){
        ArrayList<Index2D> cells = new ArrayList<>();
        for (int row = start.getX(); row <= end.getX(); row ++){
            for(int col = start.getY(); col <= end.getY(); col ++){
                String cellCurrent = Ex2Utils.ABC[row] + col;
                cells.add(new CellEntry(cellCurrent));
            }
        }
        return cells;
    }
}
