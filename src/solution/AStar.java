package solution;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.PriorityQueue;

/**
 * Implements the A-Star Algorithm to find the quickest / least expensive route
 * from the start Node to finish Node in the shortest time possible while
 * (hopefully) using the least amount of computational resources.
 * 
 * @author Route66
 *
 */
public class AStar {

	// Create a new HahMap that stores each node as well as the other Nodes that
	// can be traveled to.
	HashMap<Node, HashMap<Node, Double>> map = new HashMap<Node, HashMap<Node, Double>>();

	/**
	 * Recursively stores the path of Nodes in an ArrayList in order of travel.
	 * This function can be used by itself but is also used in the performAStar
	 * method.
	 * 
	 * @param cameFrom
	 *            is the Node that was previously traveled to.
	 * @param currentNode
	 *            is the current Node.
	 * @param start
	 *            is the original Node we started at.
	 * @return returns the path. This is used recursively in the function.
	 */
	public ArrayList<Node> reconstructPath(HashMap<Node, Node> cameFrom,
			Node currentNode, Node start) {
		Node newNode = currentNode;

		ArrayList<Node> path = new ArrayList<Node>();
		while (!newNode.equals(start)) {
			// At index 0 add the latest node. Upon recursion the node is moved
			// along an index and a newer node is added at index 0. This
			// continues until the node .equals the start node.
			path.add(0, newNode);
			// Update newNode
			newNode = cameFrom.get(newNode);

		}
		// When the current Node .equals the start Node add it to the beginning
		// of the list.
		path.add(0, start);
		return path;
	}

	/**
	 * When a map is populated with points the performAStar function returns the
	 * fastest / quickest route from the start Node to the final Node. The
	 * fastest in terms of distance traveled as well as the least
	 * computationally expensive. This function makes use of the reconstructPath
	 * method.
	 * 
	 * @requires the map must be populated with enough points, and the distance
	 *           between the points to be traversable. Essentially a journey
	 *           must be possible.
	 * 
	 * @param start
	 *            the starting Node of the journey
	 * @param end
	 *            the final Node of the journey
	 * @return an ArrayList of the nodes in order from start to finish
	 */
	public ArrayList<Node> performAStar(Node start, Node end,
			HashMap<Node, HashMap<Node, Double>> map) {
		// Creates an ArrayList containing all the Nodes that have been
		// "traveled" to
		ArrayList<Node> beenTo = new ArrayList<Node>();
		// Implements a PriorityQueue to contain the Nodes that can be
		// "traveled" to from the current Node. Organizes them according to the
		// PriorityQueue ordering and implements a Comparator.
		PriorityQueue<Node> pq = new PriorityQueue<Node>(1,
				new Comparator<Node>() {
					public int compare(Node o1, Node o2) {
						if (o1.getFScore() > o2.getFScore()) {
							return 1;
						} else if (o2.getFScore() > o1.getFScore()) {
							return -1;
						} else {
							return 0;
						}
					}
				});

		// Creates a new HashMap of Nodes to remember where we cameFrom
		HashMap<Node, Node> cameFrom = new HashMap<Node, Node>();
		// Sets the FScore of the first Node to the straight line distance to
		// the final Node.
		start.FScore = start.getDistanceTo(end);
		pq.add(start); // Adds the start Node to the PriorityQueue.
		Node current;
		double tentativeGScore; // Creates a tentativeGScore as a double.

		// While there are elements in the PriorityQueue keep searching.
		while (pq.size() != 0) {
			current = pq.remove(); // Remove the Node to be tested from the
									// Queue.
			if (current.equals(end)) {
				System.out.print("We made it!");
				// Add the Nodes into the recursive function to generate the
				// ArrayList.
				return reconstructPath(cameFrom, end, start);
			}
			beenTo.add(current); // Add the current Node to the beenTo List.
			// For each destination that can be traveled to from the current
			// Node, calculate if the remaining distance is shorter.
			for (Node edgeDestination : map.get(current).keySet()) {
				// If the node has been traveled to, do nothing.
				if (beenTo.contains(edgeDestination)) {
					// Do nothing BUT MAYBE SOMETHING LATER??
				} else {
					// the tentativeGScore = current GScore + the straight line
					// distance to the final node.
					tentativeGScore = current.getGScore()
							+ map.get(current).get(edgeDestination);

					// If the PriorityQueue does not contain the Node to be
					// tested or the tentativeGScore is less then the Node
					// currently being tested, update the map and values.
					if (!(pq.contains(edgeDestination))
							|| tentativeGScore < edgeDestination.getGScore()) {
						edgeDestination.GScore = tentativeGScore;
						edgeDestination.FScore = edgeDestination.GScore
								+ edgeDestination.getDistanceTo(end);
						// Add the Node to the path
						cameFrom.put(edgeDestination, current);
						// if (pq.contains(edgeDestination) != true) -> original
						if (!pq.contains(edgeDestination)) {
							pq.add(edgeDestination);
						}
					}
				}
			}
		}
		// If we couldn't get there print it, and return and empty ArrayList.
		System.out.print("Couldn't get there!!!!!");
		return new ArrayList<Node>();
	}
}