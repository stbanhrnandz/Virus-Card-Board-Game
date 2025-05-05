/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package model;

import enums.Color;
import interfaces.Colorable;

public abstract class Card implements Colorable {
    protected Color color;
    
    public Card(Color color) {
        this.color = color;
    }
    
    @Override
    public Color getColor() {
        return color;
    }
}