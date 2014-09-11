package solution;

import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.PriorityQueue;
import java.util.Random;

//import java.util.Set;
//import java.util.Map.Entry;
import problem.Obstacle;
import problem.ProblemSpec;

public class Alistair {

	public ProblemSpec ps;
	HashMap<Node, HashMap<Node, Double>> map;
	List<Rectangle2D.Double> rectInterList = new ArrayList<Rectangle2D.Double>();

	public Alistair() {
		ps = new ProblemSpec();

		try {
			ps.loadProblem("src/testcases/7-ASV-x4.txt");
		} catch (IOException e) {

		}
	}

	public List<List<Point2D.Double>> createPRM(Node start, Node end, int N,
			double maxEdgeDistance) {
		// double maxEdgeDistance = 0.025;

		map = new HashMap<Node, HashMap<Node, Double>>();

		Random generator = new Random();

		map.put(start, new HashMap<Node, Double>());
		map.put(end, new HashMap<Node, Double>());

		boolean valid = true;
		// graph.addVertex(newNode);
		for (int i = 0; i < N; i++) {
			Node newNode = new Node(generator.nextDouble(),
					generator.nextDouble());
			valid = true;
			for (Obstacle o : ps.getObstacles()) {
				if (o.getRect().contains(newNode)) {
					// System.out.println("Point discarded");
					valid = false;
				}
			}
			// for (Obstacle o : ps.getObstacles()) {
			// double delta = 0.001;
			// Rectangle2D rect = o.getRect();
			// Rectangle2D.Double grownRect = new Rectangle2D.Double(
			// rect.getX() - delta, rect.getY() - delta,
			// rect.getWidth() + delta * 2, rect.getHeight() + delta
			// * 2);
			//
			// if (grownRect.getMaxY() + delta > newNode.getY()) {
			// if (newNode.getY() > grownRect.getMaxY()) {
			// newNode.y = grownRect.getMaxY() + delta;
			// }
			// }
			// if (grownRect.getMinY() - delta < newNode.getY()) {
			// if (newNode.getY() < grownRect.getMinY()) {
			// newNode.y = grownRect.getMinY() - delta;
			// }
			// }
			//
			// if (grownRect.getMaxX() + delta > newNode.getX()) {
			// if (newNode.getX() > grownRect.getMaxX()) {
			// newNode.x = grownRect.getMaxX() + delta;
			// }
			// }
			// if (grownRect.getMinX() - delta < newNode.getX()) {
			// if (newNode.getX() < grownRect.getMinX()) {
			// newNode.x = grownRect.getMinX() - delta;
			// }
			// }
			// }

			if (valid) {
				map.put(newNode, new HashMap<Node, Double>());
				for (Node n : map.keySet()) {
					if ((newNode.getDistanceTo(n) < maxEdgeDistance)
							&& (!newNode.equals(n))) {
						// System.out.println("Making a link");
						boolean add = true;

						for (Obstacle o : ps.getObstacles()) {
							if (new Line2D.Double(newNode.toPoint2D(),
									n.toPoint2D()).intersects(o.getRect())) {
								add = false;
							}
						}
						if (add) {
							map.get(newNode).put(n, newNode.getDistanceTo(n));
							map.get(n).put(newNode, newNode.getDistanceTo(n));
						}

						// graph.setEdgeWeight(graph.getEdge(newNode, n),
						// newNode.getDistanceTo(n));
					}
				}

			}
		}
		List<List<Point2D.Double>> list1 = new ArrayList<List<Point2D.Double>>();
		for (Node n : map.keySet()) {
			List<Point2D.Double> point = new ArrayList<Point2D.Double>();
			point.add(n.toPoint2D());
			list1.add(point);
			for (Node n1 : map.get(n).keySet()) {
				List<Point2D.Double> l = new ArrayList<Point2D.Double>();
				l.add(n.toPoint2D());
				l.add(n1.toPoint2D());
				// System.out.println("x = " + n.getX());
				// System.out.println("y = " + n.getY());
				list1.add(l);
			}
		}

		return list1;

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
		double fCost = 0;
		double gCost = 0;

		HashMap<Node, Node> cameFrom = new HashMap<Node, Node>();
		start.FScore = start.getDistanceTo(end);
		pq.add(start);
		Node current;
		double tentativeGScore;

		while (pq.size() != 0) {
			current = pq.remove();
			if (current.equals(end)) {
				System.out.println("We made it!");
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
		System.out.println("Couldn't get there...");
		return new ArrayList<Node>();
	}

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

	public List<Point2D.Double> findPathCorners(List<Node> path) {
		ArrayList<Point2D.Double> corners = new ArrayList<Point2D.Double>();

		for (Obstacle o : ps.getObstacles()) {
			for (Node n : path) {
				if (n.distance(o.getRect().getMinX(), o.getRect().getMinY()) < 0.05) {
					corners.add(new Point2D.Double(o.getRect().getMinX(), o
							.getRect().getMinY()));
				}
				if (n.distance(o.getRect().getMaxX(), o.getRect().getMinY()) < 0.05) {
					corners.add(new Point2D.Double(o.getRect().getMaxX(), o
							.getRect().getMinY()));
				}
				if (n.distance(o.getRect().getMinX(), o.getRect().getMaxY()) < 0.05) {
					corners.add(new Point2D.Double(o.getRect().getMinX(), o
							.getRect().getMaxY()));
				}
				if (n.distance(o.getRect().getMaxX(), o.getRect().getMaxY()) < 0.05) {
					corners.add(new Point2D.Double(o.getRect().getMaxX(), o
							.getRect().getMaxY()));
				}
			}

		}
		return corners;
	}

	public List<Rectangle2D.Double> getPassage(double deltaBig,
			double deltaSmall) {

		List<Rectangle2D.Double> rectList = new ArrayList<Rectangle2D.Double>();
		List<Rectangle2D.Double> rectList2 = new ArrayList<Rectangle2D.Double>();
		List<Rectangle2D.Double> intersectList = new ArrayList<Rectangle2D.Double>();
		rectInterList = new ArrayList<Rectangle2D.Double>();

		for (Obstacle o : this.ps.getObstacles()) {
			// original double deltaSmall = 0.005;
			Rectangle2D rect = o.getRect();
			Rectangle2D.Double grownRect = new Rectangle2D.Double(rect.getX()
					- deltaSmall, rect.getY() - deltaSmall, rect.getWidth()
					+ deltaSmall * 2, rect.getHeight() + deltaSmall * 2);
			for (Rectangle2D.Double temp : rectList) {
				Rectangle2D.Double temp2 = new Rectangle2D.Double();
				Rectangle2D.intersect(temp, grownRect, temp2);
				if (!temp2.isEmpty()) {
					intersectList.add(temp2);
				}
			}
			rectList.add(grownRect);
		}

		for (Obstacle o : this.ps.getObstacles()) {
			// original deltaBig = 0.025;
			Rectangle2D rect = o.getRect();
			Rectangle2D.Double grownRect = new Rectangle2D.Double(rect.getX()
					- deltaBig, rect.getY() - deltaBig, rect.getWidth()
					+ deltaBig * 2, rect.getHeight() + deltaBig * 2);
			for (Rectangle2D.Double temp : rectList2) {
				Rectangle2D.Double temp2 = new Rectangle2D.Double();
				Rectangle2D.intersect(temp, grownRect, temp2);
				if (!temp2.isEmpty()) {
					rectInterList.add(temp2);
				}
			}
			rectList2.add(grownRect);
		}

		HashMap<Rectangle2D.Double, Rectangle2D.Double> map = new HashMap<Rectangle2D.Double, Rectangle2D.Double>();
		for (Rectangle2D.Double r2 : rectInterList) {
			for (Rectangle2D.Double r : intersectList) {
				if (r2.contains(r)) {
					map.put(r2, r);
				}
			}
		}
		rectInterList.removeAll(map.keySet());
		if (rectInterList.size() > 0) {
			Rectangle2D.Double r = rectInterList.get(0);
			if (r.getWidth() < r.getHeight()) {
				r.x = r.x - (deltaBig - r.getWidth());
				r.width = r.width + (deltaBig * 2 - r.width);
			} else {
				r.y = r.y - (deltaBig - r.getHeight());
				r.height = r.height + (deltaBig * 2 - r.height);
			}
		}
		// System.out.println(rectInterList);
		return rectInterList;
	}

	public List<Node> interpolateNodes(Node n0, Node n1) {
		double interpStep = 0.01;

		double angle1 = Math
				.atan2(n1.getY() - n0.getY(), n1.getX() - n0.getX());

		List<Node> list1 = new ArrayList<Node>();
		list1.add(n0);
		while (list1.get(list1.size() - 1).getDistanceTo(n1) > interpStep) {
			// if (rectInterList.size() > 0) {
			// Rectangle2D.Double r = rectInterList.get(0);
			// if (r.contains(n0)) {
			// interpStep = 0.001;
			// }
			// }
			// System.out.println(interpStep);
			double newx = list1.get(list1.size() - 1).getX() + interpStep
					* Math.cos(angle1);
			double newy = list1.get(list1.size() - 1).getY() + interpStep
					* Math.sin(angle1);
			list1.add(new Node(newx, newy));
		}
		return list1;
	}
}