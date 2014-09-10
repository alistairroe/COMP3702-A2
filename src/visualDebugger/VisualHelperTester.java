package visualDebugger;


import java.awt.Event;
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

//import solution.ConfigGenOld;

public class VisualHelperTester {

	/**
	 * @param args
	 */	public static void main(String[] args) {

		
		// Create list of points
//		ArrayList<Point2D> points = new ArrayList<Point2D>();
//		points.add(new Point2D.Double(0.3, 0.3));
//		points.add(new Point2D.Double(0.5, 0.5));
//		ArrayList<Point2D> points2 = new ArrayList<Point2D>();
//		points2.add(new Point2D.Double(0.8, 0.5));
//		points2.add(new Point2D.Double(0.9, 0.6));
//		points2.add(new Point2D.Double(0.9, 0.5));
//		points2.add(new Point2D.Double(0.8, 0.4));
//		
//		// Create list of rectangles
//		ArrayList<Rectangle2D> rects = new ArrayList<Rectangle2D>();
//		rects.add(new Rectangle2D.Double(0, 0, 0.2, 0.3));
//		
		
		//VisualHelper visualHelper = new VisualHelper();
		Alistair a = new Alistair();
		Node n1 = new Node(a.ps.getInitialState().getASVPositions().get(a.ps.getASVCount()/2));
		Node n2 = new Node(a.ps.getGoalState().getASVPositions().get(a.ps.getASVCount()/2));
		
		
		List<Node> path = new ArrayList<Node>();
		List<List<Point2D.Double>> edges = new ArrayList<List<Point2D.Double>>();
		int trial = 0;
		while(path.isEmpty()){
			System.out.print("Trial "+trial+" -> ");			
			edges = a.createPRM(n1,n2,10000,0.02);
			path = a.AStar(n1,n2);
			trial++;
			//ArrayList<Point2D.Double> corners = a.findPathCorners(path);
		}
		VisualHelper visualHelper = new VisualHelper();		
		
		System.out.println(path);

		
		
		ArrayList<Rectangle2D> rects = new ArrayList<Rectangle2D>();
		for(Obstacle o: a.ps.getObstacles()) {
			rects.add(o.getRect());
		}
		
		
		visualHelper.addRectangles(rects);
		
		
		List<Point2D.Double> list1 = new ArrayList<Point2D.Double>();
		 for(Node e : path){
			list1.add(e.toPoint2D());
			
			//visualHelper.addPoints(corners);
		}
		 visualHelper.addLinkedPoints2(list1);
		 visualHelper.addPoints(list1);
		
		
		List<Point2D.Double> l = new ArrayList<Point2D.Double>();
		if(path.size()!= 0) {
			l.add(path.get(0).toPoint2D());
			l.add(path.get(0).toPoint2D()); //???
			visualHelper.addLinkedPoints(l);			
			
			int i=1;
			int nASV = a.ps.getASVCount();
			int nSample=50;
			double maxLinkDist = 0.05;
			
			ConfigGen cfGen = new ConfigGen(nSample,a.findPathCorners(path));
			
			List<Point2D.Double> pos = a.ps.getInitialState().getASVPositions();
			int deltaPos;
			double angle1 = Math.atan2(pos.get(1).getY() - pos.get(0).getY(), pos.get(1).getX() - pos.get(0).getX());
			double angle2 = Math.atan2(pos.get(2).getY() - pos.get(1).getY(), pos.get(2).getX() - pos.get(1).getX());
			double deltaAngle = angle2 - angle1;
			if(deltaAngle > Math.PI) {
				deltaAngle = deltaAngle - 2 * Math.PI;
			}
			if(deltaAngle < -Math.PI) {
				deltaAngle = deltaAngle + 2* Math.PI;
			}
			if(deltaAngle > 0) {
				deltaPos = 1;
			} else {
				deltaPos = -1;
			}
			
			//ConfigGenOld cfGen = new ConfigGenOld(nSample);
			cfGen.setPS(a.ps);
			for(Node n : path) {
				l.remove(0);
				l.add(n.toPoint2D());
				visualHelper.addLinkedPoints(l);
				
				
				cfGen.generateConfigs(n, nASV, deltaPos);
				
				int j = 1;
				for (ASVConfig cfg: cfGen.getConfigs()){
					//System.out.println("Displaying CFG: "+j+"/"+nSample);
					visualHelper.addPoints(cfg.getASVPositions());
					visualHelper.addLinkedPoints(cfg.getASVPositions());
					//visualHelper.repaint();
					j++;
					if (j>nSample) j = 1;
					//visualHelper.addPoints(cfg.getASVPositions().g);
				}
				System.out.println("Sampling Around Node: "+i+"/"+path.size());
				i++;
				
//				try{
//					visualHelper.repaint();
//				} catch (Throwable e){
//					
//				} 
			}
			visualHelper.repaint();
			System.out.println("Linking Configurations...");
			cfGen.linkConfigs(maxLinkDist); //TO DO: if failed, try again!
			// Wait for user key press
			//visualHelper.waitKey();
			
			//visualHelper.clearAll();
			
			visualHelper.addRectangles(rects);
			for (ASVConfig cfg: cfGen.getConfigs()){
				visualHelper.addPoints(cfg.getASVPositions());
				visualHelper.addLinkedPoints(cfg.getASVPositions());
			}
			//visualHelper.addRectangles(rects);
			System.out.println("Removing redundancies...");
			
			AStarCfg aStarSolver = new AStarCfg(cfGen.getConfigMap()); //ASTAR!
			ArrayList<ASVConfig> validConfigs=aStarSolver.AStar(a.ps.getInitialState(), a.ps.getGoalState());
			
			visualHelper.repaint();
			
			visualHelper.waitKey();
			System.out.println("Showing valid paths...");
			visualHelper.clearAll();
			visualHelper.addRectangles(rects);

			//List<ASVConfig> validConfigs = new ArrayList<ASVConfig>(); 
			for (ASVConfig cfg: validConfigs){
				visualHelper.addPoints(cfg.getASVPositions());
				visualHelper.addLinkedPoints(cfg.getASVPositions());
			}
			visualHelper.repaint();
			a.ps.setPath(validConfigs);
			try {
				a.ps.saveSolution("src/testcases/test_out.txt");
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		
		visualHelper.repaint();
		
		//visualHelper.clearAll();

		
		// Wait for user key press
		visualHelper.waitKey();
		


		visualHelper.repaint();
	}

}
