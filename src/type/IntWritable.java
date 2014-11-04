package type;

public class IntWritable implements Writable {
	public int value;
	public IntWritable(){}
	public IntWritable(int val) {
		value = val;
	}
	public int getValue() {
		return value;
	}
	public void setValue(int value) {
		this.value = value;
	}
	@Override
	public Writable parse(String str) {
		return new IntWritable(Integer.parseInt(str));
	}
	
}
