package solution;

import problem.ASVConfig;
import problem.Obstacle;
import problem.ProblemSpec;
import tester.Tester;

import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

public class ConfigGen {
	private static final double BOOM_LENGTH = 0.05;
	private int numSamples = 10; //Number of generation samples
	private List<ASVConfig> configs = new ArrayList<ASVConfig>();
	private List<ASVConfig> configs2 = new ArrayList<ASVConfig>();
	private ProblemSpec ps = new ProblemSpec();
	private static Random rndGen = new Random(); // Random number generator
	private static Tester tester = new Tester();
	private List<Point2D.Double> corners;
	public List<Rectangle2D.Double> intersectList2;
	public double deltaBig;

	private HashMap<ASVConfig , HashMap<ASVConfig , Double>> configMap = new HashMap<
			ASVConfig, HashMap<ASVConfig,Double>>();
	
	public ConfigGen(int nSample, List<Point2D.Double> corners, ProblemSpec ps) {
		this.numSamples = nSample;
		this.corners = corners;
		this.ps = ps;
		
		List<Rectangle2D.Double> rectList = new ArrayList<Rectangle2D.Double>();
		List<Rectangle2D.Double> rectList2 = new ArrayList<Rectangle2D.Double>();
		List<Rectangle2D.Double> intersectList = new ArrayList<Rectangle2D.Double>();
		intersectList2 = new ArrayList<Rectangle2D.Double>();
		for(Obstacle o: this.ps.getObstacles()) {
			double delta = 0.005;
			Rectangle2D rect = o.getRect();
			Rectangle2D.Double grownRect = new Rectangle2D.Double(rect.getX() - delta, rect.getY() - delta,rect.getWidth() + delta * 2, rect.getHeight() + delta * 2);
			for(Rectangle2D.Double temp : rectList) {
				Rectangle2D.Double temp2 = new Rectangle2D.Double();
				Rectangle2D.intersect(temp, grownRect, temp2);
				if(!temp2.isEmpty()) {
					intersectList.add(temp2);
				}
			}
			rectList.add(grownRect);
		}
		
		
		
		for(Obstacle o: this.ps.getObstacles()) {
			deltaBig = 0.025;
			Rectangle2D rect = o.getRect();
			Rectangle2D.Double grownRect = new Rectangle2D.Double(rect.getX() - deltaBig, rect.getY() - deltaBig,rect.getWidth() + deltaBig * 2, rect.getHeight() + deltaBig * 2);
			for(Rectangle2D.Double temp : rectList2) {
				Rectangle2D.Double temp2 = new Rectangle2D.Double();
				Rectangle2D.intersect(temp, grownRect, temp2);
				if(!temp2.isEmpty()) {
					intersectList2.add(temp2);
				}
			}
			rectList2.add(grownRect);
		}
		
		HashMap<Rectangle2D.Double, Rectangle2D.Double> map = new HashMap<Rectangle2D.Double, Rectangle2D.Double>();
		for(Rectangle2D.Double r2 : intersectList2) {
			for(Rectangle2D.Double r : intersectList) {
				if(r2.contains(r)) {
					map.put(r2, r);
				}
			}
		}
		intersectList2.removeAll(map.keySet());
		if(intersectList2.size() > 0) {
			Rectangle2D.Double r = intersectList2.get(0);
			if(r.getWidth() < r.getHeight()) {
				r.x = r.x - (deltaBig - r.getWidth());
				r.width = r.width + (deltaBig*2 - r.width);
			} else {
				r.y = r.y - (deltaBig - r.getHeight());
				r.height = r.height + (deltaBig*2 - r.height);
			}
		}
		System.out.println(intersectList2);
	}

	public HashMap<ASVConfig , HashMap<ASVConfig , Double>> getConfigMap(){
		return this.configMap;
	}
	
	/**
	 * Synchronise this ProblemSpec with an external ProblemSpec
	 * @param ps - the ProblemSpec to synchronise
	 */
	public void setPS(ProblemSpec ps) {
		//this.ps = ps;
		configMap.clear();
		configs.clear();
		configs.add(ps.getInitialState());
		configs.add(ps.getGoalState());
		configMap.put(ps.getInitialState(), new HashMap<ASVConfig,Double>());
		configMap.put(ps.getGoalState()   , new HashMap<ASVConfig,Double>());
	}
	
