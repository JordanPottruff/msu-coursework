import java.io.File;
import java.time.Duration;
import java.time.Instant;
import java.util.List;
import java.util.Scanner;

/**
 * An enhanced FlowFreeSolver that adds heuristic variable preference and forward checking onto backtrack searching.
 */
public class AdvancedFlowFreeSolver extends FlowFreeSolver {

    /**
     * Creates an AdvancedFreeFlowSolver for the given (assumed to be unsolved) grid.
     * @param grid the grid to be solved.
     */
    public AdvancedFlowFreeSolver(FFGrid grid) {
        super(grid);
    }

    /**
     * Solves the grid using backtrack search with our heuristic and forward checking.
     * @return the solved grid, or null if no solution was found.
     */
    @Override
    public FFGrid solveFreeFlowGrid() {
        // When all cells are assigned a value (which only occurs if all assignments were valid) then return the
        // grid as the solved solution.
        if(grid.isComplete()) return grid;

        // Choose next unassigned variable (cell) using MRV heuristic.
        FFCell var = getNextVariable();

        // Now iterate over the domain of the variable.
        for(char value: var.getDomain()) {
            // Track variable assignments.
            numOfVariableAssignments++;
            // Try assigning each value in the domain of the variable.
            var.setColor(value);
            // Check if assignment meets constraints...
            if(gridMeetsConstraints() && forwardCheck()) {
                // If it does, recursively move forward to assign more variables.
                FFGrid result = solveFreeFlowGrid();
                // If a solved grid could be found with the assignment, continue propagating it as a solution.
                if(result != null) return result;
                // If no solved grid could be found with the assignment, move on to the next one.
                var.resetColor();
            } else {
                // If it does not meet constraints, reset and move on to next value/variable.
                var.resetColor();
            }
        }
        // No possible solution found given previous (and possible initial) assignments, so return null.
        return null;
    }

    /**
     * Determines if the current board has constrained any variable to the point where no value can be assigned to it.
     * Essentially, the forwardCheck fails (returns false) when any cell has an available domain size of 0.
     * @return true if all cells can be assigned a value given the current configuration.
     */
    private boolean forwardCheck() {
        grid.updateCellDomains();

        // Iterate over all x,y positions in the grid.
        for(int x=0; x<grid.getWidth(); x++) {
            for(int y=0; y<grid.getHeight(); y++) {
                // Get the cell at x,y
                FFCell cell = grid.getCell(x, y);

                if(!cell.isAssigned()) {
                    // If the cell is unassigned, check if domain (meaning available values) is empty.
                    if(cell.getDomain().size() == 0) {
                        // If empty, return false since no value could ever be assigned to it.
                        return false;
                    }
                }
            }
        }
        // Getting to this point means the domains for all cells have not been constrained to size 0.
        return true;
    }

    /**
     * Gets the next unassigned variable by finding the cell with the most constraints placed on it. Essentially, the
     * cell that, given the current assignments, has the smallest number of values that can be assigned to it and still
     * meet all of its constraints.
     * @return the most constrained (unassigned) cell.
     */
    @Override
    public FFCell getNextVariable() {
        // Keep track of the most constrained cell visited so far, as well as how big its domain is due to constraints
        // (level).
        FFCell mostConstrainedCell = null;
        int mostConstrainedLevel = Integer.MAX_VALUE;

        // Make sure the domains are updated.
        grid.updateCellDomains();

        // Iterate over all x,y coordinates in the grid.
        for(int x=0; x<grid.getWidth(); x++) {
            for(int y=0; y<grid.getHeight(); y++) {
                // Get the cell at x,y in the grid.
                FFCell cell = grid.getCell(x, y);

                // Skip the cell if it is assigned, we only want to choose unassigned cells.
                if(cell.isAssigned()) continue;

                // The level is the number of items in the domain of the cell, which is the number of values that
                // it can be assigned and still meet its constraints.
                int level = cell.getDomain().size();

                // If this level is smaller than the smallest one seen so far, then update it to be considered the
                // smallest.
                if(level < mostConstrainedLevel) {
                    mostConstrainedCell = cell;
                    mostConstrainedLevel = level;
                }
            }
        }

        // Return the cell with the smallest 'domain'.
        return mostConstrainedCell;
    }

    /**
     * Gets the list of values to assign to the given cell. For this simple algorithm, we just return the entire
     * possible domain (no heuristic at all).
     * @param cell
     * @return
     */
    @Override
    public List<Character> getValues(FFCell cell) {
        return grid.getDomain();
    }

    /**
     * Allows users to run the algorithm on a given file. To test this solver, run the file with three arguments:
     * <ol>
     *     <li>filename - name of the file containing the maze, including filepath</li>
     *     <li>width - the number of columns in the maze</li>
     *     <li>height - the number of rows in the maze</li>
     * </ol>
     * @param args arguments, outlines in the three items above.
     * @throws Exception if file is not found.
     */
    public static void main(String[] args) throws Exception {
        if(args.length != 3) {
            System.out.println("Incorrect number of arguments were given, please follow the format: \n");
            System.out.println("java AdvancedFlowFreeSolver *filename* *width* *height*\n");
            System.out.println("Where, ");
            System.out.println(" - *filename* = the name of the maze file, including the path if needed");
            System.out.println(" - *width* = the width of the maze (number of columns)");
            System.out.println(" - *height* = the height of the maze (number of rows)");
            return;
        }

        String filename = args[0];
        int width = Integer.parseInt(args[1]);
        int height = Integer.parseInt(args[2]);


        FFGrid grid = FFGrid.createFromInput(new Scanner(new File(filename)), width, height);
        System.out.println("--------------------");
        System.out.println("INITIAL GRID:\n");
        System.out.println(grid);
        System.out.println("domain = " + grid.getDomain());
        System.out.println("size = " + grid.getWidth() + "x" + grid.getHeight());
        System.out.println("--------------------");

        AdvancedFlowFreeSolver solver = new AdvancedFlowFreeSolver(grid);

        Instant start = Instant.now();
        FFGrid solution = solver.solveFreeFlowGrid();
        Instant end = Instant.now();

        System.out.println("SOLVED GRID:\n");
        System.out.println(solution);
        System.out.println("runtime = " + Duration.between(start, end).toMillis()/(float)1000 + " sec");
        System.out.println("assignments = " + solver.getNumOfAssignments());
        System.out.println("--------------------");
    }
}
