
public class Stat {
	private String name;
	private String value;
	
	public Stat(String name, int value){
		this.name = name;
		this.value = "" + value;
	}
	
	public Stat(String name, float value){
		this.name = name;
		this.value = "" + value;
	}
	
	public Stat(String name, String value){
		this.name = name;
		this.value = value;
	}
	
	public String getName(){
		return this.name;
	}
	
	public String getValue(){
		return this.value;
	}
	
	public String toString(){
		return this.name + ": " + this.value;
	}
}
