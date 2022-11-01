package bank.buddy.bank.transaction;

import java.util.ArrayList;
import java.util.List;
import java.util.TreeMap;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import bank.buddy.util.Memoize;

public class MonthlyMap {

	private final TreeMap<Month, List<Transaction>> map = new TreeMap<>();
	private final Supplier<List<MonthSummary>> summary;

	MonthlyMap(Stream<Transaction> orderedTransactions) {
		orderedTransactions.forEach(transaction -> {
			var key = new Month(transaction.month(), transaction.year());
			map.computeIfAbsent(key, k -> new ArrayList<>()).add(transaction);
		});

		summary = new Memoize<>(() -> map.entrySet().stream().map(entry -> {
			var month = entry.getKey();

			var expenses = 0.0;
			var income = 0.0;

			for (var value : entry.getValue()) {
				if (value.amount() > 0) {
					income += value.amount();
				} else {
					expenses += Math.abs(value.amount());
				}
			}

			return new MonthSummary(month, expenses, income);
		}).collect(Collectors.toList()));
	}

	List<MonthSummary> getSummary() {
		return summary.get();
	}

	Stream<Transaction> getTransactions(Month month) {
		return map.get(month).stream();
	}

}
