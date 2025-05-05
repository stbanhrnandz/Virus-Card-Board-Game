package enums;

// Enumeration to represent colors with their ANSI codes

public enum Color {
    RED("\033[31m"),
    GREEN("\033[32m"),
    BLUE("\033[34m"),
    YELLOW("\033[33m"),
    MULTICOLOR("\033[35m"),
    RESET("\033[0m");

    private final String code;

    Color(String code) {
        this.code = code;
    }

    public String getCode() {
        return code;
    }
}