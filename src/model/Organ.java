package model;

import enums.Color;

public class Organ extends Card {
    private boolean infected = false;
    private boolean immunized = false;
    
    public Organ(Color color) {
        super(color);
    }
    
    public void infect() {
        if (!immunized) infected = true;
    }
    
    public void heal() {
        infected = false;
    }
    
    public void immunize() {
        immunized = true;
        infected = false;
    }
    
    @Override
    public String toString() {
        String state = infected ? "🤢" : "❤️";
        return getColor().getCode() + state + Color.RESET.getCode();
    }
    
    public boolean isInfected() {
        return infected;
    }
}