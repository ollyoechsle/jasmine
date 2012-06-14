package jasmine.gp.util;


import jasmine.gp.Evolve;
import jasmine.gp.interfaces.GPActionListener;
import jasmine.gp.params.GPParams;
import jasmine.gp.problems.Problem;

import javax.swing.*;
import javax.swing.border.EtchedBorder;
import javax.swing.border.Border;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.event.ActionListener;

/**
 * Sets up parameters for GP using a nice graphical interface so you don't have to
 * get your hands dirty.
 *
 * @author Olly Oechsle, University of Essex, Date: 24-Apr-2007
 * @version 1.0
 */
public class GPStartDialog extends JDialog implements ActionListener {

    protected JTextField maxTime;
    protected JTextField populationSize, generations;
    protected JTextField tournamentSize;
    protected JTextField crossoverRate, mutationRate;
    protected JTextField pointMutationRate, ERCMutationRate, ERCJitterRate;
    protected JCheckBox autoRangeTyping;
    protected JCheckBox optimisation;
    protected JCheckBox ercoptimisation;
    protected JTextField treeSize, eliteCount;
    protected JComboBox treeBuilder, generationGap;
    protected JButton ok, cancel, reset, advanced;
    protected JTextField minTreeDepth, maxTreeDepth, terminalVsErcProbability;
    protected JTextField randomSeed;

    private Problem p;
    private GPActionListener gpinterface;
    private GPParams params;
    private JFrame owner;

    private long defaultSeed = Evolve.seed;

