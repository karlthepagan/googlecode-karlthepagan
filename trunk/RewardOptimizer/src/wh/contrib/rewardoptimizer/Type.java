package wh.contrib.rewardoptimizer;

public enum Type {
	Head(0x00,1,1),
	Neck(0x01,2,1),
	Shoulder(0x02,3,1),
	Chest(0x03,4,1),
	Waist(0x04,5,1),
	Legs(0x05,6,1),
	Feet(0x06,7,1),
	Wrist(0x07,8,1),
	Hands(0x08,9,1),
	Finger(0x09,10,2), // 2 rings
	Trinket(0x0A,11,2), // 2 trinkets
	Back(0x0B,16,1),
	Shield(0x0C,14,1),
	Off_Hand(0x8C,22,1),
	Held_In_Off_hand(0xCC,23,1),
	One_Hand(0x8D,13,1),
	Two_Hand(0xCD,17,1),
	Main_Hand(0x0D,21,1),
	Ranged(0x0E,15,1),
	Thrown(0x8E,25,1),
	Relic(0xCE,28,1),
	Shirt(0x0F,4,1),
	Tabard(0x1F,19,1),
	Projectile(0x2F,24,1),
	Bag(0x3F,18,11); // 4 on person, 7 in bank
	
	public byte id;
	public byte gameId;
	public byte max;
	
	private Type(int id, int gid, int count) {
		this.id = (byte)id;
		this.gameId = (byte)gid;
		this.max = (byte)count;
	}
	
	public static byte slot(byte typeId) {
		return (byte)(typeId & PHYSICAL_MASK);
	}
	
	/** Physical slot */
	private static final int PHYSICAL_MASK = 0x3F;
}


/* 
 * NOTES
 * Slots:
	Simple: 1/2 byte
0 - <option value="1">Head</option>
1 - <option value="2">Neck</option>
2 - <option value="3">Shoulder</option>
3 - <option value="5">Chest</option>
4 - <option value="6">Waist</option>
5 - <option value="7">Legs</option>
6 - <option value="8">Feet</option>
7 - <option value="9">Wrist</option>
8 - <option value="10">Hands</option>
9 - <option value="11">Finger</option>
10 - <option value="12">Trinket</option>
11 - <option value="16">Back</option>

Extended:
12 x <option value="14">Shield</option>
0x0C
12 x <option value="22">Off Hand</option>
0x8C
12 x <option value="23">Held In Off-hand</option>
0xCC

13 x <option value="13">One-Hand</option>
0x8D
13 x <option value="17">Two-Hand</option>
0xCD
13 x <option value="21">Main Hand</option>
0x0D

14 x <option value="15">Ranged</option>
0x0E
14 x <option value="25">Thrown</option>
0x8E
14 x <option value="28">Relic</option>
0xCE

15 x <option value="4">Shirt</option>
0x0F
15 x <option value="19">Tabard</option>
0x1F
15 x <option value="24">Projectile</option>
0x2F
15 x <option value="18">Bag</option>
0x3F
*/
