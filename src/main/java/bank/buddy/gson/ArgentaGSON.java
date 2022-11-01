package bank.buddy.gson;

public class ArgentaGSON {

	public static class AuthorizeResponse {
		public String reference;
	}

	public static class ChallengeRequestBody {

		public ChallengeContext context;

		public ChallengeRequestBody(String reference) {
			this.context = new ChallengeContext(reference);
		}
	}

	public static class ChallengeContext {
		public String reference;
		public String signingMethod;

		private ChallengeContext(String reference) {
			this.reference = reference;
			this.signingMethod = "DIGIPASS";
		}
	}

	public static class ChallengeResponse {
		public String challenge;
	}

	public static class SignRequestBody {
		public String cardNumber;
		public String response;
		public String reference;
		public String signingMethod;

		public SignRequestBody(String cardNumber, String response, String reference) {
			this.cardNumber = cardNumber;
			this.response = response;
			this.reference = reference;
			this.signingMethod = "DIGIPASS";
		}
	}

	public static class AccountsResponse {
		public String accountNumber;
		public String cashAccountClientWording;
		public boolean isSavingAccount;
		public double currentBalance;
	}

	public static class MovementsResponse {
		public MovementItem[] result;
	}

	public static class MovementItem {
		public String accountingDate;
		public String communicationPart1;
		public String communicationPart2;
		public String counterpartyName;
		public String operationCounterparty;
		public String identifier;
		public double movementAmount;
	}
}
