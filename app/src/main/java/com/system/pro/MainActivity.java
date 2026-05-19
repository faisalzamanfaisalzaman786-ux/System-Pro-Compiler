package com.system.pro;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.util.Log;
import java.text.DecimalFormat;

public class MainActivity extends AppCompatActivity {

    private TextView displayTextView;
    private StringBuilder currentNumber = new StringBuilder();
    private double operand1 = 0;
    private String operator = "";
    private boolean newOperation = true; // True if a new number should clear the display
    private static final String TAG = "CalculatorApp";
    private DecimalFormat decimalFormat = new DecimalFormat("#.##########"); // To avoid scientific notation and limit decimals

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        displayTextView = findViewById(R.id.display_text_view);
        displayTextView.setText("0"); // Initial display
        setupButtons();
    }

    private void setupButtons() {
        int[] numberButtonIds = {
            R.id.button_0, R.id.button_1, R.id.button_2, R.id.button_3,
            R.id.button_4, R.id.button_5, R.id.button_6, R.id.button_7,
            R.id.button_8, R.id.button_9
        };

        for (int id : numberButtonIds) {
            findViewById(id).setOnClickListener(this::onNumberClick);
        }

        findViewById(R.id.button_add).setOnClickListener(this::onOperatorClick);
        findViewById(R.id.button_subtract).setOnClickListener(this::onOperatorClick);
        findViewById(R.id.button_multiply).setOnClickListener(this::onOperatorClick);
        findViewById(R.id.button_divide).setOnClickListener(this::onOperatorClick);

        findViewById(R.id.button_equals).setOnClickListener(this::onEqualsClick);
        findViewById(R.id.button_clear).setOnClickListener(this::onClearClick);
        findViewById(R.id.button_decimal).setOnClickListener(this::onDecimalClick);
    }

    private void onNumberClick(View view) {
        Button button = (Button) view;
        String number = button.getText().toString();

        if (newOperation) {
            currentNumber.setLength(0); // Clear previous number
            displayTextView.setText(""); // Clear display
            newOperation = false;
        }

        if (currentNumber.length() == 1 && currentNumber.charAt(0) == '0' && !number.equals(".")) {
            currentNumber.setLength(0); // Replace single '0' if not adding a decimal
        }

        currentNumber.append(number);
        displayTextView.setText(currentNumber.toString());
    }

    private void onOperatorClick(View view) {
        Button button = (Button) view;
        String newOperator = button.getText().toString();

        if (currentNumber.length() > 0 && !newOperation) {
            if (!operator.isEmpty()) {
                // If there's a pending operation, calculate first
                performCalculation();
            } else {
                operand1 = Double.parseDouble(currentNumber.toString());
            }
            currentNumber.setLength(0); // Clear current number for the next operand
        } else if (newOperation && currentNumber.length() == 0 && displayTextView.getText().length() > 0) {
            // If an operator is pressed after equals, use the result as operand1
            try {
                operand1 = Double.parseDouble(displayTextView.getText().toString());
            } catch (NumberFormatException e) {
                Log.e(TAG, "Error parsing display text to double: " + displayTextView.getText().toString(), e);
                onClearClick(null); // Clear on error
                return;
            }
        }

        operator = newOperator;
        newOperation = true; // Next number input should clear display
        Log.d(TAG, "Operator: " + operator + ", Operand1: " + operand1);
    }

    private void onEqualsClick(View view) {
        if (!operator.isEmpty() && currentNumber.length() > 0) {
            performCalculation();
            operator = ""; // Clear operator after calculation
            newOperation = true; // Next number input should clear display
        }
    }

    private void onClearClick(View view) {
        currentNumber.setLength(0);
        operand1 = 0;
        operator = "";
        newOperation = true;
        displayTextView.setText("0");
    }

    private void onDecimalClick(View view) {
        if (newOperation) {
            currentNumber.setLength(0);
            currentNumber.append("0"); // Start with "0." if display is cleared
            newOperation = false;
        }
        if (!currentNumber.toString().contains(".")) {
            currentNumber.append(".");
            displayTextView.setText(currentNumber.toString());
        }
    }

    private void performCalculation() {
        if (currentNumber.length() == 0) {
            // If equals is pressed without a second operand, do nothing or show error
            return;
        }

        double operand2;
        try {
            operand2 = Double.parseDouble(currentNumber.toString());
        } catch (NumberFormatException e) {
            Log.e(TAG, "Error parsing current number to double: " + currentNumber.toString(), e);
            displayTextView.setText("Error");
            onClearClick(null);
            return;
        }

        double result = 0;
        boolean error = false;

        switch (operator) {
            case "+":
                result = operand1 + operand2;
                break;
            case "-":
                result = operand1 - operand2;
                break;
            case "*":
                result = operand1 * operand2;
                break;
            case "/":
                if (operand2 == 0) {
                    displayTextView.setText("Error");
                    error = true;
                } else {
                    result = operand1 / operand2;
                }
                break;
            default:
                error = true; // Should not happen if operator is set correctly
                break;
        }

        if (!error) {
            String formattedResult = decimalFormat.format(result);
            displayTextView.setText(formattedResult);
            operand1 = result; // Result becomes the first operand for chained operations
            currentNumber.setLength(0); // Clear current number
            currentNumber.append(formattedResult); // Store result for potential further operations
        } else {
            onClearClick(null); // Clear everything on error
        }
        Log.d(TAG, "Result: " + result + ", Operand1 (new): " + operand1);
    }
}