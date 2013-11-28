public class FloatStat extends Stat {
  public FloatStat(String name, int offset) {
    super(name, offset);
  }

  public StatEntry getEntry(int playerBaseAddress) {
    return new FloatStatEntry(this.name, MemoryAccess.getFloat(playerBaseAddress + offset));
  }

  public StatEntry zeroEntry() {
    return new IntStatEntry(this.name, 0);
  }
}
