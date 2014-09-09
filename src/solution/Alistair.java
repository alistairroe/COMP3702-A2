package solution;

import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.PriorityQueue;
import java.util.Random;
//import java.util.Set;




//import java.util.Map.Entry;

import problem.ProblemSpec;
import problem.Obstacle;

public class Alistair {
	
	
	public ProblemSpec ps;
	HashMap<Node,HashMap<Node,Double>> map;
	
	public List<List<Point2D.Double>> createPRM(Node start, Node end, int N, double maxEdgeDistance) {
		//double maxEdgeDistance = 0.025;
		
		map = new HashMap<Node,HashMap<Node,Double>>();
		
		
		Random generator = new Random();
		ps = new ProblemSpec();
		
		try{
			ps.loadProblem("src/testcases/7ASV-easy.txt");
		} catch (IOException e) {
			
		}
		
		map.put(start, new HashMap<Node,Double>());
		map.put(end, new HashMap<Node,Double>());
	
		boolean valid = true;
		//graph.addVertex(newNode);
		for(int i=0;i<N;i++) {
			Node newNode = new Node(generator.nextDouble(),generator.nextDouble());
			valid = true;
			for(Obstacle o: ps.getObstacles()) {
				if(o.getRect().contains(newNode)) {
					//System.out.println("Point discarded");
					valid = false;
				}
			}
			if(valid) {
				map.put(newNode,new HashMap<Node,Double>());
				for(Node n : map.keySet()) {
					if((newNode.getDistanceTo(n) < maxEdgeDistance)&&(!newNode.equals(n))) {
						//System.out.println("Making a link");
						boolean add = true;
						
						for(Obstacle o: ps.getObstacles()) {
							if(new Line2D.Double(newNode.toPoint2D(),n.toPoint2D()).intersects(o.getRect())) {
								add = false;
							}
						}
						if(add) {
							map.get(newNode).put(n, newNode.getDistanceTo(n));
							map.get(n).put(newNode, newNode.getDistanceTo(n));
						}
						
						//graph.setEdgeWeight(graph.getEdge(newNode, n), newNode.getDistanceTo(n));
					}
				}
				
			}
		}
		List<List<Point2D.Double>> list1 = new ArrayList<List<Point2D.Double>>();
		for(Node n: map.keySet()) {
			List<Point2D.Double> point = new ArrayList<Point2D.Double>();
			point.add(n.toPoint2D());
			list1.add(point);
			for(Node n1: map.get(n).keySet()) {
				List<Point2D.Double> l = new ArrayList<Point2D.Double>();
				l.add(n.toPoint2D());
				l.add(n1.toPoint2D());
				//System.out.println("x = " + n.getX());
				//System.out.println("y = " + n.getY());
				list1.add(l);
			}
		}
		
		
		return list1;
		
	}
	
	
	public ArrayList<Node> AStar(Node start, Node end) {
		
		ArrayList<Node> beenTo = new ArrayList<Node>();
		PriorityQueue<Node> pq = new PriorityQueue<Node>(1,new Comparator<Node>() {
			public int compare(Node o1,Node o2) {
				if(o1.getFScore()>o2.getFScore()) {
					return 1;
				} else if(o2.getFScore()>o1.getFScore()) {
					return -1;
				} else {
					return 0;
				}
			} 
		});
		double fCost = 0;
		double gCost = 0;

		
		HashMap<Node,Node> cameFrom = new HashMap<Node,Node>();
		start.FScore = start.getDistanceTo(end);
		pq.add(start);
		Node current;
		double tentativeGScore;
		
		while(pq.size() != 0) {
			current = pq.remove();
			if(current.equals(end)) {
				System.out.print("We made it!");
				return reconstructPath(cameFrom, end, start); //LOOK HERE
			}
			beenTo.add(current);
			for(Node edgeDestination: map.get(current).keySet()) {
				if(beenTo.contains(edgeDestination)) {
					//Do nothing BUT MAYBE SOMETHING LATER
				} else {
					 tentativeGScore = current.getGScore() + map.get(current).get(edgeDestination);
					 
					 if((pq.contains(edgeDestination)!= true)||tentativeGScore < edgeDestination.getGScore()) {
						 edgeDestination.GScore = tentativeGScore;
						 edgeDestination.FScore = edgeDestination.GScore + edgeDestination.getDistanceTo(end);
						 cameFrom.put(edgeDestination, current);
						 if(pq.contains(edgeDestination)!= true) {
							 pq.add(edgeDestination);
						 }
					 }
				}
			}
		}
		System.out.println("Couldn't get there...");
		return new ArrayList<Node>();
	}
		
	public ArrayList<Node> reconstructPath(HashMap<Node,Node> cameFrom, Node currentNode, Node start) {
		Node newNode = currentNode;
		
		ArrayList<Node> path = new ArrayList<Node>();
		while(!newNode.equals(start)) {
			path.add(0, newNode);
			newNode = cameFrom.get(newNode);
			
		}
		path.add(0,start);
		return path;
	}
	
	public ArrayList<Point2D.Double> findPathCorners(ArrayList<Node> path) {
		ArrayList<Point2D.Double> corners = new ArrayList<Point2D.Double>();
		
		for(Obstacle o: ps.getObstacles()) {
			for(Node n: path) {
				if(n.distance(o.getRect().getMinX(), o.getRect().getMinY()) < 0.05) {
					corners.add(new Point2D.Double(o.getRect().getMinX(),o.getRect().getMinY()));
				}
				if(n.distance(o.getRect().getMaxX(), o.getRect().getMinY()) < 0.05) {
					corners.add(new Point2D.Double(o.getRect().getMaxX(),o.getRect().getMinY()));
				}
				if(n.distance(o.getRect().getMinX(), o.getRect().getMaxY()) < 0.05) {
					corners.add(new Point2D.Double(o.getRect().getMinX(),o.getRect().getMaxY()));
				}
				if(n.distance(o.getRect().getMaxX(), o.getRect().getMaxY()) < 0.05) {
					corners.add(new Point2D.Double(o.getRect().getMaxX(),o.getRect().getMaxY()));
				}
			}
			
		}
		return corners;
	}
}
