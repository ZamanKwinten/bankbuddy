package bank.buddy.bank.argenta;

import java.net.CookieManager;
import java.net.http.HttpClient;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import bank.buddy.bank.argenta.ArgentaAPI.SignData;
import bank.buddy.bank.transaction.Transaction;
import bank.buddy.bank.transaction.TransactionCollection;

class ArgentaClient {

	private final HttpClient client;

	private final String csrfToken;
	private final String reference;

	private final String challenge;

	private String card;

	public ArgentaClient() {
		var cookieManager = new CookieManager();
		client = HttpClient.newBuilder().cookieHandler(cookieManager).build();

		var authorize = ArgentaAPI.authorize(client);
		var challenge = ArgentaAPI.challenge(client, authorize);

		this.csrfToken = authorize.csrfToken();
		this.reference = authorize.reference();
		this.challenge = challenge.challenge();
	}

	public String getChallenge() {
		return challenge;
	}

	public void login(String card, String digipasResponse) {
		this.card = card;
		ArgentaAPI.sign(client, new SignData(card, reference, digipasResponse, csrfToken));
	}

	public List<ArgentaAccount> getAccounts() {
		if (card == null) {
			throw new ArgentaRuntimeException("Tried to get accounts before logging in");
		}

		var accounts = ArgentaAPI.getAccounts(client, csrfToken);

		return Arrays.stream(accounts).map(acc -> {
			return new ArgentaAccount(card, acc);
		}).collect(Collectors.toList());
	}

	public TransactionCollection getTransactions(String iban) {
		var transactions = ArgentaAPI.getAllTransactions(client, iban, csrfToken);

		return new TransactionCollection(Arrays.stream(transactions).map(mi -> {
			var date = mi.accountingDate;

			int year = Integer.parseInt(date.substring(0, 4));
			int month = Integer.parseInt(date.substring(4, 6));
			int day = Integer.parseInt(date.substring(6));

			return new Transaction(mi.identifier, year, month, day, mi.operationCounterparty,
					mi.counterpartyName.replaceAll("\\s\\s+", " "), mi.movementAmount, mi.communicationPart1);
		}));
	}
}
