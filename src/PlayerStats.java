
public class PlayerStats {
	public static final int CURRENT_DEATH_TIMER = 0x18;
	public static final int TOTAL_HEALTH = 0x1C;
	public static final int CURRENT_HEALTH = 0x20;
	public static final int TOTAL_MANA = 0x24;
	public static final int CURRENT_MANA = 0x28;
	public static final int LEVEL = 0x2C;
	public static final int CURRENT_XP = 0x30;
	public static final int TOTAL_XP = 0x34;
	public static final int KILLS = 0x38;
	public static final int DEATHS = 0x3C;
	public static final int ASSISTS = 0x40;
	public static final int CS = 0x44;
	public static final int TOTAL_GOLD = 0x60;
	public static final int UNSPENT_GOLD = 0x64;
  /* Needs more research, I think they're pointers -SubD
	public static final int SPELL_Q_ID = 0x128;
	public static final int SPELL_W_ID = 0x134;
	public static final int SPELL_E_ID = 0x140;
	public static final int SPELL_R_ID = 0x150;
	*/

  public static Stat[] STATS = {
      new IntStat("Total Health", TOTAL_HEALTH),
      new IntStat("Current Health", CURRENT_HEALTH),
      new IntStat("Total Mana", TOTAL_MANA),
      new IntStat("Current Mana", CURRENT_MANA),
      new IntStat("Level", LEVEL),
      new IntStat("Current XP", CURRENT_XP),
      new IntStat("Total XP", TOTAL_XP),
      new IntStat("Kills", KILLS),
      new IntStat("Deaths", DEATHS),
      new IntStat("Assists", ASSISTS),
      new IntStat("CS", CS),
      new IntStat("Total Gold", TOTAL_GOLD),
      new FloatStat("Unspent Gold", UNSPENT_GOLD)
  };

  public static Stat[] LOGGED_STATS = {
      new IntStat("Total Gold", TOTAL_GOLD)
  };
	
	public static enum DATA_TYPE {INTEGER, FLOAT, STRING};

	public static final int[] OLD_STATS = {TOTAL_HEALTH, CURRENT_HEALTH, TOTAL_MANA, CURRENT_MANA, LEVEL, CURRENT_XP, TOTAL_XP, KILLS, DEATHS, ASSISTS, CS, TOTAL_GOLD, UNSPENT_GOLD};
	public static final String[] STAT_NAMES = {"Total Health", "Current Health", "Total Mana", "Current Mana", "Level", "Current XP", "Total XP", "Kills", "Deaths", "Assists", "CS", "Total Gold", "Unspent Gold"};
	public static final DATA_TYPE[] STAT_DATA_TYPES = {DATA_TYPE.INTEGER,DATA_TYPE.INTEGER,DATA_TYPE.INTEGER,DATA_TYPE.INTEGER,DATA_TYPE.INTEGER,DATA_TYPE.INTEGER,DATA_TYPE.INTEGER,DATA_TYPE.INTEGER,DATA_TYPE.INTEGER,DATA_TYPE.INTEGER,DATA_TYPE.INTEGER,DATA_TYPE.INTEGER, DATA_TYPE.FLOAT};
	
	public static final int[] OLD_LOGGED_STATS = {TOTAL_GOLD};
	public static final String[] LOGGED_STAT_NAMES = {"Total Gold"};
	public static final DATA_TYPE[] LOGGED_STAT_DATA_TYPES = {DATA_TYPE.INTEGER};
}
