
public class Tile {
    private static final char WALL_SYMBOL = '%';
    private static final char START_SYMBOL = 'P';
    private static final char GOAL_SYMBOL = '*';
    
    public int x;
    public int y;
    public char symbol;
    
    public Tile parent;
    public int distanceFromStart;
    
    public Tile(int x, int y, char symbol){
        this.x = x;
        this.y = y;
        this.symbol = symbol;
        
        // Default options for variables needing to be set in future
        parent = null;
        distanceFromStart = 0;
    }
    
    // Valid option for movement if not a wall
    public boolean isValid(){
        return this.symbol != WALL_SYMBOL;
    }
    
    // Tile is maze start point
    public boolean isStart(){
        return this.symbol == START_SYMBOL;
    }
    
    // Tile is maze end point
    public boolean isGoal(){
        return this.symbol == GOAL_SYMBOL;
    }
    
    public void setParent(Tile parent){
        this.distanceFromStart = parent.distanceFromStart+1;
        this.parent = parent;
    }
    
    public String toString(){
        return new Character(symbol).toString();
    }
}
