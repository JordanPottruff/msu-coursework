
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/** Best first search implementation **/
public class BestFirstSearch  implements SearchBehavior {

    public void setup(List<Tile> frontier, List<Tile> closed, Tile start){
        frontier.add(start);
    }
    
    public Tile getNextNode(List<Tile> frontier, List<Tile> closed, Tile goal) {
        int minIndex = frontier.indexOf(Collections.min(frontier, (Tile a, Tile b) -> heuristic(a, goal) - heuristic(b, goal)));
        Tile tile = frontier.remove(minIndex);
        closed.add(tile);
        return tile;
    }

    public void processChildNode(List<Tile> frontier, List<Tile> closed, Tile parent, Tile child) {
        if(!closed.contains(child) && !frontier.contains(child)){
            frontier.add(child);
            child.setParent(parent);
        }
    }
    
    private int heuristic(Tile a, Tile b){
        return Math.abs(a.x-b.x)+Math.abs(a.y-b.y);
    }

}
