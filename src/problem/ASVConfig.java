package problem;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.InputMismatchException;
import java.util.List;
import java.util.Scanner;
import java.awt.geom.Point2D;
import java.awt.geom.Point2D.Double;

/**
 * Represents a configuration of the ASVs. This class doesn't do any validity
 * checking - see the code in tester.Tester for this.
 *
 * @author lackofcheese
 */
public class ASVConfig {
	private static final double BOOM_LENGTH = 0.05;

	/** The position of each ASV */
	private List<Point2D.Double> asvPositions = new ArrayList<Point2D.Double>();

	/**
	 * Constructor. Takes an array of 2n x and y coordinates, where n is the
	 * number of ASVs
	 *
	 * @param coords
	 *            the x- and y-coordinates of the ASVs.
	 */
	public ASVConfig(double[] coords) {
		for (int i = 0; i < coords.length / 2; i++) {
			asvPositions.add(new Point2D.Double(coords[i * 2],
					coords[i * 2 + 1]));
		}
	}
	
	public ASVConfig(List<Point2D.Double> list) {
		asvPositions = list;
	}
	

	/**
	 * Constructs an ASVConfig from a space-separated string of x- and y-
	 * coordinates
	 *
	 * @param asvCount
	 *            the number of ASVs to read.
	 * @param str
	 *            the String containing the coordinates.
	 */
	public ASVConfig(int asvCount, String str) throws InputMismatchException {
		Scanner s = new Scanner(str);
		for (int i = 0; i < asvCount; i++) {
			asvPositions
					.add(new Point2D.Double(s.nextDouble(), s.nextDouble()));
		}
		s.close();
	}
	
	
	/**
	 * Constructor. Takes a boolean xy. 
	 * If xy is true: takes an array of 2n x and y coordinates, where n is the
	 * number of ASVs.
	 * If xy is false: takes an array of [x,y,theta1,theta2,theta3,...] values where thetai is
	 * the angle of the ASV relative to the previous, and x,y are the coordinates of the
	 * first ASV.
	 *
	 * @param xy
	 * 			operate in xy mode or xytheta mode
	 * @param coords
	 *            xy==true: the x- and y-coordinates of the ASVs.\
	 *            xy==false: [x,y,theta1,theta2,theta3,...] values
	 */

	public ASVConfig(boolean xyMode, double[] coords) {
		if (xyMode){ //XY-based
			for (int i = 0; i < coords.length / 2; i++) {
				asvPositions.add(new Point2D.Double(coords[i * 2],
						coords[i * 2 + 1]));
			}
		} else {    //Angle-based
			asvPositions.add(new Point2D.Double(coords[0],coords[1])); //Add initial point
			for (int i = 2; i < coords.length; i++) { //Add angled points
				asvPositions.add(new Point2D.Double(asvPositions.get(i-2).getX() 
						+ BOOM_LENGTH * Math.cos(coords[i]),
						asvPositions.get(i-2).getY() + BOOM_LENGTH * Math.sin(coords[i])));
			}
		}
		
		int centreASV = asvPositions.size() / 2;
		double dx = asvPositions.get(0).getX() - asvPositions.get(centreASV).getX();
		double dy = asvPositions.get(0).getY() - asvPositions.get(centreASV).getY();
		for(Point2D.Double point : asvPositions) {
			point.setLocation(point.getX() + dx, point.getY() + dy);
		}
		
		
		
	}
	
	public ASVConfig(boolean xyMode, double[] coords, int asvNo) {
		if (xyMode){ //XY-based
			for (int i = 0; i < coords.length / 2; i++) {
				asvPositions.add(new Point2D.Double(coords[i * 2],
						coords[i * 2 + 1]));
			}
		} else {    //Angle-based
			asvPositions.add(new Point2D.Double(coords[0],coords[1])); //Add initial point
			for (int i = 2; i < coords.length; i++) { //Add angled points
				asvPositions.add(new Point2D.Double(asvPositions.get(i-2).getX() 
						+ BOOM_LENGTH * Math.cos(coords[i]),
						asvPositions.get(i-2).getY() + BOOM_LENGTH * Math.sin(coords[i])));
			}
		}
		
		//int centreASV = asvPositions.size() / 2;
		double dx = asvPositions.get(0).getX() - asvPositions.get(asvNo).getX();
		double dy = asvPositions.get(0).getY() - asvPositions.get(asvNo).getY();
		for(Point2D.Double point : asvPositions) {
			point.setLocation(point.getX() + dx, point.getY() + dy);
		}
		
		
		
	}
	

	/**
	 * Copy constructor.
	 *
	 * @param cfg
	 *            the configuration to copy.
	 */
	public ASVConfig(ASVConfig cfg) {
		asvPositions = cfg.getASVPositions();
	}

	/**
	 * Returns a space-separated string of the ASV coordinates.
	 *
	 * @return a space-separated string of the ASV coordinates.
	 */
	public String toString() {
		StringBuilder sb = new StringBuilder();
		for (Point2D point : asvPositions) {
			if (sb.length() > 0) {
				sb.append(" ");
			}
			sb.append(point.getX());
			sb.append(" ");
			sb.append(point.getY());
		}
		return sb.toString();
	}

	/**
	 * Returns the maximum straight-line distance between the ASVs in this state
	 * vs. the other state, or -1 if the ASV counts don't match.
	 *
	 * @param otherState
	 *            the other state to compare.
	 * @return the maximum straight-line distance for any ASV.
	 */
	public double maxDistance(ASVConfig otherState) {
		if (this.getASVCount() != otherState.getASVCount()) {
			return -1;
		}
		double maxDistance = 0;
		for (int i = 0; i < this.getASVCount(); i++) {
			double distance = this.getPosition(i).distance(
					otherState.getPosition(i));
			if (distance > maxDistance) {
				maxDistance = distance;
			}
		}
		return maxDistance;
	}

	/**
	 * Returns the total straight-line distance over all the ASVs between this
	 * state and the other state, or -1 if the ASV counts don't match.
	 *
	 * @param otherState
	 *            the other state to compare.
	 * @return the total straight-line distance over all ASVs.
	 */
	public double totalDistance(ASVConfig otherState) {
		if (this.getASVCount() != otherState.getASVCount()) {
			return -1;
		}
		double totalDistance = 0;
		for (int i = 0; i < this.getASVCount(); i++) {
			totalDistance += this.getPosition(i).distance(
					otherState.getPosition(i));
		}
		return totalDistance;
	}

	/**
	 * Returns the position of the ASV with the given number.
	 *
	 * @param asvNo
	 *            the number of the ASV.
	 * @return the position of the ASV with the given number.
	 */
	public Point2D.Double getPosition(int asvNo) {
		return asvPositions.get(asvNo);
	}

	/**
	 * Returns the number of ASVs in this configuration.
	 *
	 * @return the number of ASVs in this configuration.
	 */
	public int getASVCount() {
		return asvPositions.size();
	}

	/**
	 * Returns the positions of all the ASVs, in order.
	 *
	 * @return the positions of all the ASVs, in order.
	 */
	public List<Point2D.Double> getASVPositions() {
		return new ArrayList<Point2D.Double>(asvPositions);
	}
	
	public ASVConfig shiftASVs(Point2D point) {
		List<Point2D.Double> pointList = new ArrayList<Point2D.Double>();
		for(Point2D p : asvPositions) {
			pointList.add(new Point2D.Double(p.getX() + point.getX(), p.getY()+point.getY()));
		}
		return new ASVConfig(pointList);
	}
}
