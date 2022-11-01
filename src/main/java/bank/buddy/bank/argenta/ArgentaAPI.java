package bank.buddy.bank.argenta;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpRequest.BodyPublishers;
import java.net.http.HttpRequest.Builder;
import java.net.http.HttpResponse.BodyHandlers;

import bank.buddy.gson.ArgentaGSON;
import bank.buddy.gson.ArgentaGSON.AccountsResponse;
import bank.buddy.gson.ArgentaGSON.AuthorizeResponse;
import bank.buddy.gson.ArgentaGSON.ChallengeRequestBody;
import bank.buddy.gson.ArgentaGSON.SignRequestBody;
import bank.buddy.gson.GSONProvider;

class ArgentaAPI {
	record AuthorizeResult(String reference, String csrfToken) {

	}

	static AuthorizeResult authorize(HttpClient client) {
		var authorize = prepare(URL.authorize()).POST(BodyPublishers.noBody()).build();
		try {
			var response = client.send(authorize, BodyHandlers.ofString());
			var resp = GSONProvider.gson.fromJson(response.body(), AuthorizeResponse.class);

			var cookies = client.cookieHandler().get().get(authorize.uri(), authorize.headers().map());

			var csrf = cookies.values().stream().flatMap(v -> v.stream())
					.filter(s -> s.startsWith("csrf-token-homebank")).findFirst()
					.orElseThrow(() -> new ArgentaRuntimeException("Could not find csrf cookie in authorize response"))
					.split("=")[1];
			cookies.values().stream().flatMap(v -> v.stream()).filter(s -> s.startsWith("SESSION")).findFirst()
					.orElseThrow(
							() -> new ArgentaRuntimeException("Could not find Session cookie in authorize response"));

			return new AuthorizeResult(resp.reference, csrf);
		} catch (IOException | InterruptedException e) {
			throw new ArgentaRuntimeException("Exception during authorize call", e);
		}
	}

	record ChallengeResponse(String challenge) {

	}

	static ChallengeResponse challenge(HttpClient client, AuthorizeResult authResult) {
		String json = GSONProvider.gson.toJson(new ChallengeRequestBody(authResult.reference));
		var challenge = prepare(URL.challenge(), authResult.csrfToken).POST(BodyPublishers.ofString(json)).build();

		try {
			var response = client.send(challenge, BodyHandlers.ofString());
			var resp = GSONProvider.gson.fromJson(response.body(), bank.buddy.gson.ArgentaGSON.ChallengeResponse.class);

			return new ChallengeResponse(resp.challenge);
		} catch (IOException | InterruptedException e) {
			throw new ArgentaRuntimeException("Exception during challenge call", e);
		}
	}

	record SignData(String card, String reference, String digipasResponse, String csrfToken) {

		public SignRequestBody toRequestBody() {
			return new SignRequestBody(card, digipasResponse, reference);
		}
	}

	static void sign(HttpClient client, SignData loginData) {
		var json = GSONProvider.gson.toJson(loginData.toRequestBody());
		var sign = prepare(URL.sign(), loginData.csrfToken()).POST(BodyPublishers.ofString(json)).build();

		try {
			var response = client.send(sign, BodyHandlers.discarding());

			if (200 != response.statusCode()) {
				throw new ArgentaRuntimeException("Sign call returned unexpected statusCode: " + response.statusCode());
			}

		} catch (IOException | InterruptedException e) {
			throw new ArgentaRuntimeException("Exception during sign call", e);
		}
	}

	static AccountsResponse[] getAccounts(HttpClient client, String csrf) {
		var accounts = prepare(URL.accounts(), csrf).GET().build();

		try {
			var response = client.send(accounts, BodyHandlers.ofString());
			var resp = GSONProvider.gson.fromJson(response.body(), AccountsResponse[].class);

			return resp;
		} catch (IOException | InterruptedException e) {
			throw new ArgentaRuntimeException("Exception during accounts call", e);
		}
	}

	static ArgentaGSON.MovementItem[] getAllTransactions(HttpClient client, String iban, String csrf) {
		var transactions = prepare(URL.movements(iban), csrf).GET().build();

		try {
			var response = client.send(transactions, BodyHandlers.ofString());
			var resp = GSONProvider.gson.fromJson(response.body(), ArgentaGSON.MovementsResponse.class);

			return resp.result;
		} catch (IOException | InterruptedException e) {
			throw new ArgentaRuntimeException("Exception during movements call", e);
		}
	}

	private static Builder prepare(String url, String csrf) {
		Builder reqBuilder = prepare(url);

		reqBuilder.header("X-XSRF-TOKEN-2", csrf);
		reqBuilder.header("content-type", "application/json");

		return reqBuilder;
	}

	private static Builder prepare(String url) {
		try {
			Builder reqBuilder = HttpRequest.newBuilder().uri(new URI(url)).POST(BodyPublishers.noBody());

			return reqBuilder;
		} catch (URISyntaxException e) {
			throw new ArgentaRuntimeException(String.format("Failed to parse URL: %s", url), e);
		}

	}

}
