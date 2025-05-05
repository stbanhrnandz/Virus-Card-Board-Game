package main;

import enums.Color;
import interfaces.SpecialTreatment;
import model.*;
import java.util.*;

/**
 * Main class of the VirusBoard game
 */
public class VirusBoard {
    private static final int BOARD_WIDTH = 60;
    private static final int BOARD_HEIGHT = 20;
    private List<Card> deck;
    private List<Player> players;
    private Scanner scanner;
    private Map<Player, List<Organ>> organsOnTable;

    public VirusBoard(int numPlayers) {
        this.scanner = new Scanner(System.in);
        this.organsOnTable = new HashMap<>();
        initializeDeck();
        initializePlayers(numPlayers);
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

    private void initializePlayers(int numPlayers) {
        players = new ArrayList<>();
        for (int i = 0; i < numPlayers; i++) {
            Player player = new Player("Player " + (i+1));
            players.add(player);
            organsOnTable.put(player, new ArrayList<>());
        }
    }
    
    private void dealInitialCards() {
        // Give 3 cards to each player at the beginning
        for (Player player : players) {
            for (int i = 0; i < 3; i++) {
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

            if (deck.isEmpty()) {
                System.out.println("The deck is empty!");
                reshuffleDeck();
            }

            System.out.println("\nAvailable actions:");
            System.out.println("1. Draw card from deck");
            System.out.println("2. Play card from hand");
            System.out.println("3. Use special treatment");
            System.out.println("4. View current hand");
            System.out.println("5. Pass turn");
            System.out.println("6. Exit");

            System.out.print("Select an action: ");
            int action = scanner.nextInt();

            switch (action) {
                case 1:
                    drawCardFromDeck(currentPlayer);
                    break;
                case 2:
                    playCardFromHand(currentPlayer);
                    break;
                case 3:
                    useSpecialTreatment(currentPlayer);
                    break;
                case 4:
                    viewCurrentHand(currentPlayer);
                    break;
                case 5:
                    currentPlayer = (currentPlayer + 1) % players.size();
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
        System.out.print("\033[H\033[2J");
        System.out.flush();
    }

    private void drawTopBorder() {
        System.out.println("╔" + "═".repeat(BOARD_WIDTH) + "╗");
    }

    private void drawBottomBorder() {
        System.out.println("╚" + "═".repeat(BOARD_WIDTH) + "╝");
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
        if (!deck.isEmpty()) {
            Card drawnCard = deck.remove(0);
            players.get(currentPlayer).getHand().add(drawnCard);
            System.out.println("You drew: " + drawnCard.toString());
            waitForEnter();
        } else {
            System.out.println("The deck is empty.");
            waitForEnter();
        }
    }
    
    private void playCardFromHand(int currentPlayer) {
        Player player = players.get(currentPlayer);
        List<Card> hand = player.getHand();
        
        if (hand.isEmpty()) {
            System.out.println("You have no cards in your hand.");
            waitForEnter();
            return;
        }
        
        System.out.println("\nSelect a card to play:");
        for (int i = 0; i < hand.size(); i++) {
            System.out.printf("%d. %s\n", i + 1, hand.get(i).toString());
        }
        
        System.out.print("Selection (0 to cancel): ");
        int selection = scanner.nextInt();
        
        if (selection == 0) {
            return;
        }
        
        if (selection < 1 || selection > hand.size()) {
            System.out.println("Invalid selection.");
            waitForEnter();
            return;
        }
        
        Card selectedCard = hand.get(selection - 1);
        
        if (selectedCard instanceof Organ) {
            playOrgan(currentPlayer, (Organ) selectedCard);
        } else if (selectedCard instanceof Virus) {
            playVirus(currentPlayer, (Virus) selectedCard);
        } else if (selectedCard instanceof SpecialTreatment) {
            System.out.println("Special treatments are used from the main menu.");
            player.getHand().add(selectedCard);
        } else {
            System.out.println("This type of card cannot be played directly.");
        }
        
        waitForEnter();
    }
    
    private void playOrgan(int currentPlayer, Organ organ) {
        Player player = players.get(currentPlayer);
        player.getHand().remove(organ);
        organsOnTable.get(player).add(organ);
        System.out.println("You have placed a " + organ.getColor() + " organ on the table.");
    }
    
    private void playVirus(int currentPlayer, Virus virus) {
        Player player = players.get(currentPlayer);
        player.getHand().remove(virus);
        
        // Show all players with organs of the same color
        List<Player> possibleTargets = new ArrayList<>();
        for (Map.Entry<Player, List<Organ>> entry : organsOnTable.entrySet()) {
            for (Organ organ : entry.getValue()) {
                if (organ.getColor() == virus.getColor() && !organ.isInfected()) {
                    possibleTargets.add(entry.getKey());
                    break;
                }
            }
        }
        
        if (possibleTargets.isEmpty()) {
            System.out.println("There are no compatible organs to infect. The virus is discarded.");
            return;
        }
        
        System.out.println("\nSelect a player to infect:");
        for (int i = 0; i < possibleTargets.size(); i++) {
            System.out.printf("%d. %s\n", i + 1, possibleTargets.get(i).getName());
        }
        
        System.out.print("Selection: ");
        int selection = scanner.nextInt();
        
        if (selection < 1 || selection > possibleTargets.size()) {
            System.out.println("Invalid selection. The virus is discarded.");
            return;
        }
        
        Player target = possibleTargets.get(selection - 1);
        List<Organ> targetOrgans = organsOnTable.get(target);
        
        // Filter organs of the same color as the virus
        List<Organ> compatibleOrgans = new ArrayList<>();
        for (Organ organ : targetOrgans) {
            if (organ.getColor() == virus.getColor() && !organ.isInfected()) {
                compatibleOrgans.add(organ);
            }
        }
        
        if (compatibleOrgans.isEmpty()) {
            System.out.println("There are no compatible organs to infect. The virus is discarded.");
            return;
        }
        
        System.out.println("\nSelect an organ to infect:");
        for (int i = 0; i < compatibleOrgans.size(); i++) {
            System.out.printf("%d. %s\n", i + 1, compatibleOrgans.get(i).toString());
        }
        
        System.out.print("Selection: ");
        int organSelection = scanner.nextInt();
        
        if (organSelection < 1 || organSelection > compatibleOrgans.size()) {
            System.out.println("Invalid selection. The virus is discarded.");
            return;
        }
        
        Organ targetOrgan = compatibleOrgans.get(organSelection - 1);
        targetOrgan.infect();
        System.out.println("You have infected an organ of " + target.getName() + "!");
    }

    private void useSpecialTreatment(int currentPlayer) {
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
            return;
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
            return;
        }
        
        selection -= 1;

        if (selection >= 0 && selection < treatments.size()) {
            SpecialTreatment treatment = treatments.get(selection);
            player.getHand().remove(treatment);
            treatment.apply(player, players);
            waitForEnter();
        } else {
            System.out.println("Invalid selection.");
            waitForEnter();
        }
    }

    private void viewCurrentHand(int currentPlayer) {
        System.out.println("\nYour hand:");
        showPlayerHand(currentPlayer);
        waitForEnter();
    }
    
    private void reshuffleDeck() {
        System.out.println("Reshuffling the deck...");
        
        // Collect discarded cards (future implementation)
        // For now, just create a new deck
        initializeDeck();
        
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
        System.out.print("Enter the number of players (2-6): ");
        Scanner scanner = new Scanner(System.in);
        int numPlayers = scanner.nextInt();

        if (numPlayers < 2 || numPlayers > 6) {
            System.out.println("Invalid number of players. Using 3 players by default.");
            numPlayers = 3;
        }

        VirusBoard game = new VirusBoard(numPlayers);
        game.play();
    }
}