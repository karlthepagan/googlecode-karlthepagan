package wh.contrib.rewardoptimizer;

public class Reward {
	Quest quest; // TODO if questId is fewer bits than quest reference, use id
	int itemId;
	// quality 0-6 (omit)
	// ilevel (omit)
	byte slot; // whole byte slot
	short[] stats; // high byte - stat description, low 3 - stat value (12-bit unsigned int)
}
