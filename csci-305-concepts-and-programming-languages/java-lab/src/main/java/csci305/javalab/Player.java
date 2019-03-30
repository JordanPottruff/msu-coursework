package csci305.javalab;

public abstract class Player {
    private String name;
    
    public Player(String n){
        name = n;
    }
    
    public String getName(){
        return name;
    }
    
    public abstract Element play();
}