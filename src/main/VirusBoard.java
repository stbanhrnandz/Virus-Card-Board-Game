package main;

import enums.Color;
import interfaces.SpecialTreatment;
import model.*;
import java.util.*;

/**
 * Main class of the VirusBoard game - Fixed version with improvements
 */
public class VirusBoard {
    private static final int BOARD_WIDTH = 60;
    private static final int BOARD_HEIGHT = 20;
    private static final int REQUIRED_HAND_SIZE = 3;
    private static final int NUM_PLAYERS = 2;
    
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
        
        // Create organs of different colors (5 of each)
        for (Color color : Arrays.asList(Color.RED, Color.GREEN, Color.BLUE, Color.YELLOW)) {
            for (int i = 0; i < 5; i++) {
                deck.add(new Organ(color));
            }
        }

        // Create viruses of different colors (4 of each)
        for (Color color : Arrays.asList(Color.RED, Color.GREEN, Color.BLUE, Color.YELLOW)) {
            for (int i = 0; i < 4; i++) {
                deck.add(new Virus(color));
            }
        }

        // Add more special treatments for better gameplay
        // Information treatments (Reikan)
        for (int i = 0; i < 3; i++) {
            deck.add(new Reikan());
        }
        
        // Exchange treatments
        for (int i = 0; i < 2; i++) {
            deck.add(new Exchange());
        }
        
        // Control treatments
        for (int i = 0; i < 2; i++) {
            deck.add(new Control());
        }
        
        // Medicine cards to heal organs (2 of each color)
        for (Color color : Arrays.asList(Color.RED, Color.GREEN, Color.BLUE, Color.YELLOW)) {
            for (int i = 0; i < 2; i++) {
                deck.add(new Medicina(color));
            }
        }

        // Shuffle the deck
        Collections.shuffle(deck);
        
