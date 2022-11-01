package bank.buddy.ui;

import java.util.function.Function;
import java.util.function.Predicate;

import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;

public class MaskedTextInputField extends TextField {
	private final char seperatorCharacter;

	private final String patternString;

	public MaskedTextInputField(String patternString) {
		this(patternString, 0, ' ');
	}

	public MaskedTextInputField(String patternString, int startPosition) {
		this(patternString, startPosition, ' ');
	}

	public MaskedTextInputField(String patternString, int startPosition, char seperator) {
		super(patternString);
		this.patternString = patternString;
		this.seperatorCharacter = seperator;

		focusedProperty().addListener((obs, ov, nv) -> {
			if (nv) {
				positionCaret(startPosition);
			}
		});

		addEventFilter(KeyEvent.ANY, event -> {
			if (event.getCode() != KeyCode.TAB) {
				if (event.getEventType() == KeyEvent.KEY_PRESSED) {
					var text = event.getText();
					if (text.isEmpty() || text.charAt(0) != seperatorCharacter) {
						handleKeyType(event.getCode());
					}
				}
				event.consume();
			}
		});
	}

	private void handleKeyType(KeyCode key) {
		switch (key) {
		case LEFT, KP_LEFT -> positionCaret(getNextLeftPosition(getCaretPosition()));
		case RIGHT, KP_RIGHT -> positionCaret(getNextRightPosition(getCaretPosition()));
		case DIGIT0, NUMPAD0 -> setCharacterAtCurrentPosition('0');
		case DIGIT1, NUMPAD1 -> setCharacterAtCurrentPosition('1');
		case DIGIT2, NUMPAD2 -> setCharacterAtCurrentPosition('2');
		case DIGIT3, NUMPAD3 -> setCharacterAtCurrentPosition('3');
		case DIGIT4, NUMPAD4 -> setCharacterAtCurrentPosition('4');
		case DIGIT5, NUMPAD5 -> setCharacterAtCurrentPosition('5');
		case DIGIT6, NUMPAD6 -> setCharacterAtCurrentPosition('6');
		case DIGIT7, NUMPAD7 -> setCharacterAtCurrentPosition('7');
		case DIGIT8, NUMPAD8 -> setCharacterAtCurrentPosition('8');
		case DIGIT9, NUMPAD9 -> setCharacterAtCurrentPosition('9');
		case BACK_SPACE -> {
			positionCaret(getNextLeftPosition(getCaretPosition()));
			setCharacterAtCurrentPosition('.');
			positionCaret(getNextLeftPosition(getCaretPosition()));
		}
		default -> {
		}
		}
	}

	private void setCharacterAtCurrentPosition(char c) {
		var index = getCaretPosition();
		if (index < patternString.length()) {
			var text = getText().toCharArray();
			text[index] = c;
			setText(String.valueOf(text));
			positionCaret(getNextRightPosition(index));
		}
	}

	private int getNextLeftPosition(int start) {
		return nextPosition(start, i -> i - 1, i -> i < 0, 0);
	}

	private int getNextRightPosition(int start) {
		return nextPosition(start, i -> i + 1, i -> i >= patternString.length(), patternString.length());
	}

	private int nextPosition(int start, Function<Integer, Integer> moveFunc, Predicate<Integer> outOfBoundCheck,
			int outOfRangeVal) {
		int next = moveFunc.apply(start);
		if (outOfBoundCheck.test(next)) {
			return outOfRangeVal;
		}

		while (patternString.charAt(next) == seperatorCharacter) {
			next = moveFunc.apply(next);
			if (outOfBoundCheck.test(next)) {
				return outOfRangeVal;
			}
		}
		return next;
	}

	public String getValue() {
		return getText().replaceAll(String.valueOf(seperatorCharacter), "");
	}
}
