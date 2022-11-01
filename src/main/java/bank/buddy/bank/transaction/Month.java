package bank.buddy.bank.transaction;

public record Month(int month, int year) implements Comparable<Month> {

	@Override
	public int compareTo(Month o) {
		var y = year - o.year;
		if (y != 0) {
			return y;
		}
		var m = month - o.month;
		if (m != 0) {
			return m;
		}

		return 0;
	}
}