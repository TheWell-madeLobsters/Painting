package hashcode.painting;

class PaintSquare extends Instruction {

    public int rowCenter;
    public int columnCenter;
    public int size;

    public PaintSquare(int rowCenter, int columnCenter, int size) {
        this.type = InstructionType.PAINT_SQUARE;
        this.rowCenter = rowCenter;
        this.columnCenter = columnCenter;
        this.size = size;
    }

    @Override
    public String toString() {
        return "PAINT_SQUARE " + rowCenter + " " + columnCenter + " " + size;
    }
}
