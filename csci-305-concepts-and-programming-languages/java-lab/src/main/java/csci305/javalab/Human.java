package csci305.javalab;

import java.util.Random;
import java.util.Scanner;

public class Human extends Player{
    
    public Human(String name){
        super(name);
    }
    
    public Element play(){
        Scanner in = new Scanner(System.in);
        System.out.println("  (1) : Rock");
        System.out.println("  (2) : Paper");
        System.out.println("  (3) : Scissors");
        System.out.println("  (4) : Lizard");
        System.out.println("  (5) : Spock");
        System.out.print("  Enter your move: ");
        
        int input = in.nextInt();
        
        while(input < 1 || input > 5){
            System.out.println("  Invalid move. Please try again.");
            System.out.print("  Enter your move: ");
            input = in.nextInt();
        }
        
        String move = "";
        if(input == 1) move = "Rock";
        if(input == 2) move = "Paper";
        if(input == 3) move = "Scissors";
        if(input == 4) move = "Lizard";
        if(input == 5) move = "Spock";
        
        return Main.moves.get(move); 
    }
}