package org.bugjlu.snlcompiler.syntax;

public class IllegalSyntaxException extends Exception {
    int line;
    String expected, found;
    boolean firstMatch;

    public int getLine() {
        return line;
    }

    public String getExpected() {
        return expected;
    }

    public String getFound() {
        return found;
    }

    public IllegalSyntaxException(int line, String expected, String found) {
        super("syntax analysis error: Wrong syntax at line("+line+"), expected("+expected+"), found("+found+").");
        this.line = line;
        this.expected = expected;
        this.found = found;
    }
    public IllegalSyntaxException() {
        super();
    }
    public IllegalSyntaxException(String msg) {
        super(msg);
    }
}
