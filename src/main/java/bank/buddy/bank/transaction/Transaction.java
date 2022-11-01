package bank.buddy.bank.transaction;

public record Transaction(String id, int year, int month, int day, String counterPartyIban, String counterpartyName,
		double amount, String extraInfo) {
}