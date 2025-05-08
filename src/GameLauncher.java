package main;

import enums.Color;
import java.util.Scanner;

/**
 * Launcher class for the Virus Board Game
 */
public class GameLauncher {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        
        System.out.println("Welcome to Virus Board Game!");
        System.out.println("---------------------------");
        System.out.println("Do you want to use colored text in the terminal?");
        System.out.println("1. Yes (ANSI color codes - works in most modern terminals)");
        System.out.println("2. No (Plain text - works in all terminals)");
        
        int colorChoice = 1;
        try {
            System.out.print("Your choice: ");
            colorChoice = scanner.nextInt();
        } catch (Exception e) {
            System.out.println("Invalid input. Defaulting to ANSI colors.");
        }
        
        if (colorChoice != 1) {
            // Switch to plain text mode
            Color.toggleAnsiCodes();
        }
        
        System.out.println("\nStarting game with 2 players...");
        VirusBoard game = new VirusBoard();
        game.play();
    }
}
