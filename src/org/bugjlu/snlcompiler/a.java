package org.bugjlu.snlcompiler;

import org.bugjlu.snlcompiler.lexical.IllegalWordException;
import org.bugjlu.snlcompiler.lexical.Parser;
import org.bugjlu.snlcompiler.lexical.Token;
import org.bugjlu.snlcompiler.syntax.Analyzer;
import org.bugjlu.snlcompiler.syntax.IllegalSyntaxException;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;

public class a {
    public static void main(String[] args) {
        try {
            BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream("/Users/mac/Desktop/SNL/test/test2.snl")));
            Parser parser = new Parser(br);
            ArrayList<Token> tokens = parser.parseAll();
//            for (Token t :
//                    a) {
//                System.out.println(t.toString());
//            }
            Analyzer analyzer = new Analyzer(tokens);
            analyzer.analyzeAll();
        } catch (IllegalWordException e) {
            System.out.println(e.getMessage());
        } catch (IOException e) {
            e.printStackTrace();
        } catch (IllegalSyntaxException e) {
            System.out.println(e.getMessage());
            e.printStackTrace();
        }

    }
}
