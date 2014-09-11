package solution;

import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

import problem.ASVConfig;
import problem.Obstacle;
import problem.ProblemSpec;
import tester.Tester;

public class ConfigGen {
	private static final double BOOM_LENGTH = 0.05;
	private int numSamples = 10; // Number of generation samples
	private List<ASVConfig> configs = new ArrayList<ASVConfig>();
	// private List<ASVConfig> configs2 = new ArrayList<ASVConfig>();
	private ProblemSpec ps = new ProblemSpec();
	private static Random rndGen = new Random(); // Random number generator
	private static Tester tester = new Tester();
	private List<Point2D.Double> corners;
	public List<Rectangle2D.Double> rectInterList;

	public double deltaBig;

	private HashMap<ASVConfig, HashMap<ASVConfig, Double>> configMap = new HashMap<ASVConfig, HashMap<ASVConfig, Double>>();

	public ConfigGen(int nSample, List<Point2D.Double> corners, ProblemSpec ps,
			List<Rectangle2D.Double> rectInterList) {
		this.numSamples = nSample;
		this.corners = corners;
		this.ps = ps;
		setPS(ps);
		this.rectInterList = rectInterList;
	}

	public HashMap<ASVConfig, HashMap<ASVConfig, Double>> getConfigMap() {
		return this.configMap;
	}

	/**
	 * Synchronise this ProblemSpec with an external ProblemSpec
	 * 
	 * @param ps
	 *            - the ProblemSpec to synchronise
	 */
	public void setPS(ProblemSpec ps) {
		this.ps = ps;
		configMap.clear();
		configs.clear();
		configs.add(ps.getInitialState());
		configs.add(ps.getGoalState());
		configMap.put(ps.getInitialState(), new HashMap<ASVConfig, Double>());
		configMap.put(ps.getGoalState(), new HashMap<ASVConfig, Double>());
	}

	/**
	 * Generates a set of random ASV configurations near a node n. These
	 * configurations follow the angle offset in the form
	 * [x,y,theta_1,theta_2,theta_3,...,theta_numASV] where x,y is the
	 * coordinate of the first ASV, and theta_i are angles to consecutive ASVs.
	 * Checks if the ASV configs are valid and if so, adds them to the configs
	 * list.
	 * 
	 * @param n
	 *            - the Node near which to generate the configurations
	 * @param numASV
	 *            - the number of ASVs in a configuration
	 */
	public void generateConfigs(Node n, int numASV, int convexity,
			Point2D.Double n2) {
		double maxDist = 0.005; // Max distance the first ASV will be from n
		int i = 0;
		int j = 0;
		double randVal; // Randomly generated value
		double[] coordsCFG; // Configuration-space coordinates of ASVs
							// (x,y,theta1,theta2,...)
		ASVConfig cfg;

		// double minDistance = 0.5;
		// for(Point2D.Double p : corners) {
		// double distance = n.getDistanceTo(new Node(p));
		// if(distance < minDistance) {
		// minDistance = distance;
		// }//*(6 - 10*minDistance)
		// }
		int asvNo = 0;
		List<Integer> counter = new ArrayList<Integer>();
		for (int m = 0; m < ps.getASVCount(); m++) {
			counter.add(0);
		}

		if (configs.size() > ps.getASVCount()) {
			for (int l = 0; l < ps.getASVCount(); l++) {
				ASVConfig cfg1 = moveASVsAlong(
						configs.get(configs.size() - 1 - ps.getASVCount()),
						new Node(n2), n);
				if (tester.hasValidBoomLengths(cfg1)
						&& tester.hasEnoughArea(cfg1)
						&& tester.fitsBounds(cfg1) && tester.isConvex(cfg1)
						&& !tester.hasCollision(cfg1, this.ps.getObstacles())) {
					configs.add(cfg1);
					// configs2.add(cfg1);
					// System.out.println("Moved one added.");
				}
			}
		}

		while (i < numSamples * ps.getASVCount()) {
			coordsCFG = new double[numASV + 1];
			j = 0;
			while (j < numASV) {
				if (j == 0) {
					coordsCFG[0] = n.getX()
							+ ((rndGen.nextFloat() - 0.5) * 2.0) * maxDist;
					coordsCFG[1] = n.getY()
							+ ((rndGen.nextFloat() - 0.5) * 2.0) * maxDist;
					coordsCFG[2] = rndGen.nextFloat() * 2 * Math.PI;
					// Increment immediately
					j = j + 2;
				}
				// Here j >= 2
				randVal = rndGen.nextFloat();
				int divisor = 3;
				double smallSquishDist = 0.2;
				double squishDist = 0.15;
				if (rectInterList.size() > 0) {
					Point2D.Double ASVPosition = new ASVConfig(false,
							coordsCFG, j).getPosition(j - 1);
					// don't mess with the j and j - 1, works beautifully
					Rectangle2D.Double r = rectInterList.get(0);
					if (r.contains(ASVPosition)
							&& (r.getWidth() > smallSquishDist || r.getHeight() > smallSquishDist)) {
						divisor = 25;
						// System.out.println("Width, height & divisor = "
						// + r.getWidth() + " " + r.getHeight() + " "
						// + divisor);
					} else if (r.contains(ASVPosition)
							&& (r.getWidth() > squishDist || r.getHeight() > smallSquishDist)) {
						divisor = 12;
						System.out.println("Width, height & divisor = "
								+ r.getWidth() + " " + r.getHeight() + " "
								+ divisor);
					} else {
						divisor = 3;
					}
				}
				coordsCFG[j + 1] = tester.normaliseAngle(coordsCFG[j]
						+ convexity * randVal * 2 * Math.PI / divisor);
				j++;
			}

			// Set coordinates as a configuration
			cfg = new ASVConfig(false, coordsCFG, asvNo);

			counter.set(asvNo, counter.get(asvNo) + 1);
			if (counter.get(asvNo) > 50000) {
				if (asvNo == ps.getASVCount() - 1) {
					asvNo = 0;
				} else {
					asvNo++;
				}
				// System.out.println(counter);
			}
			// Test Configuration Validity
			if (tester.hasValidBoomLengths(cfg) && tester.hasEnoughArea(cfg)
					&& tester.fitsBounds(cfg) && tester.isConvex(cfg)
					&& !tester.hasCollision(cfg, this.ps.getObstacles())) {
				configs.add(cfg); // Add if valid
				// System.out.println("ADDED CONFIG: " + (i + 1) + "/"
				// + numSamples);
				i++;
				if (asvNo == ps.getASVCount() - 1) {
					asvNo = 0;
				} else {
					asvNo++;
				}
			} else {
				cfg = null;
			}
		}
		// System.out.println(counter);
	}

