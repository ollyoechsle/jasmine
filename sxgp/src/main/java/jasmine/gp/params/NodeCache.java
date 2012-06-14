package jasmine.gp.params;


import jasmine.gp.Evolve;

import java.util.Vector;

class NodeCache {

        double nodeCount = 0;

        public Vector<NodeConstraints> functions;
        public Vector<NodeConstraints> terminals;
        public Vector<NodeConstraints> ercs;

        boolean functionsHaveVariableFitness = false;
        boolean terminalsHaveVariableFitness = false;
        boolean ercsHaveVariableFitness = false;


        protected int returnType;

        public NodeCache(int returnType) {
            functions = new Vector<NodeConstraints>(30);
            terminals = new Vector<NodeConstraints>(20);
            ercs = new Vector<NodeConstraints>();
            this.returnType = returnType;
        }

        public void deregisterNode(NodeConstraints n) {
            functions.remove(n);
            terminals.remove(n);
            ercs.remove(n);
            nodeCount--;
        }

        public void registerNode(NodeConstraints n) {
            n.cacheMemberships.add(this);
            switch (n.getType()) {
                case NodeConstraints.FUNCTION:
                    functions.add(n);
                    if (n.getFitness() != NodeConstraints.DEFAULT_FITNESS) functionsHaveVariableFitness = true;
                    break;
                case NodeConstraints.TERMINAL:
                    terminals.add(n);
                    if (n.getFitness() != NodeConstraints.DEFAULT_FITNESS) terminalsHaveVariableFitness = true;
                    break;
                default:
                    if (n.getFitness() != NodeConstraints.DEFAULT_FITNESS) ercsHaveVariableFitness = true;
                    // add everything else to the ercs, including ADFs
                    ercs.add(n);
            }
            nodeCount++;
        }

        public int chooseNodeType(GPParams p) {            
            double terminalProbability = p.getTerminalProbability();
            if (terminalProbability < 0) {
                // calculate probabilities
                terminalProbability = functions.size() / nodeCount;
            }
            if (Evolve.getRandomNumber() >= terminalProbability) {
                return NodeConstraints.FUNCTION;
            } else {
                if (Evolve.getRandomNumber() < p.getTerminalVsERCProbability()) {
                    // choose a terminal 
                    return NodeConstraints.TERMINAL;
                } else {
                    // choose the ERC
                    return NodeConstraints.ERC;
                }
            }
        }

        public Vector<NodeConstraints> getNodes(int type) {
            switch (type) {
                case NodeConstraints.FUNCTION:
                    return functions;
                case NodeConstraints.TERMINAL:
                    return terminals;
                default:
                    return ercs;
            }
        }

        public boolean hasVariableFitness(int type) {
            switch (type) {
                case NodeConstraints.FUNCTION:
                    return functionsHaveVariableFitness;
                case NodeConstraints.TERMINAL:
                    return terminalsHaveVariableFitness;
                default:
                    return ercsHaveVariableFitness;
            }
        }

        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            NodeCache nodeCache = (NodeCache) o;

            if (returnType != nodeCache.returnType) return false;

            return true;
        }

        public int hashCode() {
            return returnType;
        }

}