
public class ItemValueStatEntry extends StatEntry<Integer> {
	
	public ItemValueStatEntry(String name, int value, int quantity){
		this.name = name;
		if(quantity > 0)
			this.value = quantity * Items.itemValues.get(value);
		else
			this.value = Items.itemValues.get(value);
	}
	
	@Override
	public StatEntry<Integer> add(StatEntry<Integer> other) {
		return new IntStatEntry(name, value + other.value);
	}

}
