package model;

import enums.Color;
import enums.TreatmentType;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.InputMismatchException;

public class Medicina extends BaseTreatment {
    
    public Medicina(Color color) {
        super(color, TreatmentType.INFORMATION);
    }
    
    @Override
    public void apply(Player currentPlayer, List<Player> players) {
        // Buscar órganos infectados del jugador actual
        List<Organ> infectedOrgans = new ArrayList<>();
        
        // Necesitamos acceso a organsOnTable, pero no lo tenemos aquí
        // Por ahora, asumimos que se puede curar cualquier órgano infectado
        // Esta implementación necesitará ser ajustada cuando se integre con el juego principal
        
        System.out.println("Medicine card played! This card can cure infected organs.");
        System.out.println("Note: Implementation needs to be integrated with the main game board.");
    }
    
    // Método para curar órganos (será llamado desde el juego principal)
    public boolean healOrgan(Player player, List<Organ> playerOrgans) {
        List<Organ> infectedOrgans = new ArrayList<>();
        
        // Encontrar órganos infectados
        for (Organ organ : playerOrgans) {
            if (organ.isInfected()) {
                infectedOrgans.add(organ);
            }
        }
        
        if (infectedOrgans.isEmpty()) {
            System.out.println("You don't have any infected organs to heal.");
            return false;
        }
        
        System.out.println("\nSelect an infected organ to heal:");
        for (int i = 0; i < infectedOrgans.size(); i++) {
            System.out.printf("%d. %s\n", i + 1, infectedOrgans.get(i).toString());
        }
        
        Scanner scanner = new Scanner(System.in);
        try {
            System.out.print("Selection (0 to cancel): ");
            int selection = scanner.nextInt();
            
            if (selection == 0) {
                return false;
            }
            
            if (selection >= 1 && selection <= infectedOrgans.size()) {
                Organ organToHeal = infectedOrgans.get(selection - 1);
                organToHeal.heal();
                System.out.println("You have healed your " + organToHeal.getColor() + " organ!");
                return true;
            } else {
                System.out.println("Invalid selection.");
                return false;
            }
        } catch (InputMismatchException e) {
            System.out.println("Error: You must enter a valid number.");
            scanner.nextLine();
            return false;
        }
    }
    
    @Override
    public String toString() {
        return getColor().getCode() + "MEDICINA" + Color.RESET.getCode();
    }
}
