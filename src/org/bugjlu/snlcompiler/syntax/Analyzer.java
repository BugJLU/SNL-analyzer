package org.bugjlu.snlcompiler.syntax;

import org.bugjlu.snlcompiler.lexical.Dictionary;
import org.bugjlu.snlcompiler.lexical.Token;

import java.util.ArrayList;

public class Analyzer {
    ArrayList<Token> tokenList;
    int tokenCursor = 0;
    int line = 1;
    
    public Analyzer(ArrayList<Token> tokens) {
        tokenList = tokens;
        tokenCursor = 0;
        line = 1;
    }
    
    public TreeNode analyzeAll() throws IllegalSyntaxException {
        return Program();
    }

//    boolean match(Integer tokenType) {
//        Token token = tokenList.get(tokenCursor);
//        tokenCursor++;
//        return token.type.equals(tokenType);
//    }
//    void dealLine() {
//        line ++;
//    }

    void match(String tokenName, TreeNode father) throws IllegalSyntaxException {
        Token token;
        boolean isLF = false;
        int lines = 0;
        do {
            token = tokenList.get(tokenCursor);
            tokenCursor++;
            isLF = token.type.equals(Dictionary.tokenTypeMap.get("\n"));
            if (isLF) lines++;
        } while (isLF);

        line += lines;

        if (token.type.equals(Dictionary.tokenTypeMap.get(tokenName))) {
            TreeNode node = new TreeNode();
            node.data = token;
            father.addSon(node);
        } else {
            tokenCursor -= lines+1;
            line -= lines;
            throw new IllegalSyntaxException(line, tokenName, Dictionary.tokenNameMap.get(token.type));
        }
    }

    TreeNode Program() throws IllegalSyntaxException {
        TreeNode node = new TreeNode();
        node.data = "Program";
        node.addSon(ProgramHead());
        try {
            node.addSon(DeclarePart());
                node.addSon(ProgramBody());
        } catch(IllegalSyntaxException e) {
            e.firstMatch = false;
            throw e;
        }
        return node;
    }

    TreeNode ProgramHead() throws IllegalSyntaxException {
        TreeNode node = new TreeNode();
        node.data = "ProgramHead";
        try {
            match("program", node);
        } catch(IllegalSyntaxException e) {
            e.firstMatch = true;
            throw e;
        }
        try {
            node.addSon(ProgramName());
        } catch(IllegalSyntaxException e) {
            e.firstMatch = false;
            throw e;
        }
        return node;
    }

    TreeNode ProgramName() throws IllegalSyntaxException {
        TreeNode node = new TreeNode();
        node.data = "ProgramName";
        try {
            match("id", node);
        } catch(IllegalSyntaxException e) {
            e.firstMatch = true;
            throw e;
        }
        return node;
    }

    TreeNode DeclarePart() throws IllegalSyntaxException {
        TreeNode node = new TreeNode();
        node.data = "DeclarePart";
        node.addSon(TypeDecpart());
        try {
            node.addSon(VarDecpart());
                node.addSon(ProcDecpart());
        } catch(IllegalSyntaxException e) {
            e.firstMatch = false;
            throw e;
        }
        return node;
    }
    TreeNode TypeDecpart() throws IllegalSyntaxException {
        TreeNode node = new TreeNode();
        node.data = "TypeDecpart";
        try {
            node.addSon(TypeDec());
        } catch (IllegalSyntaxException e) {
            // empty is ok
            if (!e.firstMatch) throw e;
        }
        return node;
    }


    TreeNode TypeDec() throws IllegalSyntaxException {
        TreeNode node = new TreeNode();
        node.data = "TypeDec";
        try {
            match("type", node);
        } catch(IllegalSyntaxException e) {
            e.firstMatch = true;
            throw e;
        }
        try {
            node.addSon(TypeDecList());
        } catch(IllegalSyntaxException e) {
            e.firstMatch = false;
            throw e;
        }
        return node;
    }

    // TypeId = TypeDef ; TypeDecMore
    TreeNode TypeDecList() throws IllegalSyntaxException {
        TreeNode node = new TreeNode();
        node.data = "TypeDecList";
        node.addSon(TypeId());
        try {
            match("=", node);
                node.addSon(TypeDef());
                match(";", node);
                node.addSon(TypeDecMore());
        } catch(IllegalSyntaxException e) {
            e.firstMatch = false;
            throw e;
        }
        return node;
    }

