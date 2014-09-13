package visualDebugger;

import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.io.IOException;
import java.util.ArrayList;
//import java.util.Arrays;
//import java.util.Iterator;
//import java.util.Set;
import java.util.List;

import problem.ASVConfig;
import problem.Obstacle;
import solution.AStarCfg;
import solution.Alistair;
import solution.ConfigGen;
import solution.Node;
import tester.Tester;

//import solution.ConfigGenOld;

public class VisualHelperTester {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		final long startTime = System.currentTimeMillis();
		boolean findSoln = true;
		Alistair a = new Alistair();
		Node n1 = new Node(a.ps.getInitialState().getASVPositions()
				.get(a.ps.getASVCount() / 2));
		Node n2 = new Node(a.ps.getGoalState().getASVPositions()
				.get(a.ps.getASVCount() / 2));
		
		Tester tester = new Tester(a.ps);
		List<Node> path = new ArrayList<Node>();
		List<List<Point2D.Double>> edges = new ArrayList<List<Point2D.Double>>();
		int trial = 0;
		while (path.isEmpty()) {
			System.out.print("Trial " + trial + " -> ");
			edges = a.createPRM(n1, n2, 15000, 0.02);
			path = a.AStar(n1, n2);
			trial++;
			// ArrayList<Point2D.Double> corners = a.findPathCorners(path);
		}
		VisualHelper visualHelper = new VisualHelper();

		// System.out.println(path);
		List<Node> interpolatedPath = new ArrayList<Node>();
		for (int i = 1; i < path.size(); i++) {
			interpolatedPath.addAll(a.interpolateNodes(path.get(i - 1),
					path.get(i)));

		}
		interpolatedPath.add(path.get(path.size() - 1));
		// System.out.println(interpolatedPath);
		ArrayList<Rectangle2D> rects = new ArrayList<Rectangle2D>();
		for (Obstacle o : a.ps.getObstacles()) {
			rects.add(o.getRect());
		}

		visualHelper.addRectangles(rects);

		List<Point2D.Double> list1 = new ArrayList<Point2D.Double>();
		for (Node e : path) {
			list1.add(e.toPoint2D());

			// visualHelper.addPoints(corners);
		}
		visualHelper.addLinkedPoints2(list1);
		visualHelper.addPoints(list1);

