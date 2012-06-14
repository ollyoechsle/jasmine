package jasmine.gp;


import jasmine.gp.tree.Node;
import jasmine.gp.tree.TreeUtils;

import java.util.Vector;

public class KozaNodeSelector implements Selector {

    public static final double terminals = 0.1;
    public static final double nonTerminals = 0.9;
    public static final double root = 0;

    public Node select(Node tree, int returnType) {

        Vector<Node> notes = TreeUtils.getNodes(tree, returnType);

        double p = Evolve.getRandomNumber();
        if (p <= terminals) {
            // get a terminal
            Vector<Node> terminals = new Vector<Node>();
            for (int i = 0; i < notes.size(); i++) {
                Node node = notes.elementAt(i);
                if (node.isTerminal()) {
                    terminals.add(node);
                }
            }
            if (terminals.size() > 0) {
                return terminals.elementAt((int) (Evolve.getRandomNumber() * terminals.size()));
            }
        } else {
            // get a function
            // get a terminal
            Vector<Node> functions = new Vector<Node>();
            for (int i = 0; i < notes.size(); i++) {
                Node node = notes.elementAt(i);
                if (!node.isTerminal()) {
                    functions.add(node);
                }
            }
            if (functions.size() > 0) {
                return functions.elementAt((int) (Evolve.getRandomNumber() * functions.size()));
            }
        }

        if (notes.size() > 0) {
            return notes.elementAt((int) (Evolve.getRandomNumber() * notes.size()));
        }

        return null;


    }

}