    TreeNode TypeDecMore() throws IllegalSyntaxException {
        TreeNode node = new TreeNode();
        node.data = "TypeDecMore";
        try {
            node.addSon(TypeDecList());
        } catch (IllegalSyntaxException e) {
            // empty is ok
            if (!e.firstMatch) throw e;
        }
        return node;
    }

    TreeNode TypeId() throws IllegalSyntaxException {
        TreeNode node = new TreeNode();
        node.data = "TypeId";
        try {
            match("id", node);
        } catch(IllegalSyntaxException e) {
            e.firstMatch = true;
            throw e;
        }
        return node;
    }

    TreeNode TypeDef() throws IllegalSyntaxException {
        TreeNode node = new TreeNode();
        node.data = "TypeDef";
        try {
            node.addSon(BaseType());
            return node;
        } catch (IllegalSyntaxException e) {
            // multi condition
            if (!e.firstMatch) throw e;
        }
        try {
            node.addSon(StructrueType());
            return node;
        } catch (IllegalSyntaxException e) {
            // multi condition
            if (!e.firstMatch) throw e;
        }
        try {
            try {
                match("id", node);
            } catch(IllegalSyntaxException e) {
                e.firstMatch = true;
                throw e;
            }
            return node;
        } catch (IllegalSyntaxException e) {
            // multi condition
            if (!e.firstMatch) throw e;
        }
        throw new MultiSyntaxException(line, "TypeDef");
    }

    TreeNode BaseType() throws IllegalSyntaxException {
        TreeNode node = new TreeNode();
        node.data = "BaseType";
        try {
            try {
                match("integer", node);
            } catch(IllegalSyntaxException e) {
                e.firstMatch = true;
                throw e;
            }
            return node;
        } catch (IllegalSyntaxException e) {
            // multi condition
            if (!e.firstMatch) throw e;
        }
        try {
            try {
                match("char", node);
            } catch(IllegalSyntaxException e) {
                e.firstMatch = true;
                throw e;
            }
            return node;
        } catch (IllegalSyntaxException e) {
            // multi condition
            if (!e.firstMatch) throw e;
        }
        throw new MultiSyntaxException(line, "BaseType");
    }

    TreeNode StructrueType() throws IllegalSyntaxException {
        TreeNode node = new TreeNode();
        node.data = "StructrueType";
        try {
            node.addSon(ArrayType());
            return node;
        } catch (IllegalSyntaxException e) {
            // multi condition
            if (!e.firstMatch) throw e;
        }
        try {
            node.addSon(RecType());
            return node;
        } catch (IllegalSyntaxException e) {
            // multi condition
            if (!e.firstMatch) throw e;
        }
        throw new MultiSyntaxException(line, "StructrueType");
    }

    TreeNode ArrayType() throws IllegalSyntaxException {
        TreeNode node = new TreeNode();
        node.data = "ArrayType";
        try {
            match("array", node);
        } catch(IllegalSyntaxException e) {
            e.firstMatch = true;
            throw e;
        }
        try {
            match("[", node);
                node.addSon(Low());
                match(".", node);
                match(".", node);
                node.addSon(Top());
                match("]", node);
                match("of", node);
                node.addSon(BaseType());
        } catch(IllegalSyntaxException e) {
            e.firstMatch = false;
            throw e;
        }
        return node;
    }

    TreeNode Low() throws IllegalSyntaxException {
        TreeNode node = new TreeNode();
        node.data = "Low";
        try {
            match("uint", node);
        } catch(IllegalSyntaxException e) {
            e.firstMatch = true;
            throw e;
        }
        return node;
    }

    TreeNode Top() throws IllegalSyntaxException {
        TreeNode node = new TreeNode();
        node.data = "Top";
        try {
            match("uint", node);
        } catch(IllegalSyntaxException e) {
            e.firstMatch = true;
            throw e;
        }
        return node;
    }

    TreeNode RecType() throws IllegalSyntaxException {
        TreeNode node = new TreeNode();
        node.data = "RecType";
        try {
            match("record", node);
        } catch(IllegalSyntaxException e) {
            e.firstMatch = true;
            throw e;
        }
        try {
            node.addSon(FieldDecList());
                match("end", node);
        } catch(IllegalSyntaxException e) {
            e.firstMatch = false;
            throw e;
        }
        return node;
    }

