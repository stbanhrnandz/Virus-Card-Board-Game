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
        System.out.println("1. Draw random card from opponent");
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
        // In a 2-player game, we only have one opponent
        Player opponent = null;
        for (Player p : players) {
            if (p != currentPlayer) {
                opponent = p;
                break;
            }
        }
        
        if (opponent == null) {
            System.out.println("Error: Could not find opponent.");
            return;
        }
        
        List<Card> opponentHand = opponent.getHand();
        if (opponentHand.isEmpty()) {
            System.out.println("The opponent has no cards to steal.");
            return;
        }
        
        Random random = new Random();
        Card stolenCard = opponentHand.remove(random.nextInt(opponentHand.size()));
        
        currentPlayer.getHand().add(stolenCard);
        System.out.println("You have drawn a random card from " + opponent.getName() + ": " + stolenCard.toString());
    }
    
    private void exchangeSpecificOrgan(Player currentPlayer, List<Player> players, Scanner scanner) {
        // In a 2-player game, we only have one opponent
        Player opponent = null;
        for (Player p : players) {
            if (p != currentPlayer) {
                opponent = p;
                break;
            }
        }
        
        if (opponent == null) {
            System.out.println("Error: Could not find opponent.");
            return;
        }
        
        // Get organs of the current player
        List<Organ> playerOrgans = getOrgans(currentPlayer);
        List<Organ> opponentOrgans = getOrgans(opponent);
        
        if (playerOrgans.isEmpty()) {
            System.out.println("You don't have any organs to exchange.");
            return;
        }
        
        if (opponentOrgans.isEmpty()) {
            System.out.println("Your opponent doesn't have any organs to exchange.");
            return;
        }
        
        // Select organ from current player
        System.out.println("\nSelect an organ to exchange:");
        showOptions(playerOrgans);
        
        int organSelection;
        try {
            System.out.print("Selection: ");
            organSelection = scanner.nextInt() - 1;
            
            if (organSelection < 0 || organSelection >= playerOrgans.size())
