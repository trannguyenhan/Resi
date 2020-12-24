package custom.fattree;

public class Address {
	public final int _1;
	public final int _2;
	public final int _3;
	public final int _4;

	public Address(int _1, int _2, int _3, int _4) { // constructor method for class Address
		this._1 = _1;
		this._2 = _2;
		this._3 = _3;
		this._4 = _4;
	}

	/**
	 * This method is used to check whether an address equals another address or not
	 * 
	 * @return true if it equals
	 * @return false if it does not equal
	 */
	@Override
	public boolean equals(Object obj) {
		if (obj instanceof Address) {
			Address that = (Address) obj;
			return this._1 == that._1 && this._2 == that._2 && this._3 == that._3 && this._4 == that._4;
		} else {
			return false;
		}
	}
	
	@Override
    public int hashCode() {
    	return super.hashCode();
    }
}