    TreeNode FieldDecList() throws IllegalSyntaxException {
        TreeNode node = new TreeNode();
        node.data = "FieldDecList";
        try {
            node.addSon(BaseType());
            try {
                node.addSon(IdList());
                        match(";", node);
                        node.addSon(FieldDecMore());
            } catch(IllegalSyntaxException e) {
                e.firstMatch = false;
                throw e;
            }
            return node;
        } catch (IllegalSyntaxException e) {
            // multi condition
            if (!e.firstMatch) throw e;
        }
        try {
            node.addSon(ArrayType());
            try {
                node.addSon(IdList());
                        match(";", node);
                        node.addSon(FieldDecMore());
            } catch(IllegalSyntaxException e) {
                e.firstMatch = false;
                throw e;
            }
            return node;
        } catch (IllegalSyntaxException e) {
            // multi condition
            if (!e.firstMatch) throw e;
        }
        throw new MultiSyntaxException(line, "FieldDecList");
    }


    TreeNode FieldDecMore() throws IllegalSyntaxException {
        TreeNode node = new TreeNode();
        node.data = "FieldDecMore";
        try {
            node.addSon(FieldDecList());
        } catch (IllegalSyntaxException e) {
            // multi condition
            if (!e.firstMatch) throw e;
        }
        return node;
    }

    TreeNode IdList() throws IllegalSyntaxException {
        TreeNode node = new TreeNode();
        node.data = "IdList";
        try {
            match("id", node);
        } catch(IllegalSyntaxException e) {
            e.firstMatch = true;
            throw e;
        }
        try {
            node.addSon(IdMore());
        } catch(IllegalSyntaxException e) {
            e.firstMatch = false;
            throw e;
        }
        return node;
    }

    TreeNode IdMore() throws IllegalSyntaxException {
        TreeNode node = new TreeNode();
        node.data = "IdMore";
        try {
            try {
                match(",", node);
            } catch(IllegalSyntaxException e) {
                e.firstMatch = true;
                throw e;
            }
            try {
                node.addSon(IdList());
            } catch(IllegalSyntaxException e) {
                e.firstMatch = false;
                throw e;
            }
        } catch (IllegalSyntaxException e) {
            // multi condition
            if (!e.firstMatch) throw e;
        }
        return node;
    }

    TreeNode VarDecpart() throws IllegalSyntaxException{
        TreeNode node = new TreeNode();
        node.data = "VarDecpart";
        try {
            node.addSon(VarDec());
        } catch (IllegalSyntaxException e) {
            // multi condition
            if (!e.firstMatch) throw e;
        }
        return node;
    }

    TreeNode VarDec() throws IllegalSyntaxException {
        TreeNode node = new TreeNode();
        node.data = "VarDec";
        try {
            match("var", node);
        } catch(IllegalSyntaxException e) {
            e.firstMatch = true;
            throw e;
        }
        try {
            node.addSon(VarDecList());
        } catch(IllegalSyntaxException e) {
            e.firstMatch = false;
            throw e;
        }
        return node;
    }

    TreeNode VarDecList() throws IllegalSyntaxException {
        TreeNode node = new TreeNode();
        node.data = "VarDecList";
        node.addSon(TypeDef());
        try {
            node.addSon(VarIdList());
                match(";", node);
                node.addSon(VarDecMore());
        } catch(IllegalSyntaxException e) {
            e.firstMatch = false;
            throw e;
        }
        return node;
    }

    TreeNode VarDecMore() throws IllegalSyntaxException {
        TreeNode node = new TreeNode();
        node.data = "VarDecMore";
        try {
            node.addSon(VarDecList());
        } catch (IllegalSyntaxException e) {
            // multi condition
            if (!e.firstMatch) throw e;
        }
        return node;
    }

    TreeNode VarIdList() throws IllegalSyntaxException {
        TreeNode node = new TreeNode();
        node.data = "VarIdList";
        try {
            match("id", node);
        } catch(IllegalSyntaxException e) {
            e.firstMatch = true;
            throw e;
        }
        try {
            node.addSon(VarIdMore());
        } catch(IllegalSyntaxException e) {
            e.firstMatch = false;
            throw e;
        }
        return node;
    }

