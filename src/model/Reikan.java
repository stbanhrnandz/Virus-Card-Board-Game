package model;

import enums.Color;
import enums.TreatmentType;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.InputMismatchException;

public class Reikan extends BaseTreatment {
    
    public Reikan() {
        super(Color.MULTICOLOR, TreatmentType.INFORMATION);
    }
    
    @Override
    public void apply(Player currentPlayer, List<Player> players) {
        // Create a list to store all available cards
        List<Card> allCards = new ArrayList<>();
        // List to keep track of the owners of each card
        List<Player> owners = new ArrayList<>();
        
        // Collect all cards and their owners
        for (Player player : players) {
            for (Card card : player.getHand()) {
                allCards.add(card);
                owners.add(player);
            }
        }
        
        if (allCards.isEmpty()) {
            System.out.println("There are no cards available to steal.");
            return;
        }
        
        System.out.println("\nAvailable cards:");
        for (int i = 0; i < allCards.size(); i++) {
            Card card = allCards.get(i);
            Player owner = owners.get(i);
            System.out.printf("%d. %s (%s)\n", i + 1, card.toString(), owner.getName());
        }
        
        Scanner scanner = new Scanner(System.in);
        int selection = -1;
        
        try {
            System.out.print("Select the number of the card you want to steal: ");
            selection = scanner.nextInt() - 1;
            
            if (selection >= 0 && selection < allCards.size()) {
                Card selectedCard = allCards.get(selection);
                Player originalOwner = owners.get(selection);
                
                if (originalOwner != null && originalOwner.getHand().remove(selectedCard)) {
                    currentPlayer.getHand().add(selectedCard);
                    System.out.println("You have successfully stolen the card " + selectedCard + " from " + originalOwner.getName() + "!");
                } else {
                    System.out.println("Error stealing the card.");
                }
            } else {
                System.out.println("Invalid selection. Please choose a number between 1 and " + allCards.size());
            }
        } catch (InputMismatchException e) {
            System.out.println("Error: You must enter a valid number.");
            scanner.nextLine(); // Clear the scanner buffer
        }
    }
    
    @Override
    public String toString() {
        return "\033[35m✨\033[0m";
    }
}