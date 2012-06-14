package jasmine.gp.interfaces;


import jasmine.gp.Evolve;
import jasmine.gp.Individual;
import jasmine.gp.multiclass.ClassResults;
import jasmine.gp.params.GPParams;
import jasmine.gp.problems.DataStack;
import jasmine.gp.problems.Problem;

import javax.swing.*;
import java.awt.*;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.StringSelection;
import java.awt.datatransfer.Clipboard;
import java.awt.image.BufferedImage;
import java.util.Vector;
import java.io.File;

/**
 * Displays the progress of GP in real time in a graphical interface.
 *
 * @author Olly Oechsle, University of Essex, Date: 17-Jan-2007
 * @version 1.0
 */
public class GraphicalListener extends GPActionListener {

    public static boolean noInteractiveMessages = false;

    protected MainWindow window;
    protected ImageWindow imageWindow;
    protected ClassResultsWindow classWindow;
    protected long startTime;
    protected boolean displayOutput = true;
    protected Vector<GenerationResult> results = new Vector<GenerationResult>(10);

    protected int index = 0;
    protected int subgeneration = 0;

    protected Evolve e;
    protected Problem p;

    protected long lastTime = 0;

    protected double previousFitness = -1;

    protected boolean alreadyWorking = false;

    protected int currentGeneration = 0;

    public GraphicalListener() {
        try {
            // Go for the system look and feel if available
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception ex) {
            System.out.println("Could not load system look and feel.\nFor nicest GUI, please run using Java 1.6");
        }
        //POEY comment: (method) Segmentation - Genetic Programming window
        window = new MainWindow(this);
    }

    public void hide() {
        window.onExit();
    }

    public int getGeneration() {
        return currentGeneration;
    }

    public GPActionListener copy() {
        return new GraphicalListener();
    }

    /**
     * Called once the evolve object starts the evolution run.
     */
    public void onStartEvolution(Evolve e, Problem p) {   	
        finished = false;
        this.e = e;
        this.p = p;
        startTime = System.currentTimeMillis();

        window.setTitle(p.getName() + " - Genetic Programming");
    }

    /**
     * Called once the GP process terminates or is halted by the user.
     */
    public void onStopped() {
        finished = true;        
        long time = (System.currentTimeMillis() - startTime) / 1000;
        if (!noInteractiveMessages)       
        	JOptionPane.showMessageDialog(null, "Genetic Programming stopped.\n" + time + " seconds elapsed.");
        //POEY
        else
        	noInteractiveMessages = false;

    }

    /**
     * Called every time a new generation is created.
     */
    public void onGenerationStart(int generation) {   
        currentGeneration = generation;
        if (window != null) window.updateGenerationNumber(generation);
    }

    /**
     * Called asynchronously by problems when they find something worth reporting.
     */
    public void onGoodIndividual(Individual ind) {
        if (ind != null) {
            if (window != null) window.displayIndividual(ind);
            subgeneration++;
            results.add(new GenerationResult(currentGeneration, ind, System.currentTimeMillis() - startTime));
        }
    }

    /**
     * Records how many individuals have been evaluated.
     */
    public void incrementIndividualEvaluations(int evaluations) {
        super.incrementIndividualEvaluations(evaluations);
        if (System.currentTimeMillis() - lastTime > 1000) {
            if (window != null) {
                window.individuals.setText("Count: " + getTotalIndividualsEvaluated());
                window.updateTime();
            }
            lastTime = System.currentTimeMillis();
        }
    }

    /**
     * Called once all individuals have been evaluated in the generation.
     */
    public synchronized void onGenerationEnd(int generation) {
        if (bestIndividual != null) {       	
            results.add(new jasmine.gp.interfaces.GenerationResult(generation, bestIndividual, System.currentTimeMillis() - startTime));
            if (window != null && window.resultsSummary != null) {
                window.resultsSummary.refresh();
            }
        }

        if (window != null) {
            //window.individuals.setText("Count: " + getTotalIndividualsEvaluated());
            window.displayIndividual(bestIndividual);

            if (displayOutput && !alreadyWorking) {

                displayOutput(generation, false);

            }

        }
    }

    public void displayOutput(final int generation, final boolean force) {

        SwingUtilities.invokeLater(new Thread() {
            public void run() {
                alreadyWorking = true;

                if (force || bestIndividual.getKozaFitness() != previousFitness || imageWindow.image.getImage() == null) {
                	
                    Object o = p.describe(GraphicalListener.this, bestIndividual, new DataStack(), index);

                    if (o != null) {

                        // Problems which produce segmentation or other imaging functions can produce preview images
                        // of what their output will look like. If this is the case, then display the image in a separate
                        // window.
                        if (o instanceof BufferedImage) {

                            if (imageWindow == null) imageWindow = new ImageWindow(GraphicalListener.this);
                            previousFitness = bestIndividual.getKozaFitness();

                            imageWindow.setImage((BufferedImage) o);
                            imageWindow.lblMessage.setText("Result of generation: " + generation);

                        }

                        // If the problem is a classification problem, it may output a class results object, which
                        // we can also display in a popup window.
                        if (o instanceof ClassResults) {

                            if (classWindow == null) {
                                classWindow = new ClassResultsWindow(GraphicalListener.this, (ClassResults) o);
                            } else {
                                classWindow.update((ClassResults) o);
                            }


                        }

                    }


                }

                alreadyWorking = false;
            }
        });
    }

    /**
     * Called once evolution terminates naturally.
     */
    public void onEndEvolution(int generation, GPParams params) {
        finished = true;
        if (isIdeal) {
            if (!noInteractiveMessages)
            {
                JOptionPane.showMessageDialog(window, "Found ideal individual", "Complete!", JOptionPane.INFORMATION_MESSAGE);
                //POEY
                noInteractiveMessages = true;
            }
        } else {
            if (!noInteractiveMessages)
                JOptionPane.showMessageDialog(window, "Reached " + generation + " generations.", "Complete!", JOptionPane.INFORMATION_MESSAGE);
        }
    }


    /**
     * Called when a fatal error happens.
     */
    public void fatal(String message) {
        JOptionPane.showMessageDialog(window, message, "Fatal Error", JOptionPane.ERROR_MESSAGE);
    }

    /**
     * Called when the evolution process wants to output a message.
     */
    public void message(String message) {
        if (!noInteractiveMessages) {
            JOptionPane.showMessageDialog(window, message, "Message", JOptionPane.INFORMATION_MESSAGE);
        }
    }

    protected void writeToClipboard(String writeMe) {
        // get the system clipboard
        Clipboard systemClipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
        // set the textual content on the clipboard
        Transferable transferableText = new StringSelection(writeMe);
        systemClipboard.setContents(transferableText, null);
    }

        public void saveIndividual() {
            saveIndividual(getBestIndividual());
        }

    public void saveIndividual(Individual ind) {
        if (ind == null) return;
        if (window.chooser == null) {
            window.chooser = new JFileChooser(System.getProperty("user.home"));
        }
        int r = window.chooser.showSaveDialog(window);
        if (r == JFileChooser.APPROVE_OPTION) {
            File f = window.chooser.getSelectedFile();
            ind.save(f);
        }
    }

    public void dispose() {
        if (window != null) window.dispose();
        if (imageWindow != null) imageWindow.dispose();
        if (classWindow != null) classWindow.dispose();
    }

}
