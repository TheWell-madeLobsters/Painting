package hashcode.painting;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
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

    public static void process(String file) {
        Boolean[][] image = readFile(file + ".in");

        List<Instruction> instructions = new LinkedList<>();

        int rows = image.length;
        int columns = image[0].length;

        System.out.println("File: " + file + " has " + rows*columns + " cells in the file");

        int maxSquare = rows > columns ? columns : rows;
        maxSquare = maxSquare % 2 == 0 ? maxSquare - 1 : maxSquare;

        Boolean[][] checked = generateEmpty(rows, columns);

        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < columns; j++) {
                if (checked[i][j]) {
                    continue;
                }
                //check squares
                boolean squareFound = false;
                int size;
                for (size = maxSquare; size >= 3; size -= 2) {
                    if (i + size <= rows && j + size <= columns) {
                        if (checkForFullSquare(image, i, j, size)) {
                            squareFound = true;
                            break;
                        }
                    }
                }
                if (squareFound) {
                    int rowCenter = i + (size / 2);
                    int colCenter = j + (size / 2);
                    PaintSquare square = new PaintSquare(rowCenter, colCenter, size / 2);
                    instructions.add(square);
                    checked = areaIsUsed(checked, i, j, i + (size - 1), j + (size - 1));
                }
            }
        }

        for (int i = 0; i < rows; i++) {

            for (int j = 0; j < columns; j++) {
                if (checked[i][j]) {
                    continue;
                }
                checked[i][j] = true;

                if (image[i][j]) {
                    int endCol = j;
                    int endRow = i;
                    int lengthRow = 1;
                    int lengthCol = 1;

                    //check row length (controlling if cells haven't been already used by others)
                    for (int colj = j + 1; colj < columns; colj++) {
                        if (image[i][colj]) {
                            if (!checked[i][colj]) {
                                lengthRow++;
                            }
                            endCol++;
                        } else {
                            break;
                        }
                    }

                    //check column length (controlling if cells haven't been already used by others)
                    for (int rowi = i + 1; rowi < rows; rowi++) {
                        if (image[rowi][j]) {
                            if (!checked[rowi][j]) {
                                lengthCol++;
                            }
                            endRow++;
                        } else {
                            break;
                        }
                    }

                    //if row is longer than column -> then use row and delete it from map, use column otherwise
                    if (lengthRow > lengthCol) {
                        PaintLine line = new PaintLine(i, j, i, endCol);
                        instructions.add(line);
                        checked = areaIsUsed(checked, i, j, i, endCol);
                    } else {
                        PaintLine line = new PaintLine(i, j, endRow, j);
                        instructions.add(line);
                        checked = areaIsUsed(checked, i, j, endRow, j);
                    }
                }
            }
        }

        printResult(instructions, rows * columns, file + ".out");
    }

    public static boolean checkForFullSquare(Boolean[][] image, int startRow, int startCol, int size) {
        for (int i = startRow; i < startRow + size; i++) {
            for (int j = startCol; j < startCol + size; j++) {
                if (!image[i][j]) {
                    return false;
                }
            }
        }
        return true;
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
