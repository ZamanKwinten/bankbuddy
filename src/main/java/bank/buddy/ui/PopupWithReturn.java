package bank.buddy.ui;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Optional;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.stage.Modality;
import javafx.stage.Stage;

abstract class PopupWithReturn<T, V> extends Stage {

	protected boolean isCanceled = false;
	private final PopupWithReturn<T, V> instance;

	protected V param;

	public PopupWithReturn(V param) {
		this.param = param;
		instance = this;
		var root = new BorderPane();

		root.setPadding(new Insets(5));

		root.setCenter(getContent());

		root.setBottom(getFooter());
		Scene s = new Scene(root);

		setScene(s);
		centerOnScreen();

		setTitle(getPopupTitle());

		initModality(Modality.APPLICATION_MODAL);
	}

	protected class Footer extends HBox {

		public Footer(String okLabel, Button... buttons) {
			super(5.0);
			setAlignment(Pos.CENTER_RIGHT);

			var cancel = new Button("Cancel");
			cancel.setOnAction((event) -> {
				isCanceled = true;
				instance.close();
			});

			var list = new ArrayList<Button>();
			list.add(cancel);
			if (buttons != null) {
				list.addAll(Arrays.asList(buttons));
			}

			if (!okLabel.isBlank()) {
				var ok = new Button(okLabel);
				ok.setOnAction((event) -> {
					isCanceled = false;
					instance.close();
				});

				list.add(ok);
			}

			getChildren().setAll(list);

			setPadding(new Insets(10, 0, 0, 0));
		}
	}

	public Optional<T> open() {
		super.showAndWait();
		return isCanceled ? Optional.empty() : Optional.ofNullable(getReturnValue());
	}

	protected Footer getFooter() {
		return new Footer("Ok");
	}

	protected abstract String getPopupTitle();

	protected abstract Node getContent();

	protected abstract T getReturnValue();

	@Override
	public void showAndWait() {
		throw new UnsupportedOperationException("Use open instead");
	}
}
