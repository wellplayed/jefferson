
public class PlayerStats {

	public static final int CURRENT_DEATH_TIMER = 0x0;
	public static final int TOTAL_HEALTH = 0x4;
	public static final int CURRENT_HEALTH = 0x8;
	public static final int TOTAL_MANA = 0xC;
	public static final int CURRENT_MANA = 0x10;
	public static final int LEVEL = 0x14;
	public static final int CURRENT_XP = 0x18;
	public static final int TOTAL_XP = 0x1C;
	public static final int KILLS = 0x20;
	public static final int DEATHS = 0x24;
	public static final int ASSISTS = 0x28;
	public static final int CS = 0x2C;
	public static final int GOLD = 0x48;
	public static final int SPELL_Q_ID = 0x10C;
	public static final int SPELL_W_ID = 0x118;
	public static final int SPELL_E_ID = 0x124;
	public static final int SPELL_R_ID = 0x130;

	public static final int[] STATS = {CURRENT_DEATH_TIMER, TOTAL_HEALTH, CURRENT_HEALTH, TOTAL_MANA, CURRENT_MANA, LEVEL, CURRENT_XP, TOTAL_XP, KILLS, DEATHS, ASSISTS, CS, GOLD};
	public static final String[] STAT_NAMES = {"Current Death Timer", "Total Health", "Current Health", "Total Mana", "Current Mana", "LeveL", "Current XP", "Total XP", "Kills", "Deaths", "Assists", "CS", "GOLD"};
}
