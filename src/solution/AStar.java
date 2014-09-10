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

	HashMap<Node, HashMap<Node, Double>> map = new HashMap<Node, HashMap<Node, Double>>();

	public ArrayList<Node> reconstructPath(HashMap<Node, Node> cameFrom,
			Node currentNode, Node start) {
		Node newNode = currentNode;

		ArrayList<Node> path = new ArrayList<Node>();
		while (!newNode.equals(start)) {
			path.add(0, newNode);
			newNode = cameFrom.get(newNode);

		}
		path.add(0, start);
		return path;
	}

	public ArrayList<Node> AStar(Node start, Node end) {
		ArrayList<Node> beenTo = new ArrayList<Node>();
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

		HashMap<Node, Node> cameFrom = new HashMap<Node, Node>();
		start.FScore = start.getDistanceTo(end);
		pq.add(start);
		Node current;
		double tentativeGScore;

		while (pq.size() != 0) {
			current = pq.remove();
			if (current.equals(end)) {
				// System.out.print("We made it!");
				return reconstructPath(cameFrom, end, start); // LOOK HERE
			}
			beenTo.add(current);
			for (Node edgeDestination : map.get(current).keySet()) {
				if (beenTo.contains(edgeDestination)) {
					// Do nothing BUT MAYBE SOMETHING LATER
				} else {
					tentativeGScore = current.getGScore()
							+ map.get(current).get(edgeDestination);

					if ((pq.contains(edgeDestination) != true)
							|| tentativeGScore < edgeDestination.getGScore()) {
						edgeDestination.GScore = tentativeGScore;
						edgeDestination.FScore = edgeDestination.GScore
								+ edgeDestination.getDistanceTo(end);
						cameFrom.put(edgeDestination, current);
						if (pq.contains(edgeDestination) != true) {
							pq.add(edgeDestination);
						}
					}
				}
			}
		}
		System.out.print("Couldn't get there!!!!!");
		return new ArrayList<Node>();
	}
}