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
		
		Node n1 = new Node(0.1,0.1);
		Node n2 = new Node(0.9,0.1);
		Alistair a = new Alistair();
		
		ArrayList<Node> path = new ArrayList<Node>();
		
		int trial = 0;
		while(path.isEmpty()){
			System.out.print("Trial "+trial+" -> ");			
			List<List<Point2D.Double>> edges = a.createPRM(n1,n2,10000,0.02);
			path = a.AStar(n1, n2);
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
		
		
		/*
		 * for(List<Point2D.Double> e : edges){
	
		
			if(e.size()== 2) {
				visualHelper.addLinkedPoints(e);
			} else {
				visualHelper.addPoints(e);
			}
			//visualHelper.addPoints(corners);
		}
		*/
		
		List<Point2D.Double> l = new ArrayList<Point2D.Double>();
		if(path.size()!= 0) {
			l.add(path.get(0).toPoint2D());
			l.add(path.get(0).toPoint2D()); //???
			visualHelper.addLinkedPoints(l);			
			
			int i=1;
			int nASV = a.ps.getASVCount();
			int nSample=150;
			double maxDist = 0.1;
			maxDist = 0.05;
			ConfigGen cfGen = new ConfigGen(nSample);
			//ConfigGenOld cfGen = new ConfigGenOld(nSample);
			cfGen.setPS(a.ps);
			for(Node n : path) {
				l.remove(0);
				l.add(n.toPoint2D());
				visualHelper.addLinkedPoints(l);
				
				cfGen.generateConfigs(n, nASV);
				
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
			cfGen.linkConfigs(maxDist); //TO DO: if failed, try again!
			// Wait for user key press
			visualHelper.waitKey();
			
			visualHelper.clearAll();
			
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
