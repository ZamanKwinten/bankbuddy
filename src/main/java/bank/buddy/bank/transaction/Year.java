package bank.buddy.bank.transaction;

public record Year(int year) implements Comparable<Year> {

	@Override
	public int compareTo(Year o) {
		return year - o.year();
	}
}
