public class IntStat extends Stat {
  public IntStat(String name, int offset) {
    super(name, offset);
  }

  public StatEntry getEntry(int playerBaseAddress) {
    return new IntStatEntry(this.name, MemoryAccess.getInteger(playerBaseAddress + offset));
  }

  public StatEntry zeroEntry() {
    return new IntStatEntry(this.name, 0);
  }
}