    TreeNode VarIdMore() throws IllegalSyntaxException {
        TreeNode node = new TreeNode();
        node.data = "VarIdMore";
        try {
            try {
                match(",", node);
            } catch(IllegalSyntaxException e) {
                e.firstMatch = true;
                throw e;
            }
            try {
                node.addSon(VarIdList());
            } catch(IllegalSyntaxException e) {
                e.firstMatch = false;
                throw e;
            }
        } catch (IllegalSyntaxException e) {
            // multi condition
            if (!e.firstMatch) throw e;
        }
        return node;
    }

    TreeNode ProcDecpart() throws IllegalSyntaxException {
        TreeNode node = new TreeNode();
        node.data = "ProcDecpart";
        try {
            node.addSon(ProcDec());
        } catch (IllegalSyntaxException e) {
            // multi condition
            if (!e.firstMatch) throw e;
        }
        return node;
    }

    TreeNode ProcDec() throws IllegalSyntaxException {
        TreeNode node = new TreeNode();
        node.data = "ProcDec";
        try {
            match("procedure", node);
        } catch(IllegalSyntaxException e) {
            e.firstMatch = true;
            throw e;
        }
        try {
            node.addSon(ProcName());
                match("(", node);
                node.addSon(ParamList());
                match(")", node);
                match(";", node);
                node.addSon(ProcDecPart());
                node.addSon(ProcBody());
                node.addSon(ProcDecMore());
        } catch(IllegalSyntaxException e) {
            e.firstMatch = false;
            throw e;
        }
        return node;
    }

    TreeNode ProcDecMore() throws IllegalSyntaxException {
        TreeNode node = new TreeNode();
        node.data = "ProcDecMore";
        try {
            node.addSon(ProcDec());
        } catch (IllegalSyntaxException e) {
            // multi condition
            if (!e.firstMatch) throw e;
        }
        return node;
    }

    TreeNode ProcName() throws IllegalSyntaxException {
        TreeNode node = new TreeNode();
        node.data = "ProcName";
        try {
            match("id", node);
        } catch(IllegalSyntaxException e) {
            e.firstMatch = true;
            throw e;
        }
        return node;
    }

    TreeNode ParamList() throws IllegalSyntaxException {
        TreeNode node = new TreeNode();
        node.data = "ParamList";
        try {
            node.addSon(ParamDecList());
        } catch (IllegalSyntaxException e) {
            // multi condition
            if (!e.firstMatch) throw e;
        }
        return node;
    }

    TreeNode ParamDecList() throws IllegalSyntaxException {
        TreeNode node = new TreeNode();
        node.data = "ParamDecList";
        node.addSon(Param());
        try {
            node.addSon(ParamMore());
        } catch(IllegalSyntaxException e) {
            e.firstMatch = false;
            throw e;
        }
        return node;
    }

    TreeNode ParamMore() throws IllegalSyntaxException {
        TreeNode node = new TreeNode();
        node.data = "ParamMore";
        try {
            try {
                match(";", node);
            } catch(IllegalSyntaxException e) {
                e.firstMatch = true;
                throw e;
            }
            try {
                node.addSon(ParamDecList());
            } catch(IllegalSyntaxException e) {
                e.firstMatch = false;
                throw e;
            }
        } catch (IllegalSyntaxException e) {
            // multi condition
            if (!e.firstMatch) throw e;
        }
        return node;
    }

    TreeNode Param() throws IllegalSyntaxException {
        TreeNode node = new TreeNode();
        node.data = "Param";
        try {
            node.addSon(TypeDef());
            try {
                node.addSon(FormList());
            } catch(IllegalSyntaxException e) {
                e.firstMatch = false;
                throw e;
            }
            return node;
        } catch (IllegalSyntaxException e) {
            // multi condition
            if (!e.firstMatch) throw e;
        }
        try {
            try {
                match("var", node);
            } catch(IllegalSyntaxException e) {
                e.firstMatch = true;
                throw e;
            }
            try {
                node.addSon(TypeDef());
                        node.addSon(FormList());
            } catch(IllegalSyntaxException e) {
                e.firstMatch = false;
                throw e;
            }
            return node;
        } catch (IllegalSyntaxException e) {
            // multi condition
            if (!e.firstMatch) throw e;
        }
        throw new MultiSyntaxException(line, "Param");
    }

