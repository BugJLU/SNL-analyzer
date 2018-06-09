package org.bugjlu.snlcompiler.ui;

import org.bugjlu.snlcompiler.lexical.Dictionary;
import org.bugjlu.snlcompiler.lexical.Token;

import java.util.ArrayList;

public class TokenWriter {
    public static void writeToken(ArrayList<Token> tokens) {
        for (Token t:
             tokens) {
            System.out.print(t.toString()+" ");
            if (t.type.equals(Dictionary.tokenTypeMap.get("\n"))) {
                System.out.println();
            }
        }
        System.out.println();
    }
}
