package hashcode.painting;

public class EraseCell extends Instruction {

    public int row;
    public int column;

    public EraseCell(int row, int column) {
        this.type = InstructionType.ERASE_CELL;
        this.row = row;
        this.column = column;
    }

    @Override
    public String toString() {
        return "ERASE_CELL " + row + " " + column;
    }
}
