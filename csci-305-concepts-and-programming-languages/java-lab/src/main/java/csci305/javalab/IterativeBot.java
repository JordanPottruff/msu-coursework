package csci305.javalab;

import java.util.Random;

public class IterativeBot extends Player{
    private int move_n;
    
    public IterativeBot(String name){
        super(name);
        
        Random r = new Random();
        move_n = r.nextInt(5);
    }
    
    public Element play(){
        String move = "";
        
        if(move_n == 0) move = "Rock";
        if(move_n == 1) move = "Paper";
        if(move_n == 2) move = "Scissors";
        if(move_n == 3) move = "Lizard";
        if(move_n == 4) move = "Spock";
        move_n = (move_n+1)%5;
        return Main.moves.get(move);
    }
}