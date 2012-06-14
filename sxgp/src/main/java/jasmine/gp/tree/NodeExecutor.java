package jasmine.gp.tree;

import jasmine.gp.problems.DataStack;

/**
 * Executes a program, but faster than normal.
 *
 * @author Olly Oechsle, University of Essex, Date: 10-Nov-2008
 * @version 1.0
 */
public final class NodeExecutor {

    public static final int ADD = 1;
    public static final int SUB = 2;
    public static final int MUL = 3;
    public static final int DIV = 4;

    public static double executeFast(Node tree, DataStack data) {

        if (tree.shortcut == 0) {
            return tree.execute(data);
        } else {
            switch (tree.shortcut) {
                case ADD:
                    data.value = executeFast(tree.child[0], data) + executeFast(tree.child[1], data);
                    return data.value;
                case SUB:
                    data.value = executeFast(tree.child[0], data) - executeFast(tree.child[1], data);
                    return data.value;
                case MUL:
                    data.value = executeFast(tree.child[0], data) * executeFast(tree.child[1], data);
                    return data.value;
                case DIV:
                    double denominator = executeFast(tree.child[1], data);
                    data.value = denominator != 0?  executeFast(tree.child[0], data) / denominator : 0;
                    return data.value;
                default:
                    return tree.execute(data);
            }
        }

    }


}
