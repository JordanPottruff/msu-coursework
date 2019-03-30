package csci305.javalab;

import java.util.Random;

public class MyBot extends Player{
    
    public MyBot(String name){
        super(name);
    }
    
    public Element play(){
        int max = Integer.MIN_VALUE;
        String max_string = "";
        for(String key : Main.counts.keySet()){
            int count = Main.counts.get(key);
            if(count > max){
                max = count;
                max_string = key;
            }
        }
        
        Random r = new Random();
        boolean choice = r.nextBoolean();
        
        switch(max_string){
            case "Rock":
                if(choice)  return Main.moves.get("Paper");
                else        return Main.moves.get("Spock");
            case "Paper":
                if(choice)  return Main.moves.get("Scissors");
                else        return Main.moves.get("Lizard");
            case "Scissors":
                if(choice)  return Main.moves.get("Rock");
                else        return Main.moves.get("Spock");
            case "Lizard":
                if(choice)  return Main.moves.get("Rock");
                else        return Main.moves.get("Scissors");
            case "Spock":
                if(choice)  return Main.moves.get("Paper");
                else        return Main.moves.get("Lizard");
        }
        return Main.moves.get("Paper");
    }
}