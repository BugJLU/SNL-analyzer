package org.bugjlu.snlcompiler.lexical;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

public class Dictionary {

    public static HashSet<String> keywords;

    public static HashSet<Character> breakLetters;

    /**
     * @Description:
     *      0       identifier
     *      1       unsigned int
     *      2..m    symbol
     *      m+1..n  reserved words
     */
    public static HashMap<String, Integer> tokenTypeMap;
    public static ArrayList<String> tokenNameMap;

    static String[] kwds = {
            "program", "procedure", "begin", "end",
            "var", "type", "record", "integer", "char", "array", "of",
            "if", "then", "else", "fi", "while", "do", "endwh",
            "read", "write"
    };

    static String[] symbols = {
            "+", "-", "*", "/", "<", ">", "=", "(", ")",
            "[", "]", ",", ".", ";",/* " ", "\t", */"\n",
            ":=", "{", "}", "'", ".."
    };

    static char[] brkls = {
            '+', '-', '*', '/', '<', '>', '=', '(', ')',
            '[', ']', ',', '.', ';', ' ', '\t', '\n',
            '{', '}', '\''
    };


    static {
        keywords = new HashSet<>();
        breakLetters = new HashSet<>();
        tokenTypeMap = new HashMap<>();
        tokenNameMap = new ArrayList<>();

        for (String kwd :
                kwds) {
            keywords.add(kwd);
        }

        for (char c:
                brkls) {
            breakLetters.add(c);
        }

        tokenTypeMap.put("id", 0);
        tokenNameMap.add("id");
        tokenTypeMap.put("uint", 1);
        tokenNameMap.add("uint");
        int i = 2;
        for (String s :
                symbols) {
            tokenTypeMap.put(s, i);
            tokenNameMap.add(s);
            i++;
        }
        for (String s :
                kwds) {
            tokenTypeMap.put(s, i);
            tokenNameMap.add(s);
            i++;
        }
    }
}
