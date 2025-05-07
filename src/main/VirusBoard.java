package main;

import enums.Color;
import interfaces.SpecialTreatment;
import model.*;
import java.util.*;

/**
 * Main class of the VirusBoard game - Modified for 2 players
 */
public class VirusBoard {
    private static final int BOARD_WIDTH = 60;
    private static final int BOARD_HEIGHT = 20;
    private static final int REQUIRED_HAND_SIZE = 3;
    private static final int NUM_PLAYERS = 2; // Fixed at 2 players
    
    private List<Card> deck;
    private List<Card> discardPile;
    private List<Player> players;
    private Scanner scanner;
    private Map<Player, List<Organ>> organsOnTable;

    public VirusBoard() {
        this.scanner = new Scanner(System.in);
        this.organsOnTable = new HashMap<>();
        this.discardPile = new ArrayList<>();
        initializeDeck();
        initializePlayers();
        dealInitialCards();
    }

    private void initializeDeck() {
        deck = new ArrayList<>();
        
        // Create organs of different colors
        for (Color color : Arrays.asList(Color.RED, Color.GREEN, Color.BLUE, Color.YELLOW)) {
            for (int i = 0; i < 5; i++) {
                deck.add(new Organ(color));
            }
        }

        // Create viruses of different colors
        for (Color color : Arrays.asList(Color.RED, Color.GREEN, Color.BLUE, Color.YELLOW)) {
            for (int i = 0; i < 4; i++) {
                deck.add(new Virus(color));
            }
        }

        // Add special treatments
        deck.add(new Reikan());
        deck.add(new Exchange());
        deck.add(new Control());

        // Shuffle the deck
        Collections.shuffle(deck);
    }

    private void initializePlayers() {
        players = new ArrayList<>();
        for (int i = 0; i < NUM_PLAYERS; i++) {
            Player player = new Player("Player " + (i+1));
            players.add(player);
            organsOnTable.put(player, new ArrayList<>());
        }
    }
    
    private void dealInitialCards() {
        // Give exactly 3 cards to each player at the beginning
        for (Player player : players) {
            for (int i = 0; i < REQUIRED_HAND_SIZE; i++) {
                if (!deck.isEmpty()) {
                    player.getHand().add(deck.remove(0));
                }
            }
        }
    }

    public void play() {
        int currentPlayer = 0;

        while (true) {
            drawBoard(currentPlayer);
            System.out.println("\nTurn of " + players.get(currentPlayer).getName());

            // Check if we need to reshuffle deck
            if (deck.isEmpty() && !discardPile.isEmpty()) {
                reshuffleDeck();
            }

            System.out.println("\nAvailable actions:");
            System.out.println("1. Play card from hand");
            System.out.println("2. Use special treatment");
            System.out.println("3. Discard a card");
            System.out.println("4. View current hand");
            System.out.println("5. Exit game");

            System.out.print("Select an action: ");
            int action = scanner.nextInt();

            switch (action) {
                case 1:
                    if (playCardFromHand(currentPlayer)) {
                        // Draw a card and pass turn
                        drawCardFromDeck(currentPlayer);
                        currentPlayer = (currentPlayer + 1) % players.size();
                    }
                    break;
                case 2:
                    if (useSpecialTreatment(currentPlayer)) {
                        // Draw a card and pass turn
                        drawCardFromDeck(currentPlayer);
                        currentPlayer = (currentPlayer + 1) % players.size();
                    }
                    break;
                case 3:
                    if (discardCard(currentPlayer)) {
                        // Draw a card and pass turn
                        drawCardFromDeck(currentPlayer);
                        currentPlayer = (currentPlayer + 1) % players.size();
                    }
                    break;
                case 4:
                    viewCurrentHand(currentPlayer);
                    break;
                case 5:
                    System.out.println("Thanks for playing!");
                    return;
                default:
                    System.out.println("Invalid option.");
            }

            if (hasWon(currentPlayer)) {
                drawBoard(currentPlayer);
                System.out.println("\nCongratulations " + players.get(currentPlayer).getName() + "! You have won!");
                return;
            }
        }
    }

    private void drawBoard(int currentPlayer) {
        clearScreen();
        drawTopBorder();

        System.out.println("\nCards on the table:");
        showCardsOnTable();

        System.out.println("\nYour hand (" + players.get(currentPlayer).getName() + "):");
        showPlayerHand(currentPlayer);

        System.out.println("\nOther players:");
        showPlayersInfo(currentPlayer);

        drawBottomBorder();
    }

