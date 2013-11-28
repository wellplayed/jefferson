class FloatStatEntry extends StatEntry<Float> {
  public FloatStatEntry(String name, float value) {
    this.name = name;
    this.value = value;
  }

  public FloatStatEntry add(StatEntry<Float> other) {
    return new FloatStatEntry(name, value + other.value);
  }
}
