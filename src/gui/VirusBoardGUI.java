package gui; 
import enums.Color; 
import enums.TreatmentType; 
import interfaces.SpecialTreatment; 
import model.*; 
import javax.swing.*; 
import java.awt.*; 
import java.awt.event.ActionEvent; 
import java.awt.event.ActionListener; 
import java.util.*; 
import java.util.List; 


public class VirusBoardGUI extends JFrame {
    private static final int NUM_PLAYERS = 2;
    private static final int REQUIRED_HAND_SIZE = 3;
    
    // Game components
    private List<Card> deck;
    private List<Card> discardPile;
    private List<Player> players;
    private Map<Player, List<Organ>> organsOnTable;
    private int currentPlayerIndex = 0;
    
    // GUI components
    private JPanel mainPanel;
    private JPanel gameBoard;
    private JPanel currentPlayerPanel;
    private JPanel opponentPanel;
    private JPanel actionPanel;
    private JLabel statusLabel;
    private JLabel currentPlayerLabel;
    private JScrollPane handScrollPane;
    private java.awt.Color[] playerColors = {java.awt.Color.LIGHT_GRAY, java.awt.Color.CYAN};
    
    public VirusBoardGUI() {
        initializeGame();
        setupGUI();
        updateDisplay();
    }
    
    private void initializeGame() {
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
        
        // Add medicines of different colors
        for (Color color : Arrays.asList(Color.RED, Color.GREEN, Color.BLUE, Color.YELLOW)) {
            for (int i = 0; i < 2; i++) {
                deck.add(new Medicina(color));
            }
        }

        Collections.shuffle(deck);
    }
    
    private void initializePlayers() {
        players = new ArrayList<>();
        for (int i = 0; i < NUM_PLAYERS; i++) {
            Player player = new Player("Player " + (i + 1));
            players.add(player);
            organsOnTable.put(player, new ArrayList<>());
        }
    }
    
    private void dealInitialCards() {
        for (Player player : players) {
            for (int i = 0; i < REQUIRED_HAND_SIZE; i++) {
                if (!deck.isEmpty()) {
                    player.getHand().add(deck.remove(0));
                }
            }
        }
    }
    
    private void setupGUI() {
        setTitle("Virus Board Game");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1200, 800);
        setLocationRelativeTo(null);
        
        mainPanel = new JPanel(new BorderLayout());
        
        // Status panel
        JPanel statusPanel = new JPanel(new FlowLayout());
        currentPlayerLabel = new JLabel("Current Player: " + getCurrentPlayer().getName());
        currentPlayerLabel.setFont(new Font("Arial", Font.BOLD, 16));
        statusLabel = new JLabel("Welcome to Virus Board Game!");
        statusPanel.add(currentPlayerLabel);
        statusPanel.add(Box.createHorizontalStrut(20));
        statusPanel.add(statusLabel);
        
        // Game board (organs on table)
        gameBoard = new JPanel();
        gameBoard.setLayout(new BoxLayout(gameBoard, BoxLayout.Y_AXIS));
        gameBoard.setBorder(BorderFactory.createTitledBorder("Organs on Table"));
        
        // Current player's hand panel
        currentPlayerPanel = new JPanel();
        currentPlayerPanel.setBorder(BorderFactory.createTitledBorder("Your Hand"));
        handScrollPane = new JScrollPane(currentPlayerPanel);
        handScrollPane.setPreferredSize(new Dimension(1200, 150));
        
        // Opponent info panel
        opponentPanel = new JPanel();
        opponentPanel.setBorder(BorderFactory.createTitledBorder("Opponent"));
        
        // Action buttons panel
        actionPanel = new JPanel(new FlowLayout());
        setupActionButtons();
        
        // Layout
        mainPanel.add(statusPanel, BorderLayout.NORTH);
        mainPanel.add(gameBoard, BorderLayout.CENTER);
        
        JPanel bottomPanel = new JPanel(new BorderLayout());
        bottomPanel.add(opponentPanel, BorderLayout.NORTH);
        bottomPanel.add(handScrollPane, BorderLayout.CENTER);
        bottomPanel.add(actionPanel, BorderLayout.SOUTH);
        
        mainPanel.add(bottomPanel, BorderLayout.SOUTH);
        
