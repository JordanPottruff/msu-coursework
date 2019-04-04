
import java.util.List;

/** Allows use of a strategy pattern which lets the top-level class PacmanSearch
 * to change search behavior easily by accepting a different SearchBehavior 
 * object.
 */
public interface SearchBehavior {
    
    // Initial setup before search begins (e.g. add start to frontier).
    public void setup(List<Tile> frontier, List<Tile> closed, Tile start);
    
    // How the search alg gets the next node from the frontier.
    public Tile getNextNode(List<Tile> frontier, List<Tile> closed, Tile goal);
    
    // What the search alg does with each child of the 'gotten' node.
    public void processChildNode(List<Tile> frontier, List<Tile> closed, Tile parent, Tile child);
}
