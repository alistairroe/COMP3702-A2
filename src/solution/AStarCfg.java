package solution;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.PriorityQueue;

import problem.ASVConfig;

/**
 * Implements the A-Star Algorithm to find the quickest / least expensive route
 * from the start Node to finish Node in the shortest time possible while
 * (hopefully) using the least amount of computational resources.
 * 
 * @author Route66
 * 
 */
public class AStarCfg {

	private HashMap<ASVConfig, HashMap<ASVConfig, Double>> configMap = new HashMap<ASVConfig, HashMap<ASVConfig, Double>>();
	private HashMap<ASVConfig, Double> fCosts = new HashMap<ASVConfig, Double>();

	public AStarCfg(HashMap<ASVConfig, HashMap<ASVConfig, Double>> configMap) {
		this.configMap = configMap;
	}

	public ArrayList<ASVConfig> AStar(final ASVConfig startCfg,
			final ASVConfig endCfg) {
		HashMap<ASVConfig, Double> gCosts = new HashMap<ASVConfig, Double>();
		HashMap<ASVConfig, ASVConfig> parentMap = new HashMap<ASVConfig, ASVConfig>();
		ArrayList<ASVConfig> closedSet = new ArrayList<ASVConfig>();

		PriorityQueue<ASVConfig> openSet = new PriorityQueue<ASVConfig>(1,
				new Comparator<ASVConfig>() {
					public int compare(ASVConfig o1, ASVConfig o2) {
						if (fCosts.get(o1) > fCosts.get(o2)) {
							return 1;
						} else if (fCosts.get(o1) < fCosts.get(o2)) {
							return -1;
						} else {
							return 0;
						}
					}
				});

		ASVConfig currCfg;

		System.out.println("MAP SIZE: " + configMap.size());

		gCosts.put(startCfg, 0.0);
		fCosts.put(startCfg,
				gCosts.get(startCfg) + calcHeuristic(startCfg, endCfg));
		openSet.add(startCfg);

		System.out.println("Start Links: " + configMap.get(startCfg).size());
		System.out.println("  End Links: " + configMap.get(endCfg).size());

		double tentativeGCost;
		while (!openSet.isEmpty()) {
			// System.out.println("Closed: " + closedSet.size() + "\tOpen: "
			// + openSet.size());

			currCfg = openSet.remove();

			if (currCfg.equals(endCfg)) {
				System.out.println("Found Solution!");
				return reconstructPath(parentMap, endCfg, startCfg,
						new ArrayList<ASVConfig>()); // LOOK HERE
			}
			closedSet.add(currCfg);
			// System.out.println(configMap.keySet());
			if (configMap.keySet().contains(currCfg)) {
				// System.out.println(" -> Links: "
				// + configMap.get(currCfg).size());

				int nRedundant = 0;
				for (ASVConfig childCfg : configMap.get(currCfg).keySet()) {
					if (closedSet.contains(childCfg)) {
						nRedundant++;
						continue;
					}

					// Else use cfgs which are not already closed
					tentativeGCost = gCosts.get(currCfg)
							+ currCfg.maxDistance(childCfg);

					if (!openSet.contains(childCfg)
							|| tentativeGCost < gCosts.get(childCfg)) {
						parentMap.put(childCfg, currCfg);

						gCosts.put(childCfg, tentativeGCost);
						fCosts.put(childCfg,
								tentativeGCost
										+ calcHeuristic(childCfg, endCfg));

						if (!openSet.contains(childCfg)) {
							openSet.add(childCfg);
						}
					}
				}
				// System.out.println(" -> Redundancies: " + nRedundant);
			}
			// return reconstructPath(parentMap, currCfg, startCfg,
			// new ArrayList<ASVConfig>()); // TESTING!
		}
		System.out.print("No Solution Found!");
		// return new ArrayList<ASVConfig>();
		return closedSet;

	}

	private double calcHeuristic(ASVConfig currCfg, ASVConfig endCfg) {
		return currCfg.maxDistance(endCfg);
	}

	/*
	 * public ArrayList<ASVConfig> reconstructPath( HashMap<ASVConfig,
	 * ASVConfig> parentMap, ASVConfig currentCfg, ASVConfig startCfg) {
	 * ASVConfig newASVConfig = currentCfg;
	 * 
	 * //reconstructPath(parentMap, currentCfg, startCfg); // LOOK
	 * ArrayList<ASVConfig> path = new ArrayList<ASVConfig>();
	 * path.add(0,startCfg); while (!newASVConfig.equals(startCfg)) {
	 * System.out.println(path.size()); path.add(newASVConfig); newASVConfig =
	 * parentMap.get(newASVConfig); } //path.add(0, startCfg); return path; }
	 */

	public ArrayList<ASVConfig> reconstructPath(
			HashMap<ASVConfig, ASVConfig> parentMap, ASVConfig currentCfg,
			ASVConfig startCfg, ArrayList<ASVConfig> pathList) {
		pathList.add(currentCfg);
		if (currentCfg.equals(startCfg)) { // Base Case
			// Array is in reverse order so reverse
			ArrayList<ASVConfig> sortedPath = new ArrayList<ASVConfig>();
			for (int i = pathList.size() - 1; i > -1; i--) {
				sortedPath.add(pathList.get(i));
			}
			return sortedPath;
		} else {
			return reconstructPath(parentMap, parentMap.get(currentCfg),
					startCfg, pathList);
		}
	}
}
