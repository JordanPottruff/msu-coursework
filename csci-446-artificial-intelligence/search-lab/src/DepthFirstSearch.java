
import java.util.List;

/** Depth first search implementation **/
public class DepthFirstSearch implements SearchBehavior {

    public void setup(List<Tile> frontier, List<Tile> closed, Tile start){
        frontier.add(start);
    }
    
    public Tile getNextNode(List<Tile> frontier, List<Tile> closed, Tile goal) {
        Tile tile = frontier.remove(frontier.size()-1);
        closed.add(tile);
        return tile;
    }

    public void processChildNode(List<Tile> frontier, List<Tile> closed, Tile parent, Tile child) {
        if(closed.contains(child)) return;
        child.setParent(parent);
        frontier.add(child);
    }
    
}