	public void setNumSamples(int numSamples) {
		this.numSamples = numSamples;
	}

	/**
	 * Generates edges between ASV configurations and writes them to the
	 * configMap. Removes configurations which have no edges.
	 */
	public void linkConfigs(double maxDistance) {
		System.out.print("Linking Configurations...");
		// this.configMap;
		int nInitCfgs = configs.size();
		ArrayList<ASVConfig> invalidConfigs = new ArrayList<ASVConfig>();
		int i = 0, j = 0;
		for (ASVConfig cfg : configs) {
			configMap.put(cfg, new HashMap<ASVConfig, Double>());
			for (ASVConfig otherCfg : configs) {
				if (!cfg.equals(otherCfg)
						&& cfg.maxDistance(otherCfg) < maxDistance
						&& validEdge(cfg, otherCfg)) {
					configMap.get(cfg).put(otherCfg, cfg.maxDistance(otherCfg));
					if (!configMap.keySet().contains(otherCfg)) {
						configMap.put(otherCfg,
								new HashMap<ASVConfig, Double>());
					}
					configMap.get(otherCfg).put(cfg, otherCfg.maxDistance(cfg));
					j++;
				}
			}

			double percent = (100.0 * i) / nInitCfgs;
			if (percent % 5 < 0.005) {
				System.out.print((int) percent + "%...");

			}
			// System.out.println("Cfg " + i + " " + nInitCfgs);

			i++;
		}
		System.out.println();
		System.out.println("Configurations Generated: " + nInitCfgs);
		System.out.println("Configurations Linked: " + j);
		System.out.println("Redundancies: " + (nInitCfgs - configs.size()));
	}

	/**
	 * Tests if a valid edge can be formed between cfg1 and cfg2.
	 * 
	 * @param cfg1
	 *            - First configuration
	 * @param cfg2
	 *            - Second configuration
	 * @return true if edge is valid, false otherwise
	 */
	private boolean validEdge(ASVConfig cfg1, ASVConfig cfg2) {
		// Create line from configurations
		// Check if halfway point of line is <= half line length to closest
		// obstacle corner
		// if not, valid
		// if so, split line and recheck
		// Implement recursively

		// cfg1.maxDistance(cfg2);
		// cfg1.totalDistance(cfg2);
		for (Obstacle o : ps.getObstacles()) {
			for (int i = 0; i < cfg1.getASVCount(); i++) {
				if (new Line2D.Double(cfg1.getPosition(i), cfg2.getPosition(i))
						.intersects(o.getRect())) {
					return false;
				}
			}
		}

		return true;
	}

	public List<ASVConfig> getConfigs() {
		return this.configs;
	}

	// public List<ASVConfig> getConfigs2() {
	// return this.configs2;
	// }

	public ASVConfig moveASVsAlong(ASVConfig conf, Node direction0,
			Node direction1) {
		double angle0 = Math.atan2(direction1.getY() - direction0.getY(),
				direction1.getX() - direction0.getX());

		List<Point2D.Double> list1 = new ArrayList<Point2D.Double>();
		for (Point2D.Double point : conf.getASVPositions()) {
			double newx = point.getX() + direction0.getDistanceTo(direction1)
					* Math.cos(angle0);
			double newy = point.getY() + direction0.getDistanceTo(direction1)
					* Math.sin(angle0);
			list1.add(new Point2D.Double(newx, newy));
		}
		return new ASVConfig(list1);

	}

}
