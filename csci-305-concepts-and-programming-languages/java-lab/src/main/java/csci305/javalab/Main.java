package csci305.javalab;

import java.util.HashMap;
import java.util.Scanner;

public class Main{
    public static final HashMap<String, Element> moves = new HashMap<String, Element>() {{
        put("Rock", new Rock("Rock"));
        put("Paper", new Paper("Paper"));
        put("Scissors", new Scissors("Scissors"));
        put("Lizard", new Lizard("Lizard"));
        put("Spock", new Spock("Spock"));
    }};
    
    public static HashMap<String, Integer> counts = new HashMap<String, Integer>();
    
    public static Element last_move = null;
    
    public static int get_selection(){
        Scanner in = new Scanner(System.in);
        int n = in.nextInt();
        while(n < 1 || n > 6){
            System.out.println("\nInvalid choice. Please try again: ");
            n = in.nextInt();
        }
        return n;
    }
    
    public static Player assign_player(int selection){
        Player e;
        switch(selection){
            case 1: 
                e = new Human("Human");
                break;
            case 2:
                e = new StupidBot("StupidBot");
                break;
            case 3:
                e = new RandomBot("RandomBot");
                break;
            case 4:
                e = new IterativeBot("IterativeBot");
                break;
            case 5:
                e = new LastPlayBot("LastPlayBot");
                break;
            default:
                e = new MyBot("MyBot");
                break;
        }
        return e;
    }
    
    public static void update_counts(String name){
        if(counts.containsKey(name)){
            int prev = counts.get(name);
            counts.put(name, prev+1);
        }else{
            counts.put(name, 0);
        }
    }
    
    public static int play_round(int round, Player p1, Player p2){
        System.out.println("Round " + round);
        
        //Determine first player's selection
        Element p1_play = p1.play();
        last_move = p1_play;
        update_counts(p1_play.get_name());
        //Determine second player's selection
        Element p2_play = p2.play();
        last_move = p2_play;
        update_counts(p2_play.get_name());
       
        System.out.println();
        System.out.println("  Player 1 chose " + p1_play.get_name());
        System.out.println("  Player 2 chose " + p2_play.get_name());
        
        Outcome o = p1_play.compareTo(p2_play);
        System.out.println("  "+o.action);
        if(o.result == "Win"){
            System.out.println("  Player 1 won the round\n");
            return 1;
        }else if(o.result == "Lose"){
            System.out.println("  Player 2 won the round\n");
            return -1;
        }else{
            System.out.println("  Round was a tie\n");
            return 0;
        }
    }
    
    public static void main(String args[]){
        System.out.println("Welcome to Rock, Paper, Scissors, Lizard, Spock, implemented by Jordan Pottruff");
        System.out.println("Please choose two players:");
        System.out.println("(1) Human\n(2) StupidBot\n(3) RandomBot\n(4) IterativeBot\n(5) LastPlayBot\n(6) MyBot\n");
        
        
        System.out.print("Select player 1: ");
        int p1i = get_selection();
        System.out.print("\nSelect player 2: ");
        int p2i = get_selection();
        System.out.println("");
        
        Player p1 = null;
        Player p2 = null;
        p1 = assign_player(p1i);
        p2 = assign_player(p2i);
        
        if(p1 == null || p2 == null){
            System.out.println("Error in assignment");
            return;
        }
        
        System.out.println("\n"+p1.getName()+" vs "+p2.getName()+". Go!\n");
        
        int p1_score = 0;
        int p2_score = 0;
        for(int i=1; i<=5; i++){
            int result = play_round(i, p1, p2);
            if(result > 0){
                p1_score++;
            }else if(result < 0){
                p2_score++;
            }
        }
        
        System.out.println("The score is " + p1_score + " to " + p2_score);
        
        String outcome;
        if(p1_score > p2_score) outcome = "Player 1 won the game";
        else if(p1_score < p2_score) outcome = "Player 2 won the game";
        else outcome = "Game was a draw";
        
        System.out.println(outcome);
        
    }
}