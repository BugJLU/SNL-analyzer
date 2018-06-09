package org.bugjlu.snlcompiler.ui;

import org.bugjlu.snlcompiler.lexical.IllegalWordException;
import org.bugjlu.snlcompiler.lexical.Parser;
import org.bugjlu.snlcompiler.lexical.Token;
import org.bugjlu.snlcompiler.syntax.Analyzer;
import org.bugjlu.snlcompiler.syntax.IllegalSyntaxException;
import org.bugjlu.snlcompiler.syntax.TreeNode;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;

public class Main {

    /**
     * @Description:
     *      0   origin
     *      1   wait for exec
     *      2   finish
     */
    static int state = 0;

    static String[][] shortArg = {
            {"h", "Show all usages."},
            {"l", "Do lexical analysis."},
            {"s", "Do syntax analysis."},
            {"lo", "Do lexical analysis and output details."},
            {"so", "Do syntax analysis and output details."}
    };

    static String[][] longArg = {
            {"help", "Show all usages."},
            {"lexical", "Do lexical analysis."},
            {"syntax", "Do syntax analysis."},
            {"lexical-output", "Do lexical analysis and output details."},
            {"syntax-output", "Do syntax analysis and output details."}
    };

    static String errHelp = "use option '-h' for help";
    static void errOut(String msg) {
        System.err.println("snl error: "+msg);
        System.err.println(errHelp);
    }

    static boolean lex, stx, lot, sot;

    static BufferedReader inf;

    static {
        lex = false;
        stx = false;
        lot = false;
        sot = false;
        inf = null;
    }

    public static void main(String[] args) {
        checkArgs(args);
        switch (state) {
            case 0:
                errOut("no input file");
                return;
            case 1:
                break;
            case 2:
                return;
        }
        if (!lex && !stx) {
            lex = stx = true;
        }
        try {
            ArrayList<Token> tokens = null;
            if (lex) {
                Parser parser = new Parser(inf);
                tokens = parser.parseAll();
                if (lot) {
                    TokenWriter.writeToken(tokens);
                }
            }
            if (stx) {
                Analyzer analyzer = new Analyzer(tokens);
                TreeNode root = analyzer.analyzeAll();
                if (sot) {
                    TreeWriter.writeTree(root);
                }
            }

        } catch (IOException e) {
            errOut("cannot read from input file");
            return;
        } catch (IllegalWordException e) {
            System.err.println(e.getMessage());
        } catch (IllegalSyntaxException e) {
            System.err.println(e.getMessage());
//            e.printStackTrace();
        }
    }
    
    public static void checkArgs(String[] args) {
        for (String arg :
                args) {
            if (arg.equals("-h") || arg.equals("--help")) {
                help();
                state = 2;
                break;
            } else if (arg.equals("-l") || arg.equals("--lexical")) {
                state = 0;
                lex = true;
            } else if (arg.equals("-s") || arg.equals("--syntax")) {
                state = 0;
                lex = true;
                stx = true;
            } else if (arg.equals("-lo") || arg.equals("--lexical-output")) {
                state = 0;
                lex = true;
                lot = true;
            } else if (arg.equals("-so") || arg.equals("--syntax-output")) {
                state = 0;
                lex = true;
                stx = true;
                sot = true;
            } else if (arg.charAt(0) == '-') {
                errOut("unknown option '"+arg+"'");
                state = 2;
                break;
            } else {
                try {
                    inf = new BufferedReader(new FileReader(arg));
                } catch (FileNotFoundException e) {
                    errOut("cannot open file: '"+arg+"'");
                    state = 2;
                    break;
                }
                state = 1;
                break;
            }
        }
    }

    public static String generateHelpLine(String a, String b) {
        if (a.length() < 8) {
            return a+"\t\t\t"+b;
        } else if (a.length() < 16) {
            return a+"\t\t"+b;
        } else if (a.length() < 24) {
            return a+"\t"+b;
        } else {
            return a+"\n\t\t\t"+b;
        }
    }
    
    public static void help() {
        System.out.println("SNL compiler 1.0\n");
        System.out.println("USAGE: snl [options] <input>\n");
        System.out.println("OPTIONS: ");
        for (String[] tup :
                shortArg) {
            String a = "-"+tup[0];
            System.out.println(generateHelpLine(a, tup[1]));
        }
        for (String[] tup :
                longArg) {
            String a = "--"+tup[0];
            System.out.println(generateHelpLine(a, tup[1]));
        }
        System.out.println();
    }
}
