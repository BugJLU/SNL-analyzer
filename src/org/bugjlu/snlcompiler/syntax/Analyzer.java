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
    
    public void analyzeAll() throws IllegalSyntaxException {
        Program();
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
        node.addSon(DeclarePart());
        node.addSon(ProgramBody());
        return node;
    }

    TreeNode ProgramHead() throws IllegalSyntaxException {
        TreeNode node = new TreeNode();
        node.data = "ProgramHead";
        match("program", node);
        node.addSon(ProgramName());
        return node;
    }

    TreeNode ProgramName() throws IllegalSyntaxException {
        TreeNode node = new TreeNode();
        node.data = "ProgramName";
        match("id", node);
        return node;
    }

    TreeNode DeclarePart() throws IllegalSyntaxException {
        TreeNode node = new TreeNode();
        node.data = "DeclarePart";
        node.addSon(TypeDecpart());
        node.addSon(VarDecpart());
        node.addSon(ProcDecpart());
        return node;
    }
    TreeNode TypeDecpart() throws IllegalSyntaxException {
        TreeNode node = new TreeNode();
        node.data = "TypeDecpart";
        try {
            node.addSon(TypeDec());
        } catch (IllegalSyntaxException e) {
            // empty is ok
        }
        return node;
    }


    TreeNode TypeDec() throws IllegalSyntaxException {
        TreeNode node = new TreeNode();
        node.data = "TypeDec";
        match("type", node);
        node.addSon(TypeDecList());
        return node;
    }

    // TypeId = TypeDef ; TypeDecMore
    TreeNode TypeDecList() throws IllegalSyntaxException {
        TreeNode node = new TreeNode();
        node.data = "TypeDecList";
        node.addSon(TypeId());
        match("=", node);
        node.addSon(TypeDef());
        match(";", node);
        node.addSon(TypeDecMore());
        return node;
    }

    TreeNode TypeDecMore() throws IllegalSyntaxException {
        TreeNode node = new TreeNode();
        node.data = "TypeDecMore";
        try {
            node.addSon(TypeDecList());
        } catch (IllegalSyntaxException e) {
            // empty is ok
        }
        return node;
    }

    TreeNode TypeId() throws IllegalSyntaxException {
        TreeNode node = new TreeNode();
        node.data = "TypeId";
        match("id", node);
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
        }
        try {
            node.addSon(StructrueType());
            return node;
        } catch (IllegalSyntaxException e) {
            // multi condition
        }
        try {
            match("id", node);
            return node;
        } catch (IllegalSyntaxException e) {
            // multi condition
        }
        throw new MultiSyntaxException(line, "TypeDef");
    }

    TreeNode BaseType() throws IllegalSyntaxException {
        TreeNode node = new TreeNode();
        node.data = "BaseType";
        try {
            match("integer", node);
            return node;
        } catch (IllegalSyntaxException e) {
            // multi condition
        }
        try {
            match("char", node);
            return node;
        } catch (IllegalSyntaxException e) {
            // multi condition
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
        }
        try {
            node.addSon(RecType());
            return node;
        } catch (IllegalSyntaxException e) {
            // multi condition
        }
        throw new MultiSyntaxException(line, "StructrueType");
    }

    TreeNode ArrayType() throws IllegalSyntaxException {
        TreeNode node = new TreeNode();
        node.data = "ArrayType";
        match("array", node);
        match("[", node);
        node.addSon(Low());
        match(".", node);
        match(".", node);
        node.addSon(Top());
        match("]", node);
        match("of", node);
        node.addSon(BaseType());
        return node;
    }

    TreeNode Low() throws IllegalSyntaxException {
        TreeNode node = new TreeNode();
        node.data = "Low";
        match("uint", node);
        return node;
    }

    TreeNode Top() throws IllegalSyntaxException {
        TreeNode node = new TreeNode();
        node.data = "Top";
        match("uint", node);
        return node;
    }

    TreeNode RecType() throws IllegalSyntaxException {
        TreeNode node = new TreeNode();
        node.data = "RecType";
        match("record", node);
        node.addSon(FieldDecList());
        match("end", node);
        return node;
    }

    TreeNode FieldDecList() throws IllegalSyntaxException {
        TreeNode node = new TreeNode();
        node.data = "FieldDecList";
        try {
            node.addSon(BaseType());
            node.addSon(IdList());
            match(";", node);
            node.addSon(FieldDecMore());
            return node;
        } catch (IllegalSyntaxException e) {
            // multi condition
        }
        try {
            node.addSon(ArrayType());
            node.addSon(IdList());
            match(";", node);
            node.addSon(FieldDecMore());
            return node;
        } catch (IllegalSyntaxException e) {
            // multi condition
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
        }
        return node;
    }

    TreeNode IdList() throws IllegalSyntaxException {
        TreeNode node = new TreeNode();
        node.data = "IdList";
        match("id", node);
        node.addSon(IdMore());
        return node;
    }

    TreeNode IdMore() throws IllegalSyntaxException {
        TreeNode node = new TreeNode();
        node.data = "IdMore";
        try {
            match(",", node);
            node.addSon(IdList());
        } catch (IllegalSyntaxException e) {
            // multi condition
            if (!e.expected.equals(",")) {
                throw e;
            }
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
        }
        return node;
    }

    TreeNode VarDec() throws IllegalSyntaxException {
        TreeNode node = new TreeNode();
        node.data = "VarDec";
        match("var", node);
        node.addSon(VarDecList());
        return node;
    }

    TreeNode VarDecList() throws IllegalSyntaxException {
        TreeNode node = new TreeNode();
        node.data = "VarDecList";
        node.addSon(TypeDef());
        node.addSon(VarIdList());
        match(";", node);
        node.addSon(VarDecMore());
        return node;
    }

    TreeNode VarDecMore() throws IllegalSyntaxException {
        TreeNode node = new TreeNode();
        node.data = "VarDecMore";
        try {
            node.addSon(VarDecList());
        } catch (IllegalSyntaxException e) {
            // multi condition
        }
        return node;
    }

    TreeNode VarIdList() throws IllegalSyntaxException {
        TreeNode node = new TreeNode();
        node.data = "VarIdList";
        match("id", node);
        node.addSon(VarIdMore());
        return node;
    }

    TreeNode VarIdMore() throws IllegalSyntaxException {
        TreeNode node = new TreeNode();
        node.data = "VarIdMore";
        try {
            match(",", node);
            node.addSon(VarIdList());
        } catch (IllegalSyntaxException e) {
            // multi condition
            if (!e.expected.equals(",")) {
                throw e;
            }
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
        }
        return node;
    }

    TreeNode ProcDec() throws IllegalSyntaxException {
        TreeNode node = new TreeNode();
        node.data = "ProcDec";
        match("procedure", node);
        node.addSon(ProcName());
        match("(", node);
        node.addSon(ParamList());
        match(")", node);
        match(";", node);
        node.addSon(ProcDecPart());
        node.addSon(ProcBody());
        node.addSon(ProcDecMore());
        return node;
    }

    TreeNode ProcDecMore() throws IllegalSyntaxException {
        TreeNode node = new TreeNode();
        node.data = "ProcDecMore";
        try {
            node.addSon(ProcDec());
        } catch (IllegalSyntaxException e) {
            // multi condition
        }
        return node;
    }

    TreeNode ProcName() throws IllegalSyntaxException {
        TreeNode node = new TreeNode();
        node.data = "ProcName";
        match("id", node);
        return node;
    }

    TreeNode ParamList() throws IllegalSyntaxException {
        TreeNode node = new TreeNode();
        node.data = "ParamList";
        try {
            node.addSon(ParamDecList());
        } catch (IllegalSyntaxException e) {
            // multi condition
        }
        return node;
    }

    TreeNode ParamDecList() throws IllegalSyntaxException {
        TreeNode node = new TreeNode();
        node.data = "ParamDecList";
        node.addSon(Param());
        node.addSon(ParamMore());
        return node;
    }

    TreeNode ParamMore() throws IllegalSyntaxException {
        TreeNode node = new TreeNode();
        node.data = "ParamMore";
        try {
            match(";", node);
            node.addSon(ParamDecList());
        } catch (IllegalSyntaxException e) {
            // multi condition
            if (!e.expected.equals(";")) {
                throw e;
            }
        }
        return node;
    }

    TreeNode Param() throws IllegalSyntaxException {
        TreeNode node = new TreeNode();
        node.data = "Param";
        try {
            node.addSon(TypeDef());
            node.addSon(FormList());
            return node;
        } catch (IllegalSyntaxException e) {
            // multi condition
        }
        try {
            match("var", node);
            node.addSon(TypeDef());
            node.addSon(FormList());
            return node;
        } catch (IllegalSyntaxException e) {
            // multi condition
        }
        throw new MultiSyntaxException(line, "Param");
    }

    TreeNode FormList() throws IllegalSyntaxException {
        TreeNode node = new TreeNode();
        node.data = "FormList";
        match("id", node);
        node.addSon(FidMore());
        return node;
    }

    TreeNode FidMore() throws IllegalSyntaxException {
        TreeNode node = new TreeNode();
        node.data = "FidMore";
        try {
            match(",", node);
            node.addSon(FormList());
        } catch (IllegalSyntaxException e) {
            // multi condition
            if (!e.expected.equals(",")) {
                throw e;
            }
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
        match("begin", node);
        node.addSon(StmList());
        match("end", node);
        return node;
    }

    TreeNode StmList() throws IllegalSyntaxException {
        TreeNode node = new TreeNode();
        node.data = "StmList";
        node.addSon(Stm());
        node.addSon(StmMore());
        return node;
    }

    TreeNode StmMore() throws IllegalSyntaxException {
        TreeNode node = new TreeNode();
        node.data = "StmMore";
        try {
            match(";", node);
            node.addSon(StmList());
        } catch (IllegalSyntaxException e) {
            // multi condition
            if (!e.expected.equals(";")) {
                throw e;
            }
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
        }
        try {
            node.addSon(LoopStm());
            return node;
        } catch (IllegalSyntaxException e) {
            // multi condition
        }
        try {
            node.addSon(InputStm());
            return node;
        } catch (IllegalSyntaxException e) {
            // multi condition
        }
        try {
            node.addSon(OutputStm());
            return node;
        } catch (IllegalSyntaxException e) {
            // multi condition
        }
        try {
            node.addSon(ReturnStm());
            return node;
        } catch (IllegalSyntaxException e) {
            // multi condition
        }
        try {
            match("id", node);
            node.addSon(AssCall());
            return node;
        } catch (IllegalSyntaxException e) {
            // multi condition
        }
        throw new MultiSyntaxException(line, "Stm");
    }

    TreeNode AssCall() throws IllegalSyntaxException {
        TreeNode node = new TreeNode();
        node.data = "AssCall";
        try {
            node.addSon(AssignmentRest());
            return node;
        } catch (IllegalSyntaxException e) {
            // multi condition
        }
        try {
            node.addSon(CallStmRest());
            return node;
        } catch (IllegalSyntaxException e) {
            // multi condition
        }
        throw new MultiSyntaxException(line, "AssCall");
    }

    TreeNode AssignmentRest() throws IllegalSyntaxException {
        TreeNode node = new TreeNode();
        node.data = "AssignmentRest";
        node.addSon(VariMore());
        match(":=", node);
        node.addSon(Exp());
        return node;
    }

    TreeNode ConditionalStm() throws IllegalSyntaxException {
        TreeNode node = new TreeNode();
        node.data = "ConditionalStm";
        match("if", node);
        node.addSon(RelExp());
        match("then", node);
        node.addSon(StmList());
        match("else", node);
        node.addSon(StmList());
        match("fi", node);
        return node;
    }

    TreeNode LoopStm() throws IllegalSyntaxException {
        TreeNode node = new TreeNode();
        node.data = "LoopStm";
        match("while", node);
        node.addSon(RelExp());
        match("do", node);
        node.addSon(StmList());
        match("endwh", node);
        return node;
    }

    TreeNode InputStm() throws IllegalSyntaxException {
        TreeNode node = new TreeNode();
        node.data = "InputStm";
        match("read", node);
        match("(", node);
        node.addSon(Invar());
        match(")", node);
        return node;
    }

    TreeNode Invar() throws IllegalSyntaxException {
        TreeNode node = new TreeNode();
        node.data = "Invar";
        match("id", node);
        return node;
    }

    TreeNode OutputStm() throws IllegalSyntaxException {
        TreeNode node = new TreeNode();
        node.data = "OutputStm";
        match("write", node);
        match("(", node);
        node.addSon(Exp());
        match(")", node);
        return node;
    }

    TreeNode ReturnStm() throws IllegalSyntaxException {
        TreeNode node = new TreeNode();
        node.data = "ReturnStm";
        match("return", node);
        return node;
    }

    TreeNode CallStmRest() throws IllegalSyntaxException {
        TreeNode node = new TreeNode();
        node.data = "CallStmRest";
        match("(", node);
        node.addSon(ActParamList());
        match(")", node);
        return node;
    }

    TreeNode ActParamList() throws IllegalSyntaxException {
        TreeNode node = new TreeNode();
        node.data = "ActParamList";
        try {
            node.addSon(Exp());
            node.addSon(ActParamMore());
        } catch (IllegalSyntaxException e) {
            // multi condition
//            if (!e.expected.equals(",")) {
//                throw e;
//            }
        }
        return node;
    }

    TreeNode ActParamMore() throws IllegalSyntaxException {
        TreeNode node = new TreeNode();
        node.data = "ActParamMore";
        try {
            match(",", node);
            node.addSon(ActParamList());
        } catch (IllegalSyntaxException e) {
            // multi condition
            if (!e.expected.equals(",")) {
                throw e;
            }
        }
        return node;
    }

    TreeNode RelExp() throws IllegalSyntaxException {
        TreeNode node = new TreeNode();
        node.data = "RelExp";
        node.addSon(Exp());
        node.addSon(OtherRelE());
        return node;
    }

    TreeNode OtherRelE() throws IllegalSyntaxException {
        TreeNode node = new TreeNode();
        node.data = "OtherRelE";
        node.addSon(CmpOp());
        node.addSon(Exp());
        return node;
    }

    TreeNode Exp() throws IllegalSyntaxException {
        TreeNode node = new TreeNode();
        node.data = "Exp";
        node.addSon(Term());
        node.addSon(OtherTerm());
        return node;
    }

    TreeNode OtherTerm() throws IllegalSyntaxException{
        TreeNode node = new TreeNode();
        node.data = "OtherTerm";
        try {
            node.addSon(AddOp());
            node.addSon(Exp());
        } catch (IllegalSyntaxException e) {
            // multi condition
//            if (!e.expected.equals(",")) {
//                throw e;
//            }
        }
        return node;
    }

    TreeNode Term() throws IllegalSyntaxException {
        TreeNode node = new TreeNode();
        node.data = "Term";
        node.addSon(Factor());
        node.addSon(OtherFactor());
        return node;
    }

    TreeNode OtherFactor() throws IllegalSyntaxException {
        TreeNode node = new TreeNode();
        node.data = "OtherFactor";
        try {
            node.addSon(MultOp());
            node.addSon(Term());
        } catch (IllegalSyntaxException e) {
            // multi condition
//            if (!e.expected.equals(",")) {
//                throw e;
//            }
        }
        return node;
    }

    TreeNode Factor() throws IllegalSyntaxException {
        TreeNode node = new TreeNode();
        node.data = "Factor";
        try {
            match("(", node);
            node.addSon(Exp());
            match(")", node);
            return node;
        } catch (IllegalSyntaxException e) {
            // multi condition
        }
        try {
            match("uint", node);
            return node;
        } catch (IllegalSyntaxException e) {
            // multi condition
        }
        try {
            node.addSon(Variable());
            return node;
        } catch (IllegalSyntaxException e) {
            // multi condition
        }
        throw new MultiSyntaxException(line, "Factor");
    }

    TreeNode Variable() throws IllegalSyntaxException {
        TreeNode node = new TreeNode();
        node.data = "Variable";
        match("id", node);
        node.addSon(VariMore());
        return node;
    }

    TreeNode VariMore() throws IllegalSyntaxException {
        TreeNode node = new TreeNode();
        node.data = "VariMore";
        try {
            match("[", node);
            node.addSon(Exp());
            match("]", node);
            return node;
        } catch (IllegalSyntaxException e) {
            // multi condition
            if (!e.expected.equals("[")) {
                throw e;
            }
        }
        try {
            match(".", node);
            node.addSon(FieldVar());
            return node;
        } catch (IllegalSyntaxException e) {
            // multi condition
            if (!e.expected.equals(".")) {
                throw e;
            }
        }
        return node;
    }

    TreeNode FieldVar() throws IllegalSyntaxException {
        TreeNode node = new TreeNode();
        node.data = "FieldVar";
        match("id", node);
        node.addSon(FieldVarMore());
        return node;
    }

    TreeNode FieldVarMore() throws IllegalSyntaxException {
        TreeNode node = new TreeNode();
        node.data = "FieldVarMore";
        try {
            match("[", node);
            node.addSon(Exp());
            match("]", node);
        } catch (IllegalSyntaxException e) {
            // multi condition
            if (!e.expected.equals("[")) {
                throw e;
            }
        }
        return node;
    }

    TreeNode CmpOp() throws IllegalSyntaxException {
        TreeNode node = new TreeNode();
        node.data = "CmpOp";
        try {
            match("<", node);
            return node;
        } catch (IllegalSyntaxException e) {
            // multi condition
        }
        try {
            match(">", node);
            return node;
        } catch (IllegalSyntaxException e) {
            // multi condition
        }
        try {
            match("=", node);
            return node;
        } catch (IllegalSyntaxException e) {
            // multi condition
        }
        throw new MultiSyntaxException(line, "CmpOp");
    }

    TreeNode AddOp() throws IllegalSyntaxException {
        TreeNode node = new TreeNode();
        node.data = "AddOp";
        try {
            match("+", node);
            return node;
        } catch (IllegalSyntaxException e) {
            // multi condition
        }
        try {
            match("-", node);
            return node;
        } catch (IllegalSyntaxException e) {
            // multi condition
        }
        throw new MultiSyntaxException(line, "AddOp");
    }

    TreeNode MultOp() throws IllegalSyntaxException {
        TreeNode node = new TreeNode();
        node.data = "MultOp";
        try {
            match("*", node);
            return node;
        } catch (IllegalSyntaxException e) {
            // multi condition
        }
        try {
            match("/", node);
            return node;
        } catch (IllegalSyntaxException e) {
            // multi condition
        }
        throw new MultiSyntaxException(line, "MultOp");
    }
}
