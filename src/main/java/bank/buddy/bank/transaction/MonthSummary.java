package bank.buddy.bank.transaction;

import bank.buddy.ui.SummaryListView.Summary;

public record MonthSummary(int year, int month, double expenses, double income, double balance) implements Summary {
	MonthSummary(Month month, double expenses, double income) {
		this(month.year(), month.month(), expenses, income, income - expenses);
	}

	public Month toMonth() {
		return new Month(month, year);
	}

	public String getDateString() {
		return String.format("%02d/%s", month(), year());
	}
}
