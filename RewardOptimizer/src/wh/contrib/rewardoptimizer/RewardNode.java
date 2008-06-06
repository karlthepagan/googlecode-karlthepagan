package wh.contrib.rewardoptimizer;

public class RewardNode {
	final Quest quest; // TODO if questId is fewer bits than quest reference, use id
	final Reward reward;
	
	final RewardNode parent;
	RewardNode[] child;
	
	public RewardNode(Quest q, Reward r, RewardNode p) {
		quest = q;
		reward = r;
		parent = p;
	}
}
