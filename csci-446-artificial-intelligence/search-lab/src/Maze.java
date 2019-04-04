
import java.io.File;
import java.util.ArrayList;
import java.util.Scanner;

/** Implements Maze made up of tile 2D array **/
public class Maze { 
    private final int width;
    private final int height;
    
    private Tile[][] maze;
    public Tile start;
    public Tile goal;
    
    // Creates a maze from a filename
    public Maze(String filename) throws Exception {
        // Initialize maze, start, and goal
        Scanner file = new Scanner(new File(filename));
        
        ArrayList<String> lines = new ArrayList<>();
        while (file.hasNextLine()){
            lines.add(file.nextLine().trim());
        }
        
        width = lines.get(0).length();
        height = lines.size();
        maze = new Tile[width][height];
        
        for(int row = 0; row < height; row++){
            for(int col = 0; col < width; col++){
                char symbol = lines.get(row).charAt(col);
                Tile tile = new Tile(col, row, symbol);
                maze[col][row] = tile;
                if(tile.isStart()) start = tile;
                if(tile.isGoal()) goal = tile;
            }
        }
        
        // If no start or goal found, notify with an exception
        if(start == null) throw new Exception("No starting point found");
        if(goal == null) throw new Exception("No goal point found");
    }
    
    /**
     * Returns a list of neighbors of a tile. Assumes that mazes are surrounded
     * on the outer edge by walls and that only playable tiles are entered. 
     * This prevents the need for out-of-bounds checking on each neighbor.
     */
    public ArrayList<Tile> getChildren(Tile tile){
        ArrayList<Tile> neighbors = new ArrayList<>();
        // Store all tiles that can be reached from 'tile'
        Tile up    = maze[tile.x][tile.y-1];
        Tile right = maze[tile.x+1][tile.y];
        Tile down  = maze[tile.x][tile.y+1];
        Tile left  = maze[tile.x-1][tile.y];
        
        // Add directions if they are valid, playable spaces
        if(up.isValid()) neighbors.add(up);
        if(right.isValid()) neighbors.add(right);
        if(down.isValid()) neighbors.add(down);
        if(left.isValid()) neighbors.add(left);
        
        // Return valid neighbors, potentially empty.
        return neighbors;
    }
    
    /** String representation of maze for outputting**/
    public String toString() {
        String result = "";
        for(int row = 0; row < height; row ++){
            for(int col = 0; col < width; col++){
                result += maze[col][row];
            }
            result += "\n";
        }
        return result;
    }
   
}
