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

        // Initialize currentNumber to "0" at start
        currentNumber.append("0");

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
            String digit = button.getText().toString();

            if (isNewNumber) {
                currentNumber.setLength(0); // Clear previous number if starting new
                isNewNumber = false;
            }

            // Prevent leading zero unless it's a decimal point
            if (currentNumber.toString().equals("0") && !digit.equals(".")) {
                currentNumber.setLength(0); // Clear the single '0'
            }
            
            // Limit number of digits if desired (e.g., 15 for typical calculator) - optional
            // if (currentNumber.length() >= 15 && !currentNumber.toString().contains(".")) {
            //     Toast.makeText(MainActivity.this, "Digit limit reached", Toast.LENGTH_SHORT).show();
            //     return;
            // }

            currentNumber.append(digit);
            updateDisplay();
        }
    };

    private View.OnClickListener operatorClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Button button = (Button) v;
            if (currentNumber.length() == 0 || (currentNumber.toString().equals("0") && firstOperand == 0 && operator.isEmpty())) {
                // If no number entered yet (or just "0" initially), and no previous operand/operator, do nothing
                return;
            }

            if (!operator.isEmpty() && !isNewNumber) {
                // If an operator was already pressed and a new number was entered, calculate previous
                calculateResult();
            } else if (!operator.isEmpty() && isNewNumber) {
                // If operator already pressed, and no new number typed (e.g., 5 + * ),
                // then just change the operator.
                operator = button.getText().toString();
                updateDisplay();
                return;
            }

            firstOperand = Double.parseDouble(currentNumber.toString());
            operator = button.getText().toString();
            isNewNumber = true; // Next number entered will be a new one
            // No updateDisplay here, it will show previous result or first operand.
            // When isNewNumber is true, `updateDisplay` shows `currentNumber` which might be the first operand.
            // This is generally acceptable for showing the previous result before new number input.
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
            if (!operator.isEmpty() && currentNumber.length() > 0 && !isNewNumber) {
                calculateResult();
                operator = ""; // Clear operator after equals
                isNewNumber = true; // Next number will be a new one
            } else if (!operator.isEmpty() && currentNumber.length() > 0 && isNewNumber) {
                // If equals pressed after an operator but no second number,
                // treat second operand as same as first operand (e.g., 5 + = -> 10)
                calculateResultWithSameOperand();
                operator = "";
                isNewNumber = true;
            }
            // If operator is empty or currentNumber is empty, do nothing.
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
            if (isNewNumber) {
                // If starting a new number (e.g., after operator or equals), backspace should not modify the previous result.
                // It should act as if current number is "0" and clear it (or just do nothing if initial "0").
                if (currentNumber.toString().equals("0")) {
                    return; // Do nothing if it's already "0" and new.
                } else {
                    currentNumber.setLength(0);
                    currentNumber.append("0");
                    isNewNumber = true; // Keep it as a new number start
                }
            } else {
                // If appending to current number
                if (currentNumber.length() > 1) {
                    currentNumber.deleteCharAt(currentNumber.length() - 1);
                } else {
                    currentNumber.setLength(0);
                    currentNumber.append("0");
                    isNewNumber = true; // If it becomes "0", treat it as a new number start
                }
            }
            updateDisplay();
        }
    };


    private void calculateResult() {
        double secondOperand;
        // If currentNumber is still empty (e.g., operator pressed, then immediately equals),
        // use firstOperand as secondOperand. This case is handled by calculateResultWithSameOperand
        // in equalsClickListener logic, but good to be defensive.
        if (currentNumber.length() == 0) {
            secondOperand = firstOperand; // Should not happen with refined equalsClickListener, but safe guard.
        } else {
            secondOperand = Double.parseDouble(currentNumber.toString());
        }

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
                // If operator is invalid or empty, do nothing or display an error
                // This state should ideally not be reached if operator is correctly set.
                currentNumber.setLength(0);
                currentNumber.append(firstOperand); // Display first operand if no valid operation
                updateDisplay();
                return;
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

    private void calculateResultWithSameOperand() {
        // This method is called when an operator is pending and equals is pressed without typing a second number.
        // E.g., 5 + = should calculate 5 + 5.
        // The second operand should be `firstOperand` from previous step, not the current number in display.
        // Store the current number if it's the result of a previous operation.
        double operandForCalculation = Double.parseDouble(currentNumber.toString());

        double result = 0;
        boolean error = false;

        switch (operator) {
            case "+":
                result = firstOperand + operandForCalculation;
                break;
            case "-":
                result = firstOperand - operandForCalculation;
                break;
            case "*":
                result = firstOperand * operandForCalculation;
                break;
            case "/":
                if (operandForCalculation == 0) {
                    Toast.makeText(this, "Cannot divide by zero", Toast.LENGTH_SHORT).show();
                    error = true;
                } else {
                    result = firstOperand / operandForCalculation;
                }
                break;
            default:
                error = true;
                break;
        }

        if (!error) {
            currentNumber.setLength(0);
            if (result == (long) result) {
                currentNumber.append((long) result);
            } else {
                currentNumber.append(result);
            }
            firstOperand = result; // The result becomes the first operand for potential further calculations (e.g. 5+=+=)
        } else {
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