		List<Point2D.Double> l = new ArrayList<Point2D.Double>();
		int reruns = 0;
		while (findSoln){
			if (path.size() != 0) {
				l.add(path.get(0).toPoint2D());
				l.add(path.get(0).toPoint2D()); // ???
				visualHelper.addLinkedPoints(l);
	
				int i = 1;
				int nASV = a.ps.getASVCount();
				int nSample = 1;
				double maxLinkDist = 0.15;
	
				ConfigGen cfGen = new ConfigGen(nSample, a.findPathCorners(path),
						a.ps, a.getPassage(0.025, 0.005));
	
				List<Point2D.Double> pos = a.ps.getInitialState().getASVPositions();
				int deltaPos;
				double angle1 = Math.atan2(pos.get(1).getY() - pos.get(0).getY(),
						pos.get(1).getX() - pos.get(0).getX());
				double angle2 = Math.atan2(pos.get(2).getY() - pos.get(1).getY(),
						pos.get(2).getX() - pos.get(1).getX());
				double deltaAngle = angle2 - angle1;
				if (deltaAngle > Math.PI) {
					deltaAngle = deltaAngle - 2 * Math.PI;
				}
				if (deltaAngle < -Math.PI) {
					deltaAngle = deltaAngle + 2 * Math.PI;
				}
				if (deltaAngle > 0) {
					deltaPos = 1;
				} else {
					deltaPos = -1;
				}
	
				// ConfigGenOld cfGen = new ConfigGenOld(nSample);
				// cfGen.setPS(a.ps);
				System.out.print("Sampling Around Nodes...");
				for (Node n : interpolatedPath) {
					Point2D.Double prevN = l.remove(0);
					l.add(n.toPoint2D());
					visualHelper.addLinkedPoints(l);
	
					cfGen.generateConfigs(n, nASV, deltaPos, prevN);
	
					// for (ASVConfig cfg: cfGen.getConfigs()){
					// //System.out.println("Displaying CFG: "+j+"/"+nSample);
					// visualHelper.addPoints(cfg.getASVPositions());
					// visualHelper.addLinkedPoints(cfg.getASVPositions());
					// //visualHelper.repaint();
					// //visualHelper.addPoints(cfg.getASVPositions().g);
					// }
	
					double percent = ((100.0 * i) / interpolatedPath.size());
					if (percent % 5 < 0.5) {
						System.out.print((int) percent + "%...");
	
					}
					// System.out.println("Sampling Around Node: " + i + "/"
					// + interpolatedPath.size());
					i++;
	
					// try{
					// visualHelper.repaint();
					// } catch (Throwable e){
					//
					// }
				}
				System.out.println();
				visualHelper.repaint();
				cfGen.linkConfigs(maxLinkDist); // TO DO: if failed, try again!
				// Wait for user key press
				// visualHelper.waitKey();
	
				// visualHelper.clearAll();
				System.out.println(cfGen.getConfigs().size());
				visualHelper.addRectangles(rects);
				for (ASVConfig cfg : cfGen.getConfigs()) {
					visualHelper.addPoints(cfg.getASVPositions());
					visualHelper.addLinkedPoints(cfg.getASVPositions());
				}
	
				//visualHelper.waitKey();
				// visualHelper.addRectangles(rects);
				System.out.println("Removing redundancies...");
	
				AStarCfg aStarSolver = new AStarCfg(cfGen.getConfigMap()); // ASTAR!
				ArrayList<ASVConfig> validConfigs = aStarSolver.AStar(
						a.ps.getInitialState(), a.ps.getGoalState());
	
				visualHelper.repaint();
	
				//visualHelper.waitKey();
				System.out.println("Showing valid paths...");
				visualHelper.clearAll();
				visualHelper.addRectangles(rects);
				
				List<ASVConfig> validConfigs2 = a.interpolateSolution(validConfigs);
				
				// List<ASVConfig> validConfigs = new ArrayList<ASVConfig>();
	//			cfGen.generateConfigs(new Node(0.5,0.488), 7, 1, new Point2D.Double(0.495,0.488));
	//			System.out.println(cfGen.getConfigs().size());
				for (ASVConfig cfg : validConfigs2) {
					visualHelper.addPoints(cfg.getASVPositions());
					visualHelper.addLinkedPoints(cfg.getASVPositions());
				}
				visualHelper.repaint();
				//visualHelper.waitKey();
				a.ps.setPath(validConfigs2);
				System.out.println(validConfigs2.size());
				System.out.println(tester.getInvalidAreaStates());
				for(Integer x : tester.getInvalidAreaStates()) {
					System.out.println(tester.hasArea(validConfigs2.get(x)));
				}
				
				
				System.out.println("Bounds " + tester.getOutOfBoundsStates());
				System.out.println("Area " +tester.getInvalidAreaStates());
				System.out.println("Booms " + tester.getInvalidBoomStates());
				System.out.println("Collisions " + tester.getCollidingStates());
				System.out.println("Steps " + tester.getInvalidSteps());
				System.out.println("Convexity " + tester.getNonConvexStates());
				System.out.println("Final " + tester.hasGoalLast());
				try {
					a.ps.saveSolution("src/testcases/test_out.txt");
				} catch (IOException e) {
					e.printStackTrace();
				}
				
				if (       tester.getOutOfBoundsStates().isEmpty() 
						&& tester.getInvalidAreaStates().isEmpty()
						&& tester.getInvalidBoomStates().isEmpty()
						&& tester.getCollidingStates().isEmpty()
						&& tester.getInvalidSteps().isEmpty()
						&& tester.getNonConvexStates().isEmpty()
						&& tester.hasGoalLast()){
					findSoln = false;
					System.out.println("Solution found! :D");
					break;
				}
				if (reruns > 10){
					System.out.println("Could not find a solution :(");
					return;
				}
			}
			reruns++;
		}

		final long endTime = System.currentTimeMillis();
		System.out.println("Total execution time: " + (endTime - startTime)
				/ 1000.0);
		

		visualHelper.repaint();
		// visualHelper.clearAll();

		// Wait for user key press
	}

}