    TreeNode FormList() throws IllegalSyntaxException {
        TreeNode node = new TreeNode();
        node.data = "FormList";
        try {
            match("id", node);
        } catch(IllegalSyntaxException e) {
            e.firstMatch = true;
            throw e;
        }
        try {
            node.addSon(FidMore());
        } catch(IllegalSyntaxException e) {
            e.firstMatch = false;
            throw e;
        }
        return node;
    }

    TreeNode FidMore() throws IllegalSyntaxException {
        TreeNode node = new TreeNode();
        node.data = "FidMore";
        try {
            try {
                match(",", node);
            } catch(IllegalSyntaxException e) {
                e.firstMatch = true;
                throw e;
            }
            try {
                node.addSon(FormList());
            } catch(IllegalSyntaxException e) {
                e.firstMatch = false;
                throw e;
            }
        } catch (IllegalSyntaxException e) {
            // multi condition
            if (!e.firstMatch) throw e;
        }
        return node;
    }

    TreeNode ProcDecPart() throws IllegalSyntaxException {
        TreeNode node = new TreeNode();
        node.data = "ProcDecPart";
        node.addSon(DeclarePart());
        return node;
    }

    TreeNode ProcBody() throws IllegalSyntaxException {
        TreeNode node = new TreeNode();
        node.data = "ProcBody";
        node.addSon(ProgramBody());
        return node;
    }

    TreeNode ProgramBody() throws IllegalSyntaxException {
        TreeNode node = new TreeNode();
        node.data = "ProgramBody";
        try {
            match("begin", node);
        } catch(IllegalSyntaxException e) {
            e.firstMatch = true;
            throw e;
        }
        try {
            node.addSon(StmList());
                match("end", node);
        } catch(IllegalSyntaxException e) {
            e.firstMatch = false;
            throw e;
        }
        return node;
    }

    TreeNode StmList() throws IllegalSyntaxException {
        TreeNode node = new TreeNode();
        node.data = "StmList";
        node.addSon(Stm());
        try {
            node.addSon(StmMore());
        } catch(IllegalSyntaxException e) {
            e.firstMatch = false;
            throw e;
        }
        return node;
    }

    TreeNode StmMore() throws IllegalSyntaxException {
        TreeNode node = new TreeNode();
        node.data = "StmMore";
        try {
            try {
                match(";", node);
            } catch(IllegalSyntaxException e) {
                e.firstMatch = true;
                throw e;
            }
            try {
                node.addSon(StmList());
            } catch(IllegalSyntaxException e) {
                e.firstMatch = false;
                throw e;
            }
        } catch (IllegalSyntaxException e) {
            // multi condition
            if (!e.firstMatch) throw e;
        }
        return node;
    }

    TreeNode Stm() throws IllegalSyntaxException {
        TreeNode node = new TreeNode();
        node.data = "Stm";
        try {
            node.addSon(ConditionalStm());
            return node;
        } catch (IllegalSyntaxException e) {
            // multi condition
            if (!e.firstMatch) throw e;
        }
        try {
            node.addSon(LoopStm());
            return node;
        } catch (IllegalSyntaxException e) {
            // multi condition
            if (!e.firstMatch) throw e;
        }
        try {
            node.addSon(InputStm());
            return node;
        } catch (IllegalSyntaxException e) {
            // multi condition
            if (!e.firstMatch) throw e;
        }
        try {
            node.addSon(OutputStm());
            return node;
        } catch (IllegalSyntaxException e) {
            // multi condition
            if (!e.firstMatch) throw e;
        }
        try {
            node.addSon(ReturnStm());
            return node;
        } catch (IllegalSyntaxException e) {
            // multi condition
            if (!e.firstMatch) throw e;
        }
        try {
            try {
                match("id", node);
            } catch(IllegalSyntaxException e) {
                e.firstMatch = true;
                throw e;
            }
            try {
                node.addSon(AssCall());
            } catch(IllegalSyntaxException e) {
                e.firstMatch = false;
                throw e;
            }
            return node;
        } catch (IllegalSyntaxException e) {
            // multi condition
            if (!e.firstMatch) throw e;
        }
        throw new MultiSyntaxException(line, "Stm");
    }

