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
        // In a 2-player game, we only have one opponent
        Player opponent = null;
        for (Player player : players) {
            if (player != currentPlayer) {
                opponent = player;
                break;
            }
        }
        
        if (opponent == null) {
            System.out.println("Error: Could not find opponent.");
            return;
        }
        
        System.out.println("Exchanging hands with " + opponent.getName());
        
        List<Card> currentPlayerCards = new ArrayList<>(currentPlayer.getHand());
        List<Card> opponentCards = new ArrayList<>(opponent.getHand());
        
        currentPlayer.getHand().clear();
        opponent.getHand().clear();
        
        currentPlayer.getHand().addAll(opponentCards);
        opponent.getHand().addAll(currentPlayerCards);
        System.out.println("Exchange completed!");
    }

    @Override
    public String toString() {
        return Color.BLUE.getCode() + "EXCHANGE" + Color.RESET.getCode();
    }
}
