package custom.fattree;

public class Address {
	public final int _1, _2, _3, _4;

	public Address(int _1, int _2, int _3, int _4) {
		this._1 = _1;
		this._2 = _2;
		this._3 = _3;
		this._4 = _4;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj instanceof Address) {
			Address that = (Address) obj;
			return this._1 == that._1 && this._2 == that._2 && this._3 == that._3 && this._4 == that._4;
		} else {
			return false;
		}
	}
}