	/**
	 * Generates a set of random ASV configurations near a node n. These configurations follow
	 *  the angle offset in the form [x,y,theta_1,theta_2,theta_3,...,theta_numASV] where 
	 *  x,y is the coordinate of the first ASV, and theta_i are angles to consecutive ASVs.
	 *  Checks if the ASV configs are valid and if so, adds them to the configs list.
	 *  
	 * @param n - the Node near which to generate the configurations
	 * @param numASV - the number of ASVs in a configuration
	 */
	public void generateConfigs(Node n, int numASV, int convexity, Point2D.Double n2) {
		double maxDist = 0.005; //Max distance the first ASV will be from n 
		int i = 0;
		int j = 0;
		double randVal; //Randomly generated value
		double[] coordsCFG; //Configuration-space coordinates of ASVs (x,y,theta1,theta2,...)
		ASVConfig cfg;
		
		double minDistance = 0.5;
		//for(Point2D.Double p : corners) {
		//	double distance = n.getDistanceTo(new Node(p));
		//	if(distance < minDistance) {
		//		minDistance = distance;
		//	}//*(6 - 10*minDistance)
		//}
		int asvNo = 0;
		List<Integer> counter = new ArrayList<Integer>();
		for(int m = 0; m < ps.getASVCount(); m++) {
			counter.add(0);
		}
		//int incrementer = 0;
		
		int divisor = 3;
		
		if(intersectList2.size() > 0) {
			System.out.println(intersectList2.get(0).toString());
			Rectangle2D.Double r = intersectList2.get(0);
			
			
			
			
			if(r.contains(n)) {
				divisor = 24;
				System.out.println(divisor);
			}
			
		}
		if(configs.size() > ps.getASVCount()) {
			for(int l = 0; l < ps.getASVCount(); l++) {
				ASVConfig cfg1 = moveASVsAlong(configs.get(configs.size() - 1 - ps.getASVCount()),new Node(n2),n);
				if (tester.hasValidBoomLengths(cfg1) && tester.hasEnoughArea(cfg1)
						&& tester.fitsBounds(cfg1) && tester.isConvex(cfg1)
						&& !tester.hasCollision(cfg1, this.ps.getObstacles())) {
					configs.add(cfg1);
					//configs2.add(cfg1);
					System.out.println("Moved one added.");
				}
			}
		}
		
		while (i < numSamples*ps.getASVCount()) {
			coordsCFG = new double[numASV + 1];
			j = 0;
			while (j < numASV) {
				if (j == 0) {
					//coordsCFG[0] = n.getX();
					//coordsCFG[1] = n.getX();
					coordsCFG[0] = n.getX() + ((rndGen.nextFloat() - 0.5)*2.0) * maxDist;
					coordsCFG[1] = n.getY() + ((rndGen.nextFloat() - 0.5)*2.0) * maxDist;
					coordsCFG[2] = rndGen.nextFloat() * 2 * Math.PI;
					j = j + 2;
					//Increment immediately
				}
				// Here j >= 1
				
				randVal = rndGen.nextFloat();
				coordsCFG[j + 1] = tester.normaliseAngle(coordsCFG[j] + convexity * randVal * 2 * Math.PI / divisor);
				j++;
			}
			//Set coordinates as a configuration
			
			cfg = new ASVConfig(false, coordsCFG, asvNo);

			counter.set(asvNo,counter.get(asvNo) + 1);
			if(counter.get(asvNo) > 50000) {
				if(asvNo == ps.getASVCount() - 1) {
			 		asvNo = 0;
				} else {
					asvNo++;
				}
			}
			// Test Configuration Validity
			if (tester.hasValidBoomLengths(cfg) && tester.hasEnoughArea(cfg)
					&& tester.fitsBounds(cfg) && tester.isConvex(cfg)
					&& !tester.hasCollision(cfg, this.ps.getObstacles())) {
				configs.add(cfg); //Add if valid
				// System.out.println("ADDED CONFIG: " + (i + 1) + "/"
				// + numSamples);
				i++;
				if(asvNo == ps.getASVCount() - 1) {
			 		asvNo = 0;
				} else {
					asvNo++;
				}
			} else {
				cfg = null;
			}
		}
		System.out.println(counter);
	}
	
