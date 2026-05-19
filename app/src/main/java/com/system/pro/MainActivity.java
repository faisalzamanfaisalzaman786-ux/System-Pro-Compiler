package com.system.pro;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    private TextView displayTextView;
    private StringBuilder currentInput = new StringBuilder();
    private double operand1 = 0;
    private String operator = "";
    private boolean newOperation = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        displayTextView = findViewById(R.id.displayTextView);
        displayTextView.setText("0");

        setNumberButtonListeners();
        setOperatorButtonListeners();
        setSpecialButtonListeners();
    }

    private void setNumberButtonListeners() {
        int[] numberButtonIds = {
                R.id.button0, R.id.button1, R.id.button2, R.id.button3, R.id.button4,
                R.id.button5, R.id.button6, R.id.button7, R.id.button8, R.id.button9
        };

        View.OnClickListener numberListener = v -> {
            Button b = (Button) v;
            if (newOperation) {
                currentInput.setLength(0);
                newOperation = false;
            }
            if (currentInput.length() == 1 && currentInput.charAt(0) == '0' && !b.getText().toString().equals(".")) {
                currentInput.setLength(0);
            }
            currentInput.append(b.getText().toString());
            displayTextView.setText(currentInput.toString());
        };

        for (int id : numberButtonIds) {
            findViewById(id).setOnClickListener(numberListener);
        }

        findViewById(R.id.buttonDot).setOnClickListener(v -> {
            if (newOperation) {
                currentInput.setLength(0);
                currentInput.append("0");
                newOperation = false;
            }
            if (!currentInput.toString().contains(".")) {
                currentInput.append(".");
                displayTextView.setText(currentInput.toString());
            }
        });
    }

    private void setOperatorButtonListeners() {
        int[] operatorButtonIds = {
                R.id.buttonAdd, R.id.buttonSubtract, R.id.buttonMultiply, R.id.buttonDivide
        };

        View.OnClickListener operatorListener = v -> {
            Button b = (Button) v;
            String buttonText = b.getText().toString();

            if (currentInput.length() > 0 && !newOperation) {
                calculateResult();
            } else if (newOperation && displayTextView.getText().length() > 0) {
                operand1 = Double.parseDouble(displayTextView.getText().toString());
            }

            switch (buttonText) {
                case "+":
                    operator = "+";
                    break;
                case "-":
                    operator = "-";
                    break;
                case "\u00D7": // Multiplication sign
                case "*":
                    operator = "*";
                    break;
                case "\u00F7": // Division sign
                case "/":
                    operator = "/";
                    break;
                default:
                    operator = "";
                    break;
            }
            
            newOperation = true;
            currentInput.setLength(0);
        };

        for (int id : operatorButtonIds) {
            findViewById(id).setOnClickListener(operatorListener);
        }
    }

    private void setSpecialButtonListeners() {
        findViewById(R.id.buttonEquals).setOnClickListener(v -> {
            calculateResult();
            operator = "";
            newOperation = true;
        });

        findViewById(R.id.buttonClear).setOnClickListener(v -> {
            currentInput.setLength(0);
            currentInput.append("0");
            operand1 = 0;
            operator = "";
            newOperation = true;
            displayTextView.setText("0");
        });
    }

    private void calculateResult() {
        if (operator.isEmpty() || currentInput.length() == 0) {
            return;
        }

        double operand2 = Double.parseDouble(currentInput.toString());
        double result = 0;

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
                if (operand2 != 0) {
                    result = operand1 / operand2;
                } else {
                    displayTextView.setText("Error");
                    operand1 = 0;
                    currentInput.setLength(0);
                    currentInput.append("0");
                    operator = "";
                    newOperation = true;
                    return;
                }
                break;
            default:
                return;
        }

        String formattedResult = formatResult(result);
        displayTextView.setText(formattedResult);
        operand1 = result;
        currentInput.setLength(0);
        currentInput.append(formattedResult);
    }

    private String formatResult(double result) {
        if (result == (long) result) {
            return String.format("%d", (long) result);
        } else {
            return String.valueOf(result);
        }
    }
}