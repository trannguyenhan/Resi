package infrastructure.entity;

public abstract class Device {
	protected int id;

	public Device(int id) {
		this.id = id;
	}

	public void clear() {
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

}
