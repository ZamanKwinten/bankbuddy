package bank.buddy.gson;

import java.util.Map.Entry;

import javafx.beans.property.SimpleObjectProperty;
import javafx.scene.paint.Color;

public class GenericGSON {

	public static class AccountMeta {
		public int bankID;
		public String[] cardNumber;
		public String name;
		public String iban;
		public long lastUpdated;
	}

	public static class Transaction {
		public String id;
		public int year;
		public int month;
		public int day;
		public String counterPartyIban;
		public String counterPartyName;
		public double amount;
		public String extraInfo;

		public Transaction(bank.buddy.bank.transaction.Transaction src) {
			this.id = src.id();
			this.year = src.year();
			this.month = src.month();
			this.day = src.day();
			this.counterPartyIban = src.counterPartyIban();
			this.counterPartyName = src.counterpartyName();
			this.amount = src.amount();
			this.extraInfo = src.extraInfo();
		}

		public bank.buddy.bank.transaction.Transaction to() {
			return new bank.buddy.bank.transaction.Transaction(id, year, month, day, counterPartyIban, counterPartyName,
					amount, extraInfo);
		}

	}

	public static class Category {
		public String name;
		public String frontRGB;
		public String backRGB;

		public Category(bank.buddy.bank.transaction.Category src) {
			this.name = src.name();
			this.frontRGB = src.text().toString();
			this.backRGB = src.background().toString();
		}

		public bank.buddy.bank.transaction.Category to() {
			return new bank.buddy.bank.transaction.Category(name, Color.web(frontRGB), Color.web(backRGB));
		}
	}

	public static class TransactionCategoryMap {
		public TransactionCategoryEntry[] counterPartyName;
		public TransactionCategoryEntry[] transactionID;

		public TransactionCategoryMap(TransactionCategoryEntry[] counterPartyName,
				TransactionCategoryEntry[] transactionID) {

			this.counterPartyName = counterPartyName;
			this.transactionID = transactionID;
		}
	}

	public static class TransactionCategoryEntry {
		public String key;
		public String value;

		public TransactionCategoryEntry(
				Entry<String, SimpleObjectProperty<bank.buddy.bank.transaction.Category>> entry) {
			key = entry.getKey();
			value = entry.getValue().get().name();
		}
	}
}
