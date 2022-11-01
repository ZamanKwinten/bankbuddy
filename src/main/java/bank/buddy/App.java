package bank.buddy;

import bank.buddy.bank.BankSelectionStage;
import bank.buddy.bank.MyAccountsView;
import bank.buddy.file.FileMgr;
import javafx.application.Application;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.geometry.VPos;
import javafx.scene.Cursor;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.text.Font;
import javafx.stage.Stage;

public class App extends Application {

	public static void main(String[] args) {
		App.launch(args);
	}

	@Override
	public void start(Stage primaryStage) throws Exception {
		var root = new BorderPane();
		// make sure to init the file manager
		FileMgr.get();

		var center = new GridPane();
		center.setHgap(15);
		center.setVgap(15);

		var myAccounts = new Label("Mijn Rekeningen");
		myAccounts.setFont(new Font(25));

		center.add(myAccounts, 0, 0);

		var addAccount = new Label("Voeg een nieuwe rekening toe ...");
		GridPane.setValignment(addAccount, VPos.BOTTOM);
		GridPane.setHalignment(addAccount, HPos.RIGHT);
		addAccount.setCursor(Cursor.HAND);
		addAccount.setOnMouseClicked(event -> {
			BankSelectionStage bankStage = new BankSelectionStage();

			bankStage.showAndWait();
		});
		center.add(addAccount, 1, 0);

		var accounts = new MyAccountsView();
		center.add(accounts, 0, 1, 2, 1);

		root.setCenter(center);

		root.setPadding(new Insets(5));

		primaryStage.setScene(new Scene(root));
		primaryStage.setResizable(false);

		primaryStage.setTitle("Bank Buddy");
		primaryStage.show();
	}
}
