package odobo.control.component;

import java.text.NumberFormat;
import java.util.regex.Pattern;

import odobo.calculate.bean.GeneratorConfig;

import javafx.beans.InvalidationListener;
import javafx.beans.Observable;
import javafx.beans.property.IntegerProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.scene.control.*;
import javafx.scene.layout.HBoxBuilder;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBoxBuilder;

/**
 * Panel for data inputting
 * 
 * @author anna
 * 
 */
public class InputPanel extends Pane {

	private Slider slider;
	private TextField textField;
	private IntegerProperty value;
	private NumberFormat numberFormat = NumberFormat.getIntegerInstance();

	/**
	 * Listens for new values for text field
	 */
	private InvalidationListener numberOfRectsFrom = new InvalidationListener() {

		@Override
		public void invalidated(Observable arg0) {
			if (!textField.isFocused()) {
				textField.setText(numberFormat.format(value.get()));
			}
		}
	};

	/**
	 * Listens for new values for slider
	 */
	private InvalidationListener numberOfRectsTo = new InvalidationListener() {

		@Override
		public void invalidated(Observable arg0) {
			if (!textField.isFocused()) {
				return;
			}
			try {
				int val = numberFormat.parse(textField.getText()).intValue();
				slider.setValue(val);
			} catch (Exception ignored) {
			}

		}
	};

	/**
	 * Validation for text field
	 */
	private ChangeListener<Boolean> textFieldLostFocus = new ChangeListener<Boolean>() {

		@Override
		public void changed(ObservableValue<? extends Boolean> arg0, Boolean oldVal, Boolean newVal) {
			try {
				if (newVal) {
					return;
				}
				int val = numberFormat.parse(textField.getText()).intValue();
				val = val < GeneratorConfig.MIN ? GeneratorConfig.MIN
						: (val > GeneratorConfig.MAX ? GeneratorConfig.MAX : val);
				textField.setText(numberFormat.format(value.get()));
			} catch (Exception ignored) {
				textField.setText(numberFormat.format(value.get()));
			}
		}
	};

	/**
	 * Constructs input panel with given label
	 * 
	 * @param title
	 *            label
	 * @param value
	 *            number field value binded with inputs
	 */
	public InputPanel(String title, IntegerProperty value) {

		textField = new TextField() {
			Pattern p = Pattern.compile("[a-zA-Z]");

			@Override
			public void replaceText(int start, int end, String text) {
				// If the replaced text would end up being invalid, then ignore
				// this call!
				if (!p.matcher(text).matches()) {
					super.replaceText(start, end, text);
				}
			}

			@Override
			public void replaceSelection(String text) {
				if (!p.matcher(text).matches()) {
					super.replaceSelection(text);
				}
			}

		};

		getChildren().add(
				HBoxBuilder
						.create()
						.children(
								VBoxBuilder
										.create()
										.children(
												new Label(title),
												textField,
												slider = SliderBuilder.create().min(GeneratorConfig.MIN)
														.max(GeneratorConfig.MAX).build()).build()).build());
		this.value = value;

		value.addListener(numberOfRectsFrom);
		textField.textProperty().addListener(numberOfRectsTo);
		textField.focusedProperty().addListener(textFieldLostFocus);
		numberOfRectsFrom.invalidated(null);
		slider.valueProperty().bindBidirectional(value);
	}
}