        add(mainPanel);
    }
    
    private void setupActionButtons() {
        JButton playCardBtn = new JButton("Play Card");
        JButton useSpecialBtn = new JButton("Use Special Treatment");
        JButton useMedicineBtn = new JButton("Use Medicine");
        JButton discardBtn = new JButton("Discard Card");
        JButton endTurnBtn = new JButton("End Turn");
        
        playCardBtn.addActionListener(e -> playSelectedCard());
        useSpecialBtn.addActionListener(e -> useSpecialTreatment());
        useMedicineBtn.addActionListener(e -> useMedicine());
        discardBtn.addActionListener(e -> discardSelectedCard());
        endTurnBtn.addActionListener(e -> endTurn());
        
        actionPanel.add(playCardBtn);
        actionPanel.add(useSpecialBtn);
        actionPanel.add(useMedicineBtn);
        actionPanel.add(discardBtn);
        actionPanel.add(endTurnBtn);
    }
    
    private void updateDisplay() {
        updateGameBoard();
        updateCurrentPlayerHand();
        updateOpponentInfo();
        updateStatusLabel();
        currentPlayerLabel.setText("Current Player: " + getCurrentPlayer().getName());
        
        // Update panel backgrounds to show current player
        currentPlayerPanel.setBackground(playerColors[currentPlayerIndex]);
        
        // Check for special cards and show notifications
        checkForSpecialCards();
        
        repaint();
    }
    
    private void checkForSpecialCards() {
        Player currentPlayer = getCurrentPlayer();
        boolean hasReikan = false;
        boolean hasMedicine = false;
        
        for (Card card : currentPlayer.getHand()) {
            if (card instanceof Reikan) {
                hasReikan = true;
            }
            if (card instanceof Medicina) {
                hasMedicine = true;
            }
        }
        
        // Show Reikan notification if player has it
        if (hasReikan) {
            showReikanNotification();
        }
        
        // Show medicine notification if player has medicine and infected organs
        if (hasMedicine && hasInfectedOrgans(currentPlayer)) {
            showMedicineNotification();
        }
    }
    
    private boolean hasInfectedOrgans(Player player) {
        List<Organ> organs = organsOnTable.get(player);
        for (Organ organ : organs) {
            if (organ.isInfected()) {
                return true;
            }
        }
        return false;
    }
    
    private void showMedicineNotification() {
        JOptionPane.showMessageDialog(this, 
            "💊 ¡Tienes medicina disponible! 💊\n" +
            "Puedes curar tus órganos infectados usando el botón 'Use Medicine'.",
            "Medicina Disponible",
            JOptionPane.INFORMATION_MESSAGE);
    }
    
    private void showReikanNotification() {
        // Create a custom dialog for Reikan notification
        JDialog reikanDialog = new JDialog(this, "¡Carta Especial Detectada!", true);
        reikanDialog.setSize(450, 350);
        reikanDialog.setLocationRelativeTo(this);
        
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        panel.setBackground(new java.awt.Color(255, 200, 255)); // Multicolor background
        
        // Title
        JLabel titleLabel = new JLabel("🎴 CARTA ESPECIAL REIKAN 🎴");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 20));
        titleLabel.setForeground(java.awt.Color.MAGENTA);
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        // Description
        JLabel descLabel = new JLabel("<html><center>¡Tienes la carta especial Reikan!<br><br>" +
                                     "Esta poderosa carta te permite:<br>" +
                                     "• Robar cualquier carta específica del oponente<br>" +
                                     "• Ver todas las cartas disponibles<br>" +
                                     "• Elegir exactamente la que necesitas<br><br>" +
                                     "¡Úsala sabiamente para obtener ventaja!<br><br>" +
                                     "Usa el botón 'Use Special Treatment' para jugarla.</center></html>");
        descLabel.setFont(new Font("Arial", Font.PLAIN, 13));
        descLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        // Icon/Symbol
        JLabel iconLabel = new JLabel("🌟✨🎯✨🌟");
        iconLabel.setFont(new Font("Arial", Font.BOLD, 28));
        iconLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        // Button
        JButton okButton = new JButton("¡Entendido!");
        okButton.setFont(new Font("Arial", Font.BOLD, 14));
        okButton.setBackground(java.awt.Color.MAGENTA);
        okButton.setForeground(java.awt.Color.WHITE);
        okButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        okButton.addActionListener(e -> reikanDialog.dispose());
        
        // Add components
        panel.add(iconLabel);
        panel.add(Box.createVerticalStrut(10));
        panel.add(titleLabel);
        panel.add(Box.createVerticalStrut(15));
        panel.add(descLabel);
        panel.add(Box.createVerticalStrut(20));
        panel.add(okButton);
        
        reikanDialog.add(panel);
        reikanDialog.setVisible(true);
    }
    
    private void updateGameBoard() {
        gameBoard.removeAll();
        
        for (int i = 0; i < players.size(); i++) {
            Player player = players.get(i);
            List<Organ> organs = organsOnTable.get(player);
            
            JPanel playerOrganPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
            playerOrganPanel.setBorder(BorderFactory.createTitledBorder(player.getName() + "'s Organs"));
            playerOrganPanel.setBackground(playerColors[i]);
            
            if (organs.isEmpty()) {
                playerOrganPanel.add(new JLabel("No organs"));
            } else {
                for (Organ organ : organs) {
                    JPanel organCard = createOrganCard(organ);
                    playerOrganPanel.add(organCard);
                }
            }
            
            gameBoard.add(playerOrganPanel);
        }
        
        gameBoard.revalidate();
    }
    
    private void updateCurrentPlayerHand() {
        currentPlayerPanel.removeAll();
        currentPlayerPanel.setLayout(new FlowLayout(FlowLayout.LEFT));
        
        Player currentPlayer = getCurrentPlayer();
        for (int i = 0; i < currentPlayer.getHand().size(); i++) {
            Card card = currentPlayer.getHand().get(i);
            JPanel cardPanel = createCardPanel(card, i);
            currentPlayerPanel.add(cardPanel);
        }
        
        currentPlayerPanel.revalidate();
    }
    
    private void updateOpponentInfo() {
        opponentPanel.removeAll();
        opponentPanel.setLayout(new FlowLayout());
        
        Player opponent = getOpponent();
        JLabel opponentInfo = new JLabel(String.format("%s: %d cards in hand, %d organs on table", 
            opponent.getName(), 
            opponent.getHand().size(), 
            organsOnTable.get(opponent).size()));
        opponentInfo.setFont(new Font("Arial", Font.PLAIN, 14));
        
        opponentPanel.add(opponentInfo);
        opponentPanel.revalidate();
    }
    
    private void updateStatusLabel() {
        statusLabel.setText("Deck: " + deck.size() + " cards | Discard: " + discardPile.size() + " cards");
    }
    
    private JPanel createCardPanel(Card card, int index) {
        JPanel cardPanel = new JPanel();
        cardPanel.setLayout(new BoxLayout(cardPanel, BoxLayout.Y_AXIS));
        cardPanel.setBorder(BorderFactory.createRaisedBevelBorder());
        cardPanel.setPreferredSize(new Dimension(100, 80));
        cardPanel.setBackground(getCardColor(card));
        
        JLabel typeLabel = new JLabel(getCardType(card), SwingConstants.CENTER);
        typeLabel.setFont(new Font("Arial", Font.BOLD, 10));
        typeLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        JLabel colorLabel = new JLabel(card.getColor().toString(), SwingConstants.CENTER);
        colorLabel.setFont(new Font("Arial", Font.PLAIN, 9));
        colorLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        JLabel indexLabel = new JLabel(String.valueOf(index + 1), SwingConstants.CENTER);
        indexLabel.setFont(new Font("Arial", Font.BOLD, 12));
        indexLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        cardPanel.add(Box.createVerticalGlue());
        cardPanel.add(typeLabel);
        cardPanel.add(colorLabel);
        if (card instanceof Organ && ((Organ) card).isInfected()) {
            JLabel infectedLabel = new JLabel("INFECTED", SwingConstants.CENTER);
            infectedLabel.setFont(new Font("Arial", Font.BOLD, 8));
            infectedLabel.setForeground(java.awt.Color.RED);
            infectedLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
            cardPanel.add(infectedLabel);
        }
        cardPanel.add(indexLabel);
        cardPanel.add(Box.createVerticalGlue());
        
        return cardPanel;
    }
    
    private JPanel createOrganCard(Organ organ) {
        JPanel organPanel = new JPanel();
        organPanel.setLayout(new BoxLayout(organPanel, BoxLayout.Y_AXIS));
        organPanel.setBorder(BorderFactory.createRaisedBevelBorder());
        organPanel.setPreferredSize(new Dimension(80, 60));
        organPanel.setBackground(getCardColor(organ));
        
        JLabel typeLabel = new JLabel("ORGAN", SwingConstants.CENTER);
        typeLabel.setFont(new Font("Arial", Font.BOLD, 10));
        typeLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        JLabel colorLabel = new JLabel(organ.getColor().toString(), SwingConstants.CENTER);
        colorLabel.setFont(new Font("Arial", Font.PLAIN, 9));
        colorLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        organPanel.add(Box.createVerticalGlue());
        organPanel.add(typeLabel);
        organPanel.add(colorLabel);
        
        if (organ.isInfected()) {
            JLabel infectedLabel = new JLabel("INFECTED", SwingConstants.CENTER);
            infectedLabel.setFont(new Font("Arial", Font.BOLD, 8));
            infectedLabel.setForeground(java.awt.Color.RED);
            infectedLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
            organPanel.add(infectedLabel);
        }
        
        organPanel.add(Box.createVerticalGlue());
        
        return organPanel;
    }
    
    private java.awt.Color getCardColor(Card card) {
        switch (card.getColor()) {
            case RED: return new java.awt.Color(255, 200, 200);
            case GREEN: return new java.awt.Color(200, 255, 200);
            case BLUE: return new java.awt.Color(200, 200, 255);
            case YELLOW: return new java.awt.Color(255, 255, 200);
            case MULTICOLOR: return new java.awt.Color(255, 200, 255);
            default: return java.awt.Color.WHITE;
        }
    }
    
    private String getCardType(Card card) {
        if (card instanceof Organ) return "ORGAN";
        if (card instanceof Virus) return "VIRUS";
        if (card instanceof Medicina) return "MEDICINA";
        if (card instanceof SpecialTreatment) {
            SpecialTreatment treatment = (SpecialTreatment) card;
            if (treatment instanceof Reikan) return "REIKAN";
            return treatment.getType().toString();
        }
        return "CARD";
    }
    
    private Player getCurrentPlayer() {
        return players.get(currentPlayerIndex);
    }
    
    private Player getOpponent() {
        return players.get((currentPlayerIndex + 1) % players.size());
    }
    
    private void playSelectedCard() {
        String input = JOptionPane.showInputDialog(this, 
            "Enter the number of the card to play (1-" + getCurrentPlayer().getHand().size() + "):");
        
        if (input == null || input.trim().isEmpty()) return;
        
        try {
            int cardIndex = Integer.parseInt(input.trim()) - 1;
            Player currentPlayer = getCurrentPlayer();
            
            if (cardIndex < 0 || cardIndex >= currentPlayer.getHand().size()) {
                JOptionPane.showMessageDialog(this, "Invalid card number!");
                return;
            }
            
            Card selectedCard = currentPlayer.getHand().get(cardIndex);
            
            if (selectedCard instanceof Organ) {
                playOrgan(currentPlayer, (Organ) selectedCard);
                endTurn();
            } else if (selectedCard instanceof Virus) {
                if (playVirus(currentPlayer, (Virus) selectedCard)) {
                    endTurn();
                }
            } else {
                JOptionPane.showMessageDialog(this, "Use 'Use Special Treatment' button for special cards or 'Use Medicine' for medicine cards!");
            }
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Please enter a valid number!");
        }
    }
    
    private void playOrgan(Player player, Organ organ) {
        player.getHand().remove(organ);
        organsOnTable.get(player).add(organ);
        JOptionPane.showMessageDialog(this, "You placed a " + organ.getColor() + " organ on the table!");
    }
    
    private boolean playVirus(Player player, Virus virus) {
        Player opponent = getOpponent();
        List<Organ> opponentOrgans = organsOnTable.get(opponent);
        
        List<Organ> healthyOrgans = new ArrayList<>();
        for (Organ organ : opponentOrgans) {
            if (!organ.isInfected()) {
                healthyOrgans.add(organ);
            }
        }
        
        if (healthyOrgans.isEmpty()) {
            JOptionPane.showMessageDialog(this, "The opponent has no healthy organs to infect!");
            return false;
        }
        
        String[] organOptions = new String[healthyOrgans.size()];
        for (int i = 0; i < healthyOrgans.size(); i++) {
            organOptions[i] = (i + 1) + ". " + healthyOrgans.get(i).getColor() + " ORGAN";
        }
        
        String choice = (String) JOptionPane.showInputDialog(this, 
            "Select an organ to infect:", 
            "Play Virus", 
            JOptionPane.QUESTION_MESSAGE, 
            null, 
            organOptions, 
            organOptions[0]);
        
        if (choice != null) {
            int organIndex = Integer.parseInt(choice.substring(0, 1)) - 1;
            Organ targetOrgan = healthyOrgans.get(organIndex);
            
            player.getHand().remove(virus);
            targetOrgan.infect();
            JOptionPane.showMessageDialog(this, "You infected " + opponent.getName() + "'s " + targetOrgan.getColor() + " organ!");
            return true;
        }
        
        return false;
    }
    
    private void useMedicine() {
        Player currentPlayer = getCurrentPlayer();
        List<Medicina> medicines = new ArrayList<>();
        List<Integer> medicineIndices = new ArrayList<>();
        
        for (int i = 0; i < currentPlayer.getHand().size(); i++) {
            Card card = currentPlayer.getHand().get(i);
            if (card instanceof Medicina) {
                medicines.add((Medicina) card);
                medicineIndices.add(i);
            }
        }
        
        if (medicines.isEmpty()) {
            JOptionPane.showMessageDialog(this, "You don't have any medicine cards!");
            return;
        }
        
        String[] medicineOptions = new String[medicines.size()];
        for (int i = 0; i < medicines.size(); i++) {
            medicineOptions[i] = (i + 1) + ". MEDICINA (" + medicines.get(i).getColor() + ")";
        }
        
        String choice = (String) JOptionPane.showInputDialog(this, 
            "Select a medicine to use:", 
            "Use Medicine", 
            JOptionPane.QUESTION_MESSAGE, 
            null, 
            medicineOptions, 
            medicineOptions[0]);
        
        if (choice != null) {
            int medicineIndex = Integer.parseInt(choice.substring(0, 1)) - 1;
            Medicina medicine = medicines.get(medicineIndex);
            
            // Use the medicine to heal an organ
            if (useMedicineToHeal(medicine)) {
                currentPlayer.getHand().remove(medicine);
                discardPile.add(medicine);
                endTurn();
            }
        }
    }
    
    private boolean useMedicineToHeal(Medicina medicine) {
        Player currentPlayer = getCurrentPlayer();
        List<Organ> playerOrgans = organsOnTable.get(currentPlayer);
        
        List<Organ> infectedOrgans = new ArrayList<>();
        for (Organ organ : playerOrgans) {
            if (organ.isInfected()) {
                infectedOrgans.add(organ);
            }
        }
        
        if (infectedOrgans.isEmpty()) {
            JOptionPane.showMessageDialog(this, "You don't have any infected organs to heal!");
            return false;
        }
        
        String[] organOptions = new String[infectedOrgans.size()];
        for (int i = 0; i < infectedOrgans.size(); i++) {
            organOptions[i] = (i + 1) + ". " + infectedOrgans.get(i).getColor() + " ORGAN (INFECTED)";
        }
        
        String choice = (String) JOptionPane.showInputDialog(this, 
            "Select an infected organ to heal:", 
            "Heal Organ", 
            JOptionPane.QUESTION_MESSAGE, 
            null, 
            organOptions, 
            organOptions[0]);
        
        if (choice != null) {
            int organIndex = Integer.parseInt(choice.substring(0, 1)) - 1;
            Organ organToHeal = infectedOrgans.get(organIndex);
            
            organToHeal.heal();
            
            // Show enhanced healing confirmation
            showHealingSuccessDialog(organToHeal);
            return true;
        }
        
        return false;
    }
    
    private void showHealingSuccessDialog(Organ healedOrgan) {
        JDialog healDialog = new JDialog(this, "¡Órgano Curado!", true);
        healDialog.setSize(400, 300);
        healDialog.setLocationRelativeTo(this);
        
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        panel.setBackground(new java.awt.Color(200, 255, 200)); // Light green background
        
        // Title
        JLabel titleLabel = new JLabel("💊 ¡CURACIÓN EXITOSA! 💊");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 18));
        titleLabel.setForeground(new java.awt.Color(0, 128, 0));
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        // Icon
        JLabel iconLabel = new JLabel("✨🏥✨");
        iconLabel.setFont(new Font("Arial", Font.BOLD, 24));
        iconLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        // Description
        JLabel descLabel = new JLabel("<html><center>Has curado exitosamente tu órgano " + 
                                     healedOrgan.getColor() + "!<br><br>" +
                                     "El órgano ya no está infectado y<br>" +
                                     "puede volver a funcionar normalmente.<br><br>" +
                                     "¡Excelente trabajo, doctor!</center></html>");
        descLabel.setFont(new Font("Arial", Font.PLAIN, 14));
        descLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        // Button
        JButton okButton = new JButton("¡Perfecto!");
        okButton.setFont(new Font("Arial", Font.BOLD, 14));
        okButton.setBackground(new java.awt.Color(0, 128, 0));
        okButton.setForeground(java.awt.Color.WHITE);
        okButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        okButton.addActionListener(e -> healDialog.dispose());
        
        // Add components
        panel.add(iconLabel);
        panel.add(Box.createVerticalStrut(10));
        panel.add(titleLabel);
        panel.add(Box.createVerticalStrut(15));
        panel.add(descLabel);
        panel.add(Box.createVerticalStrut(20));
        panel.add(okButton);
        
        healDialog.add(panel);
        healDialog.setVisible(true);
    }
    
    private void useSpecialTreatment() {
        Player currentPlayer = getCurrentPlayer();
        List<SpecialTreatment> treatments = new ArrayList<>();
        List<Integer> treatmentIndices = new ArrayList<>();
        
        for (int i = 0; i < currentPlayer.getHand().size(); i++) {
            Card card = currentPlayer.getHand().get(i);
            if (card instanceof SpecialTreatment) {
                treatments.add((SpecialTreatment) card);
                treatmentIndices.add(i);
            }
        }
        
        if (treatments.isEmpty()) {
            JOptionPane.showMessageDialog(this, "You don't have any special treatments!");
            return;
        }
        
        String[] treatmentOptions = new String[treatments.size()];
        for (int i = 0; i < treatments.size(); i++) {
            treatmentOptions[i] = (i + 1) + ". " + treatments.get(i).getType();
        }
        
        String choice = (String) JOptionPane.showInputDialog(this, 
            "Select a special treatment to use:", 
            "Use Special Treatment", 
            JOptionPane.QUESTION_MESSAGE, 
            null, 
            treatmentOptions, 
            treatmentOptions[0]);
        
        if (choice != null) {
            int treatmentIndex = Integer.parseInt(choice.substring(0, 1)) - 1;
            SpecialTreatment treatment = treatments.get(treatmentIndex);
            
            currentPlayer.getHand().remove(treatment);
            
            // Apply the treatment using a custom implementation for GUI
            applySpecialTreatmentGUI(treatment, currentPlayer);
            
            endTurn();
        }
    }
    
    private void applySpecialTreatmentGUI(SpecialTreatment treatment, Player currentPlayer) {
        Player opponent = getOpponent();
        
        if (treatment.getType() == TreatmentType.EXCHANGE) {
            // Exchange hands
            List<Card> tempHand = new ArrayList<>(currentPlayer.getHand());
            currentPlayer.getHand().clear();
            currentPlayer.getHand().addAll(opponent.getHand());
            opponent.getHand().clear();
            opponent.getHand().addAll(tempHand);
            JOptionPane.showMessageDialog(this, "Hands exchanged with " + opponent.getName() + "!");
            
        } else if (treatment.getType() == TreatmentType.CONTROL) {
            String[] options = {"Draw random card", "View opponent's hand"};
            String choice = (String) JOptionPane.showInputDialog(this, 
                "Choose control action:", 
                "Control", 
                JOptionPane.QUESTION_MESSAGE, 
                null, 
                options, 
                options[0]);
            
            if (choice != null) {
                if (choice.equals("Draw random card")) {
                    if (!opponent.getHand().isEmpty()) {
                        Random random = new Random();
                        Card stolenCard = opponent.getHand().remove(random.nextInt(opponent.getHand().size()));
                        currentPlayer.getHand().add(stolenCard);
                        JOptionPane.showMessageDialog(this, "You drew: " + getCardType(stolenCard) + " (" + stolenCard.getColor() + ")");
                    } else {
                        JOptionPane.showMessageDialog(this, "Opponent has no cards!");
                    }
                } else {
                    StringBuilder handInfo = new StringBuilder(opponent.getName() + "'s hand:\n");
                    for (int i = 0; i < opponent.getHand().size(); i++) {
                        Card card = opponent.getHand().get(i);
                        handInfo.append((i + 1)).append(". ").append(getCardType(card))
                               .append(" (").append(card.getColor()).append(")\n");
                    }
                    JOptionPane.showMessageDialog(this, handInfo.toString());
                }
            }
            
        } else if (treatment.getType() == TreatmentType.INFORMATION) {
            // Reikan - steal specific card
            if (opponent.getHand().isEmpty()) {
                JOptionPane.showMessageDialog(this, "Opponent has no cards to steal!");
                return;
            }
            
            String[] cardOptions = new String[opponent.getHand().size()];
            for (int i = 0; i < opponent.getHand().size(); i++) {
                Card card = opponent.getHand().get(i);
                cardOptions[i] = (i + 1) + ". " + getCardType(card) + " (" + card.getColor() + ")";
            }
            
            String choice = (String) JOptionPane.showInputDialog(this, 
                "Select a card to steal from " + opponent.getName() + ":", 
                "Information", 
                JOptionPane.QUESTION_MESSAGE, 
                null, 
                cardOptions, 
                cardOptions[0]);
            
            if (choice != null) {
                int cardIndex = Integer.parseInt(choice.substring(0, 1)) - 1;
                Card stolenCard = opponent.getHand().remove(cardIndex);
                currentPlayer.getHand().add(stolenCard);
                JOptionPane.showMessageDialog(this, "You stole: " + getCardType(stolenCard) + " (" + stolenCard.getColor() + ")");
            }
        }
    }
    
    private void discardSelectedCard() {
        String input = JOptionPane.showInputDialog(this, 
            "Enter the number of the card to discard (1-" + getCurrentPlayer().getHand().size() + "):");
        
        if (input == null || input.trim().isEmpty()) return;
        
        try {
            int cardIndex = Integer.parseInt(input.trim()) - 1;
            Player currentPlayer = getCurrentPlayer();
            
            if (cardIndex < 0 || cardIndex >= currentPlayer.getHand().size()) {
                JOptionPane.showMessageDialog(this, "Invalid card number!");
                return;
            }
            
            Card discardedCard = currentPlayer.getHand().remove(cardIndex);
            discardPile.add(discardedCard);
            JOptionPane.showMessageDialog(this, "You discarded: " + getCardType(discardedCard) + " (" + discardedCard.getColor() + ")");
            
            endTurn();
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Please enter a valid number!");
        }
    }
    
    private void endTurn() {
        drawCardFromDeck();
        currentPlayerIndex = (currentPlayerIndex + 1) % players.size();
        
        if (checkWinCondition()) {
            return;
        }
        
        updateDisplay();
    }
    
    private void drawCardFromDeck() {
        if (deck.isEmpty() && !discardPile.isEmpty()) {
            deck.addAll(discardPile);
            discardPile.clear();
            Collections.shuffle(deck);
            JOptionPane.showMessageDialog(this, "Deck reshuffled!");
        }
        
        if (!deck.isEmpty()) {
            Card drawnCard = deck.remove(0);
            getCurrentPlayer().getHand().add(drawnCard);
        }
    }
    
    private boolean checkWinCondition() {
        for (int i = 0; i < players.size(); i++) {
            Player player = players.get(i);
            List<Organ> organs = organsOnTable.get(player);
            
            Set<Color> healthyOrganColors = new HashSet<>();
            for (Organ organ : organs) {
                if (!organ.isInfected()) {
                    healthyOrganColors.add(organ.getColor());
                }
            }
            
            if (healthyOrganColors.size() >= 4) {
                JOptionPane.showMessageDialog(this, 
                    "🎉 " + player.getName() + " WINS! 🎉\n" +
                    "They have 4 different healthy organs!", 
                    "Game Over", 
                    JOptionPane.INFORMATION_MESSAGE);
                
                int choice = JOptionPane.showConfirmDialog(this, 
                    "Do you want to play again?", 
                    "Play Again?", 
                    JOptionPane.YES_NO_OPTION);
                
                if (choice == JOptionPane.YES_OPTION) {
                    restartGame();
                } else {
                    System.exit(0);
                }
                return true;
            }
        }
        return false;
    }
    
    private void restartGame() {
        currentPlayerIndex = 0;
        organsOnTable.clear();
        discardPile.clear();
        
        for (Player player : players) {
            player.getHand().clear();
        }
        
        initializeGame();
        updateDisplay();
    }
    
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
            } catch (Exception e) {
                e.printStackTrace();
            }
            
            new VirusBoardGUI().setVisible(true);
        });
    }
}
