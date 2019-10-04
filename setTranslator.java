package teamTranslator;

import java.util.*;
import java.io.*;

public class setTranslator {

    private static setScannerSolution sc;

    private static PrintWriter dest;
    private static HashMap<String, String> typeTable;
    private static Boolean processingNat;
    public static int varNum;
    private static String setExpResultVariable;
    private static int recursionCounter; // initialized to 0
    private static StringBuilder setResult;

    // <program> ::= PROGRAM ID VAR <dec> BEGIN <prog st list> END <out>;
    // Lookahead: PROGRAM
    private static void program() throws Exception {
        // attempts the translation of the source file
        // into a Java source file
        Token tk = sc.lookahead();
        sc.consume();

        if (Token.programSet.contains(tk.getTokenType())) {
            tk = sc.lookahead();
            dest = new PrintWriter("out/" + tk.getTokenString() + ".java");
            sc.consume();
            sc.consume(); // these are terminals
            dest.append("public class " + tk.getTokenString() + "{\n\n\n" + dec()
                    + "    public static void main(String[] args){ \n\n" + compute() + "\n    "
                    + "    System.out.println(" + display() + ".toString());\n\n" + "    }\n}");

        }

    }

    private static String display() {
        Token tk = sc.lookahead();
        StringBuilder res = new StringBuilder();
        int counter = 0;
        boolean setdiff = false;
        if (tk.getTokenType() == Token.END) {
            sc.consume();
            tk = sc.lookahead();
        }

        while (tk.getTokenType() != Token.PERIOD && tk.getTokenType() != Token.EOF) {
            int varCounter = 0; // for assigning temporary variables
            if (tk.getTokenType() == Token.ID) {

                res.append(tk.getTokenString());
                if (setdiff) {
                    res.append(".complement");
                    setdiff = false;
                }
                sc.consume();
                tk = sc.lookahead();
                while (counter > 0) {
                    res.append(")");
                    counter--;
                }
            } else if (tk.getTokenType() == Token.UNION) {
                res.append(".union(");
                sc.consume();
                tk = sc.lookahead();
                counter++;
            } else if (tk.getTokenType() == Token.SETDIFFERENCE) {
                res.append(".intersect(");
                setdiff = true;
                counter++;
                sc.consume();
                tk = sc.lookahead();
            } else if (tk.getTokenType() == Token.INTERSECTION) {
                res.append(".intersect(");
                sc.consume();
                tk = sc.lookahead();
                counter++;
            } else if (tk.getTokenType() == Token.COMPLEMENT) {
                res.append(tk.getTokenString());
                sc.consume();
                tk = sc.lookahead();
            }

        }
        System.out.println("    System.out.println(" + res.toString() + ")  ");
        return res.toString();
    }

    private static String compute() {
        StringBuilder statements = new StringBuilder();
        Token tk = sc.lookahead();
        System.out.println("Commppuuutee");
        System.out.println(tk.getTokenType());
        if (Token.progStListSet.contains(tk.getTokenType())) {
            if (tk.getTokenType() == 4) { // end
                // nullable
                statements.append("");
            } else if (tk.getTokenType() == 1) { // ID

                statements.append(listStatement());
            }
            sc.consume(); // end

        }
        System.out.println("Compute result: " + statements.toString());
        return statements.toString();
    }

    private static String listStatement() {
        String statementList = new String();
        Token tk = sc.lookahead();

        if (Token.progStListSet.contains(tk.getTokenType())) {
            statementList = neStList();

        } else if (tk.getTokenType() == 4) {
            statementList = "";
        }
        return statementList;
    }

    private static String neStList() {
        Token tk = sc.lookahead();
        StringBuilder neStatements = new StringBuilder();
        if (Token.neStListSet.contains(tk.getTokenType())) {
            neStatements.append(statement());

        }

        return neStatements.toString();
    }

