package type;

public class DoubleWritable implements Writable {
	private double value;
	public DoubleWritable(){}
	public DoubleWritable(double val) {
		value = val;
	}
	public double getValue() {
		return value;
	}
	public void setValue(double value) {
		this.value = value;
	}
	@Override
	public Writable parse(String str) {
		return new DoubleWritable(Double.parseDouble(str));
	}
	@Override
	public String toString() {
		return String.valueOf(value);
	}
}
