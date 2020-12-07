package life;

public enum Buttons {

	START(0), ONESTEP(1), ZOOMIN(2), ZOOMOUT(3), SPEEDUP(4), SLOWDOWN(5);
	
	private Buttons(int i) {
		this.number = i;
	}
	
	public int getNumber() {
		return this.number;
	}

	
	private final int number;
}
