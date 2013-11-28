public class IntStatEntry extends StatEntry<Integer> {
  public IntStatEntry(String name, int value) {
    this.name = name;
    this.value = value;
  }

  public IntStatEntry add(StatEntry<Integer> other) {
    return new IntStatEntry(name, value + other.value);
  }
}
