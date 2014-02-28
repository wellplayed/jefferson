
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
	public static final int ITEM0 = 0xCC;
	public static final int ITEM1 = 0xDC;
	public static final int ITEM2 = 0xEC;
	public static final int ITEM3 = 0xFC;
	public static final int ITEM4 = 0x10C;
	public static final int ITEM5 = 0x11C;
	public static final int ITEM6 = 0x12C;
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
      new FloatStat("Unspent Gold", UNSPENT_GOLD),
      new IntStat("Item 0 ID", ITEM0),
      new ItemValueStat("Item 0 Value", ITEM0),
      new IntStat("Item 1 ID", ITEM1),
      new ItemValueStat("Item 1 Value", ITEM1),
      new IntStat("Item 2 ID", ITEM2),
      new ItemValueStat("Item 2 Value", ITEM2),
      new IntStat("Item 3 ID", ITEM3),
      new ItemValueStat("Item 3 Value", ITEM3),
      new IntStat("Item 4 ID", ITEM4),
      new ItemValueStat("Item 4 Value", ITEM4),
      new IntStat("Item 5 ID", ITEM5),
      new ItemValueStat("Item 5 Value", ITEM5),
      new IntStat("Item 6 ID", ITEM6),
      new ItemValueStat("Item 6 Value", ITEM6)
  };
  
  public static Stat[] SC2_STATS = {
	  new IntStat("Average APM", 0x0),
	  new IntStat("Current APM", 0x38),
	  new IntStat("Units Killed", 0x70),
	  new IntStat("Units Lost", 0x78),
	  new IntStat("Workers", 0x1E8),
	  new IntStat("Minerals", 0x300),
	  new IntStat("Gas", 0x308),
	  new IntStat("Mineral Income", 0x380),
	  new IntStat("Gas Income", 0x388)
  };
  
  public static Stat[] SC2_LOGGED_STATS = {
	  new IntStat("Average APM", 0x0),
	  new IntStat("Workers", 0x1E8),
	  new IntStat("Mineral Income", 0x380),
	  new IntStat("Gas Income", 0x388)
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
