package bank.buddy.bank.transaction;

import java.util.List;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import bank.buddy.util.Memoize;

public class TransactionCollection {

	private final List<Transaction> orderedTransactions;
	private final Supplier<MonthlyMap> monthlyMap;
	private final Supplier<YearlyMap> yearlyMap;

	public TransactionCollection(Stream<Transaction> transactions) {
		orderedTransactions = transactions.sorted((a, b) -> {
			var y = a.year() - b.year();

			if (y != 0) {
				return y;
			}

			var m = a.month() - b.month();
			if (m != 0) {
				return m;
			}

			var d = a.day() - b.day();
			if (d != 0) {
				return d;
			}

			return -1;
		}).collect(Collectors.toList());

		monthlyMap = new Memoize<>(() -> new MonthlyMap(orderedTransactions.stream()));
		yearlyMap = new Memoize<>(() -> new YearlyMap(orderedTransactions.stream()));
	}

	public List<MonthSummary> getMontlyOverview() {
		return monthlyMap.get().getSummary();
	}

	public List<TransactionWrapper> getMontlyTransactions(Month month) {
		return monthlyMap.get().getTransactions(month).map(TransactionWrapper::new).toList();
	}

	public List<YearSummary> getYearlyOverview() {
		return yearlyMap.get().getSummary();
	}

	public List<TransactionWrapper> getYearlyTransactions(Year year) {
		return yearlyMap.get().getTransactions(year).map(TransactionWrapper::new).toList();
	}

	public Stream<Transaction> getAllTransactions() {
		return orderedTransactions.stream();
	}

}