	public void setNumSamples(int numSamples){
		this.numSamples = numSamples;
	}

	
	/**
	 * Generates edges between ASV configurations and writes them to the configMap. Removes
	 * configurations which have no edges.
	 */
	public void linkConfigs(double maxDistance){
		//this.configMap;
		int nInitCfgs = configs.size();
		ArrayList<ASVConfig> invalidConfigs = new ArrayList<ASVConfig>();
		int i = 0, j=0;
		for (ASVConfig cfg: configs){
			configMap.put(cfg, new HashMap<ASVConfig,Double>());
			for (ASVConfig otherCfg: configs){
				if (!cfg.equals(otherCfg) && cfg.maxDistance(otherCfg)<maxDistance
						&& validEdge(cfg,otherCfg)){
					configMap.get(cfg).put(otherCfg,cfg.maxDistance(otherCfg));
					if (!configMap.keySet().contains(otherCfg)){
						configMap.put(otherCfg, new HashMap<ASVConfig,Double>());
					}
					configMap.get(otherCfg).put(cfg,otherCfg.maxDistance(cfg));
					j++;
				}
			}
			
			System.out.println("Cfg "+i+" " + nInitCfgs);
			
			i++;
		}
		/*for (ASVConfig cfg: configMap.keySet()){
			if (!cfg.equals(ps.getInitialState()) && !cfg.equals(ps.getGoalState())){
				//Remove configs which have 1 connection or less
				if (configMap.get(cfg).size() < 2 || configMap.get(cfg).isEmpty()){ 
					configMap.remove(cfg);
					invalidConfigs.add(cfg);
					
					if (configMap.keySet().contains(cfg)) {
						System.out.println("NUUUU");
						System.exit(1);
					}
					continue;
				}
				i++;
			}
		}
		configs.removeAll(invalidConfigs);
		*/
		System.out.println("Configurations Generated: "+nInitCfgs);
		System.out.println("Configurations Linked: "+ j);
		System.out.println("Redundancies: "+(nInitCfgs-configs.size()));
	}
	
	
	
	
	
	/**
	 * Tests if a valid edge can be formed between cfg1 and cfg2. 
	 * @param cfg1 - First configuration
	 * @param cfg2 - Second configuration
	 * @return true if edge is valid, false otherwise
	 */
	private boolean validEdge(ASVConfig cfg1, ASVConfig cfg2){
		//Create line from configurations
		//Check if halfway point of line is <= half line length to closest obstacle corner
			//if not, valid
			//if so, split line and recheck
		//Implement recursively
		
		//cfg1.maxDistance(cfg2);
		//cfg1.totalDistance(cfg2);
		for(Obstacle o: ps.getObstacles()) {
			for(int i=0; i< cfg1.getASVCount(); i++) {
				if(new Line2D.Double(cfg1.getPosition(i),cfg2.getPosition(i)).intersects(o.getRect())) {
					return false;
				}
			}
		}
		
		
		return true;
	}
	
	public List<ASVConfig> getConfigs() {
		return this.configs;
	}
	
	public List<ASVConfig> getConfigs2() {
		return this.configs2;
	}

	public ASVConfig moveASVsAlong(ASVConfig conf, Node direction0,
			Node direction1) {
		double angle0 = Math.atan2(direction1.getY() - direction0.getY(),
				direction1.getX() - direction0.getX());


		List<Point2D.Double> list1 = new ArrayList<Point2D.Double>();
		for (Point2D.Double point : conf.getASVPositions()) {
			double newx = point.getX() + direction0.getDistanceTo(direction1)* Math.cos(angle0);
			double newy = point.getY() + direction0.getDistanceTo(direction1)* Math.sin(angle0);
			list1.add(new Point2D.Double(newx, newy));
		}
		return new ASVConfig(list1);

	}
	
	/**
	 * @param args
	 */
//	public static void main(String[] args) {
//
//		ConfigGen a = new ConfigGen(5);
//
//		// generateConfig(2);
//
//		System.out.print(a.getConfigs().toString());
//
//	}
	
	

}
