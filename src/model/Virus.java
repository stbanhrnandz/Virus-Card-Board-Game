/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package model;

import enums.Color;

public class Virus extends Card {
    public Virus(Color color) {
        super(color);
    }

    @Override
    public String toString() {
        return getColor().getCode() + "🦠" + Color.RESET.getCode();
    }
}