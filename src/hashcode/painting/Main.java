package hashcode.painting;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class Main {
    public static String LOGO = "logo";
    public static String RIGHT_ANGLE = "right_angle";
    public static String LEARN_AND_TEACH = "learn_and_teach";

    public static void main(String[] args) {
        process(LOGO);
        process(RIGHT_ANGLE);
        process(LEARN_AND_TEACH);
    }
    
    public static List<Instruction> process(String file) {
        Boolean[][] image = readFile(file + ".in");
        
        int rows = image.length;
        int columns = image[0].length;
        
        System.out.println("File: " + file + " has " + rows*columns + " cells in the file");
        
        List<Instruction> horizontal = processDirection(image, false);
        List<Instruction> vertical = processDirection(image, true);
        
        List<Instruction> instructions = horizontal.size() < vertical.size() ? horizontal : vertical;
        
        printResult(instructions, rows * columns, file + ".out");
        
        return instructions;
    }

    public static List<Instruction> processDirection(Boolean[][] image, boolean vertical) {
        List<Instruction> instructions = new LinkedList<>();

        int rows = image.length;
        int columns = image[0].length;

        int maxSquare = rows > columns ? columns : rows;
        maxSquare = maxSquare % 2 == 0 ? maxSquare - 1 : maxSquare;

        Boolean[][] checked = generateEmpty(rows, columns);

        int firstIndex, secondIndex;
        if(vertical) {
            firstIndex = columns;
            secondIndex = rows;
        } else {
            firstIndex = rows;
            secondIndex = columns;
        }
        
        for (int i = 0; i < firstIndex; i++) {
            for (int j = 0; j < secondIndex; j++) {
                int rowPos, colPos;
                if(vertical) {
                    rowPos = j;
                    colPos = i;
                } else {
                    rowPos = i;
                    colPos = j;
                }
                
                if (checked[rowPos][colPos]) {
                    continue;
                }
                //check squares
                boolean squareFound = false;
                int size;
                for (size = maxSquare; size >= 3; size -= 2) {
                    if (rowPos + size <= rows && colPos + size <= columns) {
                        List<Instruction> deleteEmpty = checkForFullSquare(image, rowPos, colPos, size);
                        if (deleteEmpty != null) {
                            int rowCenter = rowPos + (size / 2);
                            int colCenter = colPos + (size / 2);
                            instructions.add(new PaintSquare(rowCenter, colCenter, size / 2));
                            instructions.addAll(deleteEmpty);
                            checked = areaIsUsed(checked, rowPos, colPos, rowPos + (size - 1), colPos + (size - 1));
                            break;
                        }
                    }
                }
            }
        }

        for (int i = 0; i < firstIndex; i++) {
            for (int j = 0; j < secondIndex; j++) {
                int rowPos, colPos;
                if(vertical) {
                    rowPos = j;
                    colPos = i;
                } else {
                    rowPos = i;
                    colPos = j;
                }
                
                if (checked[rowPos][colPos]) {
                    continue;
                }
                checked[rowPos][colPos] = true;

                if (image[rowPos][colPos]) {
                    int endCol = colPos;
                    int endRow = rowPos;
                    int lengthRow = 1;
                    int lengthCol = 1;

                    //check row length (controlling if cells haven't been already used by others)
                    for (int colj = colPos + 1; colj < columns; colj++) {
                        if (image[rowPos][colj]) {
                            if (!checked[rowPos][colj]) {
                                lengthRow++;
                            }
                            endCol++;
                        } else {
                            break;
                        }
                    }

                    //check column length (controlling if cells haven't been already used by others)
                    for (int rowi = rowPos + 1; rowi < rows; rowi++) {
                        if (image[rowi][colPos]) {
                            if (!checked[rowi][colPos]) {
                                lengthCol++;
                            }
                            endRow++;
                        } else {
                            break;
                        }
                    }

                    //if row is longer than column -> then use row and delete it from map, use column otherwise
                    if (lengthRow > lengthCol) {
                        PaintLine line = new PaintLine(rowPos, colPos, rowPos, endCol);
                        instructions.add(line);
                        checked = areaIsUsed(checked, rowPos, colPos, rowPos, endCol);
                    } else {
                        PaintLine line = new PaintLine(rowPos, colPos, endRow, colPos);
                        instructions.add(line);
                        checked = areaIsUsed(checked, rowPos, colPos, endRow, colPos);
                    }
                }
            }
        }

        return instructions;
    }

    public static List<Instruction> checkForFullSquare(Boolean[][] image, int startRow, int startCol, int size) {
        int emptySpaces = 0;
        List<Instruction> deleteEmpty = new ArrayList<>();
        for (int i = startRow; i < startRow + size; i++) {
            for (int j = startCol; j < startCol + size; j++) {
                if (!image[i][j]) {
                    if(size > 3) {
                        emptySpaces++;
                        deleteEmpty.add(new EraseCell(i, j));
                        if(emptySpaces >= (size / 2)) {
                            return null;
                        }
                    } else {
                        return null;
                    }
                }
            }
        }
        return deleteEmpty;
    }

    public static Boolean[][] areaIsUsed(Boolean[][] checked, int startRow, int startCol, int endRow,
            int endCol) {
        for (int i = startRow; i <= endRow; i++) {
            for (int j = startCol; j <= endCol; j++) {
                checked[i][j] = true;
            }
        }
        return checked;
    }

    public static Boolean[][] generateEmpty(int rows, int columns) {
        Boolean[][] empty = new Boolean[rows][];
        for (int i = 0; i < rows; i++) {
            empty[i] = new Boolean[columns];
            for(int j = 0; j < columns; j++) {
                empty[i][j] = false;
            }
        }
        return empty;
    }

    public static void printResult(List<Instruction> instructions, int size, String file) {
        PrintWriter writer = null;
        System.out.println("File: " + file + " has " + size + " cells, " + instructions.size() + " instructions needed");
        System.out.println("SCORE: " + (size - instructions.size()));
        try {
            writer = new PrintWriter(file, "UTF-8");
        } catch (FileNotFoundException | UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        writer.println(instructions.size());
        for (Instruction instruction : instructions) {
            writer.println(instruction);
        }
        writer.close();
    }

    public static Boolean[][] readFile(String file) {
        Boolean[][] image = null;
        BufferedReader br = null;
        try {
            String sCurrentLine;
            br = new BufferedReader(new FileReader(file));

            sCurrentLine = br.readLine();

            String[] size = sCurrentLine.split(" ");
            int rows = Integer.parseInt(size[0]);
            int columns = Integer.parseInt(size[1]);

            image = new Boolean[rows][];
            int count = 0;
            for (int i = 0; i < rows; i++) {
                image[i] = new Boolean[columns];

                sCurrentLine = br.readLine();

                for (int j = 0; j < columns; j++) {
                    char cell = sCurrentLine.charAt(j);
                    if (cell == '#') {
                        count++;
                        image[i][j] = true;
                    } else {
                        image[i][j] = false;
                    }
                }
            }
            System.out.println("File: " + file + " has " + count + " hashes in the file");

        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (br != null) {
                    br.close();
                }
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
        return image;
    }
}
