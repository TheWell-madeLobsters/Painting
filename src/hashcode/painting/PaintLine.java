package hashcode.painting;

class PaintLine extends Instruction {

    public int rowStart;
    public int columnStart;
    public int rowEnd;
    public int columnEnd;

    public PaintLine(int rowStart, int columnStart, int rowEnd, int columnEnd) {
        this.type = InstructionType.PAINT_LINE;
        this.rowStart = rowStart;
        this.columnStart = columnStart;
        this.rowEnd = rowEnd;
        this.columnEnd = columnEnd;
    }

    public boolean isHorizontal() {
        return rowStart == rowEnd;
    }

    public boolean isVertical() {
        return columnStart == columnEnd;
    }

    @Override
    public String toString() {
        return "PAINT_LINE " + rowStart + " " + columnStart + " " + rowEnd + " " + columnEnd;
    }
}