    TreeNode AssCall() throws IllegalSyntaxException {
        TreeNode node = new TreeNode();
        node.data = "AssCall";
        try {
            node.addSon(CallStmRest());
            return node;
        } catch (IllegalSyntaxException e) {
            // multi condition
            if (!e.firstMatch) throw e;
        }
        try {
            node.addSon(AssignmentRest());
            return node;
        } catch (IllegalSyntaxException e) {
            // multi condition
            if (!e.firstMatch) throw e;
        }
        throw new MultiSyntaxException(line, "AssCall");
    }

    TreeNode AssignmentRest() throws IllegalSyntaxException {
        TreeNode node = new TreeNode();
        node.data = "AssignmentRest";
        node.addSon(VariMore());
        try {
            match(":=", node);
                node.addSon(Exp());
        } catch(IllegalSyntaxException e) {
            e.firstMatch = false;
            throw e;
        }
        return node;
    }

    TreeNode ConditionalStm() throws IllegalSyntaxException {
        TreeNode node = new TreeNode();
        node.data = "ConditionalStm";
        try {
            match("if", node);
        } catch(IllegalSyntaxException e) {
            e.firstMatch = true;
            throw e;
        }
        try {
            node.addSon(RelExp());
                match("then", node);
                node.addSon(StmList());
                match("else", node);
                node.addSon(StmList());
                match("fi", node);
        } catch(IllegalSyntaxException e) {
            e.firstMatch = false;
            throw e;
        }
        return node;
    }

    TreeNode LoopStm() throws IllegalSyntaxException {
        TreeNode node = new TreeNode();
        node.data = "LoopStm";
        try {
            match("while", node);
        } catch(IllegalSyntaxException e) {
            e.firstMatch = true;
            throw e;
        }
        try {
            node.addSon(RelExp());
                match("do", node);
                node.addSon(StmList());
                match("endwh", node);
        } catch(IllegalSyntaxException e) {
            e.firstMatch = false;
            throw e;
        }
        return node;
    }

    TreeNode InputStm() throws IllegalSyntaxException {
        TreeNode node = new TreeNode();
        node.data = "InputStm";
        try {
            match("read", node);
        } catch(IllegalSyntaxException e) {
            e.firstMatch = true;
            throw e;
        }
        try {
            match("(", node);
                node.addSon(Invar());
                match(")", node);
        } catch(IllegalSyntaxException e) {
            e.firstMatch = false;
            throw e;
        }
        return node;
    }

    TreeNode Invar() throws IllegalSyntaxException {
        TreeNode node = new TreeNode();
        node.data = "Invar";
        try {
            match("id", node);
        } catch(IllegalSyntaxException e) {
            e.firstMatch = true;
            throw e;
        }
        return node;
    }

    TreeNode OutputStm() throws IllegalSyntaxException {
        TreeNode node = new TreeNode();
        node.data = "OutputStm";
        try {
            match("write", node);
        } catch(IllegalSyntaxException e) {
            e.firstMatch = true;
            throw e;
        }
        try {
            match("(", node);
                node.addSon(Exp());
                match(")", node);
        } catch(IllegalSyntaxException e) {
            e.firstMatch = false;
            throw e;
        }
        return node;
    }

    TreeNode ReturnStm() throws IllegalSyntaxException {
        TreeNode node = new TreeNode();
        node.data = "ReturnStm";
        try {
            match("return", node);
        } catch(IllegalSyntaxException e) {
            e.firstMatch = true;
            throw e;
        }
        return node;
    }

    TreeNode CallStmRest() throws IllegalSyntaxException {
        TreeNode node = new TreeNode();
        node.data = "CallStmRest";
        try {
            match("(", node);
        } catch(IllegalSyntaxException e) {
            e.firstMatch = true;
            throw e;
        }
        try {
            node.addSon(ActParamList());
                match(")", node);
        } catch(IllegalSyntaxException e) {
            e.firstMatch = false;
            throw e;
        }
        return node;
    }

    TreeNode ActParamList() throws IllegalSyntaxException {
        TreeNode node = new TreeNode();
        node.data = "ActParamList";
        try {
            node.addSon(Exp());
            try {
                node.addSon(ActParamMore());
            } catch(IllegalSyntaxException e) {
                e.firstMatch = false;
                throw e;
            }
        } catch (IllegalSyntaxException e) {
            // multi condition
            if (!e.firstMatch) throw e;
        }
        return node;
    }

