import java.util.Map;

public abstract class StatEntry<T> implements Map.Entry<String, String> {
  protected String name;
  protected T value;

  public String getKey() {
    return getName();
  }

  public String getName(){
    return this.name;
  }

  public String getValue() {
    return this.value.toString();
  }

  public String toString(){
    return this.name + ": " + this.value;
  }

  public String setValue(String newValue) {
    throw new UnsupportedOperationException("We're not hacking the game! Stats are READ ONLY");
  }

  public boolean equals(Object o) {
    if(o.getClass() == this.getClass()) {
      StatEntry other = (StatEntry)o;
      return other.name.equals(this.name) && other.value.equals(this.value);
    } else {
      return false;
    }
  }

  public int hashCode() {
    return (getKey() == null ? 0 : getKey().hashCode()) ^
           (getValue()==null ? 0 : getValue().hashCode());
  }

  public abstract StatEntry<T> add(StatEntry<T> other);
}