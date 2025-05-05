package model;

import enums.Color;
import enums.TreatmentType;
import interfaces.SpecialTreatment;
import java.util.List;
import java.util.Random;

public abstract class BaseTreatment extends Card implements SpecialTreatment {
    private final TreatmentType type;
    
    public BaseTreatment(Color color, TreatmentType type) {
        super(color);
        this.type = type;
    }
    
    @Override
    public TreatmentType getType() {
        return type;
    }
    
    // Helper methods for subclasses
    protected void drawRandomCard(Player currentPlayer, List<Player> players) {
        List<Player> otherPlayers = new java.util.ArrayList<>();
        for (Player p : players) {
            if (p != currentPlayer && !p.getHand().isEmpty()) {
                otherPlayers.add(p);
            }
        }
        
        if (otherPlayers.isEmpty()) {
            System.out.println("There are no other players with cards to steal.");
            return;
        }
        
        Random random = new Random();
        Player targetPlayer = otherPlayers.get(random.nextInt(otherPlayers.size()));
        List<Card> targetHand = targetPlayer.getHand();
        Card stolenCard = targetHand.remove(random.nextInt(targetHand.size()));
        
        currentPlayer.getHand().add(stolenCard);
        System.out.println("You have stolen a random card from " + targetPlayer.getName() + ": " + stolenCard.toString());
    }
    
    protected Player getCardOwner(Card card, List<Player> players) {
        for (Player player : players) {
            if (player.getHand().contains(card)) {
                return player;
            }
        }
        return null;
    }
}