package com.example.app;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import java.util.ArrayList;

public class MainActivity extends Activity implements View.OnClickListener {

    private TextView txtInput, txtResult;
    private String processExpression = "";
    private boolean isOpLast = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(getResources().getIdentifier("activity_main", "layout", getPackageName()));

        txtInput = findViewById(getResources().getIdentifier("txtInput", "id", getPackageName()));
        txtResult = findViewById(getResources().getIdentifier("txtResult", "id", getPackageName()));

        int[] btnIds = {
                getResources().getIdentifier("btn0", "id", getPackageName()),
                getResources().getIdentifier("btn1", "id", getPackageName()),
                getResources().getIdentifier("btn2", "id", getPackageName()),
                getResources().getIdentifier("btn3", "id", getPackageName()),
                getResources().getIdentifier("btn4", "id", getPackageName()),
                getResources().getIdentifier("btn5", "id", getPackageName()),
                getResources().getIdentifier("btn6", "id", getPackageName()),
                getResources().getIdentifier("btn7", "id", getPackageName()),
                getResources().getIdentifier("btn8", "id", getPackageName()),
                getResources().getIdentifier("btn9", "id", getPackageName()),
                getResources().getIdentifier("btnC", "id", getPackageName()),
                getResources().getIdentifier("btnDot", "id", getPackageName()),
                getResources().getIdentifier("btnPlus", "id", getPackageName()),
                getResources().getIdentifier("btnSub", "id", getPackageName()),
                getResources().getIdentifier("btnMul", "id", getPackageName()),
                getResources().getIdentifier("btnDiv", "id", getPackageName()),
                getResources().getIdentifier("btnEqual", "id", getPackageName()),
                getResources().getIdentifier("btnPercent", "id", getPackageName()),
                getResources().getIdentifier("btnBrack", "id", getPackageName())
        };

        for (int id : btnIds) {
            View btn = findViewById(id);
            if (btn != null) {
                btn.setOnClickListener(this);
            }
        }
    }

    @Override
    public void onClick(View v) {
        Button btn = (Button) v;
        String btnText = btn.getText().toString();

        if (btnText.equals("C")) {
            processExpression = "";
            txtInput.setText("");
            txtResult.setText("0");
            isOpLast = false;
            return;
        }

        if (btnText.equals("=")) {
            calculateFinalResult();
            return;
        }

        if (btnText.equals("+") || btnText.equals("-") || btnText.equals("x") || btnText.equals("/")) {
            if (processExpression.isEmpty() || isOpLast) return;
            isOpLast = true;
        } else {
            isOpLast = false;
        }

        processExpression += btnText;
        txtInput.setText(processExpression);
    }

    private void calculateFinalResult() {
        if (processExpression.isEmpty() || isOpLast) return;
        try {
            String exp = processExpression.replace("x", "*");
            double res = evaluateSimpleExpression(exp);
            
            if (res == (long) res) {
                txtResult.setText(String.valueOf((long) res));
            } else {
                txtResult.setText(String.valueOf(res));
            }
        } catch (Exception e) {
            txtResult.setText("Error");
        }
    }

    private double evaluateSimpleExpression(String expression) {
        // ٹائٹن ماسٹر لائٹ ویٹ پارسر برائے کیلکولیشن
        return new Object() {
            int pos = -1, ch;

            void nextChar() {
                ch = (++pos < expression.length()) ? expression.charAt(pos) : -1;
            }

            boolean eat(int charToEat) {
                while (ch == ' ') nextChar();
                if (ch == charToEat) {
                    nextChar();
                    return true;
                }
                return false;
            }

            double parse() {
                nextChar();
                double x = parseExpression();
                if (pos < expression.length()) throw new RuntimeException("Unexpected: " + (char)ch);
                return x;
            }

            double parseExpression() {
                double x = parseTerm();
                for (;;) {
                    if      (eat('+')) x += parseTerm(); // Addition
                    else if (eat('-')) x -= parseTerm(); // Subtraction
                    else return x;
                }
            }

            double parseTerm() {
                double x = parseFactor();
                for (;;) {
                    if      (eat('*')) x *= parseFactor(); // Multiplication
                    else if (eat('/')) x /= parseFactor(); // Division
                    else return x;
                }
            }

            double parseFactor() {
                if (eat('+')) return parseFactor(); 
                if (eat('-')) return -parseFactor(); 

                double x;
                int startPos = this.pos;
                if (eat('(')) { 
                    x = parseExpression();
                    eat(')');
                } else if ((ch >= '0' && ch <= '9') || ch == '.') { 
                    while ((ch >= '0' && ch <= '9') || ch == '.') nextChar();
                    x = Double.parseDouble(expression.substring(startPos, this.pos));
                } else {
                    throw new RuntimeException("Unexpected: " + (char)ch);
                }
                return x;
            }
        }.parse();
    }
}