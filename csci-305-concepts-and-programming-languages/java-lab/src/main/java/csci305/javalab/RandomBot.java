package csci305.javalab;

import java.util.Random;

public class RandomBot extends Player{
    
    public RandomBot(String name){
        super(name);
    }
    
    public Element play(){
        Random r = new Random();
        int n = r.nextInt(5);
        String move = "";
        
        if(n == 0) move = "Rock";
        if(n == 1) move = "Paper";
        if(n == 2) move = "Scissors";
        if(n == 3) move = "Lizard";
        if(n == 4) move = "Spock";
        
        return Main.moves.get(move);
    }
}