    private static String statement() {
        Token tk = sc.lookahead();
        StringBuilder statement = new StringBuilder();
        StringBuilder prevStatement = new StringBuilder();
        while (Token.stSet.contains(tk.getTokenType())) {

            if (tk.getTokenType() == 1) {
                prevStatement = statement;
                statement.append(assign());
                if (statement.toString() != prevStatement.toString())
                    statement.append(";\n\n");
                tk = sc.lookahead();
            } else if (tk.getTokenType() == Token.IF) {
                // TODO
            }
        }

        return statement.toString();
    }

// lookahead: ID
// format: ID ASSIGN <set exp>
    private static String assign() {
        Token tk = sc.lookahead();
        StringBuilder assign = new StringBuilder();
        String id = tk.getTokenString();
        boolean comp = false;

        if (Token.asgnSet.contains(tk.getTokenType())) {
            if (typeTable.get(id) == "set") {
                assign.append(setAssign());
            } else if (typeTable.get(id) == "int") {
                assign.append(intAssign());
            } else if ((typeTable.get(id) == null)) {
                // TODO: throw exception
            }

        }
        return assign.toString();
    }

    private static String intAssign() {
        // TODO Auto-generated method stub
        return null;
    }

// lookahead: ID
// format: ID ASSIGN <set exp>
    private static String setAssign() {
        Token tk = sc.lookahead();
        StringBuilder assign = new StringBuilder();
        if (Token.asgnSet.contains(tk.getTokenType())) {
            String id = tk.getTokenString();
            sc.consume(); // ID
            assign.append("        " + id);
            sc.consume(); // assign
            assign.append(" = ");
            assign.append(setExp());
        }

        return assign.toString();
    }

// lookahead: ID, LEFTBRACE, LEFTPAREN, COMPLEMENT, CMP; same
// format: <setlevel 2> (SETDIFFERENCE <set level 2>)*
// (while: lookahead = SETDIFFERENCE...) 
    private static String setExp() {
        Token tk = sc.lookahead();
        String res = "";
        int counter = 0;
        StringBuilder setExp = new StringBuilder();
        if (Token.setExpSet.contains(tk.getTokenType())) {
            res = setLevel2();
            setExp.append(res);
            while (sc.lookahead().getTokenType() == Token.SETDIFFERENCE) {
                setExp.append(".intersect(");
                sc.consume(); // UNION
                if (sc.lookahead().getTokenType() == Token.ID) {
                    setExp.append(setLevel2() + ".complement())");
                }
            }
        }

        return setExp.toString();
    }

// lookahead: ID, LEFTBRACE, LEFTPAREN, COMPLEMENT, CMP
// format: <set level 1> (UNION <set level 1>)*
// while lookahead = UNION...
    private static String setLevel2() {
        Token tk = sc.lookahead();
        String res = "";
        StringBuilder setExp = new StringBuilder();
        int counter = 0;
        if (Token.setExpSet.contains(tk.getTokenType())) {
            res = setLevel1();
            setExp.append(res.toString());
        }
        // iterative union processing
        while (sc.lookahead().getTokenType() == Token.UNION) {
            setExp.append(".union(");
            sc.consume(); // UNION
            if (sc.lookahead().getTokenType() == Token.ID) {
                setExp.append(setLevel1() + ")");
            }

        }

        return setExp.toString();
    }

// lookahead: ID, LEFTBRACE, LEFTPAREN, COMPLEMENT, CMP
// format: <set level 0> (INTERSECTION <set level 0>)*
// again.. while lookahead = INTERSECTION....
    private static String setLevel1() {
        Token tk = sc.lookahead();
        String res = "";
        StringBuilder setExp = new StringBuilder();

        int counter = 0;
        if (Token.setExpSet.contains(tk.getTokenType())) {
            res = setLevel0();
            setExp.append(res);
        }

        // iterative intersect processing
        while (sc.lookahead().getTokenType() == Token.INTERSECTION) {
            setExp.append(".intersect(");

            sc.consume(); // INTERSECTION

            if (sc.lookahead().getTokenType() == Token.ID) {
                setExp.append(setLevel0() + ")");
            }
        }

        return setExp.toString();
    }

// lookahead: COMPLEMENT, ID, LEFTBRACE, LEFTPAREN, CMP
// format: <set atomic> (COMPLEMENT <set atomic>)*
    private static String setLevel0() {
        Token tk = sc.lookahead();
        StringBuilder res = new StringBuilder();
        int counter = 0;
        if (Token.setExpSet.contains(tk.getTokenType())) {
            res.append(setAtomic());
        }
        while (sc.lookahead().getTokenType() == Token.COMPLEMENT) {

            sc.consume(); // COMPLEMENT
            if (sc.lookahead().getTokenType() == Token.ID) {
                res.append(setAtomic() + ".complement()");
            }
        }

        return res.toString();
    }

// lookahead: ID | LEFTBRACE, CMP | LEFTPAREN
// format: ID | <set const> | LEFTPAREN <set exp> RIGHTPAREN
    private static String setAtomic() {
        Token tk = sc.lookahead();
        StringBuilder res = new StringBuilder();

        if (Token.setAtomicSet.contains(tk.getTokenType())) {
            // three cases:
            if (tk.getTokenType() == 1) { // ID
                res.append(tk.getTokenString());
                sc.consume(); // consume ID...
                if (sc.lookahead().getTokenType() == Token.SEMICOLON) {
                    sc.consume();
                }
            } else if (tk.getTokenType() == 11 || tk.getTokenType() == 28) { // { or CMP
                // Set declaration
                res.append(setConst());
            } else if (tk.getTokenType() == 13) { // (
                sc.consume(); // LEFTPAREN
                res.append(setExp());
                sc.consume(); // RIGHTPAREN
            } else {
                // exception
            }
        }

        return res.toString();
    }

// lookahead: CMP | LEFTBRACE
// format: <complemented> | <set literal>
    private static String setConst() {
        Token tk = sc.lookahead();
        StringBuilder res = new StringBuilder();

        if (Token.setConstSet.contains(tk.getTokenType())) {
            res.append("(new CofinFin(");
            if (tk.getTokenType() == 11) { // {
                // Set declaration
                res.append("false, ");
                res.append(setLiteral());
            } else if (tk.getTokenType() == 28) { // CMP
                // complemented set declaration!
                res.append("true, ");
                res.append(complemented());
            }
        }
        return res.toString();
    }

//lookahead: LEFTBRACE
//format: LEFTBRACE <nat list> RIGHTBRACE
    private static String setLiteral() {
        Token tk = sc.lookahead();
        StringBuilder res = new StringBuilder();
        System.out.println("HELL\n\n\nO");
        if (Token.setLiteralSet.contains(tk.getTokenType())) {
            sc.consume(); // leftbrace
            res.append(natList());
        }
        return res.toString();
    }

//lookahead: CMP
//format: CMP <set literal>
    private static String complemented() {
        Token tk = sc.lookahead();
        StringBuilder res = new StringBuilder();

        if (Token.complementedSet.contains(tk.getTokenType())) {

            sc.consume(); // CMP
            res.append(setLiteral());

        }
        return res.toString();
    }

