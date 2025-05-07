package model;

import enums.Color;

public class Virus extends Card {
    public Virus(Color color) {
        super(color);
    }

    @Override
    public String toString() {
        return getColor().getCode() + "VIRUS" + Color.RESET.getCode();
    }
}