    TreeNode ActParamMore() throws IllegalSyntaxException {
        TreeNode node = new TreeNode();
        node.data = "ActParamMore";
        try {
            try {
                match(",", node);
            } catch(IllegalSyntaxException e) {
                e.firstMatch = true;
                throw e;
            }
            try {
                node.addSon(ActParamList());
            } catch(IllegalSyntaxException e) {
                e.firstMatch = false;
                throw e;
            }
        } catch (IllegalSyntaxException e) {
            // multi condition
            if (!e.firstMatch) throw e;
        }
        return node;
    }

    TreeNode RelExp() throws IllegalSyntaxException {
        TreeNode node = new TreeNode();
        node.data = "RelExp";
        node.addSon(Exp());
        try {
            node.addSon(OtherRelE());
        } catch(IllegalSyntaxException e) {
            e.firstMatch = false;
            throw e;
        }
        return node;
    }

    TreeNode OtherRelE() throws IllegalSyntaxException {
        TreeNode node = new TreeNode();
        node.data = "OtherRelE";
        node.addSon(CmpOp());
        try {
            node.addSon(Exp());
        } catch(IllegalSyntaxException e) {
            e.firstMatch = false;
            throw e;
        }
        return node;
    }

    TreeNode Exp() throws IllegalSyntaxException {
        TreeNode node = new TreeNode();
        node.data = "Exp";
        node.addSon(Term());
        try {
            node.addSon(OtherTerm());
        } catch(IllegalSyntaxException e) {
            e.firstMatch = false;
            throw e;
        }
        return node;
    }

    TreeNode OtherTerm() throws IllegalSyntaxException{
        TreeNode node = new TreeNode();
        node.data = "OtherTerm";
        try {
            node.addSon(AddOp());
            try {
                node.addSon(Exp());
            } catch(IllegalSyntaxException e) {
                e.firstMatch = false;
                throw e;
            }
        } catch (IllegalSyntaxException e) {
            // multi condition
            if (!e.firstMatch) throw e;
        }
        return node;
    }

    TreeNode Term() throws IllegalSyntaxException {
        TreeNode node = new TreeNode();
        node.data = "Term";
        node.addSon(Factor());
        try {
            node.addSon(OtherFactor());
        } catch(IllegalSyntaxException e) {
            e.firstMatch = false;
            throw e;
        }
        return node;
    }

    TreeNode OtherFactor() throws IllegalSyntaxException {
        TreeNode node = new TreeNode();
        node.data = "OtherFactor";
        try {
            node.addSon(MultOp());
            try {
                node.addSon(Term());
            } catch(IllegalSyntaxException e) {
                e.firstMatch = false;
                throw e;
            }
        } catch (IllegalSyntaxException e) {
            // multi condition
            if (!e.firstMatch) throw e;
        }
        return node;
    }

    TreeNode Factor() throws IllegalSyntaxException {
        TreeNode node = new TreeNode();
        node.data = "Factor";
        try {
            try {
                match("(", node);
            } catch(IllegalSyntaxException e) {
                e.firstMatch = true;
                throw e;
            }
            try {
                node.addSon(Exp());
                        match(")", node);
            } catch(IllegalSyntaxException e) {
                e.firstMatch = false;
                throw e;
            }
            return node;
        } catch (IllegalSyntaxException e) {
            // multi condition
            if (!e.firstMatch) throw e;
        }
        try {
            try {
                match("uint", node);
            } catch(IllegalSyntaxException e) {
                e.firstMatch = true;
                throw e;
            }
            return node;
        } catch (IllegalSyntaxException e) {
            // multi condition
            if (!e.firstMatch) throw e;
        }
        try {
            node.addSon(Variable());
            return node;
        } catch (IllegalSyntaxException e) {
            // multi condition
            if (!e.firstMatch) throw e;
        }
        throw new MultiSyntaxException(line, "Factor");
    }

    TreeNode Variable() throws IllegalSyntaxException {
        TreeNode node = new TreeNode();
        node.data = "Variable";
        try {
            match("id", node);
        } catch(IllegalSyntaxException e) {
            e.firstMatch = true;
            throw e;
        }
        try {
            node.addSon(VariMore());
        } catch(IllegalSyntaxException e) {
            e.firstMatch = false;
            throw e;
        }
        return node;
    }

