module bank.buddy {

	exports bank.buddy;
	exports bank.buddy.gson to com.google.gson;
	exports bank.buddy.bank.argenta to javafx.graphics;

	requires transitive javafx.graphics;
	requires javafx.controls;
	requires java.net.http;
	requires com.google.gson;
	requires javafx.base;
}