    private void clearScreen() {
        // Using a more compatible approach for terminal clearing
        for (int i = 0; i < 50; i++) {
            System.out.println();
        }
    }

    private void drawTopBorder() {
        System.out.println("+" + "-".repeat(BOARD_WIDTH) + "+");
    }

    private void drawBottomBorder() {
        System.out.println("+" + "-".repeat(BOARD_WIDTH) + "+");
    }

    private void showCardsOnTable() {
        for (Map.Entry<Player, List<Organ>> entry : organsOnTable.entrySet()) {
            Player player = entry.getKey();
            List<Organ> organs = entry.getValue();
            
            System.out.print(player.getName() + ": ");
            if (organs.isEmpty()) {
                System.out.println("No organs on table.");
            } else {
                for (Organ organ : organs) {
                    System.out.print(organ.toString() + " ");
                }
                System.out.println();
            }
        }
    }

    private void showPlayerHand(int currentPlayer) {
        Player player = players.get(currentPlayer);
        for (int i = 0; i < player.getHand().size(); i++) {
            Card card = player.getHand().get(i);
            System.out.printf("%d. %s\n", i + 1, card.toString());
        }
    }

    private void showPlayersInfo(int currentPlayer) {
        for (int i = 0; i < players.size(); i++) {
            if (i != currentPlayer) {
                Player player = players.get(i);
                System.out.printf("%s (%d cards in hand, %d organs on table)\n", 
                    player.getName(), 
                    player.getHand().size(),
                    organsOnTable.get(player).size());
            }
        }
    }

    private void drawCardFromDeck(int currentPlayer) {
        if (deck.isEmpty() && !discardPile.isEmpty()) {
            reshuffleDeck();
        }
        
        if (!deck.isEmpty()) {
            Card drawnCard = deck.remove(0);
            players.get(currentPlayer).getHand().add(drawnCard);
            System.out.println("You drew: " + drawnCard.toString());
            waitForEnter();
        } else {
            System.out.println("The deck is empty and there are no cards in the discard pile.");
            waitForEnter();
        }
    }
    
    private boolean playCardFromHand(int currentPlayer) {
        Player player = players.get(currentPlayer);
        List<Card> hand = player.getHand();
        
        if (hand.isEmpty()) {
            System.out.println("You have no cards in your hand.");
            waitForEnter();
            return false;
        }
        
        System.out.println("\nSelect a card to play:");
        for (int i = 0; i < hand.size(); i++) {
            System.out.printf("%d. %s\n", i + 1, hand.get(i).toString());
        }
        
        System.out.print("Selection (0 to cancel): ");
        int selection = scanner.nextInt();
        
        if (selection == 0) {
            return false;
        }
        
        if (selection < 1 || selection > hand.size()) {
            System.out.println("Invalid selection.");
            waitForEnter();
            return false;
        }
        
        Card selectedCard = hand.get(selection - 1);
        
        if (selectedCard instanceof Organ) {
            playOrgan(currentPlayer, (Organ) selectedCard);
            return true;
        } else if (selectedCard instanceof Virus) {
            return playVirus(currentPlayer, (Virus) selectedCard);
        } else if (selectedCard instanceof SpecialTreatment) {
            System.out.println("Special treatments are used from the main menu.");
            waitForEnter();
            return false;
        } else {
            System.out.println("This type of card cannot be played directly.");
            waitForEnter();
            return false;
        }
    }
    
    private void playOrgan(int currentPlayer, Organ organ) {
        Player player = players.get(currentPlayer);
        player.getHand().remove(organ);
        organsOnTable.get(player).add(organ);
        System.out.println("You have placed a " + organ.getColor() + " organ on the table.");
        waitForEnter();
    }
    
