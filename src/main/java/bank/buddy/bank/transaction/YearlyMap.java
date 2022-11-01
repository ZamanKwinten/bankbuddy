package bank.buddy.bank.transaction;

import java.util.ArrayList;
import java.util.List;
import java.util.TreeMap;
import java.util.function.Supplier;
import java.util.stream.Stream;

import bank.buddy.util.Memoize;

public class YearlyMap {

	private final TreeMap<Year, List<Transaction>> map = new TreeMap<>();
	private final Supplier<List<YearSummary>> summary;

	YearlyMap(Stream<Transaction> orderedTransactions) {
		orderedTransactions.forEach(transaction -> {
			var key = new Year(transaction.year());
			map.computeIfAbsent(key, k -> new ArrayList<>()).add(transaction);
		});

		summary = new Memoize<List<YearSummary>>(() -> map.entrySet().stream().map(entry -> {
			var year = entry.getKey();

			var expenses = 0.0;
			var income = 0.0;

			for (var value : entry.getValue()) {
				if (value.amount() > 0) {
					income += value.amount();
				} else {
					expenses += Math.abs(value.amount());
				}
			}

			return new YearSummary(year, expenses, income);
		}).toList());
	}

	List<YearSummary> getSummary() {
		return summary.get();
	}

	Stream<Transaction> getTransactions(Year year) {
		return map.get(year).stream();
	}
}
