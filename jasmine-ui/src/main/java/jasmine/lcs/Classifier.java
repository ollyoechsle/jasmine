package jasmine.lcs;

import jasmine.lcs.Action;

/**
 * <p/>
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version,
 * provided that any use properly credits the author.
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details at http://www.gnu.org
 * </p>
 *
 * @author Olly Oechsle, University of Essex, Date: 14-Jan-2008
 * @version 1.0
 */
public class Classifier {

    public double p;
    public double e;
    public double fitness;

    public double accuracy;
    private double relativeAccuracy;

    public int numerosity = 1;

    public Condition condition;
    public Action action;

    public int matchSizeEstimate;

    public int timestamp;
    public int experience = 0;

    public Classifier(Condition condition, Action action, int timestamp) {

        this.condition = condition;
        this.action = action;
        this.timestamp = timestamp;

        // start with default values
        p = 20;
        e = 0.5;
        fitness = 0.01;

        previousPValues = new Average();
        relativeAccuracyValues = new Average();
        previousSomethingValues = new Average();

    }

	/**
	 * Updates the match size estimate of the Rule according to the current set "size"
	 */
	public void updateMatchSizeEstimate(double beta, double size) {
		if(experience < 1.0/beta) {
			matchSizeEstimate += (size - matchSizeEstimate) / experience;
        } else {
			matchSizeEstimate += beta * (size - matchSizeEstimate);
        }
    }

    public double getRelativeAccuracy() {
        return relativeAccuracy;
    }

    public void setRelativeAccuracy(double relativeAccuracy) {
        this.relativeAccuracy = relativeAccuracy;
        this.relativeAccuracyValues.add(relativeAccuracy);
    }

    public boolean equals(Object other) {

        if (other instanceof Classifier) {

            Classifier otherClassifier = (Classifier) other;

            return otherClassifier.condition.equals(this.condition) && otherClassifier.action.equals(this.action);

        } else {
            return false;
        }

    }

    public Average previousPValues;
    public Average relativeAccuracyValues;
    public Average previousSomethingValues;

    public String toString() {
        return condition.toString() + ":" + action.getId() + " | p=" + p + ", e=" + e + ", F=" + fitness;
    }

}
