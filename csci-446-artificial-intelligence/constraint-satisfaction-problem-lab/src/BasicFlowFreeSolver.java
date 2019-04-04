import java.io.File;
import java.time.Duration;
import java.time.Instant;
import java.util.List;
import java.util.Scanner;

/**
 * A simple FlowFreeSolver that uses a backtrack search to solve the given grid.
 */
public class BasicFlowFreeSolver extends FlowFreeSolver {

    /**
     * Creates a BasicFlowFreeSolver for the given (assumed to be unsolved) grid.
     * @param grid the grid to be solved.
     */
    public BasicFlowFreeSolver(FFGrid grid) {
        super(grid);
    }

    /**
     * Solves the grid using backtrack search.
     * @return the solved grid, or null if no solution was found.
     */
    @Override
    public FFGrid solveFreeFlowGrid() {
        // When all cells are assigned a value (which only occurs if all assignments were valid) then return the
        // grid as the solved solution.
        if(grid.isComplete()) return grid;

        // Choose next unassigned variable (cell).
        FFCell var = getNextVariable();

        // Now iterate over all possible values for the variable (characters).
        for(char value: getValues(var)) {
            // Track variable assignments.
            numOfVariableAssignments++;
            // Try assigning the color to the variable...
            var.setColor(value);
            // Check if assignment meets constraints...
            if(gridMeetsConstraints()) {
                // If it does, recursively move forward to assign more variables.
                FFGrid result = solveFreeFlowGrid();
                // If a solved grid came out of the assignment, continue propagating it up the call stack.
                if(result != null) return result;
                // If no solved grid could be found with the assignment, move on to the next value/variable.
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
     * Gets the next unassigned variable in the grid by iterating right to left, top to bottom to find the first
     * cell that is not assigned.
     * @return the next unassigned cell.
     */
    @Override
    public FFCell getNextVariable() {
        // Iterate over all x and y positions in the grid.
        for(int x=0; x<grid.getWidth(); x++) {
            for(int y=0; y<grid.getHeight(); y++) {
                // Get the cell at x, y
                FFCell cell = grid.getCell(x, y);
                // Return first one we can find that isn't assigned.
                if(!cell.isAssigned()) return cell;
            }
        }
        // If no unassigned variable exists, return null.
        return null;
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
            System.out.println("java BasicFlowFreeSolver *filename* *width* *height*\n");
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

        BasicFlowFreeSolver solver = new BasicFlowFreeSolver(grid);

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
