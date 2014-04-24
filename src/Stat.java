import java.util.Map;

public abstract class Stat {
  protected String name;
  protected int offset;
  
  public Stat(String name){
	  this.name = name;
  }

  public Stat(String name, int offset) {
    this.name = name;
    this.offset = offset;
  }

  public abstract StatEntry getEntry(int playerBaseAddress);
  public abstract StatEntry zeroEntry();
}