/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package model;

import enums.Color;
import enums.TreatmentType;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class Exchange extends BaseTreatment {
    public Exchange() {
        super(Color.BLUE, TreatmentType.EXCHANGE);
    }

    @Override
    public void apply(Player currentPlayer, List<Player> players) {
        Scanner scanner = new Scanner(System.in);
        System.out.println("\nAvailable players to exchange:");
        for (int i = 0; i < players.size(); i++) {
            Player otherPlayer = players.get(i);
            if (otherPlayer != currentPlayer) {
                System.out.printf("%d. %s (%d cards)\n", 
                    i + 1, 
                    otherPlayer.getName(), 
                    otherPlayer.getHand().size());
            }
        }

        System.out.print("Select the number of the player to exchange with: ");
        int selection = scanner.nextInt() - 1;

        if (selection >= 0 && selection < players.size()) {
            Player otherPlayer = players.get(selection);
            if (otherPlayer != currentPlayer) {
                List<Card> currentPlayerCards = new ArrayList<>(currentPlayer.getHand());
                List<Card> otherPlayerCards = new ArrayList<>(otherPlayer.getHand());
                
                currentPlayer.getHand().clear();
                otherPlayer.getHand().clear();
                
                currentPlayer.getHand().addAll(otherPlayerCards);
                otherPlayer.getHand().addAll(currentPlayerCards);
                System.out.println("Exchange completed!");
            }
        } else {
            System.out.println("Invalid selection.");
        }
    }

    @Override
    public String toString() {
        return "\033[36m↔\033[0m";
    }
}