package com.smu.control;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import com.smu.linucb.algorithm.LinUCB_IND;
import com.smu.linucb.algorithm.LinUCB_SIN;
import com.smu.linucb.algorithm.LinUCB_TREE;
import com.smu.linucb.global.AlgorithmType;
import com.smu.linucb.global.Environment;
import com.smu.linucb.verification.TreeFixedCluster;

class RewardUpdate {
	private int count = 0;
	private double reward = 0;

	public double getReward() {
		return reward;
	}

	public void setReward(double reward) {
		this.reward = reward;
	}

	public int getCount() {
		return count;
	}

	public void setCount(int count) {
		this.count = count;
	}
}

public class AlgorithmThreadBuilder extends ALGControl {

	protected Random rBM;
	protected Random rUSR;
	private AlgorithmType algType;
	private Map<Integer, RewardUpdate> rewardMonitor;
	private List<Integer> rewardIndex;
	protected AlgorithmThreadBuilder inClass;

	public AlgorithmThreadBuilder() {
		this.rUSR = new Random(System.nanoTime()
				* Thread.currentThread().getId());
		// this.rUSR.setSeed((long) (this.rUSR.nextInt() * Math.random() * 10));
		this.rBM = new Random(System.nanoTime()
				* Thread.currentThread().getId());
		// this.rBM.setSeed((long) (this.rBM.nextInt() * Math.random() * 10));

	}

	public AlgorithmThreadBuilder(AlgorithmType algType) {
		this.algType = algType;
		this.setRewardMonitor(new HashMap<Integer, RewardUpdate>());
		this.setRewardIndex(new ArrayList<Integer>());
	}

	protected AlgorithmType getAlgType() {
		return algType;
	}

	public void setAlgType(AlgorithmType algType) {
		this.algType = algType;
	}

	public synchronized void displayResult(int count, double reward) {
		// TODO Auto-generated method stub

		// System.out.println("\n\nRound: " + count);
		// System.out.println("=========" + reward + "============\n\n");
		Environment.drChart.addData(this.algType, count, reward);
	}

	public static AlgorithmThreadBuilder factoryInstanceAlg(AlgorithmType type) {
		AlgorithmThreadBuilder alg = null;
		switch (type) {
		case LINUCB_SIN:
			alg = new LinUCB_SIN();
			// Environment.drChart.genDiffConfig(AlgorithmType.LINUCB_SIN);
			break;
		case LINUCB_IND:
			alg = new LinUCB_IND();
			// Environment.drChart.genDiffConfig(AlgorithmType.LINUCB_IND);
			break;
		case LINUCB_TREE:
			alg = new LinUCB_TREE();
			// Environment.drChart.genDiffConfig(AlgorithmType.LINUCB_TREE);
			break;
		case LINUCB_VER:
			alg = new TreeFixedCluster(false);
			// Environment.drChart.genDiffConfig(AlgorithmType.LINUCB_VER);
			break;
		case LINUCB_WARM:
			alg = new TreeFixedCluster(true);
			// Environment.drChart.genDiffConfig(AlgorithmType.LINUCB_WARM);
			break;
		}
		return alg;
	}

	@Override
	public void run() {
		AlgorithmThreadBuilder alg;
		Environment.drChart.genDiffConfig(this.algType);
		for (int k = 0; k < Environment.numAvgLoop; k++) {
			alg = AlgorithmThreadBuilder.factoryInstanceAlg(this.algType);
			alg.setInClass(this);
			alg.start();
		}
		int time;
		double rewardDisplay;
		while (true) {
			if (this.getRewardIndex().size() != 0) {
				time = this.getRewardIndex().get(0);
				if (this.getRewardMonitor().get(time).getCount() == Environment.numAvgLoop) {
					rewardDisplay = this.getRewardMonitor().get(time).getReward()
							/ Environment.numAvgLoop;
					this.displayResult(time, rewardDisplay);
					System.out.println("Time: " + time + " Reward: " + rewardDisplay);
				}
				this.getRewardIndex().remove(0);
				if (time == Environment.limitTime) {
					this.interrupt();
				}
			}
			try {
				Thread.sleep(50);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	protected synchronized void updateRewardMap(AlgorithmThreadBuilder rwMap, int count,
			double rw) {
		// int index;
		RewardUpdate ru;
		if ((count % Environment.buffSizeDisplay) == 0) {
			// index = count / Environment.buffSizeDisplay;
			if (rwMap.getRewardMonitor().containsKey(count)) {
				ru = rwMap.getRewardMonitor().get(count);
				ru.setCount(ru.getCount() + 1);
				ru.setReward(ru.getReward() + rw);
				rwMap.getRewardMonitor().put(count, ru);
			} else {
				ru = new RewardUpdate();
				ru.setCount(1);
				ru.setReward(rw);
				rwMap.getRewardMonitor().put(count, ru);
				rwMap.getRewardIndex().add(count);
				// Collections.sort(rwMap.rewardIndex);
			}
//			rwMap.getRewardMonitor().put(count, ru);
		}
	}

	protected Map<Integer, RewardUpdate> getRewardMonitor() {
		return rewardMonitor;
	}

	protected void setRewardMonitor(Map<Integer, RewardUpdate> rewardMonitor) {
		this.rewardMonitor = rewardMonitor;
	}

	protected synchronized AlgorithmThreadBuilder getInClass() {
		return inClass;
	}

	public void setInClass(AlgorithmThreadBuilder parent) {
		this.inClass = parent;
	}

	protected List<Integer> getRewardIndex() {
		return rewardIndex;
	}

	protected synchronized void setRewardIndex(List<Integer> rewardIndex) {
		this.rewardIndex = rewardIndex;
	}
}