        System.out.println("Deck initialized with " + deck.size() + " cards.");
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
                } else {
                    System.out.println("Warning: Not enough cards to deal initial hand!");
                }
            }
        }
    }

    public void play() {
        int currentPlayer = 0;

        while (true) {
            // Ensure current player has exactly 3 cards
            ensureHandSize(currentPlayer);
            
            drawBoard(currentPlayer);
            System.out.println("\nTurn of " + players.get(currentPlayer).getName());

            System.out.println("\nAvailable actions:");
            System.out.println("1. Play card from hand");
            System.out.println("2. Use special treatment");
            System.out.println("3. Discard a card");
            System.out.println("4. View current hand");
            System.out.println("5. View deck/discard info");
            System.out.println("6. Exit game");

            System.out.print("Select an action: ");
            int action = scanner.nextInt();

            switch (action) {
                case 1:
                    if (playCardFromHand(currentPlayer)) {
                        currentPlayer = nextTurn(currentPlayer);
                    }
                    break;
                case 2:
                    if (useSpecialTreatment(currentPlayer)) {
                        currentPlayer = nextTurn(currentPlayer);
                    }
                    break;
                case 3:
                    if (discardCard(currentPlayer)) {
                        currentPlayer = nextTurn(currentPlayer);
                    }
                    break;
                case 4:
                    viewCurrentHand(currentPlayer);
                    break;
                case 5:
                    viewDeckInfo();
                    break;
                case 6:
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
    
    private void ensureHandSize(int playerIndex) {
        Player player = players.get(playerIndex);
        
        // If player has more than 3 cards, they must discard
        while (player.getHand().size() > REQUIRED_HAND_SIZE) {
            System.out.println("\n" + player.getName() + " has " + player.getHand().size() + " cards. You must discard to have exactly 3 cards.");
            viewCurrentHand(playerIndex);
            forceDiscardCard(playerIndex);
        }
        
        // If player has less than 3 cards, draw until they have 3
        while (player.getHand().size() < REQUIRED_HAND_SIZE) {
            if (!drawCardFromDeck(playerIndex)) {
                // If we can't draw more cards and player has less than 3, game should end
                System.out.println("Cannot maintain required hand size. Game ending...");
                return;
            }
        }
    }
    
    private int nextTurn(int currentPlayer) {
        // Ensure current player ends with exactly 3 cards
        ensureHandSize(currentPlayer);
        return (currentPlayer + 1) % players.size();
    }
    
    private void viewDeckInfo() {
        System.out.println("\n=== DECK INFORMATION ===");
        System.out.println("Cards remaining in deck: " + deck.size());
        System.out.println("Cards in discard pile: " + discardPile.size());
        System.out.println("Total cards in game: " + (deck.size() + discardPile.size() + getTotalCardsInHands() + getTotalCardsOnTable()));
        waitForEnter();
    }
    
    private int getTotalCardsInHands() {
        int total = 0;
        for (Player player : players) {
            total += player.getHand().size();
        }
        return total;
    }
    
    private int getTotalCardsOnTable() {
        int total = 0;
        for (List<Organ> organs : organsOnTable.values()) {
            total += organs.size();
        }
        return total;
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
        
        System.out.println("\nDeck: " + deck.size() + " cards | Discard: " + discardPile.size() + " cards");

        drawBottomBorder();
    }

    private void clearScreen() {
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

    private boolean drawCardFromDeck(int currentPlayer) {
        // First check if we need to reshuffle
        if (deck.isEmpty() && !discardPile.isEmpty()) {
            reshuffleDeck();
        }
        
        if (!deck.isEmpty()) {
            Card drawnCard = deck.remove(0);
            players.get(currentPlayer).getHand().add(drawnCard);
            System.out.println("You drew: " + drawnCard.toString());
            return true;
        } else {
            System.out.println("No more cards available to draw!");
            return false;
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
        } else if (selectedCard instanceof Medicina) {
            return playMedicina(currentPlayer, (Medicina) selectedCard);
        } else if (selectedCard instanceof SpecialTreatment) {
            System.out.println("Special treatments are used from the main menu option 2.");
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
        
        // Filter non-infected organs of the same color
        List<Organ> healthyOrgans = new ArrayList<>();
        for (Organ organ : opponentOrgans) {
            if (!organ.isInfected() && organ.getColor() == virus.getColor()) {
                healthyOrgans.add(organ);
            }
        }
        
        if (healthyOrgans.isEmpty()) {
            System.out.println("The opponent has no healthy " + virus.getColor() + " organs to infect. You cannot play this virus.");
            waitForEnter();
            return false;
        }
        
        // Choose an organ to infect
        System.out.println("\nSelect a " + virus.getColor() + " organ to infect:");
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
        System.out.println("You have infected a " + targetOrgan.getColor() + " organ of " + opponent.getName() + "!");
        waitForEnter();
        return true;
    }
    
    private boolean playMedicina(int currentPlayer, Medicina medicina) {
        Player player = players.get(currentPlayer);
        List<Organ> playerOrgans = organsOnTable.get(player);
        
        // Filter infected organs of the same color
        List<Organ> infectedOrgans = new ArrayList<>();
        for (Organ organ : playerOrgans) {
            if (organ.isInfected() && organ.getColor() == medicina.getColor()) {
                infectedOrgans.add(organ);
            }
        }
        
        if (infectedOrgans.isEmpty()) {
            System.out.println("You don't have any infected " + medicina.getColor() + " organs to heal.");
            waitForEnter();
            return false;
        }
        
        System.out.println("\nSelect an infected " + medicina.getColor() + " organ to heal:");
        for (int i = 0; i < infectedOrgans.size(); i++) {
            System.out.printf("%d. %s\n", i + 1, infectedOrgans.get(i).toString());
        }
        
        System.out.print("Selection (0 to cancel): ");
        int selection = scanner.nextInt();
        
        if (selection == 0) {
            return false;
        }
        
        if (selection < 1 || selection > infectedOrgans.size()) {
            System.out.println("Invalid selection.");
            waitForEnter();
            return false;
        }
        
        // Heal the selected organ
        Organ organToHeal = infectedOrgans.get(selection - 1);
        player.getHand().remove(medicina);
        organToHeal.heal();
        System.out.println("You have healed your " + organToHeal.getColor() + " organ!");
        waitForEnter();
        return true;
    }

    private boolean useSpecialTreatment(int currentPlayer) {
        Player player = players.get(currentPlayer);
        List<SpecialTreatment> treatments = new ArrayList<>();

        for (Card card : player.getHand()) {
            if (card instanceof SpecialTreatment && !(card instanceof Medicina)) {
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
    
    private void forceDiscardCard(int currentPlayer) {
        Player player = players.get(currentPlayer);
        List<Card> hand = player.getHand();
        
        System.out.println("\nYou MUST discard a card:");
        for (int i = 0; i < hand.size(); i++) {
            System.out.printf("%d. %s\n", i + 1, hand.get(i).toString());
        }
        
        int selection = -1;
        while (selection < 1 || selection > hand.size()) {
            System.out.print("Selection: ");
            try {
                selection = scanner.nextInt();
                if (selection < 1 || selection > hand.size()) {
                    System.out.println("Invalid selection. Please choose a number between 1 and " + hand.size());
                }
            } catch (Exception e) {
                System.out.println("Please enter a valid number.");
                scanner.nextLine(); // Clear scanner buffer
            }
        }
        
        Card discardedCard = hand.remove(selection - 1);
        discardPile.add(discardedCard);
        System.out.println("You have discarded: " + discardedCard.toString());
    }

    private void viewCurrentHand(int currentPlayer) {
        System.out.println("\nYour hand:");
        showPlayerHand(currentPlayer);
        waitForEnter();
    }
    
    private void reshuffleDeck() {
        System.out.println("\n=== RESHUFFLING DECK ===");
        System.out.println("Moving " + discardPile.size() + " cards from discard pile to deck...");
        
        // Add all cards from the discard pile back to the deck
        deck.addAll(discardPile);
        discardPile.clear();
        
        // Shuffle the deck
        Collections.shuffle(deck);
        
        System.out.println("Deck successfully reshuffled. New deck size: " + deck.size());
        waitForEnter();
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
        System.out.println("Starting Virus Board Game with 2 players...");
        VirusBoard game = new VirusBoard();
        game.play();
    }
}
