package life;

public class Tile {

	public Tile(int xPosition, int yPosition) {
		this.xPosition = xPosition;
		this.yPosition = yPosition;
		this.grid = new boolean[TILESIZE+2][TILESIZE+2];
		for(int i=0; i<TILESIZE+2; i++) {
			for(int j=0; j<TILESIZE+2; j++) {
				this.grid[i][j] = false;
			}
		}
	}
	
	public boolean pixelInRange(int x, int y) {
		return (x>=this.xPosition*TILESIZE-1)&&(x<=(this.xPosition+1)*TILESIZE)&&(y>=this.yPosition*TILESIZE-1)&&(y<=(this.yPosition+1)*TILESIZE);
	}
	
	public void setSquare(int x, int y, boolean value) {
		if (!pixelInRange(x,y)) {
			System.out.println("setter position out of range");
			return;
		}
		int xx = x-this.xPosition*TILESIZE+1;
		int yy = y-this.yPosition*TILESIZE+1;
		grid[xx][yy] = value;
		this.active = true;
	}
	
	public boolean getSquare(int x, int y) {
		if (!pixelInRange(x,y)) {
			System.out.println("getter position out of range");
			return false;
		}
		int xx = x-this.xPosition*TILESIZE+1;
		int yy = y-this.yPosition*TILESIZE+1;
		return grid[xx][yy];
	}
	
	/**
	 * true will be alive, false will be dead
	 */
	private boolean[][] grid;
	
	public void advanceTime() {
		boolean[][] newGrid = new boolean[TILESIZE][TILESIZE];
		int counter = 0;
		for(int x=0; x<TILESIZE; x++) {
			for(int y=0; y<TILESIZE; y++) {
				if (this.grid[x][y]) counter++;
				if (this.grid[x+1][y]) counter++;
				if (this.grid[x+2][y]) counter++;
				if (this.grid[x+2][y+1]) counter++;
				if (this.grid[x+2][y+2]) counter++;
				if (this.grid[x+1][y+2]) counter++;
				if (this.grid[x][y+2]) counter++;
				if (this.grid[x][y+1]) counter++;
				newGrid[x][y] = ((counter==2&&grid[x+1][y+1])||counter==3);
				counter = 0;
			}
		}
		boolean temp = this.active;
		this.active = false;
		this.topChanged = false;
		this.bottomChanged = false;
		this.leftChanged = false;
		this.rightChanged = false;
		this.topLeftChanged = false;
		this.topRightChanged = false;
		this.bottomLeftChanged = false;
		this.bottomRightChanged = false;
		for(int x=0; x<TILESIZE; x++) {
			for(int y=0; y<TILESIZE; y++) {
				if (this.grid[x+1][y+1] != newGrid[x][y]) {
					this.active = true;
					if(x == 0) {
						this.leftChanged = true;
						if(y == 0) {
							this.topChanged = true;
							this.topLeftChanged = true;
						}else {
							if(y == TILESIZE - 1) {
								this.bottomChanged = true;
								this.bottomLeftChanged = true;
							}
						}
					}else {
						if(x == TILESIZE-1) {
							this.rightChanged = true;
							if(y == 0) {
								this.topChanged = true;
								this.topRightChanged = true;
							}else {
								if(y == TILESIZE - 1) {
									this.bottomChanged = true;
									this.bottomRightChanged = true;
								}
							}
						}else {
							if(y == 0) {
								this.topChanged = true;
							}else {
								if(y == TILESIZE - 1) {
									this.bottomChanged = true;
								}
							}
						}
					}
				}
				this.grid[x+1][y+1] = newGrid[x][y];
			}
		}
		this.justDeactivated = temp && !this.active;
	}
	
	public final int xPosition;
	
	public final int yPosition;
	
	public static final int TILESIZE = 16;
	
	public boolean active;
	
	public boolean justDeactivated;
	
	public boolean leftChanged = false;
	
	public boolean rightChanged = false;

	public boolean topChanged = false;

	public boolean bottomChanged = false;

	public boolean topLeftChanged = false;

	public boolean topRightChanged = false;

	public boolean bottomLeftChanged = false;

	public boolean bottomRightChanged = false;

}