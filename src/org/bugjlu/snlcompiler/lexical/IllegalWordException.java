package org.bugjlu.snlcompiler.lexical;

public class IllegalWordException extends Exception {

    int line, pos, wrongChar;

    public int getLine() {
        return line;
    }

    public int getPos() {
        return pos;
    }

    public int getWrongChar() {
        return wrongChar;
    }

    public IllegalWordException(int line, int pos, char wrongChar) {
        super("lexical analysis error: Wrong word at line("+line+"), pos("+pos+"), which is("+wrongChar+")");
        this.line = line;
        this.pos = pos;
        this.wrongChar = wrongChar;
    }
}