    TreeNode VariMore() throws IllegalSyntaxException {
        TreeNode node = new TreeNode();
        node.data = "VariMore";
        try {
            try {
                match("[", node);
            } catch(IllegalSyntaxException e) {
                e.firstMatch = true;
                throw e;
            }
            try {
                node.addSon(Exp());
                        match("]", node);
            } catch(IllegalSyntaxException e) {
                e.firstMatch = false;
                throw e;
            }
            return node;
        } catch (IllegalSyntaxException e) {
            // multi condition
            if (!e.firstMatch) throw e;
        }
        try {
            try {
                match(".", node);
            } catch(IllegalSyntaxException e) {
                e.firstMatch = true;
                throw e;
            }
            try {
                node.addSon(FieldVar());
            } catch(IllegalSyntaxException e) {
                e.firstMatch = false;
                throw e;
            }
            return node;
        } catch (IllegalSyntaxException e) {
            // multi condition
            if (!e.firstMatch) throw e;
        }
        return node;
    }

    TreeNode FieldVar() throws IllegalSyntaxException {
        TreeNode node = new TreeNode();
        node.data = "FieldVar";
        try {
            match("id", node);
        } catch(IllegalSyntaxException e) {
            e.firstMatch = true;
            throw e;
        }
        try {
            node.addSon(FieldVarMore());
        } catch(IllegalSyntaxException e) {
            e.firstMatch = false;
            throw e;
        }
        return node;
    }

    TreeNode FieldVarMore() throws IllegalSyntaxException {
        TreeNode node = new TreeNode();
        node.data = "FieldVarMore";
        try {
            try {
                match("[", node);
            } catch(IllegalSyntaxException e) {
                e.firstMatch = true;
                throw e;
            }
            try {
                node.addSon(Exp());
                        match("]", node);
            } catch(IllegalSyntaxException e) {
                e.firstMatch = false;
                throw e;
            }
        } catch (IllegalSyntaxException e) {
            // multi condition
            if (!e.firstMatch) throw e;
        }
        return node;
    }

    TreeNode CmpOp() throws IllegalSyntaxException {
        TreeNode node = new TreeNode();
        node.data = "CmpOp";
        try {
            try {
                match("<", node);
            } catch(IllegalSyntaxException e) {
                e.firstMatch = true;
                throw e;
            }
            return node;
        } catch (IllegalSyntaxException e) {
            // multi condition
            if (!e.firstMatch) throw e;
        }
        try {
            try {
                match(">", node);
            } catch(IllegalSyntaxException e) {
                e.firstMatch = true;
                throw e;
            }
            return node;
        } catch (IllegalSyntaxException e) {
            // multi condition
            if (!e.firstMatch) throw e;
        }
        try {
            try {
                match("=", node);
            } catch(IllegalSyntaxException e) {
                e.firstMatch = true;
                throw e;
            }
            return node;
        } catch (IllegalSyntaxException e) {
            // multi condition
            if (!e.firstMatch) throw e;
        }
        throw new MultiSyntaxException(line, "CmpOp");
    }

    TreeNode AddOp() throws IllegalSyntaxException {
        TreeNode node = new TreeNode();
        node.data = "AddOp";
        try {
            try {
                match("+", node);
            } catch(IllegalSyntaxException e) {
                e.firstMatch = true;
                throw e;
            }
            return node;
        } catch (IllegalSyntaxException e) {
            // multi condition
            if (!e.firstMatch) throw e;
        }
        try {
            try {
                match("-", node);
            } catch(IllegalSyntaxException e) {
                e.firstMatch = true;
                throw e;
            }
            return node;
        } catch (IllegalSyntaxException e) {
            // multi condition
            if (!e.firstMatch) throw e;
        }
        throw new MultiSyntaxException(line, "AddOp");
    }

    TreeNode MultOp() throws IllegalSyntaxException {
        TreeNode node = new TreeNode();
        node.data = "MultOp";
        try {
            try {
                match("*", node);
            } catch(IllegalSyntaxException e) {
                e.firstMatch = true;
                throw e;
            }
            return node;
        } catch (IllegalSyntaxException e) {
            // multi condition
            if (!e.firstMatch) throw e;
        }
        try {
            try {
                match("/", node);
            } catch(IllegalSyntaxException e) {
                e.firstMatch = true;
                throw e;
            }
            return node;
        } catch (IllegalSyntaxException e) {
            // multi condition
            if (!e.firstMatch) throw e;
        }
        throw new MultiSyntaxException(line, "MultOp");
    }
}
