package solution;

import problem.ASVConfig;
import problem.Obstacle;
import problem.ProblemSpec;
import tester.Tester;

import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

public class ConfigGen {
	private static final double BOOM_LENGTH = 0.05;
	private int numSamples = 10; //Number of generation samples
	private List<ASVConfig> configs = new ArrayList<ASVConfig>();
	private ProblemSpec ps = new ProblemSpec();
	private static Random rndGen = new Random(); // Random number generator
	private static Tester tester = new Tester();

	private HashMap<ASVConfig , HashMap<ASVConfig , Double>> configMap = new HashMap<
			ASVConfig, HashMap<ASVConfig,Double>>();
	
	public ConfigGen(int nSample) {
		this.numSamples = nSample;
		setPS(ps);
	}

	public HashMap<ASVConfig , HashMap<ASVConfig , Double>> getConfigMap(){
		return this.configMap;
	}
	
	/**
	 * Synchronise this ProblemSpec with an external ProblemSpec
	 * @param ps - the ProblemSpec to synchronise
	 */
	public void setPS(ProblemSpec ps) {
		this.ps = ps;
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
	public void generateConfigs(Node n, int numASV) {
		double maxDist = 0.05; //Max distance the first ASV will be from n 
		int i = 0;
		int j = 0;
		double randVal; //Randomly generated value
		double[] coordsCFG; //Configuration-space coordinates of ASVs (x,y,theta1,theta2,...)
		ASVConfig cfg;
		while (i < numSamples) {
			coordsCFG = new double[numASV + 1];
			j = 0;
			while (j < numASV) {
				if (j == 0) {
					//coordsCFG[0] = n.getX();
					//coordsCFG[1] = n.getX();
					coordsCFG[0] = n.getX() + ((rndGen.nextFloat() - 0.5)*2.0) * maxDist;
					coordsCFG[1] = n.getY() + ((rndGen.nextFloat() - 0.5)*2.0) * maxDist;
					j++; //Increment immediately
				}
				// Here j >= 1
				randVal = rndGen.nextFloat();
				coordsCFG[j + 1] = randVal * 2 * Math.PI;
				j++;
			}
			//Set coordinates as a configuration
			cfg = new ASVConfig(false, coordsCFG);

			
			
			// Test Configuration Validity
			if (tester.hasValidBoomLengths(cfg) && tester.hasEnoughArea(cfg)
					&& tester.fitsBounds(cfg) && tester.isConvex(cfg)
					&& !tester.hasCollision(cfg, this.ps.getObstacles())) {
				configs.add(cfg); //Add if valid
				// System.out.println("ADDED CONFIG: " + (i + 1) + "/"
				// + numSamples);
				i++;
			}
		}
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
			//System.out.println("Cfg "+i+" maps to "+j);
			j=0;
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
		System.out.println("Configurations Linked: "+configs.size());
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

	
	/**
	 * @param args
	 */
	public static void main(String[] args) {

		ConfigGen a = new ConfigGen(5);

		// generateConfig(2);

		System.out.print(a.getConfigs().toString());

	}

}
