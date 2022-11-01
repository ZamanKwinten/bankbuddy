package bank.buddy.bank.argenta;

class URL {
	private static final String AUTHORIZE = "https://homebank.argenta.be/direct/authentication/authorize";
	private static final String CHALLENGE = "https://homebank.argenta.be/direct/authentication/universal-signing/select-method";
	private static final String ACCOUNTS = "https://homebank.argenta.be/sbb/services/rest/v1/accounts";
	private static final String SIGN = "https://homebank.argenta.be/direct/authentication/sign";
	// _=1666592354082 could be missing
	private static final String MOVEMENTS = "https://homebank.argenta.be/sbb/services/rest/v1/accountingmovements?accountNumber=%s&maxResults=%d&start=%d";

	static String authorize() {
		return AUTHORIZE;
	}

	static String challenge() {
		return CHALLENGE;
	}

	static String accounts() {
		return ACCOUNTS;
	}

	static String sign() {
		return SIGN;
	}

	static String movements(String iban) {
		return String.format(MOVEMENTS, iban, 0, 0);
	}

	static String movements(String iban, int start, int amount) {
		return String.format(MOVEMENTS, iban, amount, start);
	}
}
