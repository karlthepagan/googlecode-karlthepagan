package wh.contrib.rewardoptimizer;

/**
Reward Optimizer


traverse quests and pick all usable items - nodes on a decision graph
calculate maximum item benefit for the leaf nodes
	ilevel points
	tank points - http://www.wowinterface.com/downloads/info6419-TankPoints.html
	healer points - healing until OOM
	dps points - sum dps cd until OOM
	pvp points - time until death -> maximum damage output in that time


structures:

Quest
	List<Reward>
	level
	quest id

Reward
	item id
	quest id
	stats


RewardNode
	quest ref
	reward ref

	parent node

	RewardNode[]


building values
Map<RewardNode,Valuation>
 */
public class RewardOptimizer {

}
