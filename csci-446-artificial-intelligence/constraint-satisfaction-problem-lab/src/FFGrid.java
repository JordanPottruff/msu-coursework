import java.io.File;
import java.util.ArrayList;
import java.util.Scanner;

/**
 * Acts as a collection of FFCells in the form of a 2 dimensional grid. Intended to represent a game grid in Flow Free.
 * This class provides a useful way to initializing game grids from input systems as well as utility methods for
 * clients dependent on FFGrid.
 */
public class FFGrid {

    private final FFCell[][] grid;              // The grid of FFCells, representing the board
    private final ArrayList<Character> domain;  // The domain for the game, all possible values that can be assigned.
    private final int width;                    // The width of the grid.
    private final int height;                   // The height of the grid.

    /**
     * Private constructor intended to be used by static factory methods that provide the mechanics for creating a
     * grid.
     * @param grid the 2-d array of cells representing the board.
     * @param domain the domain of possible values for the grid.
     */
    private FFGrid(FFCell[][] grid, ArrayList<Character> domain) {
        this.grid = grid;
        this.domain = domain;
        this.width = grid[0].length;
        this.height = grid.length;
    }

    /**
     * Creates a FFGrid from a scanner input. This can be used to handle grids found in files and in standard input.
     * Expects *height* rows of *width* size. Will skip empty spaces between rows.
     * @param input the scanner that retrieves the text representation of the grid.
     * @param width the width of the scanner's grid.
     * @param height the height of the scanner's grid.
     * @return a new FFGrid built from the design found within the Scanner.
     */
    public static FFGrid createFromInput(Scanner input, int width, int height) {
        FFCell[][] grid = new FFCell[height][width];
        ArrayList<Character> domain = new ArrayList<>();

        for(int row=0; row<height; row++) {
            // Extract line of input as array of characters (values/non-value).
            String strLine = input.nextLine();
            // Skip empty lines.
            if(strLine.equals("")) {
                row--;
                continue;
            }
            char[] line = strLine.toCharArray();

            // Assign objects to grid based on each value/non-value.
            for(int col=0; col<width; col++) {
                char cell = line[col];

                // If the cell's symbol refers to empty...
                if(cell == FFCell.EMPTY_CELL_SYMBOL){
                    // Create empty cell.
                    grid[row][col] = FFCell.createEmptyCell(col, row);
                    // Otherwise...
                } else {
                    // Create source cell and add value to domain if not already included.
                    grid[row][col] = FFCell.createSourceCell(col, row, cell);
                    if(!domain.contains(cell)) domain.add(cell);
                }
            }
        }

        // Now assign neighbors
        for(int x=0; x<width; x++) {
            for(int y=0; y<height; y++) {
                // Get the cell at x, y
                FFCell cell = grid[y][x];
                // Add neighbors of cell, if exist...
                if(x-1 >= 0)        cell.addNeighbor(grid[y][x-1]);
                if(x+1 <= width-1)  cell.addNeighbor(grid[y][x+1]);
                if(y-1 >= 0)        cell.addNeighbor(grid[y-1][x]);
                if(y+1 <= height-1) cell.addNeighbor(grid[y+1][x]);
            }
        }

        // Now update domains
        for(int x=0; x<width; x++) {
            for(int y=0; y<height; y++) {
                // Get the cell at x,y
                FFCell cell = grid[y][x];
                // If the cell is not assigned, update its domain.
                if(!cell.isAssigned()) cell.updateDomain(domain);
            }
        }
        return new FFGrid(grid, domain);
    }

    /**
     * Returns the cell found at the given coordinates.
     * @param x the x-coordinate of the desired cell.
     * @param y the y-coordinate of the desired cell.
     * @return
     */
    public FFCell getCell(int x, int y) {
        return this.grid[y][x];
    }

    /**
     * Returns the width of the game grid.
     * @return the game grid's width.
     */
    public int getWidth() {
        return this.width;
    }

    /**
     * Returns the height of the game grid.
     * @return the game grid's height.
     */
    public int getHeight() {
        return this.height;
    }

    /**
     * Returns the list of possible colors for the game grid.
     * @return
     */
    public ArrayList<Character> getDomain() {
        return this.domain;
    }

    /**
     * Updates the domains (possible values to assign without breaking constraints) of each cell in the grid.
     */
    public void updateCellDomains() {
        for(int x=0; x<width; x++) {
            for(int y=0; y<height; y++) {
                // Get the cell at x,y
                FFCell cell = getCell(x, y);
                // If the cell is not assigned, update its domain.
                if(!cell.isAssigned()) cell.updateDomain(domain);
            }
        }
    }

    /**
     * Determines whether the board is entirely filled in (no unassigned cells).
     * @return true if no cells are unassigned, false otherwise.
     */
    public boolean isComplete() {
        for(int x=0; x<width; x++) {
            for(int y=0; y<height; y++) {
                if(!getCell(x, y).isAssigned()) return false;
            }
        }
        return true;
    }

    /**
     * Useful for representing the grid in string form for printing.
     * @return a string representation of the grid.
     */
    public String toString() {
        StringBuilder builder = new StringBuilder();
        // Append each character onto the builder, with new lines for each row.
        for(FFCell[] row: grid) {
            for(FFCell cell: row) {
                builder.append(cell.getColor());
            }
            builder.append("\n");
        }
        return builder.toString();
    }

    public static void main(String args[]) throws Exception {
        FFGrid grid = FFGrid.createFromInput(new Scanner(new File("5x5maze.txt")), 5, 5);

        for(int x=0; x<grid.getWidth(); x++) {
            for(int y=0; y<grid.getHeight(); y++) {
                FFCell cell = grid.getCell(x, y);
                if(cell.isAssigned()) continue;
                cell.updateDomain(grid.getDomain());
                System.out.println("["+cell.getX() +","+cell.getY() + "] = " + cell.getDomain());
            }
        }
    }
}
