package org.bugjlu.snlcompiler.lexical;

import java.util.HashSet;

public class Token {

    public final static int TYPE_ID = 0;
    public final static int TYPE_UINT = 1;
    public final static int TYPE_BREAK = 1;

    /**
     * @Description:
     *      0       identifier
     *      1       unsigned int
     *      2..m    symbol
     *      m+1..n  reserved words
     */
    public Integer type;
    public Object value;

    @Override
    public String toString() {
        String ts = Dictionary.tokenNameMap.get(type);
        if (ts.equals("\n")) ts = "\\n";
        return "Token{" +
                "type=" + ts +
                ", value=" + value +
                '}';
    }
}
