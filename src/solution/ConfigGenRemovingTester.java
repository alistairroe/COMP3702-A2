package solution;

import problem.ASVConfig;
import problem.Obstacle;
import problem.ProblemSpec;
import tester.Tester;

import java.awt.geom.Point2D;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

public class ConfigGenRemovingTester {

	private static final double BOOM_LENGTH = 0.05;
	public static final double DEFAULT_MAX_ERROR = 1e-5;

	private int numSamples = 1000;
	private List<ASVConfig> configs = new ArrayList<ASVConfig>();
	private List<ASVConfig> configsCSpace = new ArrayList<ASVConfig>();
	private Random rndGen = new Random(); // Random number generator
	private ProblemSpec ps = new ProblemSpec();

	public ConfigGenRemovingTester() {

	}

	public ConfigGenRemovingTester(int nSample) {
		this.numSamples = nSample;
	}

	public void generateConfig(Node node1, int numASV) {
		int i = 0;
		int j = 0;
		int kTrial = 0;
		double randVal;
		double[] coords, coordsCFG;
		double initX, initY;
		ASVConfig cfg, cfgCSpace;
		Tester tester = new Tester();// tester.DEFAULT_MAX_ERROR);

		ASVConfig cfgTest;

		while (i < numSamples) {
			coords = new double[2 * numASV];
			coordsCFG = new double[numASV + 1];
			j = 0;
			while (j < numASV) {
				if (j == 0) {
					initX = node1.getX() + ((rndGen.nextFloat() - 0.5) / 2.0)
							* (BOOM_LENGTH);
					initY = node1.getY() + ((rndGen.nextFloat() - 0.5) / 2.0)
							* (BOOM_LENGTH);
					// coords[0] = node1.getX();
					// coords[1] = node1.getY();
					// coordsCFG[0] = node1.getX();
					// coordsCFG[1] = node1.getX();

					//coords[0] = node1.getX() + ((rndGen.nextFloat() - 0.5)/2.0)
					//		* (numASV*BOOM_LENGTH);
					// coords[1] = node1.getY() + ((rndGen.nextFloat() - 0.5)/2.0) 
					//      * (numASV*BOOM_LENGTH);
					// coordsCFG[0] = node1.getX() + ((rndGen.nextFloat() - 0.5)/2.0) 
					//      * (numASV*BOOM_LENGTH);
					// coordsCFG[1] = node1.getY() + ((rndGen.nextFloat() - 0.5)/2.0) 
					//      * (numASV*BOOM_LENGTH);
					
					coords[0] = initX;
					coords[1] = initY;
					coordsCFG[0] = initX;
					coordsCFG[1] = initY;

					j++;
				}

				// Here j >= 1
				randVal = rndGen.nextFloat();
				coords[2 * j] = coords[2 * j - 2] + BOOM_LENGTH
						* Math.cos(randVal * 2 * Math.PI);
				coords[2 * j + 1] = coords[2 * j - 1] + BOOM_LENGTH
						* Math.sin(randVal * 2 * Math.PI);
				coordsCFG[j + 1] = randVal * 2 * Math.PI;

				/*
				cfgTest = new ASVConfig(Arrays.copyOf(coords, 2 * (j + 1)));
				
				//cfgTest = new ASVConfig(false,Arrays.copyOf(coordsCFG, j+1));
				if (!tester.hasValidBoomLengths(cfgTest)
						|| !tester.fitsBounds(cfgTest)
						//|| !tester.hasEnoughArea(cfgTest)
						//|| !tester.isConvex(cfgTest)
				// || tester.hasCollision(cfgTest, this.ps.getObstacles())
				) {
					// System.out.println(tester.hasEnoughArea(cfgTest));
					// System.out.println("BAD!"+j+"\n"+cfgTest+"\n"+(new
					// ASVConfig(coords)));
					kTrial++;
					if (kTrial > 10) { // Failed for more than 10 trials
						j = 0;
						kTrial = 0;
						continue;
					}
					j--;
					continue;
				}
				// System.out.println("GOOD!");
				
				*/
				j++;
			}

			cfg = new ASVConfig(false, coordsCFG);//new ASVConfig(coords);
			cfgCSpace = new ASVConfig(false, coordsCFG);

			// Test Configuration Validity
			if (tester.hasValidBoomLengths(cfg) && tester.hasEnoughArea(cfg)
					&& tester.fitsBounds(cfg) && tester.isConvex(cfg)
					&& !tester.hasCollision(cfg, this.ps.getObstacles())) {
				configs.add(cfg);
				configsCSpace.add(cfgCSpace);
				// System.out.println("ADDED CONFIG: " + (i + 1) + "/"
				// + numSamples);
				System.out.println(cfg);
				System.out.println(cfgCSpace + "\n");
				i++;
			}
		}

	}

	public List<ASVConfig> getConfigs() {
		return configs;
	}

	public List<ASVConfig> getCSpaceConfigs() {
		return configsCSpace;
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {

		ConfigGenRemovingTester a = new ConfigGenRemovingTester(5);

		// generateConfig(2);

		System.out.print(a.getConfigs().toString());

	}

	public void setPS(ProblemSpec ps) {
		this.ps = ps;
	}

}