    private static String natList() {
        Token tk = sc.lookahead();
        StringBuilder res = new StringBuilder();
        res.append("new int[]{");
        if (Token.natListSet.contains(tk.getTokenType())) {
            if (tk.getTokenType() == Token.RIGHTBRACE) {
                // do nothing
                sc.consume(); // advance cursor
                sc.consume(); // to next set declaration
                res.append("}))");

            } else if (tk.getTokenType() == Token.NATCONST) {
                res.append(tk.getTokenString());
                sc.consume(); // natconst
                tk = sc.lookahead();
                while (tk.getTokenType() == Token.COMMA) {
                    sc.consume(); // ,
                    tk = sc.lookahead();
                    res.append("," + tk.getTokenString());
                    sc.consume(); // natconst
                    tk = sc.lookahead();
                }
                res.append("}))");
                sc.consume(); // }
                if (sc.lookahead().getTokenType() == Token.SEMICOLON)
                    sc.consume(); // ;

            }
        }
        return res.toString();
    }

// <dec> ::= <nat dec> <set dec>;
    // NAT, SET, BEGIN
    private static String dec() {
        Token tk = sc.lookahead();
        String setDec = "";
        String natDec = "";
        natDec = natDec();
        setDec = setDec();
        String dec = natDec.toString() + setDec.toString();
        System.out.println(dec);
        System.out.println(typeTable.toString());
        sc.consume(); // consume terminal
        return dec;
    }

