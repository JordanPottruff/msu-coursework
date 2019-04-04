import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * A FlowFreeSolver is able to treat an unsolved FFGrid, representing an instance of a Flow Free board, as a constraint
 * satisfaction problem. Every solver requires implementations for methods that choose the next variable in the CSP,
 * determine the order of values to assign it, and use these to solve the given grid.
 *
 * FlowFreeSolver includes an implementation of a method that can determine if constraints have been met, as this
 * condition is critical to any Flow Free solver.
 */
public abstract class FlowFreeSolver {

    // The grid that the solver will attempt to solve using solveFreFlowGrid()
    protected final FFGrid grid;

    // Count of variable assignments conducted by the solver.
    protected int numOfVariableAssignments = 0;

    /**
     * Abstract constructor that subclasses can use to setup a solver's grid.
     * @param grid the grid to be solved.
     */
    public FlowFreeSolver(FFGrid grid) {
        this.grid = grid;
    }

    /**
     * Determines if all cells in the grid meet their constraints about cardinality (number of neighbors of each color)
     * and having a path to a source cell if all neighbors are assigned.
     * @return true if all constraints are met for every cell in the grid.
     */
    public boolean gridMeetsConstraints() {
        // Iterate over all x, y coordinates in grid.
        for(int x=0; x<grid.getWidth(); x++) {
            for(int y=0; y<grid.getHeight(); y++) {
                // Get the cell at x,y
                FFCell cell = grid.getCell(x, y);
                // Check that cell meets its two constraints.
                if(!cell.meetsConstraints()) return false;
            }
        }
        // Getting to this point means no constraint has been violated, so return true.
        return true;
    }

    /**
     * Returns the number of assignments conducted by the solver.
     * @return the number of assignments.
     */
    public int getNumOfAssignments() {
        return numOfVariableAssignments;
    }

    /**
     * Solves the solver's grid using the implemented CSP-solving algorithm.
     * @return a solved version of the grid.
     */
    public abstract FFGrid solveFreeFlowGrid();

    /**
     * Determines the next unassigned variable that should be assigned.
     * @return the next unassigned variable.
     */
    public abstract FFCell getNextVariable();

    /**
     * A list of values that can be assigned to the given cell. Assume that the list is ordered from first-to-try to
     * last-to-try (although some algorithms may not care about order).
     * @param cell
     * @return a list of values to be assigned to the cell.
     */
    public abstract List<Character> getValues(FFCell cell);
}
