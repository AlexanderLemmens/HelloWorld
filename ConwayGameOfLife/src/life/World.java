package life;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;
import java.util.concurrent.locks.ReentrantLock;

public class World {

	public World() {
		this.lock = new ReentrantLock();
	}
	
	private ReentrantLock lock;
	
	public void advanceTime() {
		this.lock.lock();
		this.addNewTiles();
		Iterator<Integer> it = tiles.keySet().iterator();
		int xPosition;
		int yPosition;
		int tileSize = Tile.TILESIZE;
		while(it.hasNext()) {
			Tile tile = tiles.get(it.next());
			if(tile.active) {
				tile.advanceTime();
			}
		}
		it = tiles.keySet().iterator();
		while(it.hasNext()) {
			Tile tile = tiles.get(it.next());
			if(tile.active
//					||tile.justDeactivated
					) {
				xPosition = tile.xPosition;
				yPosition = tile.yPosition;
				if(tile.leftChanged) {
					Tile tile2 = this.getTile(xPosition-1,yPosition);
					tile2.active = true;
					int x = tileSize*xPosition;
					for(int y = tileSize*yPosition; y<tileSize*(yPosition+1);y++) {
						tile2.setSquare(x, y, tile.getSquare(x, y));
					}
				}
				if(tile.rightChanged) {
					Tile tile2 = this.getTile(xPosition+1,yPosition);
					tile2.active = true;
					int x = tileSize*(xPosition+1)-1;
					for(int y = tileSize*yPosition; y<tileSize*(yPosition+1);y++) {
						tile2.setSquare(x, y, tile.getSquare(x, y));
					}
				}
				if(tile.topChanged) {
					Tile tile2 = this.getTile(xPosition,yPosition-1);
					tile2.active = true;
					int y = tileSize*yPosition;
					for(int x = tileSize*xPosition; x<tileSize*(xPosition+1);x++) {
						tile2.setSquare(x, y, tile.getSquare(x, y));
					}
				}
				if(tile.bottomChanged) {
					Tile tile2 = this.getTile(xPosition,yPosition+1);
					tile2.active = true;
					int y = tileSize*(yPosition+1)-1;
					for(int x = tileSize*xPosition; x<tileSize*(xPosition+1);x++) {
						tile2.setSquare(x, y, tile.getSquare(x, y));
					}
				}
				if(tile.topLeftChanged) {
					Tile tile2 = this.getTile(xPosition-1, yPosition-1);
					tile2.active = true;
					int x = tileSize*xPosition;
					int y = tileSize*yPosition;
					tile2.setSquare(x, y, tile.getSquare(x, y));
				}
				if(tile.topRightChanged) {
					Tile tile2 = this.getTile(xPosition+1, yPosition-1);
					tile2.active = true;
					int x = tileSize*(xPosition+1)-1;
					int y = tileSize*yPosition;
					tile2.setSquare(x, y, tile.getSquare(x, y));
				}
				if(tile.bottomLeftChanged) {
					Tile tile2 = this.getTile(xPosition-1, yPosition+1);
					tile2.active = true;
					int x = tileSize*xPosition;
					int y = tileSize*(yPosition+1)-1;
					tile2.setSquare(x, y, tile.getSquare(x, y));
				}
				if(tile.bottomRightChanged) {
					Tile tile2 = this.getTile(xPosition+1, yPosition+1);
					tile2.active = true;
					int x = tileSize*(xPosition+1)-1;
					int y = tileSize*(yPosition+1)-1;
					tile2.setSquare(x, y, tile.getSquare(x, y));
				}
				tile.justDeactivated = false;
			}
		}
		this.lock.unlock();
	}
	
	public void changeSquare(int x, int y, boolean value) {
		this.lock.lock();
		int tileSize = Tile.TILESIZE;
		int xPosition;
		int yPosition;
		if(x>=0) {xPosition = x/tileSize;}
		else {xPosition = -1 - ((-x-1)/tileSize); }
		if(y>=0) {yPosition = y/tileSize;}
		else {yPosition = -1 - ((-y-1)/tileSize); }
		Tile tile = getTile(xPosition, yPosition);
		tile.setSquare(x, y, value);
		tile = getTile(xPosition-1, yPosition);
		if(tile.pixelInRange(x, y)) tile.setSquare(x, y, value);
		tile = getTile(xPosition-1, yPosition-1);
		if(tile.pixelInRange(x, y)) tile.setSquare(x, y, value);
		tile = getTile(xPosition, yPosition-1);
		if(tile.pixelInRange(x, y)) tile.setSquare(x, y, value);
		tile = getTile(xPosition+1, yPosition-1);
		if(tile.pixelInRange(x, y)) tile.setSquare(x, y, value);
		tile = getTile(xPosition+1, yPosition);
		if(tile.pixelInRange(x, y)) tile.setSquare(x, y, value);
		tile = getTile(xPosition+1, yPosition+1);
		if(tile.pixelInRange(x, y)) tile.setSquare(x, y, value);
		tile = getTile(xPosition, yPosition+1);
		if(tile.pixelInRange(x, y)) tile.setSquare(x, y, value);
		tile = getTile(xPosition-1, yPosition+1);
		if(tile.pixelInRange(x, y)) tile.setSquare(x, y, value);
		this.lock.unlock();
	}

	public boolean getSquare(int x, int y) {
		this.lock.lock();
		int tileSize = Tile.TILESIZE;
		int xPosition;
		int yPosition;
		if(x>=0) {xPosition = x/tileSize;}
		else {xPosition = -1 - ((-x-1)/tileSize); }
		if(y>=0) {yPosition = y/tileSize;}
		else {yPosition = -1 - ((-y-1)/tileSize); }
		Tile tile = getTile(xPosition, yPosition);
		this.lock.unlock();
		return tile.getSquare(x, y);
	}
	
	private Tile getTile(int xPosition, int yPosition) {
		int x = 2*Math.abs(xPosition);
		if (xPosition < 0) x--;
		int y = 2*Math.abs(yPosition);
		if (yPosition < 0) y--;
		Integer key = (x+y+1)*(x+y+2)/2+x;
		Tile tile = this.tiles.get(key);
		if(tile!=null) return tile;
		tile = this.newTiles.get(key);
		if(tile!=null) return tile;
		tile = new Tile(xPosition,yPosition);
		newTiles.put(key, tile);
		return tile;
	}
	
	private void addNewTiles() {
		Iterator<Integer> it = newTiles.keySet().iterator();
		while(it.hasNext()) {
			Integer x = (Integer) it.next();
			this.tiles.put(x, this.newTiles.get(x));
		}
		this.newTiles = new HashMap<Integer,Tile>();
	}
	
	/**
	 * The Integer will encode the position of the tile
	 */
	private HashMap<Integer,Tile> tiles = new HashMap<Integer,Tile>();
	
	private HashMap<Integer,Tile> newTiles = new HashMap<Integer,Tile>();
}