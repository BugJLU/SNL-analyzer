package org.bugjlu.snlcompiler.syntax;

public class MultiSyntaxException extends IllegalSyntaxException {

//    int line;
    String syntax;

    public MultiSyntaxException() {
        super();
        super.expected = "";
        super.found = "";
        super.line = 0;
        firstMatch = true;
    }
    public MultiSyntaxException(int line, String syntax) {
        super("syntax analysis error: Wrong syntax at line("+line+"), syntax("+syntax+").");
        super.expected = "";
        super.found = "";
        this.line = line;
        this.syntax = syntax;
        firstMatch = true;
    }
}
