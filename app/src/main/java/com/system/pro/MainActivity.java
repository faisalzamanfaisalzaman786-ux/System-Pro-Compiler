package com.system.pro;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    private TextView displayTextView;
    private StringBuilder currentNumber = new StringBuilder();
    private double firstOperand = 0;
    private String operator = "";
    private boolean isNewNumber = true; // Flag to indicate if we are starting a new number or appending to current

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        displayTextView = findViewById(R.id.displayTextView);

        // Set OnClickListener for all number buttons
        int[] numberButtonIds = {
                R.id.button0, R.id.button1, R.id.button2, R.id.button3, R.id.button4,
                R.id.button5, R.id.button6, R.id.button7, R.id.button8, R.id.button9
        };
        for (int id : numberButtonIds) {
            findViewById(id).setOnClickListener(numberClickListener);
        }

        // Set OnClickListener for operator buttons
        findViewById(R.id.buttonAdd).setOnClickListener(operatorClickListener);
        findViewById(R.id.buttonSubtract).setOnClickListener(operatorClickListener);
        findViewById(R.id.buttonMultiply).setOnClickListener(operatorClickListener);
        findViewById(R.id.buttonDivide).setOnClickListener(operatorClickListener);

        // Set OnClickListener for special buttons
        findViewById(R.id.buttonDecimal).setOnClickListener(decimalClickListener);
        findViewById(R.id.buttonEquals).setOnClickListener(equalsClickListener);
        findViewById(R.id.buttonClear).setOnClickListener(clearClickListener);
        findViewById(R.id.buttonBackspace).setOnClickListener(backspaceClickListener);

        updateDisplay();
    }

    private View.OnClickListener numberClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Button button = (Button) v;
            if (isNewNumber) {
                currentNumber.setLength(0); // Clear previous number if starting new
                isNewNumber = false;
            }
            // Prevent leading zero unless it's the only digit or followed by a decimal
            if (currentNumber.toString().equals("0") && !button.getText().toString().equals(".")) {
                currentNumber.setLength(0); // Clear the single '0'
            }
            currentNumber.append(button.getText().toString());
            updateDisplay();
        }
    };

    private View.OnClickListener operatorClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Button button = (Button) v;
            if (currentNumber.length() == 0 && operator.isEmpty()) {
                // If no number entered yet, and no previous operator, do nothing
                return;
            }

            if (!operator.isEmpty() && !isNewNumber) {
                // If an operator was already pressed and a number was entered, calculate previous
                calculateResult();
            }

            firstOperand = Double.parseDouble(currentNumber.toString());
            operator = button.getText().toString();
            isNewNumber = true; // Next number entered will be a new one
            updateDisplay(); // Show the current result or first operand
        }
    };

    private View.OnClickListener decimalClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if (isNewNumber) {
                currentNumber.setLength(0);
                currentNumber.append("0"); // Start with "0." if new number
                isNewNumber = false;
            }
            if (!currentNumber.toString().contains(".")) {
                currentNumber.append(".");
            }
            updateDisplay();
        }
    };

    private View.OnClickListener equalsClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if (!operator.isEmpty() && currentNumber.length() > 0) {
                calculateResult();
                operator = ""; // Clear operator after equals
                isNewNumber = true; // Next number will be a new one
            }
        }
    };

    private View.OnClickListener clearClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            currentNumber.setLength(0);
            currentNumber.append("0");
            firstOperand = 0;
            operator = "";
            isNewNumber = true;
            updateDisplay();
        }
    };

    private View.OnClickListener backspaceClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if (currentNumber.length() > 1 && !isNewNumber) {
                currentNumber.deleteCharAt(currentNumber.length() - 1);
            } else {
                currentNumber.setLength(0);
                currentNumber.append("0");
                isNewNumber = true; // If it becomes "0", treat it as a new number start
            }
            updateDisplay();
        }
    };


    private void calculateResult() {
        if (currentNumber.length() == 0) {
            // If no second operand, just use the first operand as result
            currentNumber.append(firstOperand);
            return;
        }

        double secondOperand = Double.parseDouble(currentNumber.toString());
        double result = 0;
        boolean error = false;

        switch (operator) {
            case "+":
                result = firstOperand + secondOperand;
                break;
            case "-":
                result = firstOperand - secondOperand;
                break;
            case "*":
                result = firstOperand * secondOperand;
                break;
            case "/":
                if (secondOperand == 0) {
                    Toast.makeText(this, "Cannot divide by zero", Toast.LENGTH_SHORT).show();
                    error = true;
                } else {
                    result = firstOperand / secondOperand;
                }
                break;
            default:
                error = true; // Should not happen if operator is set correctly
                break;
        }

        if (!error) {
            currentNumber.setLength(0);
            // Format result to avoid unnecessary .0
            if (result == (long) result) {
                currentNumber.append((long) result);
            } else {
                currentNumber.append(result);
            }
            firstOperand = result; // Result becomes the first operand for chained operations
        } else {
            // Reset state on error
            currentNumber.setLength(0);
            currentNumber.append("0");
            firstOperand = 0;
            operator = "";
            isNewNumber = true;
        }
        updateDisplay();
    }

    private void updateDisplay() {
        displayTextView.setText(currentNumber.length() == 0 ? "0" : currentNumber.toString());
    }
}