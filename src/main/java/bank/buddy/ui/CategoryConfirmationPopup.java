package bank.buddy.ui;

import bank.buddy.ui.CategoryConfirmationPopup.CategoryConfirmationParam;
import bank.buddy.ui.CategoryConfirmationPopup.CategoryConfirmationReturn;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;

public class CategoryConfirmationPopup extends PopupWithReturn<CategoryConfirmationReturn, CategoryConfirmationParam> {

	public enum CategoryConfirmationReturn {
		ONLY_THIS_TRANSACTION, ALL_TRANSACTIONS;
	}

	public record CategoryConfirmationParam(boolean isExpense) {

	}

	private CategoryConfirmationReturn result;

	public CategoryConfirmationPopup(CategoryConfirmationParam categoryConfirmationParam) {
		super(categoryConfirmationParam);
	}

	@Override
	protected String getPopupTitle() {
		return "Bevestigen";
	}

	@Override
	protected Footer getFooter() {
		Button b1 = new Button("Ja maar enkel voor deze transactie");
		b1.setOnAction(e -> {
			result = CategoryConfirmationReturn.ONLY_THIS_TRANSACTION;
			this.close();
		});

		Button b2 = new Button(String.format("Ja voor alle %s transacties met dit rekening nummer",
				param.isExpense() ? "uitgaande" : "inkomende"));
		b2.setOnAction(e -> {
			result = CategoryConfirmationReturn.ALL_TRANSACTIONS;
			this.close();
		});

		return new Footer("", b1, b2);
	}

	@Override
	protected Node getContent() {
		return new Label("Bent u zeker dat u de categorie wil wijzigen?");
	}

	@Override
	protected CategoryConfirmationReturn getReturnValue() {
		return result;
	}

}
