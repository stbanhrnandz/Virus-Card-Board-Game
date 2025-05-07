package enums;

// Enumeration to represent colors with their ANSI codes - made more compatible

public enum Color {
    RED("[RED]"),
    GREEN("[GREEN]"),
    BLUE("[BLUE]"),
    YELLOW("[YELLOW]"),
    MULTICOLOR("[MULTI]"),
    RESET("[RESET]");
    
    // Alternative ANSI codes for terminals that support them
    private static final String ANSI_RED = "\033[31m";
    private static final String ANSI_GREEN = "\033[32m";
    private static final String ANSI_BLUE = "\033[34m";
    private static final String ANSI_YELLOW = "\033[33m";
    private static final String ANSI_PURPLE = "\033[35m";
    private static final String ANSI_RESET = "\033[0m";
    
    private static boolean useAnsiCodes = true;

    private final String text;

    Color(String text) {
        this.text = text;
    }

    public String getCode() {
        if (useAnsiCodes) {
            switch (this) {
                case RED:
                    return ANSI_RED;
                case GREEN:
                    return ANSI_GREEN;
                case BLUE:
                    return ANSI_BLUE;
                case YELLOW:
                    return ANSI_YELLOW;
                case MULTICOLOR:
                    return ANSI_PURPLE;
                case RESET:
                    return ANSI_RESET;
                default:
                    return "";
            }
        } else {
            return text;
        }
    }
    
    // Method to toggle between ANSI codes and plain text
    public static void toggleAnsiCodes() {
        useAnsiCodes = !useAnsiCodes;
        System.out.println("Color mode changed to: " + (useAnsiCodes ? "ANSI codes" : "Plain text"));
    }
    
    @Override
    public String toString() {
        return text;
    }
}