    private static String natDec() {
        Token tk = sc.lookahead();
        StringBuilder natDec = new StringBuilder();
        if (tk.getTokenType() == 3 || tk.getTokenType() == 9) {
            // return null
            return "";
        } else if (tk.getTokenType() == 8) {
            // nat declarations
            sc.consume();
            tk = sc.lookahead();
            // TODO: Errors on typeTable
            typeTable.put(tk.getTokenString(), "nat");
            natDec.append("    private static int " + tk.getTokenString() + ";\n\n");
            sc.consume();
            while (sc.lookahead().getTokenType() == 17) {
                sc.consume();
                if (sc.lookahead().getTokenType() != 1) {
                    // throw exception
                } else {
                    // TODO: Errors on typeTable
                    typeTable.put(sc.lookahead().getTokenString(), "nat");
                    natDec.append("    private static int " + sc.lookahead().getTokenString() + ";\n\n");
                    sc.consume();
                }
            }
            sc.consume(); // consume semicolon
        } else {
            // error
        }

        return natDec.toString();
    }

    private static String setDec() {
        Token tk = sc.lookahead();
        System.out.println("in setdec: " + tk);
        StringBuilder setDec = new StringBuilder();
        if (tk.getTokenType() == 9) {
            // set declarations
            sc.consume();
            tk = sc.lookahead();
            // TODO: Errors on typeTable
            typeTable.put(tk.getTokenString(), "set");
            setDec.append("    private static CofinFin " + tk.getTokenString() + " = new CofinFin();\n\n");
            sc.consume();
            while (sc.lookahead().getTokenType() == 17) {
                sc.consume();
                if (sc.lookahead().getTokenType() != 1) {
                    // throw exception
                } else {
                    // TODO: Errors on typeTable
                    typeTable.put(sc.lookahead().getTokenString(), "set");
                    setDec.append("    private static CofinFin " + sc.lookahead().getTokenString()
                            + " = new CofinFin();\n\n");
                    sc.consume();
                }
            }
            sc.consume(); // consume semicolon
        } else if (tk.getTokenType() == 3) {
            // return null
            return "";
        } else {
            // error
        }

        return setDec.toString();
    }

// you should NOT modify the main method, at least your final submission should
// follow this;
    public static void main(String[] args) throws Exception {

        if (args.length == 0)
            sc = new setScannerSolution(new Scanner(System.in));
        else
            sc = new setScannerSolution(new Scanner(new File(args[0])));

        typeTable = new HashMap<String, String>();
        processingNat = true;
        varNum = 1;
        recursionCounter = 0;
        setResult = new StringBuilder();
        Token currTok = sc.lookahead();

        try {
            if (Token.programSet.contains(currTok.getTokenType())) {
                program();
                // add a comment indicating a successful parse
                if (dest != null) {
                    dest.println("\n// Successful parse.");
                    dest.close();
                }
            } else
                // for the other error messages driven by invalid lookaheads
                // you should write a general method to
                // take the set of tokens that would be right and the line number and
                // have it create and throw the exception with
                // the right error message for the other error messages
                throw new Exception("[line " + currTok.getLineNum() + "]: \"program\" expected.");
        } catch (Exception e) {
            // if the translation throws an exception, it is written to standard error
            System.err.println(e.getMessage());
        }
    }
}
