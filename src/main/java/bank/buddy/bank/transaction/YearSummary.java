package bank.buddy.bank.transaction;

import bank.buddy.ui.SummaryListView.Summary;

public record YearSummary(int year, double expenses, double income, double balance) implements Summary {

	YearSummary(Year year, double expenses, double income) {
		this(year.year(), expenses, income, income - expenses);
	}

	public Year toYear() {
		return new Year(year);
	}

	public String getDateString() {
		return "" + year;
	}
}
