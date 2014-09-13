package solution;

import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

import problem.Obstacle;
import problem.ProblemSpec;

/**
 * Creates a PRM of the configuration.
 * 
 * @author Route66
 *
 */
public class PRM {

	private ProblemSpec ps;
	public HashMap<Node, HashMap<Node, Double>> validPointMap;
	public List<List<Point2D.Double>> validPointList;

	/**
	 * Takes 4 values and generates a PRM of the environment. createPRM takes
	 * the start Node and the final Node as an x & y value, the amount of random
	 * points to generate and the maximum traversable distance (edge distance).
	 * The randomly generated points are spread throughout the map and only the
	 * valid points are retained i.e. points that collide with obstacles are
	 * discarded. The amount of valid points is not guaranteed.
	 * 
	 * Makes use of the ProblemSpec Class to open and load a text file
	 * containing a problem.
	 * 
	 * @param start
	 *            the starting node
	 * @param end
	 *            the desired Node
	 * @param numberOfPoints
	 *            the number of points to be generated over the entire map
	 * @param maxEdgeDistance
	 *            the maximum traversable distance
	 * @return a map of valid points as the key and available, valid connections
	 *         as the value
	 */
	public HashMap<Node, HashMap<Node, Double>> createPRM(Node start, Node end,
			int numberOfPoints, double maxEdgeDistance) {

		validPointMap = new HashMap<Node, HashMap<Node, Double>>();
		Random generator = new Random();
		this.ps = new ProblemSpec();

		// Attempt to open to the file. If the filename is wrong throw an
		// exception.
		try {
			ps.loadProblem("src/testcases/7ASV.txt");
		} catch (IOException e) {
			System.err.println("File cannot be found (IOException): "
					+ e.getMessage());
		}

		// Add the start Node to the map.
		validPointMap.put(start, new HashMap<Node, Double>());
		// Add the final Node to the map.
		validPointMap.put(end, new HashMap<Node, Double>());

		boolean validPoint = true;
		// Populate the map with valid points
		for (int i = 0; i < numberOfPoints; i++) {
			Node newNode = new Node(generator.nextDouble(),
					generator.nextDouble());
			validPoint = true;
			// Check to see if the point falls over an obstacle. If it does do
			// nothing with it.
			for (Obstacle o : ps.getObstacles()) {
				if (o.getRect().contains(newNode)) {
					// System.out.println("Point discarded");
					validPoint = false;
				}
			}
			// If the point is valid add it to the map.
			if (validPoint) {
				validPointMap.put(newNode, new HashMap<Node, Double>());

				for (Node n : validPointMap.keySet()) {
					if ((newNode.getDistanceTo(n) < maxEdgeDistance)
							&& (!newNode.equals(n))) {
						// System.out.println("Making a link");
						boolean lineCollision = true;

						// Check to see if the line between valid, connectable
						// points collides with obstacles
						for (Obstacle o : ps.getObstacles()) {
							if (new Line2D.Double(newNode.toPoint2D(),
									n.toPoint2D()).intersects(o.getRect())) {
								lineCollision = false;
							}
						}
						if (lineCollision) {
							// Add the connection to both nodes
							validPointMap.get(newNode).put(n,
									newNode.getDistanceTo(n));
							validPointMap.get(n).put(newNode,
									newNode.getDistanceTo(n));
						}
					}
				}
			}
		}
		return validPointMap;
	}

	/**
	 * Converts a map of Nodes to a List containing a List of Point2D.Double.
	 * This function is intended to allow the VisualHelperTester to dislplay the
	 * PRM. This function does not check for validity of either the Map or the
	 * List.
	 * 
	 * @param pointMap
	 *            a HashMap<Node, HashMap<Node, Double>> with Nodes and
	 *            available connections.
	 * @return an ArrayList<List<Point2D.Double>> containing the Nodes and
	 *         available connections in Point2D.Double format.
	 */
	public List<List<Point2D.Double>> convertPRM(
			HashMap<Node, HashMap<Node, Double>> pointMap) {

		validPointList = new ArrayList<List<Point2D.Double>>();

		for (Node n : pointMap.keySet()) {
			List<Point2D.Double> point = new ArrayList<Point2D.Double>();
			point.add(n.toPoint2D());
			validPointList.add(point);
			for (Node n1 : pointMap.get(n).keySet()) {
				List<Point2D.Double> l = new ArrayList<Point2D.Double>();
				l.add(n.toPoint2D());
				l.add(n1.toPoint2D());
				validPointList.add(l);
			}
		}
		return validPointList;
	}

	public ProblemSpec getPS() {
		return this.ps;
	}
}