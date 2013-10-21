/**
 * 
 */
package org.intersect.mgwt.client;

/**
 * @author Gabriel Gasser Noblia
 * 
 */
public class Experiment {
	private int id;
	private String name;
	private String backgroudImage;
	private String foregroundImage;
	private int top;
	private int left;
	private int delay;

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getBackgroudImage() {
		return backgroudImage;
	}

	public void setBackgroudImage(String backgroudImage) {
		this.backgroudImage = backgroudImage;
	}

	public String getForegroundImage() {
		return foregroundImage;
	}

	public void setForegroundImage(String foregroundImage) {
		this.foregroundImage = foregroundImage;
	}

	public int getTop() {
		return top;
	}

	public void setTop(int top) {
		this.top = top;
	}

	public int getLeft() {
		return left;
	}

	public void setLeft(int left) {
		this.left = left;
	}

	public int getDelay() {
		return delay;
	}

	public void setDelay(int delay) {
		this.delay = delay;
	}
}
