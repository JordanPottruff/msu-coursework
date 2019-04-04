import java.util.ArrayList;
import java.util.HashMap;

/**
 * Represents a cell within the game board for Flow Free. Each cell is either a source cell, which need to be connected
 * to another source of the same color, or an initially unassigned (empty) cell that will be used to create paths.
 *
 * In addition to containing basic data and methods for evaluating the cell's position, neighbors, and domain, the
 * FFCell class also includes definitions for constraints that each cell needs to have to enforce a correct solution
 * to the Flow Free game.
 */
public class FFCell {
    public static final char EMPTY_CELL_SYMBOL = '_';

    private final char originalSymbol;      // Allows us to reset symbol to initial level, which is always empty.
    private final int x, y;                 // Coordinates of the cell within a greater data structure (FFGrid).

    private char currentSymbol;             // Keeps track of the current assignment if not a source.
    private boolean source;                 // True if the cell is a source cell.
    private boolean assigned;               // True if the cell is assigned a value (always true for source cells)
    private ArrayList<FFCell> neighbors;    // A list of neighbors to the cell in a greater data structure (FFGrid).
    private ArrayList<Character> domain;    // Somewhat of a misnomer, but represents a collection of values that can
                                            // be assigned to the cell without breaking constraints.

    /**
     * Intentionally private constructor to be used by public static factory methods.
     * @param x the x-coordinate of the cell.
     * @param y the y-coordinate of the cell.
     * @param symbol the color of the cell, represented as a character.
     */
    private FFCell(int x, int y, char symbol) {
        this.originalSymbol = symbol;
        this.x = x;
        this.y = y;

        this.currentSymbol = symbol;
        this.source = symbol != EMPTY_CELL_SYMBOL;
        this.assigned = this.source;
        // Neighbors and domains need to be updated after initialization.
        this.neighbors = new ArrayList<>();
        this.domain = new ArrayList<>();
    }

    /**
     * Static factory method that creates a source cell for a specific color.
     * @param x the x-coordinate of the cell.
     * @param y the y-coordinate of the cell.
     * @param color the color of the cell, represented as a character.
     * @return a new FFCell representing a source cell with the given attributes.
     */
    public static FFCell createSourceCell(int x, int y, char color) {
        return new FFCell(x, y, color);
    }

    /**
     * Static factory method that creates an initially unassigned, non-source cell.
     * @param x the x-coordinate of the cell.
     * @param y the y-coordinate of the cell.
     * @return a new FFCell representing a non-source cell with the given position.
     */
    public static FFCell createEmptyCell(int x, int y) {
        return new FFCell(x, y, EMPTY_CELL_SYMBOL);
    }

    /**
     * Adds a neighboring cell to the cell. This needs to be done by any data structure containing the cell.
     * @param neighbor a new neighbor to the cell.
     */
    public void addNeighbor(FFCell neighbor) {
        this.neighbors.add(neighbor);
    }

    /**
     * Updates the 'domain' of the cell to be all values that the cell can be assigned without breaking any constraints.
     * @param fullDomain the full, unconstrained domain of possible values that a cell can have. Essentially, all the
     *                   colors in the game grid.
     */
    public void updateDomain(ArrayList<Character> fullDomain) {
        domain.clear();

        // Try each color/symbol...
        for(char color: fullDomain) {
            // Except the empty symbol...
            if(color == EMPTY_CELL_SYMBOL) continue;

            setColor(color);

            // Check that all of the neighbors constraints are met with the given assignment.
            boolean neighborsConstraintsMet = true;
            for(FFCell neighbor: getNeighbors()) {
                if(!neighbor.meetsConstraints()) {
                    neighborsConstraintsMet = false;
                }
            }

            // If all constraints were met, neighborConstraintsMet is still true and we can add the color.
            if(neighborsConstraintsMet) {
                domain.add(color);
            }

            // Undo assignment to go to the next option.
            resetColor();
        }
    }

    /**
     * Determines whether the cell's current assignment meets all constraints. Specifically, it checks that no colors
     * cardinality (number of neighbors of that color) exceeds two at any point and that if the cell is complete
     * (all neighbors assigned), that its touching exactly 1 of the same color if its a source and exactly 2 if its
     * not a source cell. Also, it checks that the cell has a path to the source unless a path is not yet finished.
     *
     * @return true if all constraints are met.
     */
    public boolean meetsConstraints() {
        return cardinalityConstraint() && connectedToSourceConstraint();
    }