    //POEY comment: a window for GP parameters setting when a user choose a listbox Advanced Settings
    public GPStartDialog(final JFrame owner, Problem p, GPActionListener gpinterface) {

        super(owner);

        this.owner = owner;
        
        // the evolve object calls the problem's init params function which we need to initialise the form
        // we won't run the evolve at this time though
        Evolve e = new Evolve(p, gpinterface, new GPParams());
        this.params = e.getParams();
        this.gpinterface = gpinterface;
        this.p = p;

        Container c = getContentPane();

        Border loweredetched = BorderFactory.createEtchedBorder(EtchedBorder.LOWERED);

        JPanel basicParameters = new JPanel(new GridLayout(5, 2));
        basicParameters.setBorder(BorderFactory.createTitledBorder(loweredetched, "Basic Parameters"));

        populationSize = new JTextField(String.valueOf(params.getPopulationSize()));
        basicParameters.add(new JLabel("Population Size"));
        basicParameters.add(populationSize);

        generations = new JTextField(String.valueOf(params.getGenerations()));
        basicParameters.add(new JLabel("Generations"));
        basicParameters.add(generations);

        tournamentSize = new JTextField(String.valueOf(params.getTournamentSize()));
        basicParameters.add(new JLabel("Tournament Size"));
        basicParameters.add(tournamentSize);

        maxTime = new JTextField(String.valueOf(params.getMaxTime()));
        basicParameters.add(new JLabel("Max Time (s)"));
        basicParameters.add(maxTime);

        randomSeed = new JTextField(String.valueOf(Evolve.seed));
        basicParameters.add(new JLabel("Random Seed"));
        basicParameters.add(randomSeed);

        JPanel treeParameters = new JPanel(new GridLayout(5, 2));
        treeParameters.setBorder(BorderFactory.createTitledBorder(loweredetched, "Tree Parameters"));

        treeBuilder =  new JComboBox(GPParams.treeBuilderNames);
        treeBuilder.setSelectedIndex(params.getTreeBuilderID());
        treeParameters.add(new JLabel("Tree Builder"));
        treeParameters.add(treeBuilder);

        minTreeDepth = new JTextField(String.valueOf(params.getMinTreeDepth()));
        treeParameters.add(new JLabel("Min Tree Depth"));
        treeParameters.add(minTreeDepth);

        maxTreeDepth = new JTextField(String.valueOf(params.getMaxTreeDepth()));
        treeParameters.add(new JLabel("Max Tree Depth"));
        treeParameters.add(maxTreeDepth);
        
        terminalVsErcProbability = new JTextField(String.valueOf(params.getTerminalVsERCProbability()));
        treeParameters.add(new JLabel("Feature vs. Erc %"));
        treeParameters.add(terminalVsErcProbability);

        optimisation = new JCheckBox("On", params.isOptimisationEnabled());
        treeParameters.add(new JLabel("Tree Optimisation"));
        treeParameters.add(optimisation);


        JPanel geneticOperators = new JPanel(new GridLayout(5, 2));
        geneticOperators.setBorder(BorderFactory.createTitledBorder(loweredetched, "Genetic Operators"));

        crossoverRate = new JTextField(String.valueOf(params.getCrossoverProbability()));
        geneticOperators.add(new JLabel("Crossover Rate %"));
        geneticOperators.add(crossoverRate);

        mutationRate = new JTextField(String.valueOf(params.getMutationProbability()));
        geneticOperators.add(new JLabel("Mutation Rate %"));
        geneticOperators.add(mutationRate);

        pointMutationRate = new JTextField(String.valueOf(params.getPointMutationProbability()));
        geneticOperators.add(new JLabel("Point Mutation Rate %"));
        geneticOperators.add(pointMutationRate);

        ERCMutationRate = new JTextField(String.valueOf(params.getERCmutateProbability()));
        geneticOperators.add(new JLabel("ERC Mutation Rate %"));
        geneticOperators.add(ERCMutationRate);

        ERCJitterRate = new JTextField(String.valueOf(params.getERCjitterProbability()));
        geneticOperators.add(new JLabel("ERC Jitter Rate %"));
        geneticOperators.add(ERCJitterRate);


        JPanel options = new JPanel(new GridLayout(4, 2));
        options.setBorder(BorderFactory.createTitledBorder(loweredetched, "Options"));
      
        generationGap =  new JComboBox(GPParams.generationGapNames);
        generationGap.setSelectedIndex(params.getGenerationGapMethod());
        options.add(new JLabel("Generation Gap"));
        options.add(generationGap);

        eliteCount = new JTextField(String.valueOf(params.getEliteCount()));
        options.add(new JLabel("Elite Count"));
        options.add(eliteCount);

        autoRangeTyping = new JCheckBox("On", params.isAutomaticRangeTypingEnabled());
        options.add(new JLabel("Auto Range Typing"));
        options.add(autoRangeTyping);

        ercoptimisation = new JCheckBox("On", params.isERCOptimisationEnabled());
        options.add(new JLabel("ERC Optimisation"));
        options.add(ercoptimisation);

        JPanel form = new JPanel(new GridLayout(4, 1));
        form.add(basicParameters);
        form.add(treeParameters);
        form.add(geneticOperators);
        form.add(options);

        c.add(form, BorderLayout.CENTER);

        JPanel buttons = new JPanel(new FlowLayout(FlowLayout.RIGHT));

        advanced = new JButton("Advanced...");
        advanced.addActionListener(this);

        ok = new JButton("Start");
        ok.addActionListener(this);
        cancel = new JButton("Close");
        cancel.addActionListener(this);
        reset = new JButton("Reset");
        reset.addActionListener(this);

        buttons.add(advanced);
        buttons.add(reset);
        buttons.add(cancel);
        buttons.add(ok);

        c.add(buttons, BorderLayout.SOUTH);

        addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                onCancel();
                if (owner == null) {
                    System.exit(0);
                }
            }
        });

        setTitle("GP Parameters");
        setSize(420, 600);
        setLocation(200, 200);
        setVisible(true);

    }

    public void actionPerformed(ActionEvent e) {

        if (e.getSource() == ok) onOK();
        if (e.getSource() == cancel) onCancel();
        if (e.getSource() == reset) onReset();
        if (e.getSource() == advanced) onAdvanced();

    }

    public void onAdvanced() {
        new NodeSelectionDialog(owner, params);
    }

    public void onOK() {

        try {

            Evolve e = new Evolve(p, gpinterface, params);

            if (!randomSeed.getText().trim().equals("")) {
                Evolve.seed = Long.parseLong(randomSeed.getText());
            } else {
                Evolve.seed = -1;
            }

            params.setMaxTime(Integer.parseInt(maxTime.getText()));

            params.setPopulationSize(Integer.parseInt(populationSize.getText()));
            params.setGenerations(Integer.parseInt(generations.getText()));
            params.setTournamentSize(Integer.parseInt(tournamentSize.getText()));

            params.setTreeBuilder(treeBuilder.getSelectedIndex());
            params.setMinTreeDepth(Integer.parseInt(minTreeDepth.getText()));
            params.setMaxTreeDepth(Integer.parseInt(maxTreeDepth.getText()));
            params.setOptimisationEnabled(optimisation.isSelected());
            params.setTerminalVsERCProbability(Double.parseDouble(terminalVsErcProbability.getText()));

            params.setCrossoverProbability(Double.parseDouble(crossoverRate.getText()));
            params.setMutationProbability(Double.parseDouble(mutationRate.getText()));

            params.setPointMutationProbability(Double.parseDouble(pointMutationRate.getText()));
            params.setERCmutateProbability(Double.parseDouble(ERCMutationRate.getText()));
            params.setERCjitterProbability(Double.parseDouble(ERCJitterRate.getText()));

            params.setAutomaticRangeTypingEnabled(autoRangeTyping.isSelected());
            params.setGenerationGapMethod(generationGap.getSelectedIndex());
            params.setEliteCount(Integer.parseInt(eliteCount.getText()));
            params.setERCOptimisationEnabled(ercoptimisation.isSelected());

            e.start();

            dispose();

        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, e.getMessage());
        }

    }

    public void onCancel() {
        dispose();
        if (owner == null) {
            System.exit(0);
        }
    }

    public void onReset() {

        randomSeed.setText(String.valueOf(defaultSeed));

        maxTime.setText(String.valueOf(params.getMaxTime()));
        populationSize.setText(String.valueOf(params.getPopulationSize()));
        generations.setText(String.valueOf(params.getGenerations()));
        tournamentSize.setText(String.valueOf(params.getTournamentSize()));
        eliteCount.setText(String.valueOf(params.getEliteCount()));

        treeBuilder.setSelectedIndex(params.getTreeBuilderID());
        minTreeDepth.setText(String.valueOf(params.getMinTreeDepth()));
        maxTreeDepth.setText(String.valueOf(params.getMaxTreeDepth()));
        optimisation.setSelected(params.isOptimisationEnabled());
        terminalVsErcProbability.setText(String.valueOf(params.getTerminalVsERCProbability()));

        crossoverRate.setText(String.valueOf(params.getCrossoverProbability()));
        mutationRate.setText(String.valueOf(params.getMutationProbability()));
        
        pointMutationRate.setText(String.valueOf(params.getPointMutationProbability()));
        ERCMutationRate.setText(String.valueOf(params.getERCmutateProbability()));
        ERCJitterRate.setText(String.valueOf(params.getERCjitterProbability()));

        autoRangeTyping = new JCheckBox("On", params.isAutomaticRangeTypingEnabled());
        ercoptimisation.setSelected(params.isERCOptimisationEnabled());
        treeBuilder.setSelectedIndex(params.getGenerationGapMethod());

    }

}

