package bank.buddy.bank;

import javafx.collections.FXCollections;
import javafx.collections.MapChangeListener;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.util.Callback;

public class MyAccountsView extends ListView<ABankAccount> {

	public MyAccountsView() {
		GlobalState.myAccounts.addListener(new MapChangeListener<String, ABankAccount>() {
			@Override
			public void onChanged(Change<? extends String, ? extends ABankAccount> change) {
				setItems();
			}
		});

		setItems();
		setCellFactory(new MyAccountsViewCellFactory());
	}

	private void setItems() {
		setItems(FXCollections.observableArrayList(GlobalState.myAccounts.values()));
	}

	private static class MyAccountsViewCellFactory implements Callback<ListView<ABankAccount>, ListCell<ABankAccount>> {

		@Override
		public ListCell<ABankAccount> call(ListView<ABankAccount> param) {
			return new ListCell<>() {

				@Override
				protected void updateItem(ABankAccount item, boolean empty) {
					super.updateItem(item, empty);

					if (empty || item == null) {
						setText(null);
						setGraphic(null);
					} else {
						setText(null);
						setGraphic(new BankAccountCard(item));
						setOnMouseClicked(event -> {
							var overview = new BankAccountDetailsStage(item);
							overview.show();
						});
					}
				}

			};
		}

	}
}
