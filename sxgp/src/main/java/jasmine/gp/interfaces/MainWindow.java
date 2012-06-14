package jasmine.gp.interfaces;

import org.jfree.data.category.DefaultCategoryDataset;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.plot.PlotOrientation;

import jasmine.gp.Individual;
import jasmine.gp.problems.ImagingProblem;
import jasmine.gp.tree.TreeUtils;
import jasmine.gp.treeanimator.TreeFrame;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.event.*;
import java.util.Vector;
import java.util.Date;
import java.text.DecimalFormat;
import java.io.File;


/**
 * Main window - displays graphs and stats and allows the user to interact.
 */
class MainWindow extends JFrame implements ActionListener {

    JLabel generationNumber;
    JLabel fitness;
    JLabel hits, misses, size;
    JLabel time;
    JLabel individuals;

    JMenuItem mnuExit, mnuSave, mnuStop, mnuViewTree;
    JCheckBoxMenuItem mnuDisplayImage, mnuGpSummary;

    JLabel chart;
    JTextArea displayIndividual;
    DefaultCategoryDataset series;

    JButton smack;
    protected GraphicalListener graphicalListener;
    JButton save, stop, close;

    public ResultsSummary resultsSummary;

    //POEY comment: (method) Segmentation - Genetic Programming window
    public MainWindow(GraphicalListener gl) {
        this.graphicalListener = gl;

        try {
            setIconImage(new ImageIcon(getClass().getResource("/sxgp.png")).getImage());
        } catch (Exception e) {
        }


        series = new DefaultCategoryDataset();

        Container c = getContentPane();
        generationNumber = new JLabel("Initialising...");
        fitness = new JLabel();
        fitness.setToolTipText("The fitness of the current best individual. The lower the fitness the better the individual.");
        hits = new JLabel();
        hits.setToolTipText("The number of correct classifications that the current best individual made");
        misses = new JLabel();
        misses.setToolTipText("The number of mistakes that the current best individual made");
        time = new JLabel();
        time.setToolTipText("The amount of time that the evolution process has taken so far");
        size = new JLabel();
        size.setToolTipText("The size of the individual displayed above in nodes");
        individuals = new JLabel();
        individuals.setToolTipText("The number of individuals that have been evaluated");

        JToolBar toolbar = new JToolBar();
        toolbar.setLayout(new BoxLayout(toolbar, BoxLayout.LINE_AXIS));
        toolbar.setOrientation(JToolBar.HORIZONTAL);
        stop = new ToolboxButton("Stop", "Stops the evolutionary run after the end of the generation", "stop.png");
        save = new ToolboxButton("Save", "Saves the individual", "save.png");
        close = new ToolboxButton("Close", "Closes the window", "close.png");
        toolbar.add(stop);
        toolbar.add(save);
        toolbar.add(close);
        toolbar.add(Box.createHorizontalGlue());

        chart = new JLabel();
        c.add(chart, BorderLayout.CENTER);
        c.add(toolbar, BorderLayout.NORTH);

        displayIndividual = new JTextArea("Evaluating: Generation 0");
        JScrollPane pane = new JScrollPane(displayIndividual);
        pane.setPreferredSize(new Dimension(350, -1));
        c.add(pane, BorderLayout.EAST);

        // MENUS

        mnuDisplayImage = new JCheckBoxMenuItem("Display describe() Output");
        mnuDisplayImage.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_D, ActionEvent.CTRL_MASK));
        mnuDisplayImage.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                if (!mnuDisplayImage.isSelected()) {
                    if (graphicalListener.imageWindow != null) graphicalListener.imageWindow.onClose();
                } else {
                    graphicalListener.previousFitness = -1;
                    //POEY comment: a window for showing a gained segmentation result image
                    graphicalListener.imageWindow = new ImageWindow(graphicalListener);
                }
                graphicalListener.displayOutput = mnuDisplayImage.isSelected();
            }
        });
        mnuDisplayImage.setSelected(graphicalListener.displayOutput);

        mnuGpSummary = new JCheckBoxMenuItem("GP Summary");
        mnuGpSummary.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_U, ActionEvent.CTRL_MASK));
        mnuGpSummary.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                //if (resultsSummary == null || !mnuGpSummary.isSelected()) {
                
            	//POEY
                if (resultsSummary == null || mnuGpSummary.isSelected()) {
                	
                    resultsSummary = new  ResultsSummary(MainWindow.this, graphicalListener.results);
                    mnuGpSummary.setSelected(true);
                } else {
                    if (resultsSummary != null)  resultsSummary.dispose();
                    mnuGpSummary.setSelected(false);
                }
            }
        });

        mnuViewTree = new JMenuItem("Tree Structure");
        mnuViewTree.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_T, ActionEvent.CTRL_MASK));
        mnuViewTree.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                if (graphicalListener.getBestIndividual() != null) {
                    new TreeFrame(TreeUtils.getAnimatedTree(graphicalListener.getBestIndividual()));
                }
            }
        });

        mnuSave = new JMenuItem("Save Solution");
        mnuSave.setEnabled(false);
        mnuSave.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_S, ActionEvent.CTRL_MASK));
        mnuSave.addActionListener(this);

        mnuStop = new JMenuItem("Stop Evolution");
        mnuStop.addActionListener(this);

        mnuExit = new JMenuItem("Close");
        mnuExit.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                onExit();
            }
        });

        JMenuItem mnuCopyToClipboard = new JMenuItem("Copy code to clipboard");
        mnuCopyToClipboard.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                graphicalListener.writeToClipboard(displayIndividual.getText());
            }
        });

        JMenu edit = new JMenu("Edit");
        edit.add(mnuCopyToClipboard);


        JMenu file = new JMenu("File");
        file.add(mnuSave);
        file.addSeparator();
        file.add(mnuStop);
        file.add(mnuExit);

        JMenu view = new JMenu("View");
        view.add(mnuGpSummary);
        view.add(mnuViewTree);
        view.add(mnuDisplayImage);


        JMenuBar bar = new JMenuBar();
        bar.add(file);
        bar.add(edit);
        bar.add(view);

        if (graphicalListener.p instanceof ImagingProblem) {
            ImagingProblem ip = ((ImagingProblem) graphicalListener.p);
            int trainingCount = ip.getImageCount();
            JMenu preview = new JMenu("Preview");
            for (int i = 0; i < trainingCount; i++) {
                preview.add(new ImageMenuItem(graphicalListener, ip.getImageName(i), i));
            }
            bar.add(preview);
        }

        setJMenuBar(bar);

        // END MENUS

        JPanel p = new JPanel(new GridLayout(1,7));

        p.add(generationNumber);
        p.add(individuals);
        p.add(fitness);
        p.add(hits);
        p.add(misses);
        p.add(size);
        p.add(time);

        c.add(p, BorderLayout.SOUTH);
        setTitle("Genetic Programming");

        addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                onExit();
            }
        });

        try {
            setIconImage(new ImageIcon(getClass().getResource("/sxgp.png")).getImage());
        } catch (Exception e) {
        }            

        setSize(700, 350);
        setLocationRelativeTo(null);
        setVisible(true);

    }

    JFileChooser chooser = null;

    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == stop || e.getSource() == mnuStop) {
            graphicalListener.e.stopFlag = true;
            mnuStop.setEnabled(false);
        }
        if (e.getSource() == save || e.getSource() == mnuSave) {
            graphicalListener.saveIndividual();
        }
        if (e.getSource() == close) {
            onExit();
        }
    }

    public void onExit() {
        if (graphicalListener.e != null) graphicalListener.e.stopFlag = true;
        if (graphicalListener.imageWindow != null) graphicalListener.imageWindow.dispose();
        dispose();
    }

    public void updateGenerationNumber(int num) {
        generationNumber.setText("Gen: " + num);
    }

    public void updateTime() {
        long elapsed = (System.currentTimeMillis() - graphicalListener.startTime) / 1000;
        time.setText(elapsed + " secs.");
    }

    DecimalFormat f = new DecimalFormat("0.000");

    public void displayIndividual(final Individual ind) {
        if (ind != null) {
            mnuSave.setEnabled(true);
            //if (graphicalListener.p.getGpVariant() == Problem.TYPE_STANDARD_GP) {
                String java = ind.toJava(graphicalListener.p.getMethodSignature(graphicalListener.e.getBestIndividual()));
                if (!java.equals(displayIndividual.getText())) {
                    String info = "// Fitness: " + ind.getKozaFitness() + "\n";
                    info += "// Hits: "  + ind.getHits() + "\n// Mistakes: " + ind.getMistakes() + "\n// Evolved at: " + new Date().toString();

                    displayIndividual.setText(info + "\n\n" + java);
                }
/*            } else {
                SubGenerationalProblem cpp = (SubGenerationalProblem) graphicalListener.p;
                displayIndividual.setText(cpp.getStrongClassifier().toString());
            }*/
            fitness.setText("F: " + f.format(ind.getKozaFitness()));
            hits.setText("TP: " + ind.getHits());
            misses.setText("FP: " + ind.getMistakes());
            size.setText("Size: " + ind.getTreeSize());


            
                        updateChart(ind, graphicalListener.currentGeneration);

/*            if (resultsSummary != null) {
                resultsSummary.displayResults(graphicalListener.results);
            }*/
        }
    }

    boolean updating = false;

    public void updateChart(Individual ind, int generation) {
        updating = true;
        series.addValue(ind.getKozaFitness(), "training", String.valueOf(generation));
        //series.addValue(ind.getTreeSize(), "size", String.valueOf(generation));
        JFreeChart myChart = ChartFactory.createLineChart(null, "Generation", "Error", series, PlotOrientation.VERTICAL, false, false, false);
        myChart.setBackgroundPaint(new Color(255,255,255,0));
        myChart.getCategoryPlot().getRenderer().setSeriesPaint(0, Color.RED);
        myChart.getCategoryPlot().getRenderer().setSeriesPaint(1, Color.BLUE);        
        BufferedImage image = myChart.createBufferedImage(chart.getWidth(), chart.getHeight());
        chart.setIcon(new ImageIcon(image));
        updating = false;

    }

    class ToolboxButton extends JButton {

        protected String icon;

        public ToolboxButton(String text, String tooltip, String icon) {

            this.icon = icon;
            setToolTipText(tooltip);
            setVerticalTextPosition(BOTTOM);
            setHorizontalTextPosition(CENTER);
            setPreferredSize(new Dimension(80, 50));
            setMinimumSize(new Dimension(80, 50));
            setText(text);
            Font f = getFont();
            f.getSize();
            setFont(new Font(f.getName(), f.getStyle(), 10));
            try {
                setIcon(new ImageIcon(getClass().getResource("/" + icon)));
            } catch (Exception e) {
                //System.err.println("Could load load icon: " + icon);
            }
            addActionListener(MainWindow.this);
            putClientProperty("JButton.buttonType", "text");

        }

        public String toString() {
            return icon;
        }

    }

}
