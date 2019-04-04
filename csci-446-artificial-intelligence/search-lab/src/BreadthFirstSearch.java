
import java.util.List;

/** Breadth first search implementation **/
public class BreadthFirstSearch implements SearchBehavior {

    public void setup(List<Tile> frontier, List<Tile> closed, Tile start){
        frontier.add(start);
        closed.add(start);
    }
    
    public Tile getNextNode(List<Tile> frontier, List<Tile> closed, Tile goal) {
        return frontier.remove(0);
    }

    public void processChildNode(List<Tile> frontier, List<Tile> closed, Tile parent, Tile child) {
        if(closed.contains(child)) return;
        child.parent = parent;
        closed.add(child);
        frontier.add(child);
    }
    
}
