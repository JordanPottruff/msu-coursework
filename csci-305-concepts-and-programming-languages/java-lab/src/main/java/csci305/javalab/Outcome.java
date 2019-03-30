package csci305.javalab;

public class Outcome {
    public String action;
    public String result;
    
    public Outcome(String first, String second){
        final String win = "Win";
        final String tie = "Tie";
        final String lose = "Lose";
        final String scissors_paper = "Scissors cut Paper";
        final String paper_rock = "Paper covers rock";
        final String rock_lizard = "Rock crushes Lizard";
        final String lizard_spock = "Lizard poisons Spock";
        final String spock_scissors = "Spock smashes Scissors";
        final String scissors_lizard = "Scissors decapitate Lizard";
        final String lizard_paper = "Lizard eats Paper";
        final String paper_spock = "Paper disproves Spock";
        final String spock_rock = "Spock vaporizes Rock";
        final String rock_scissors = "Rock crushes Scissors";
        
        switch(first){
            case "Rock":
                if(second.equals("Paper")) set(paper_rock, lose);
                if(second.equals("Scissors")) set(rock_scissors, win);
                if(second.equals("Lizard")) set(rock_lizard, win);
                if(second.equals("Spock")) set(spock_rock, lose);
                break;
            case "Paper":
                if(second.equals("Rock")) set(paper_rock, win);
                if(second.equals("Scissors")) set(scissors_paper, lose);
                if(second.equals("Lizard")) set(lizard_paper, lose);
                if(second.equals("Spock")) set(paper_spock, win);
                break;
            case "Scissors":
                if(second.equals("Rock")) set(rock_scissors, lose);
                if(second.equals("Paper")) set(scissors_paper, win);
                if(second.equals("Lizard")) set(scissors_lizard, win);
                if(second.equals("Spock")) set(spock_scissors, lose);
                break;
            case "Lizard":
                if(second.equals("Rock")) set(rock_lizard, lose);
                if(second.equals("Paper")) set(lizard_paper, win);
                if(second.equals("Scissors")) set(scissors_lizard, lose);
                if(second.equals("Spock")) set(lizard_spock, win);
                break;
            case "Spock":
                if(second.equals("Rock")) set(spock_rock, win);
                if(second.equals("Paper")) set(paper_spock, lose);
                if(second.equals("Scissors")) set(spock_scissors, win);
                if(second.equals("Lizard")) set(lizard_spock, lose);
                break;
        }
        if(first.equals(second)) set(first+" equals "+second, tie);
    }
    
    private void set(String a, String r){
        action = a;
        result = r;
    }
    
    public String toString(){
        return action + " -- " + result;
    }
}