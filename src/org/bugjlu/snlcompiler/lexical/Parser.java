package org.bugjlu.snlcompiler.lexical;

import java.io.*;
import java.util.ArrayList;

public class Parser {

    /**
     * @Description:
     *          0   origin state
     *          1   id state
     *          2   unsigned num state
     *          3   := state
     *          4   comment state
     */
    int state = 0;
    int line = 1;
    int pos = 0;
    StringBuilder wordBuffer;


    private BufferedReader reader;

    private ArrayList<Token> result;

    public Parser(BufferedReader br) {
        reader = br;
        wordBuffer = new StringBuilder();
    }

    public static boolean isLetter(char c) {
        if ((c >= 'a' && c <= 'z') || (c >= 'A' && c <= 'Z')) {
            return true;
        }
        return false;
    }

    public static boolean isDigit(char c){
        if (c >= '0' && c <= '9') {
            return true;
        }
        return false;
    }

    public static boolean isBreak(char c) {
        if (Dictionary.breakLetters.contains(c)) {
            return true;
        }
        else return false;
    }

    void dealLine() {
        line += 1;
        pos = 0;
    }

    void parseOne(char c) throws IllegalWordException {
        switch (state) {
            case 0:
                if (isLetter(c)) {
                    state = 1;
                    wordBuffer.append(c);
                } else if (isDigit(c)) {
                    state = 2;
                    wordBuffer.append(c);
                } else if (isBreak(c)) {
                    state = 0;

//                    Token token1 = new Token();
//                    token1.type = Dictionary.tokenTypeMap.get(wordBuffer.toString());
//                    if (token1.type == null) {
//                        token1.type = 0;    //Dictionary.tokenTypeMap.get("id")
//                        token1.value = wordBuffer.toString();
//                    }
                    wordBuffer.setLength(0);

                    if (c == ' ' || c== '\t') {
                        // do nothing
                    } else if (c == '{') {
                        state = 4;
                    } else {
                        Token token2 = new Token();
                        token2.type = Dictionary.tokenTypeMap.get(String.valueOf(c));
                        result.add(token2);
                    }
                    if (c == '\n') {
                        dealLine();
                    }
                } else if (c == ':') {
                    state = 3;
                } else {
                    throw new IllegalWordException(line, pos, c);
                }
                break;
            case 1:
                if (isLetter(c)) {
                    state = 1;
                    wordBuffer.append(c);
                } else if (isDigit(c)) {
                    state = 1;
                    wordBuffer.append(c);
                } else if (isBreak(c)) {
                    state = 0;
                    Token token1 = new Token();
                    token1.type = Dictionary.tokenTypeMap.get(wordBuffer.toString());
                    if (token1.type == null) {
                        token1.type = 0;    //Dictionary.tokenTypeMap.get("id")
                        token1.value = wordBuffer.toString();
                    }
                    result.add(token1);
                    wordBuffer.setLength(0);
                    if (c == ' ' || c== '\t') {
                        // do nothing
                    } else if (c == '{') {
                        state = 4;
                    } else {
                        Token token2 = new Token();
                        token2.type = Dictionary.tokenTypeMap.get(String.valueOf(c));
                        result.add(token2);
                    }
                    if (c == '\n') {
                        dealLine();
                    }
                } else if (c == ':') {
                    Token token1 = new Token();
                    token1.type = Dictionary.tokenTypeMap.get(wordBuffer.toString());
                    if (token1.type == null) {
                        token1.type = 0;    //Dictionary.tokenTypeMap.get("id")
                        token1.value = wordBuffer.toString();
                    }
                    result.add(token1);
                    wordBuffer.setLength(0);
                    state = 3;
                } else {
                    throw new IllegalWordException(line, pos, c);
                }
                break;
            case 2:
                if (isDigit(c)) {
                    state = 2;
                    wordBuffer.append(c);
                } else if (isBreak(c)) {
                    state = 0;
                    Token token1 = new Token();
                    token1.type = 1;    //Dictionary.tokenTypeMap.get("uint")
                    token1.value = Integer.parseInt(wordBuffer.toString());
                    result.add(token1);
                    wordBuffer.setLength(0);
                    if (c == ' ' || c== '\t') {
                        // do nothing
                    } else if (c == '{') {
                        state = 4;
                    } else {
                        Token token2 = new Token();
                        token2.type = Dictionary.tokenTypeMap.get(String.valueOf(c));
                        result.add(token2);
                    }
                    if (c == '\n') {
                        dealLine();
                    }
                } else if (c == ':') {
                    Token token1 = new Token();
                    token1.type = 1;    //Dictionary.tokenTypeMap.get("uint")
                    token1.value = Integer.parseInt(wordBuffer.toString());
                    result.add(token1);
                    wordBuffer.setLength(0);
                    state = 3;
                } else {
                    throw new IllegalWordException(line, pos, c);
                }
                break;
            case 3:
                if (c == '=') {
                    state = 0;
                    Token token = new Token();
                    token.type = Dictionary.tokenTypeMap.get(":=");
                    result.add(token);
                } else {
                    throw new IllegalWordException(line, pos, c);
                }
                break;
            case 4:
                if (c == '}') {
                    state = 0;
                } else if (c == '\n') {
                    Token token2 = new Token();
                    token2.type = Dictionary.tokenTypeMap.get(String.valueOf(c));
                    result.add(token2);
                    dealLine();
                } else {
                    state = 4;
                }
                break;
            default:
                throw new IllegalWordException(line, pos, c);
        }
    }

    public ArrayList<Token> parseAll() throws IOException, IllegalWordException {
        state = 0;
        line = 1;
        pos = 0;
        result = new ArrayList<>();
        int rtmp;
        char ch;
        while ((rtmp = reader.read()) >= 0) {
            ch = (char) rtmp;
            pos += 1;
            parseOne(ch);
        }

        return result;
    }


}
