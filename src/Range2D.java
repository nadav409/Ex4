import java.util.ArrayList;

public class Range2D {
    private Index2D start;
    private Index2D end;
    private String[][] value;

    public Range2D(Index2D start, Index2D end) {
        this.start = start;
        this.end = end;
        this.value = new String [end.getY() - start.getY() + 1][end.getX() - end.getX() + 1];
    }

    public Range2D(String range) {
        int index = range.indexOf(":");
        String start = range.substring(0, index);
        String end = range.substring(index + 1);
        Index2D s = new CellEntry(start);
        Index2D e = new CellEntry(end);
        this.start = s;
        this.end = e;
        this.value = new String [e.getY() - s.getY() + 1][e.getX() - s.getX() + 1];

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
        for (int i = 0, row = start.getY(); row <= end.getY(); i++, row++) {
            for (int j = 0, col = start.getX(); col <= end.getX(); j++, col++) {
                this.value[i][j] = table.value(row, col);
            }
        }
    }
    public String minValue(){
        Double min = Double.MAX_VALUE;
        for(int i = 0; i<this.value.length; i++){
            for (int j = 0; j<this.value[0].length; j++){
                double current = Double.parseDouble(value[i][j]);
                if (current < min){
                    min = current;
                }
            }
        }
        return min.toString();
    }
    public String maxValue(){
        Double max = Double.MIN_VALUE;
        for(int i = 0; i<this.value.length; i++){
            for (int j = 0; j<this.value[i].length; i++){
                double current = Double.parseDouble(value[i][j]);
                if (current > max){
                    max = current;
                }
            }
        }
        return max.toString();
    }
    public String sumValue(){
        Double sum = 0.0;
        for(int i = 0; i<this.value.length; i++){
            for (int j = 0; j<this.value[i].length; i++){
                double current = Double.parseDouble(value[i][j]);
                sum += current;
            }
        }
        return sum.toString();
    }
    public String averageValue(){
        Double average = Double.parseDouble(this.sumValue()) / this.value.length;
        return average.toString();
    }
    public static String findStartAndEndValid(String line){
        int indexStart = line.indexOf("(");
        int indexEnd = line.indexOf(")");
        return line.substring(indexStart+1,indexEnd);
    }
}
