
public class ItemValueStat extends Stat  {

	  public ItemValueStat(String name, int offset) {
	    super(name, offset);
	  }

	  public StatEntry getEntry(int playerBaseAddress) {
	    return new ItemValueStatEntry(this.name, MemoryAccess.getInteger(playerBaseAddress + offset), MemoryAccess.getInteger(playerBaseAddress + offset + 0x4));
	  }

	  public StatEntry zeroEntry() {
	    return new ItemValueStatEntry(this.name, 0, 0);
	  }

}
