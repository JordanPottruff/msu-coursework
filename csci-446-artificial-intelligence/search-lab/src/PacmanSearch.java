
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/** Unified top-level class that can use any search method assigned to it **/
public class PacmanSearch {
    /** Value class for storing results of a search **/
    private static class SearchResult {
        public Maze solvedMaze;
        public int pathCost;
        public int expandedNodes;
        
        public SearchResult(Maze m, int cost, int expanded){
            solvedMaze = m;
            pathCost = cost;
            expandedNodes = expanded;
        }
        
        public String toString(){
            String result = "";
            result += "\n------------------------------ RESULT OF SEARCH ------------------------------\n";
            result += String.format("Shortest path cost: %s\n", pathCost);
            result += String.format("Num of expanded nodes: %s\n", expandedNodes);
            result += "Solved maze: \n";
            result += solvedMaze.toString();
            
            return result;
        }
    }
    
    // Filename of the maze
    private String mazeFilename;
    // Current search method
    private SearchBehavior searchBehavior;
    
    public PacmanSearch(String mazeFilename){
        this.mazeFilename = mazeFilename;
        this.searchBehavior = new DepthFirstSearch();
    }
    
    /** Changes the search method **/
    public void setSearchStrategy(SearchBehavior searchBehavior){
        this.searchBehavior = searchBehavior;
    }
    
    /** Conducts the search using the method on the filename **/
    public SearchResult search() throws Exception{
        // Data structures for searching
        Maze maze = new Maze(mazeFilename);
        ArrayList<Tile> frontier = new ArrayList<>();
        ArrayList<Tile> closed = new ArrayList<>();
        
        // Keep track of expanded nodes
        int expandedNodes = 0;
        
        // FIRST SEARCH METHOD: setup prior to search
        searchBehavior.setup(frontier, closed, maze.start);
        while(!frontier.isEmpty()){
            expandedNodes++;
            // SECOND SEARCH METHOD: how to get the next node?
            Tile parent = searchBehavior.getNextNode(frontier, closed, maze.goal);
            
            // Iterate over all children nodes for parent
            for(Tile child: maze.getChildren(parent)){
                // Goal check each child
                if(child.isGoal()){
                    child.setParent(parent);
                    int shortestPathCost = determineShortestPath(child, maze.start);
                    return new SearchResult(maze, shortestPathCost, expandedNodes);
                }
                
                // THIRD SEARCH METHOD: process the child
                searchBehavior.processChildNode(frontier, closed, parent, child);
            }
        }
        
        return null;
    }
    
    /** Iterates up over ancestors of Tile to find shortest path **/
    public int determineShortestPath(Tile end, Tile start){
        int cost = 1;
        Tile cur = end.parent;
        while(cur != null && cur != start){
            cost++;
            // Update maze with path symbol
            cur.symbol = '.';
            cur = cur.parent;
        }
        return cost;
    }
    
    public static void main(String[] args) throws Exception {
        String[] mazeNames = {"open maze.txt", "medium maze.txt", "large maze.txt"};
        
        /** Demonstrate all search behaviors on each maze **/
        for(String filename: mazeNames){
            PacmanSearch pacmanSearch = new PacmanSearch(filename);
            
            //DFS
            pacmanSearch.setSearchStrategy(new DepthFirstSearch());
            System.out.println("Depth first search on: " + filename);
            System.out.println(pacmanSearch.search());
            //BFS
            pacmanSearch.setSearchStrategy(new BreadthFirstSearch());
            System.out.println("Breadth first search on: " + filename);
            System.out.println(pacmanSearch.search());
            //Greedy
            pacmanSearch.setSearchStrategy(new BestFirstSearch());
            System.out.println("Best first search on: " + filename);
            System.out.println(pacmanSearch.search());
            //A*
            pacmanSearch.setSearchStrategy(new AStarSearch());
            System.out.println("A star search on: " + filename);
            System.out.println(pacmanSearch.search());
        }
    }
    
}