    private boolean playVirus(int currentPlayer, Virus virus) {
        Player player = players.get(currentPlayer);
        
        // Find the opponent player
        Player opponent = null;
        for (Player p : players) {
            if (p != player) {
                opponent = p;
                break;
            }
        }
        
        // Check if opponent has any organs on the table
        List<Organ> opponentOrgans = organsOnTable.get(opponent);
        if (opponentOrgans.isEmpty()) {
            System.out.println("The opponent has no organs to infect. You cannot play this virus.");
            waitForEnter();
            return false;
        }
        
        // Filter non-infected organs
        List<Organ> healthyOrgans = new ArrayList<>();
        for (Organ organ : opponentOrgans) {
            if (!organ.isInfected()) {
                healthyOrgans.add(organ);
            }
        }
        
        if (healthyOrgans.isEmpty()) {
            System.out.println("The opponent has no healthy organs to infect. You cannot play this virus.");
            waitForEnter();
            return false;
        }
        
        // Choose an organ to infect
        System.out.println("\nSelect an organ to infect:");
        for (int i = 0; i < healthyOrgans.size(); i++) {
            System.out.printf("%d. %s\n", i + 1, healthyOrgans.get(i).toString());
        }
        
        System.out.print("Selection (0 to cancel): ");
        int selection = scanner.nextInt();
        
        if (selection == 0) {
            return false;
        }
        
        if (selection < 1 || selection > healthyOrgans.size()) {
            System.out.println("Invalid selection.");
            waitForEnter();
            return false;
        }
        
        // Infect the selected organ
        Organ targetOrgan = healthyOrgans.get(selection - 1);
        player.getHand().remove(virus);
        targetOrgan.infect();
        System.out.println("You have infected an organ of " + opponent.getName() + "!");
        waitForEnter();
        return true;
    }

    private boolean useSpecialTreatment(int currentPlayer) {
        Player player = players.get(currentPlayer);
        List<SpecialTreatment> treatments = new ArrayList<>();

        for (Card card : player.getHand()) {
            if (card instanceof SpecialTreatment) {
                treatments.add((SpecialTreatment) card);
            }
        }

        if (treatments.isEmpty()) {
            System.out.println("You don't have any special treatments to use.");
            waitForEnter();
            return false;
        }

        System.out.println("\nAvailable treatments:");
        for (int i = 0; i < treatments.size(); i++) {
            System.out.printf("%d. %s (%s)\n", 
                i + 1, 
                treatments.get(i).toString(), 
                treatments.get(i).getType());
        }

        System.out.print("Select the treatment number to use (0 to cancel): ");
        int selection = scanner.nextInt();
        
        if (selection == 0) {
            return false;
        }
        
        selection -= 1;

        if (selection >= 0 && selection < treatments.size()) {
            SpecialTreatment treatment = treatments.get(selection);
            player.getHand().remove(treatment);
            treatment.apply(player, players);
            waitForEnter();
            return true;
        } else {
            System.out.println("Invalid selection.");
            waitForEnter();
            return false;
        }
    }

    private boolean discardCard(int currentPlayer) {
        Player player = players.get(currentPlayer);
        List<Card> hand = player.getHand();
        
        if (hand.isEmpty()) {
            System.out.println("You have no cards to discard.");
            waitForEnter();
            return false;
        }
        
        System.out.println("\nSelect a card to discard:");
        for (int i = 0; i < hand.size(); i++) {
            System.out.printf("%d. %s\n", i + 1, hand.get(i).toString());
        }
        
        System.out.print("Selection (0 to cancel): ");
        int selection = scanner.nextInt();
        
        if (selection == 0) {
            return false;
        }
        
        if (selection < 1 || selection > hand.size()) {
            System.out.println("Invalid selection.");
            waitForEnter();
            return false;
        }
        
        Card discardedCard = hand.remove(selection - 1);
        discardPile.add(discardedCard);
        System.out.println("You have discarded: " + discardedCard.toString());
        waitForEnter();
        return true;
    }

    private void viewCurrentHand(int currentPlayer) {
        System.out.println("\nYour hand:");
        showPlayerHand(currentPlayer);
        waitForEnter();
    }
    
    private void reshuffleDeck() {
        System.out.println("Reshuffling the discard pile into the deck...");
        
        // Add all cards from the discard pile back to the deck
        deck.addAll(discardPile);
        discardPile.clear();
        
        // Shuffle the deck
        Collections.shuffle(deck);
        
        System.out.println("Deck successfully reshuffled.");
    }
    
    private void waitForEnter() {
        System.out.println("\nPress Enter to continue...");
        scanner.nextLine(); // Consume pending newline
        scanner.nextLine(); // Wait for Enter key
    }

    private boolean hasWon(int currentPlayer) {
        // A player wins when they have 4 different organs (not infected) on the table
        Player player = players.get(currentPlayer);
        List<Organ> organs = organsOnTable.get(player);
        
        Set<Color> organColors = new HashSet<>();
        for (Organ organ : organs) {
            if (!organ.isInfected()) {
                organColors.add(organ.getColor());
            }
        }

        return organColors.size() >= 4;
    }

    public static void main(String[] args) {
        // Fixed at 2 players, no need to ask for number of players
        System.out.println("Starting Virus Board Game with 2 players...");
        VirusBoard game = new VirusBoard();
        game.play();
    }
}
