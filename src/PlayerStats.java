
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
	public static final int AD	= 0x74;
	public static final int AP	= 0x78;
	public static final int ATTACK_SPEED = 0x7C;
	public static final int MOVESPEED = 0x80;
	public static final int ARMOR =	0x84;
	public static final int MR = 0x88;
	public static final int HEALTH_REGEN = 0x98;
	public static final int MANA_REGEN = 0x9C;
	public static final int PERCENT_ARMOR_PEN = 0xA0;
	public static final int ARMOR_PEN = 0xA4;
	public static final int LIFESTEAL = 0xA8;
	public static final int PERCENT_MAGIC_PEN = 0xAC;
	public static final int MAGIC_PEN = 0xB0;
	public static final int SPELLVAMP = 0xB4;
	public static final int COOLDOWN_REDUCTION = 0xB8;
	public static final int SPELL_Q_LEVEL = 0x140;
	public static final int SPELL_W_LEVEL = 0x14C;
	public static final int SPELL_E_LEVEL = 0x158;
	public static final int SPELL_R_LEVEL = 0x164;
	public static final int SPELL_Q_COOLDOWN = 0x13C;
	public static final int SPELL_W_COOLDOWN = 0x148;
	public static final int SPELL_E_COOLDOWN = 0x154;
	public static final int SPELL_R_COOLDOWN = 0x160;
	public static final int SUMMONER_1_COOLDOWN = 0x16C;
	public static final int SUMMONER_2_COOLDOWN = 0x178;

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
      new IntStat("Total Gold", TOTAL_GOLD),
      new IntStat("Kills", KILLS),
      new IntStat("Deaths", DEATHS),
      new IntStat("Assists", ASSISTS),
  };
	
	public static enum DATA_TYPE {INTEGER, FLOAT, STRING};

	public static final int[] OLD_STATS = {TOTAL_HEALTH, CURRENT_HEALTH, TOTAL_MANA, CURRENT_MANA, LEVEL, CURRENT_XP, TOTAL_XP, KILLS, DEATHS, ASSISTS, CS, TOTAL_GOLD, UNSPENT_GOLD};
	public static final String[] STAT_NAMES = {"Total Health", "Current Health", "Total Mana", "Current Mana", "Level", "Current XP", "Total XP", "Kills", "Deaths", "Assists", "CS", "Total Gold", "Unspent Gold"};
	public static final DATA_TYPE[] STAT_DATA_TYPES = {DATA_TYPE.INTEGER,DATA_TYPE.INTEGER,DATA_TYPE.INTEGER,DATA_TYPE.INTEGER,DATA_TYPE.INTEGER,DATA_TYPE.INTEGER,DATA_TYPE.INTEGER,DATA_TYPE.INTEGER,DATA_TYPE.INTEGER,DATA_TYPE.INTEGER,DATA_TYPE.INTEGER,DATA_TYPE.INTEGER, DATA_TYPE.FLOAT};
	
	public static final int[] OLD_LOGGED_STATS = {TOTAL_GOLD};
	public static final String[] LOGGED_STAT_NAMES = {"Total Gold"};
	public static final DATA_TYPE[] LOGGED_STAT_DATA_TYPES = {DATA_TYPE.INTEGER};
	
	public static void main(String[] args){
		for(int i=0; i<STATS.length; i++){
			String type = "";
			if(STATS[i].getClass().getName().equals("IntStat")){
				type = "0";
			}else if(STATS[i].getClass().getName().equals("FloatStat")){
				type= "1";
			}else{
				type="2";
			}
			System.out.println("{\"name\":\"" + STATS[i].name + "\",\"offset\":\"" +  String.format("0x%03X", STATS[i].offset) + "\", \"log\" : false, \"type\": " + type + "}," );
		}
	}
}
