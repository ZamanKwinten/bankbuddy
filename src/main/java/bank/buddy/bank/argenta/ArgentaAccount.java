package bank.buddy.bank.argenta;

import bank.buddy.gson.ArgentaGSON.AccountsResponse;
import javafx.scene.image.Image;

class ArgentaAccount {

	private final String cardNumber;
	private final String iban;
	private final String humanReadableName;
	private final boolean isSavingsAccount;
	private final double balance;

	ArgentaAccount(String card, AccountsResponse response) {
		this(card, response.accountNumber, response.cashAccountClientWording, response.isSavingAccount,
				response.currentBalance);
	}

	ArgentaAccount(String cardNumber, String iban, String humanReadableName, boolean isSavings, double balance) {
		this.cardNumber = cardNumber;
		this.iban = iban;
		this.humanReadableName = humanReadableName;
		this.isSavingsAccount = isSavings;
		this.balance = balance;
	}

	public String cardNumber() {
		return cardNumber;
	}

	public String iban() {
		return iban;
	}

	public String accountName() {
		return humanReadableName;
	}

	public Image getBankIcon() {
		return new Image(this.getClass().getResourceAsStream("icon.png"));
	}

	boolean isSaving() {
		return isSavingsAccount;
	}

	double balance() {
		return balance;
	}

	@Override
	public String toString() {
		return String.format("cardNumber: %s, humanReadableName: %s, isSaving: %s, balance: %s", iban,
				humanReadableName, isSavingsAccount, balance);
	}
}
