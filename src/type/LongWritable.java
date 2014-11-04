package type;

public class LongWritable implements Writable{
	private long value;
	public LongWritable(){}
	public LongWritable(long val) {
		this.value = val;
	}

	public long getValue() {
		return value;
	}

	public void setValue(long value) {
		this.value = value;
	}
	@Override
	public Writable parse(String str) {
		return new LongWritable(Long.parseLong(str));
	}
}
