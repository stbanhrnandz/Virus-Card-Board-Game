package model;
import enums.Color;
import enums.TreatmentType;
import java.util.ArrayList;
import java.util.InputMismatchException;
import java.util.List;
import java.util.Random;
import java.util.Scanner;

public class Control extends BaseTreatment {
    public Control() {
        super(Color.YELLOW, TreatmentType.CONTROL);
    }
    
    @Override
    public void apply(Player currentPlayer, List<Player> players) {
        Scanner scanner = new Scanner(System.in);
        System.out.println("\nControl options:");
        System.out.println("1. Draw random card");
        System.out.println("2. Exchange specific organ");
        
        try {
            System.out.print("Select an option: ");
            int option = scanner.nextInt();
            switch (option) {
                case 1:
                    drawRandomCard(currentPlayer, players);
                    break;
                case 2:
                    exchangeSpecificOrgan(currentPlayer, players, scanner);
                    break;
                default:
                    System.out.println("Invalid option. Please select 1 or 2.");
            }
        } catch (InputMismatchException e) {
            System.out.println("Error: You must enter a valid number.");
            scanner.nextLine(); // Clear scanner buffer
        }
    }
    
    protected void drawRandomCard(Player currentPlayer, List<Player> players) {
        // Verify input
        if (currentPlayer == null || players == null || players.isEmpty()) {
            System.out.println("Error: Invalid parameters for drawing a card.");
            return;
        }
        
        // Create list of eligible players (not the current player and with cards)
        List<Player> otherPlayers = new ArrayList<>();
        for (Player p : players) {
            if (p != currentPlayer && p.getHand() != null && !p.getHand().isEmpty()) {
                otherPlayers.add(p);
            }
        }
        
        if (otherPlayers.isEmpty()) {
            System.out.println("There are no other players with cards to draw from.");
            return;
        }
        
        try {
            Random random = new Random();
            Player targetPlayer = otherPlayers.get(random.nextInt(otherPlayers.size()));
            List<Card> targetHand = targetPlayer.getHand();
            
            // Verify that the target player's hand has cards
            if (targetHand == null || targetHand.isEmpty()) {
                System.out.println("Error: The target player's hand is empty.");
                return;
            }
            
            Card stolenCard = targetHand.remove(random.nextInt(targetHand.size()));
            currentPlayer.getHand().add(stolenCard);
            System.out.println("You have drawn a random card from " + targetPlayer.getName() + ": " + stolenCard.toString());
        } catch (Exception e) {
            System.out.println("Error drawing card: " + e.getMessage());
        }
    }
    
    private void exchangeSpecificOrgan(Player currentPlayer, List<Player> players, Scanner scanner) {
        // Get organs of the current player
        List<Organ> playerOrgans = getOrgans(currentPlayer);
        
        if (playerOrgans.isEmpty()) {
            System.out.println("You don't have any organs to exchange.");
            return;
        }
        
        // Select organ from current player
        System.out.println("\nSelect an organ to exchange:");
        showOptions(playerOrgans);
        
        int organSelection;
        try {
            System.out.print("Selection: ");
            organSelection = scanner.nextInt() - 1;
            
            if (organSelection < 0 || organSelection >= playerOrgans.size()) {
                System.out.println("Invalid selection. You must choose between 1 and " + playerOrgans.size());
                return;
            }
        } catch (InputMismatchException e) {
            System.out.println("Error: You must enter a valid number.");
            scanner.nextLine(); // Clear buffer
            return;
        }
        
        Organ playerOrgan = playerOrgans.get(organSelection);
        
        // Get other players
        List<Player> otherPlayers = new ArrayList<>();
        for (Player p : players) {
            if (p != currentPlayer) {
                otherPlayers.add(p);
            }
        }
        
        // Select target player
        System.out.println("\nSelect a player to exchange with:");
        for (int i = 0; i < otherPlayers.size(); i++) {
            System.out.printf("%d. %s\n", i + 1, otherPlayers.get(i).getName());
        }
        
        int playerSelection;
        try {
            System.out.print("Selection: ");
            playerSelection = scanner.nextInt() - 1;
            
            if (playerSelection < 0 || playerSelection >= otherPlayers.size()) {
                System.out.println("Invalid selection. You must choose between 1 and " + otherPlayers.size());
                return;
            }
        } catch (InputMismatchException e) {
            System.out.println("Error: You must enter a valid number.");
            scanner.nextLine(); // Clear buffer
            return;
        }
        
        Player targetPlayer = otherPlayers.get(playerSelection);
        List<Organ> targetOrgans = getOrgans(targetPlayer);
        
        if (targetOrgans.isEmpty()) {
            System.out.println(targetPlayer.getName() + " has no organs to exchange.");
            return;
        }
        
        // Select organ from target player
        System.out.println("\nSelect an organ from " + targetPlayer.getName() + ":");
        showOptions(targetOrgans);
        
        int targetOrganSelection;
        try {
            System.out.print("Selection: ");
            targetOrganSelection = scanner.nextInt() - 1;
            
            if (targetOrganSelection < 0 || targetOrganSelection >= targetOrgans.size()) {
                System.out.println("Invalid selection. You must choose between 1 and " + targetOrgans.size());
                return;
            }
        } catch (InputMismatchException e) {
            System.out.println("Error: You must enter a valid number.");
            scanner.nextLine(); // Clear buffer
            return;
        }
        
        Organ targetOrgan = targetOrgans.get(targetOrganSelection);
        
        // Perform the exchange
        currentPlayer.getHand().remove(playerOrgan);
        targetPlayer.getHand().remove(targetOrgan);
        
        currentPlayer.getHand().add(targetOrgan);
        targetPlayer.getHand().add(playerOrgan);
        
        System.out.println("Exchange completed successfully!");
        System.out.println("You gave " + playerOrgan + " and received " + targetOrgan + " from " + targetPlayer.getName());
    }
    
    private List<Organ> getOrgans(Player player) {
        List<Organ> organs = new ArrayList<>();
        for (Card card : player.getHand()) {
            if (card instanceof Organ) {
                organs.add((Organ) card);
            }
        }
        return organs;
    }
    
    private void showOptions(List<?> options) {
        for (int i = 0; i < options.size(); i++) {
            System.out.printf("%d. %s\n", i + 1, options.get(i).toString());
        }
    }
    
    @Override
    public String toString() {
        return "\033[33m⚡\033[0m";
    }
}