package solution;

import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.PriorityQueue;
import java.util.Random;
//import java.util.Set;




//import java.util.Map.Entry;






import problem.ASVConfig;
import problem.ProblemSpec;
import problem.Obstacle;
import tester.Tester;

public class Alistair {
	
	
	public ProblemSpec ps;
	HashMap<Node,HashMap<Node,Double>> map;
	public Tester tester;
	
	public Alistair() {
		ps = new ProblemSpec();
		tester = new Tester();
		
		try{
			ps.loadProblem("src/testcases/7-ASV-x2.txt");
		} catch (IOException e) {
			
		}
	}
	
	public List<List<Point2D.Double>> createPRM(Node start, Node end, int N, double maxEdgeDistance) {
		//double maxEdgeDistance = 0.025;
		
		map = new HashMap<Node,HashMap<Node,Double>>();
		
		
		Random generator = new Random();
		
		
		map.put(start, new HashMap<Node,Double>());
		map.put(end, new HashMap<Node,Double>());
	
		boolean valid = true;
		//graph.addVertex(newNode);
		for(int i=0;i<N;i++) {
			Node newNode = new Node(generator.nextDouble(),generator.nextDouble());
			valid = true;
			for(Obstacle o: ps.getObstacles()) {
				double delta = 0.01;
				Rectangle2D rect = o.getRect();
				Rectangle2D.Double grownRect = new Rectangle2D.Double(rect.getX() - delta, rect.getY() - delta,rect.getWidth() + delta * 2, rect.getHeight() + delta * 2);
				if(grownRect.contains(newNode)) {
					//System.out.println("Point discarded");
					valid = false;
				}
			}
//			for(Obstacle o: ps.getObstacles()) {
//				double delta = 0.028;
//				Rectangle2D rect = o.getRect();
//				//Rectangle2D.Double grownRect = new Rectangle2D.Double(rect.getX() - delta, rect.getY() - delta,rect.getWidth() + delta * 2, rect.getHeight() + delta * 2);
//
////				if(grownRect.getMaxY()  > newNode.getY()) {
////					if(newNode.getY() > rect.getMaxY()) {
////						newNode.y = rect.getMaxY() + 0.012;
////					}
////				}
////				if(grownRect.getMinY()  < newNode.getY()) {
////					if(newNode.getY() < rect.getMinY()) {
////						newNode.y = rect.getMinY() - 0.012;
////					}
////				}
////				if(rect.getMaxY() + delta  > newNode.getY()) {
////					if(newNode.getY() > rect.getMaxY()) {
////						newNode.y = rect.getMaxY() + delta;
////					}
////				}
////				if(rect.getMinY() - delta  < newNode.getY()) {
////					if(newNode.getY() < rect.getMinY()) {
////						newNode.y = rect.getMinY() - delta;
////					}
////				}
//				
//			}
			
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
	
	public List<Point2D.Double> findPathCorners(List<Node> path) {
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
	
	public List<Node> interpolateNodes(Node n0, Node n1) {

		double angle1 = Math.atan2(n1.getY() - n0.getY(),
				n1.getX() - n0.getX());
		
		List<Node> list1 = new ArrayList<Node>();
		list1.add(n0);
		while(list1.get(list1.size()-1).getDistanceTo(n1)>0.008) {
			double newx = list1.get(list1.size()-1).getX() + 0.008 * Math.cos(angle1);
			double newy = list1.get(list1.size()-1).getY() + 0.008 * Math.sin(angle1);
			list1.add(new Node(newx, newy));
		}
		return list1;
	}
	
	public List<ASVConfig> interpolateSolution(ArrayList<ASVConfig> path) {
		List<ASVConfig> interpolatedPath = new ArrayList<ASVConfig>();
		double step = 0.0008;
		int asvCount = path.get(0).getASVCount();
		int asvMid = asvCount/2;
		for(int i = 1; i < path.size(); i++) {
			System.out.println("Node: " + i);
			interpolatedPath.add(path.get(i-1));
			ASVConfig source = path.get(i - 1);
			ASVConfig dest = path.get(i);
			//double dist = dest.getPosition(asvMid).distance(source.getPosition(asvMid));
			double angle = Math.atan2(dest.getPosition(asvMid).getY() - source.getPosition(asvMid).getY(),
					dest.getPosition(asvMid).getX() - source.getPosition(asvMid).getX());
			
			ASVConfig newConf = new ASVConfig(source);
			ASVConfig prevConf = new ASVConfig(source);
			int m = 0;
			while(prevConf.maxDistance(dest) > 0.001) {
				//System.out.println("D: " + newConf.maxDistance(dest));
//				if(m > 1000) {
//					System.out.println("Max m reached");
//					return interpolatedPath;
//				}
				int k = 0;
				while((newConf.maxDistance(prevConf) > 0.001)||(k == 0)) {
					if(k > 10) {
						System.out.println("Max k reached");
						return interpolatedPath;
					}
					
					//System.out.println(newConf.getASVPositions());
					
					//System.out.println("Step: " + step);
					HashMap<Integer,Point2D> newPosMap = getNextStep(prevConf,dest,asvMid,asvCount,step,angle);
					
					List<Point2D.Double> newASVPositions = new ArrayList<Point2D.Double>();
					for(int j = 0; j < asvCount; j++) {
						newASVPositions.add(new Point2D.Double(newPosMap.get(j).getX(),newPosMap.get(j).getY()));
					}
					
					//step = step / 2;
					//System.out.println(newASVPositions);
					//System.out.println(newASVPositions);
					//prevConf = new ASVConfig(newConf);
					newConf = new ASVConfig(newASVPositions);
					//System.out.println(newASVPositions);
					//System.out.println("Distance moved for k= "+k+" : " + newConf.maxDistance(prevConf));
					//System.out.println("Step size: " + step);
					//System.out.println("Step: " + step);
					step = step *(0.0008/newConf.maxDistance(prevConf));
					//System.out.println("New Step: "+ step);
					//System.out.println("Step distance: " + newConf.maxDistance(prevConf));
					//System.out.println("D: " + newConf.maxDistance(dest));
					//System.out.println("k: "+ k);
					k++;
					
				} // IMPLEMENT ANOTHER WHILE
				step = 0.0008;
				interpolatedPath.add(newConf);
				//System.out.println(newConf);
				prevConf = new ASVConfig(newConf);
				//System.out.println("Distance to goal: "+ prevConf.maxDistance(dest));
				m++;
			}
			
		}
		return interpolatedPath;
	}
	
	public HashMap<Integer,Point2D> getNextStep(ASVConfig source, ASVConfig dest, int asvMid, int asvCount, double step, double angle) {
		HashMap<Integer,Point2D> newPosMap = new HashMap<Integer,Point2D>();
		HashMap<Integer,Point2D> relPosMap = new HashMap<Integer,Point2D>();
		HashMap<Integer,Point2D> destPosMap = new HashMap<Integer,Point2D>();
		double scalar = 1;
		//System.out.println("From: "+source.getASVPositions());
		//System.out.println("To: " + dest.getASVPositions());
		HashMap<Integer,Double> distanceMap = new HashMap<Integer,Double>();
		distanceMap.put(asvMid,source.getPosition(asvMid).distance(dest.getPosition(asvMid)));
		for(int i =0; i < asvCount; i++) {
			if(i < asvMid) {
				Point2D relPos = new Point2D.Double(source.getPosition(i).getX() - source.getPosition(i+1).getX(),
						source.getPosition(i).getY() -source.getPosition(i+1).getY());
				relPosMap.put(i,relPos);
				Point2D destPos = new Point2D.Double(dest.getPosition(i).getX() - dest.getPosition(i+1).getX(),
						dest.getPosition(i).getY() - dest.getPosition(i+1).getY());
				destPosMap.put(i, destPos);
				double angleDiff = tester.normaliseAngle(Math.atan2(destPos.getY(), destPos.getX()) - Math.atan2(relPos.getY(), relPos.getX()));
				distanceMap.put(i, 0.05*angleDiff);
			} else if(i == asvMid) {
				
			} else if(i > asvMid) {
				Point2D relPos = new Point2D.Double(source.getPosition(i).getX() - source.getPosition(i-1).getX(),
						source.getPosition(i).getY() -source.getPosition(i-1).getY());
				relPosMap.put(i,relPos);
				Point2D destPos = new Point2D.Double(dest.getPosition(i).getX() - dest.getPosition(i-1).getX(),
						dest.getPosition(i).getY() - dest.getPosition(i-1).getY());
				destPosMap.put(i, destPos);
				double angleDiff = tester.normaliseAngle(Math.atan2(destPos.getY(), destPos.getX()) - Math.atan2(relPos.getY(), relPos.getX()));
				distanceMap.put(i, 0.05*angleDiff);
			}
		}
		double maxDist =0;
		int asvMax = 0;
		for(Integer asv : distanceMap.keySet()) {
			if(Math.abs(distanceMap.get(asv)) > Math.abs(maxDist)) {
				maxDist = distanceMap.get(asv);
				asvMax = asv;
			}
		}
		List<Double> multipliers = new ArrayList<Double>();
		//System.out.println(distanceMap);
		for(int i = 0; i < asvCount; i++) {
			if(i == asvMid) {
				multipliers.add(step*distanceMap.get(i)/Math.abs(distanceMap.get(asvMax)));
			} else {
				multipliers.add(step*20*distanceMap.get(i)/Math.abs(distanceMap.get(asvMax)));
			}
			
			//multipliers.add(step);
		}
		//System.out.println(distanceMap);
		//System.out.println(multipliers);
		//if(source.getPosition(asvMid).distance(dest.getPosition(asvMid))<0.0009) {
		//	newPosMap.put(asvMid, dest.getPosition(asvMid));
		//} else {
			newPosMap.put(asvMid, new Point2D.Double(source.getPosition(asvMid).getX() + multipliers.get(asvMid)*Math.cos(angle),source.getPosition(asvMid).getY() + multipliers.get(asvMid)*Math.sin(angle)));
		//}
		
		//System.out.println("ASV "+asvMid+": "+newPosMap.get(asvMid));
		//System.out.println("ASV: "+asvMid+", distance to new pos: " + newPosMap.get(asvMid).distance(dest.getPosition(asvMid)));
		for(int j = asvMid-1; j > -1; j--) {
			//System.out.println("ASV "+j+": ");
			//if(source.getPosition(j).distance(dest.getPosition(j))<0.0009) {
			//	newPosMap.put(j, dest.getPosition(j));
				//System.out.println("ASV: "+j+", distance to new pos: 0");
			//} else {
//				Point2D relPos = new Point2D.Double(source.getPosition(j).getX() - newPosMap.get(j+1).getX(),
//						source.getPosition(j).getY() - newPosMap.get(j+1).getY());
//				Point2D destPos = new Point2D.Double(dest.getPosition(j).getX() - newPosMap.get(j+1).getX(),
//						dest.getPosition(j).getY() - newPosMap.get(j+1).getY());
//				Point2D relPos = new Point2D.Double(source.getPosition(j).getX() - source.getPosition(j+1).getX(),
//						source.getPosition(j).getY() -source.getPosition(j+1).getY());
//				Point2D destPos = new Point2D.Double(dest.getPosition(j).getX() - dest.getPosition(j+1).getX(),
//						dest.getPosition(j).getY() - dest.getPosition(j+1).getY());
				//System.out.println("relPos: " + relPos);
				//System.out.println("destPos: " + destPos);
				//double angleDiff = tester.normaliseAngle(Math.atan2(destPos.getY(), destPos.getX()) - Math.atan2(relPos.getY(), relPos.getX()));
				//System.out.println("Angle: "+ angleDiff);
				Point2D newPos;
//				if(distanceMap.get(j) > 0) {
					newPos = new Point2D.Double(relPosMap.get(j).getX()*scalar*Math.cos(multipliers.get(j)) - relPosMap.get(j).getY()*scalar*Math.sin(multipliers.get(j)),
							relPosMap.get(j).getX()*scalar*Math.sin(multipliers.get(j)) + relPosMap.get(j).getY()*scalar*Math.cos(multipliers.get(j)));
					//System.out.println("Rotating positively");
//				} else {
//					newPos = new Point2D.Double(relPosMap.get(j).getX()*Math.cos(-multipliers.get(j)) - relPosMap.get(j).getY()*Math.sin(-multipliers.get(j)),
//							relPosMap.get(j).getX()*Math.sin(-multipliers.get(j)) + relPosMap.get(j).getY()*Math.cos(-multipliers.get(j)));
//					//System.out.println("Rotating negatively");
//				}
				newPos.setLocation(newPos.getX() + newPosMap.get(j+1).getX(), newPos.getY() + newPosMap.get(j+1).getY());
				newPosMap.put(j, newPos);
				//System.out.println("ASV: "+j+", distance to new pos: " + newPos.distance(dest.getPosition(j)));
				//System.out.println("ASV "+j+": "+newPos);
			//}
		}
		for(int j = asvMid + 1; j < asvCount; j++) {
			//System.out.println("ASV "+j+": ");
			//if(source.getPosition(j).distance(dest.getPosition(j))<0.0009) {
			//	newPosMap.put(j, dest.getPosition(j));
				//System.out.println("ASV: "+j+", distance to new pos: 0");
			//} else {
//				Point2D relPos = new Point2D.Double(source.getPosition(j).getX() - newPosMap.get(j-1).getX(),
//						source.getPosition(j).getY() - newPosMap.get(j-1).getY());
//				Point2D destPos = new Point2D.Double(dest.getPosition(j).getX() - newPosMap.get(j-1).getX(),
//						dest.getPosition(j).getY() - newPosMap.get(j-1).getY());
//				Point2D relPos = new Point2D.Double(source.getPosition(j).getX() - source.getPosition(j-1).getX(),
//						source.getPosition(j).getY() -source.getPosition(j-1).getY());
//				Point2D destPos = new Point2D.Double(dest.getPosition(j).getX() - dest.getPosition(j-1).getX(),
//						dest.getPosition(j).getY() - dest.getPosition(j-1).getY());
//				double angleDiff = tester.normaliseAngle(Math.atan2(destPos.getY(), destPos.getX()) - Math.atan2(relPos.getY(), relPos.getX()));
				Point2D newPos;
				//System.out.println(angleDiff);
//				if(distanceMap.get(j) > 0) {
					newPos = new Point2D.Double(relPosMap.get(j).getX()*scalar*Math.cos(multipliers.get(j)) - relPosMap.get(j).getY()*scalar*Math.sin(multipliers.get(j)),
							relPosMap.get(j).getX()*scalar*Math.sin(multipliers.get(j)) + relPosMap.get(j).getY()*scalar*Math.cos(multipliers.get(j)));
					//System.out.println("Rotating positively");
//				} else {
//					newPos = new Point2D.Double(relPosMap.get(j).getX()*Math.cos(-multipliers.get(j)) - relPosMap.get(j).getY()*Math.sin(-multipliers.get(j)),
//							relPosMap.get(j).getX()*Math.sin(-multipliers.get(j)) + relPosMap.get(j).getY()*Math.cos(-multipliers.get(j)));
//					//System.out.println("Rotating negatively");
//				}
				newPos.setLocation(newPos.getX() + newPosMap.get(j-1).getX(), newPos.getY() + newPosMap.get(j-1).getY());
				newPosMap.put(j, newPos);
				//System.out.println("ASV: "+j+", distance to new pos: " + newPos.distance(dest.getPosition(j)));
				//System.out.println("ASV "+j+": "+newPos);
			//}
		}
		
		return newPosMap;
	}

}