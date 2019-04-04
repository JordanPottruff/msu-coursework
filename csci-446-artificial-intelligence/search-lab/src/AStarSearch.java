
import java.util.Collections;
import java.util.List;

/** A* search implementation **/
public class AStarSearch implements SearchBehavior {

    public void setup(List<Tile> frontier, List<Tile> closed, Tile start){
        frontier.add(start);
    }
    
    public Tile getNextNode(List<Tile> frontier, List<Tile> closed, Tile goal) {
        int minIndex = frontier.indexOf(Collections.min(frontier, (Tile a, Tile b) -> {
                int aCost = aStarCost(a, goal);
                int bCost = aStarCost(b, goal);
                // Tie breaking: return smallest heuristic cost
                if(aCost == bCost) return heuristic(a, goal) - heuristic(b, goal);
                // Otherwise, just return smallest total cost
                return aCost - bCost;
        }));
        Tile tile = frontier.remove(minIndex);
        closed.add(tile);
        return tile;
    }

    public void processChildNode(List<Tile> frontier, List<Tile> closed, Tile parent, Tile child) {
        int neighborCurrentCost = parent.distanceFromStart+1;
        if(frontier.contains(child)){
            if(child.distanceFromStart <= neighborCurrentCost) return;
        }else if(closed.contains(child)){
            if(child.distanceFromStart <= neighborCurrentCost) return;
            closed.remove(closed.indexOf(child));
            frontier.add(child);
        }else{
            frontier.add(child);
        }
        child.distanceFromStart = neighborCurrentCost;
        child.setParent(parent);
    }
    
    // Cost for A* is heuristic + distance from the start
    private int aStarCost(Tile tile, Tile goal){
        return tile.distanceFromStart+heuristic(tile, goal);
    }
    
    // Same heuristic as greedy
    private int heuristic(Tile a, Tile b){
        return Math.abs(a.x-b.x)+Math.abs(a.y-b.y);
    }

}