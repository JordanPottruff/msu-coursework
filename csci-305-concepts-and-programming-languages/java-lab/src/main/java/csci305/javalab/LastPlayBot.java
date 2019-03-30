package csci305.javalab;

import java.util.Random;

public class LastPlayBot extends Player{
    
    public LastPlayBot(String name){
        super(name);
    }
    
    public Element play(){
        String move = "";
        int n = 0;
        if(Main.last_move == null){
            Random r = new Random();
            n = r.nextInt(5);
        }else{
            String name = Main.last_move.get_name();
            if(name.equals("Rock")) n = 0;
            if(name.equals("Paper")) n = 1;
            if(name.equals("Scissors")) n = 2;
            if(name.equals("Lizard")) n = 3;
            if(name.equals("Spock")) n = 4;
            n = (n+1)%5;
        }
        if(n == 0) move = "Rock";
        if(n == 1) move = "Paper";
        if(n == 2) move = "Scissors";
        if(n == 3) move = "Lizard";
        if(n == 4) move = "Spock";
        return Main.moves.get(move);
    }
}