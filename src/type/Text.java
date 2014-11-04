package type;

public class Text implements Writable {
	private String text;
	public Text(){}
	public Text(String text) {
		this.text = text;
	}
	public String getText() {
		return text;
	}
	public void setText(String text) {
		this.text = text;
	}
	@Override
	public Writable parse(String str) {
		return new Text(str);
	}
}
