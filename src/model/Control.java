package model;
import enums.Color;
import enums.TreatmentType;
import java.util.ArrayList;
import java.util.InputMismatchException;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Scanner;

public class Control extends BaseTreatment {
    private Map<Player, List<Organ>> organsOnTable;
    
    public Control() {
        super(Color.YELLOW, TreatmentType.CONTROL);
    }
    
    @Override
    public void apply(Player currentPlayer, List<Player> players) {
        Scanner scanner = new Scanner(System.in);
        System.out.println("\nControl options:");
        System.out.println("1. Draw random card from opponent");
        System.out.println("2. View opponent's hand");
        
        try {
            System.out.print("Select an option: ");
            int option = scanner.nextInt();
            switch (option) {
                case 1:
                    drawRandomCard(currentPlayer, players);
                    break;
                case 2:
                    viewOpponentHand(currentPlayer, players);
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
    
    private void viewOpponentHand(Player currentPlayer, List<Player> players) {
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
            System.out.println(opponent.getName() + " has no cards in hand.");
            return;
        }
        
        System.out.println("\n" + opponent.getName() + "'s hand:");
        for (int i = 0; i < opponentHand.size(); i++) {
            System.out.printf("%d. %s\n", i + 1, opponentHand.get(i).toString());
        }
    }
    
    @Override
    public String toString() {
        return Color.YELLOW.getCode() + "CONTROL" + Color.RESET.getCode();
    }
}