    /**
     * Determines if the cardinality constraint is met. This constraint checks that the cardinality of any color is
     * equal or less than 2, and if the cell is complete, that exactly one neighbor cell shares the same color if its
     * a source, or exactly 2 if its not a source.
     * @return true if cardinality constraint is met.
     */
    private boolean cardinalityConstraint() {
        HashMap<Character, Integer> neighborColors = getNeighborColors();

        boolean hasUnassignedNeighbor = neighborColors.containsKey(EMPTY_CELL_SYMBOL);
        boolean isSource = isSource();
        boolean isAssigned = isAssigned();

        if(!isAssigned) return true;

        if(neighborColors.containsKey(getColor())) {
            if(neighborColors.get(getColor()) > 2) return false;
        }

        if(!hasUnassignedNeighbor) {
            // All cells with no unassigned neighbors needs to contain at least one neighbor of the same color...
            if(!neighborColors.containsKey(getColor())) return false;

            // If the cell is a source, it should have specifically one neighbor of the same color.
            if(isSource) {
                if(neighborColors.get(getColor()) != 1) return false;
                // If the cell is not a source but is assigned, it should have exactly two neighbors with the same color.
            } else {
                if(neighborColors.get(getColor()) != 2) return false;
            }
        }

        // Otherwise, all cardinality constraints have been met.
        return true;
    }

    /**
     * Determines if the cell is connected to a source if the path is complete. Essentially the cell's path is searched
     * using DFS, and if any of the cells touch a source or an unassigned cell, then the constraint is met. Otherwise,
     * a path has been created that is not connected to a source, and can NEVER be connected to a source under the
     * current assignments.
     * @return true if the connected to source constraint is met.
     */
    private boolean connectedToSourceConstraint() {
        if(isSource() || !isAssigned()) return true;
        return hasPathToSource(new ArrayList<>());
    }

    /**
     * Recursive utility method for determining if a cell is connected to a source or unassigned cell. Basically a
     * Depth First Search.
     * @param visited list of visited nodes, so we don't revisit them infinitely.
     * @return true if there is path to source or unassigned cell, false otherwise.
     */
    private boolean hasPathToSource(ArrayList<FFCell> visited) {
        HashMap<Character, Integer> neighborColors = getNeighborColors();
        if(neighborColors.containsKey(EMPTY_CELL_SYMBOL)) return true;
        if(neighborColors.containsKey(getColor())) return true;

        visited.add(this);

        for(FFCell neighbor: getNeighbors()) {
            if(neighbor.getColor() == getColor() && !visited.contains(neighbor)) {
                if(neighbor.hasPathToSource(visited)) return true;
            }
        }

        return false;
    }

    /**
     * Assigns a color to the cell.
     * @param color the color to be assigned.
     */
    public void setColor(char color) {
        // If source or assigned, throw an error to notify client that you can't assign those cells.
        if(this.source) throw new RuntimeException("Cannot assign a source cell a new color.");
        if(this.assigned) throw new RuntimeException("Cannot reassign a cell's color without resetting it first.");
        this.currentSymbol = color;
        this.assigned = true;
    }

    /**
     * Resets to the original (empty) color.
     */
    public void resetColor() {
        // Notify client if attempting to reset a source or unassigned cell.
        if(this.source) throw new RuntimeException("Cannot reset a source cell's color.");
        if(!this.assigned) throw new RuntimeException("Cannot unassign a cell's color that has not yet been assigned.");
        this.currentSymbol = originalSymbol;
        this.assigned = false;
    }

    /**
     * Gets the currently assigned color.
     * @return the current color of the cell.
     */
    public char getColor() {
        return this.currentSymbol;
    }

    /**
     * Gets the x-coordinate of the cell.
     * @return the cell's x-coordinate.
     */
    public int getX() {
        return this.x;
    }

    /**
     * Gets the y-coordinate of the cell.
     * @return the cell's y-coordinate.
     */
    public int getY() {
        return this.y;
    }

    /**
     * Returns the list of values that the cell can be assigned and not break its constraints.
     * @return
     */
    public ArrayList<Character> getDomain() {
        return this.domain;
    }

    /**
     * Returns the list of adjacent cells (up, down, right, left; if exist).
     * @return
     */
    public ArrayList<FFCell> getNeighbors() {
        return this.neighbors;
    }

    /**
     * Returns the count of each color found in neighboring cells.
     * @return a HashMap with keys that are the symbols, and values that are corresponding counts for those symbols.
     */
    public HashMap<Character, Integer> getNeighborColors() {
        HashMap<Character, Integer> colorCounts = new HashMap<>();

        // Iterate over all neighbors...
        for(FFCell neighbor: neighbors) {

            if(colorCounts.containsKey(neighbor.getColor())) {
                // If the neighbors color is in the count map, increment the count stored in it for that symbol.
                int previousCount = colorCounts.get(neighbor.getColor());
                colorCounts.put(neighbor.getColor(), previousCount+1);
            } else {
                // If the neighbors color is not in the count map, add the color with a count = 1.
                colorCounts.put(neighbor.getColor(), 1);
            }
        }
        // Return the count map.
        return colorCounts;
    }

    /**
     * Returns whether the cell is a source cell or is an initially unassigned (empty) cell.
     * @return true if the cell is a source cell.
     */
    public boolean isSource() {
        return this.source;
    }

    /**
     * Returns true if the cell is currently assigned a color. This is always true for source cells.
     * @return true if the cell is assigned a color.
     */
    public boolean isAssigned() {
        return this.assigned;
    }

    /**
     * Allows the cells to be printed cleanly for debugging.
     * @return a string representation of the cell: the symbol/color.
     */
    public String toString() {
        return Character.toString(this.currentSymbol);
    }
}
