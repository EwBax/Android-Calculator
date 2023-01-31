package com.ewbax.calculator;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.android.material.snackbar.Snackbar;

import java.text.DecimalFormat;

public class MainActivity extends AppCompatActivity {

    private static final int MAX_NUMBER_LENGTH = 15;
    private static final int SHRINK_TEXT_SIZE_LIMIT = 8;
    private static final float SMALL_TEXT_SIZE = 42f;
    private static final float LARGE_TEXT_SIZE = 56f;

    // GUI components
    private Button      num0Btn;
    private Button      num1Btn;
    private Button      num2Btn;
    private Button      num3Btn;
    private Button      num4Btn;
    private Button      num5Btn;
    private Button      num6Btn;
    private Button      num7Btn;
    private Button      num8Btn;
    private Button      num9Btn;
    private Button      allClearBtn;
    private Button      deleteBtn;
    private Button      divisionBtn;
    private Button      multiplicationBtn;
    private Button      additionBtn;
    private Button      subtractionBtn;
    private Button      negateBtn;
    private Button      equalsBtn;
    private Button      decimalBtn;
    private TextView    expressionTV;
    private TextView    solutionTV;
    private Snackbar    lengthMessage;
    private Snackbar    rangeMessage;

    // Numbers and operator variables for calculations
    private Double      leftNumber;
    private Double      rightNumber;
    private Character   operator;
    private boolean     evaluatedByEquals;


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        evaluatedByEquals = false;

        // Assigning buttons to views by ID
        assignIDAndListener(num0Btn, R.id.num0Btn);
        assignIDAndListener(num1Btn, R.id.num1Btn);
        assignIDAndListener(num2Btn, R.id.num2Btn);
        assignIDAndListener(num3Btn, R.id.num3Btn);
        assignIDAndListener(num4Btn, R.id.num4Btn);
        assignIDAndListener(num5Btn, R.id.num5Btn);
        assignIDAndListener(num6Btn, R.id.num6Btn);
        assignIDAndListener(num7Btn, R.id.num7Btn);
        assignIDAndListener(num8Btn, R.id.num8Btn);
        assignIDAndListener(num9Btn, R.id.num9Btn);
        assignIDAndListener(allClearBtn, R.id.allClearBtn);
        assignIDAndListener(deleteBtn, R.id.deleteBtn);
        assignIDAndListener(divisionBtn, R.id.divisionBtn);
        assignIDAndListener(multiplicationBtn, R.id.multiplicationBtn);
        assignIDAndListener(additionBtn, R.id.additionBtn);
        assignIDAndListener(subtractionBtn, R.id.subtractionBtn);
        assignIDAndListener(negateBtn, R.id.negateBtn);
        assignIDAndListener(equalsBtn, R.id.equalsBtn);
        assignIDAndListener(decimalBtn, R.id.decimalBtn);

        // Assigning textView IDs
        expressionTV = findViewById(R.id.expressionTV);
        solutionTV = findViewById(R.id.solutionTV);

        lengthMessage = Snackbar.make(findViewById(R.id.rootLayout), R.string.lengthMessage, Snackbar.LENGTH_SHORT);
        rangeMessage = Snackbar.make(findViewById(R.id.rootLayout), R.string.rangeMessage, Snackbar.LENGTH_SHORT);

    } // End onCreate method


    private void assignIDAndListener(Button btn, int id) {
        btn = findViewById(id);
        btn.setOnClickListener(onButtonClicked);
    }


    // Common listener for buttons
    public View.OnClickListener onButtonClicked = new View.OnClickListener() {
        @Override
        public void onClick(View v) {

            // Checking if the expression was just evaluated with equals button
            if (evaluatedByEquals) {

                switch (v.getId()) {

                    // any operator button pressed
                    case R.id.additionBtn:
                    case R.id.subtractionBtn:
                    case R.id.multiplicationBtn:
                    case R.id.divisionBtn:
                        //do nothing
                        break;

                    // negate button pressed
                    case R.id.negateBtn:
                        // clearing expressionTextView and keeping solutionTextView
                        expressionTV.setText("");
                        break;

                    // if anything but an operator was pressed we want to reset everything
                    default:
                        allClearPressed();
                        break;

                }// end switch v.getID()

                evaluatedByEquals = false;

            } // end if(evaluatedByEquals)

            switch (v.getId()) {

                //any numerical button pressed
                case R.id.num0Btn:
                case R.id.num1Btn:
                case R.id.num2Btn:
                case R.id.num3Btn:
                case R.id.num4Btn:
                case R.id.num5Btn:
                case R.id.num6Btn:
                case R.id.num7Btn:
                case R.id.num8Btn:
                case R.id.num9Btn:

                    numberPressed((Button) v);
                    break;

                // any operator button pressed
                case R.id.additionBtn:
                case R.id.subtractionBtn:
                case R.id.multiplicationBtn:
                case R.id.divisionBtn:
                    operatorPressed((Button) v);
                    break;

                // All clear button pressed
                case R.id.allClearBtn:

                    allClearPressed();
                    break;

                // decimal button pressed
                case R.id.decimalBtn:
                    decimalPressed();
                    break;

                // delete button pressed
                case R.id.deleteBtn:

                    deletePressed();
                    break;

                // equals button pressed
                case R.id.equalsBtn:
                    equalsPressed();
                    break;

                // negate button pressed
                case R.id.negateBtn:
                    negatePressed();
                    break;

            }//end switch v.getId()

        }// end onClick method
    }; //end inner class


    // Method for when a number button is pressed
    private void numberPressed(Button buttonPressed) {

        String solutionText = solutionTV.getText().toString();
        String expressionText = expressionTV.getText().toString();

        // If only "0" is displayed
        if (solutionText.matches("-?0")) {

            // If left number is saved but operator is not, set operator to last char in expressionText
            if (leftNumber != null && operator == null) {
                operator = expressionText.charAt(expressionText.length() - 1);
            }

            solutionText = solutionText.substring(0, solutionText.length() - 1);
            solutionText += buttonPressed.getText();
            solutionTV.setText(solutionText);

        // if right number is not null then an expression was just evaluated by pressing equals button
        // Or if NaN then we just tried to divide by zero and need to reset
        } else if (rightNumber != null || solutionText.equals("NaN")) {

            allClearPressed();
            solutionTV.setText(buttonPressed.getText());

        // finally, checking number length
        } else if (checkNumberLength()){

            solutionText += buttonPressed.getText();
            solutionTV.setText(solutionText);

        } // end if/else block

    } // end numberPressed method


    // Method to clear memory and both displays
    private void allClearPressed() {

        // Clearing both text views and setting solution view to "0"
        solutionTV.setText("0");
        expressionTV.setText("");
        solutionTV.setTextSize(LARGE_TEXT_SIZE);

        // Clearing left right and operator in memory
        leftNumber = null;
        rightNumber = null;
        operator = null;

    } // end allClear method


    // method for when delete button is pressed
    private void deletePressed() {

        String solutionText = solutionTV.getText().toString();

        // checking to see if solutionTV is just "0"
        if (!(solutionText.matches("-?0"))) {

            // removing the last character from solutionText
            solutionText = solutionText.substring(0, solutionText.length() - 1);

            // If there was only one character and it was deleted the string is empty now, so we set it to "0"
            if (solutionText.isEmpty()) {
                solutionText = "0";
            }

            // Setting solutionTV text to the new text
            solutionTV.setText(solutionText);

        } // end if block

    } // end deletePressed method


    // Method for when decimal button pressed
    private void decimalPressed() {

        String solutionText = solutionTV.getText().toString();

        // If left number is saved but operator is not, set operator to last char in expressionText
        if (leftNumber != null && operator == null) {
            operator = expressionTV.getText().charAt(expressionTV.length() - 1);
        }

        // Using regex to check if there is already a decimal, and checking number of digits in number
        if (checkNumberLength() && !(solutionText.matches("-?\\d+.\\d*"))) {
            solutionText += ".";    // appending decimal to end of string
            solutionTV.setText(solutionText);   //updating text displayed
        }

    } // end decimalPressed method


    // Method for when an operator button is pressed
    private void operatorPressed(Button btn) {

        String operatorString = btn.getText().toString();
        String expressionText;

        // if operator has already been saved
        if (operator != null) {

            // saving right number
            rightNumber = Double.parseDouble(solutionTV.getText().toString().replaceAll(",", ""));

            // evaluating expression
            double result = validateAndEvaluate();

            // Making sure result is in acceptable range
            if (result > (Double.MAX_VALUE * -1) && result < Double.MAX_VALUE) {

                // setting solutionTextView to show result, and resetting numbers in memory
                solutionTV.setText(formatDouble(result));

                leftNumber = null;
                operator = null;
                rightNumber = null;

            } else {
                rangeMessage.show();
                allClearPressed();
            }

        }

        // if left number has not been saved yet
        if (leftNumber == null) {

            // saving left number and moving it to expression text view, appending operator, and resetting solutionTextView
            leftNumber = Double.parseDouble(solutionTV.getText().toString().replaceAll(",", ""));
            expressionText = formatDouble(leftNumber) + operatorString;
            expressionTV.setText(expressionText);
            solutionTV.setText("0");
            solutionTV.setTextSize(LARGE_TEXT_SIZE);

        // if left number is saved but operator is not
        } else {

            //removing last character from expressionTV and replacing with new operator
            expressionText = expressionTV.getText().toString();
            expressionText = expressionText.substring(0, expressionText.length() - 1);
            expressionText += operatorString;
            expressionTV.setText(expressionText);

        }

    } // end operatorPressed method


    // method for when equals button is pressed
    private void equalsPressed() {

        String expressionText = expressionTV.getText().toString();

        // Checking to see if there has been an operator pressed by checking if saved in memory
        // and checking if the last character in expressionText is not a digit
        if (operator == null) {

            if (expressionText.length() > 0 && !Character.isDigit(expressionText.charAt(expressionText.length() - 1))) {
                operator = expressionText.charAt(expressionText.length() - 1);
            }

        }

        // If an operator has been saved
        if (operator != null) {

            // getting right number from solutionTV, appending it and = to expressionTV
            rightNumber = Double.parseDouble(solutionTV.getText().toString().replaceAll(",", ""));
            expressionText += formatDouble(rightNumber) + "=";
            expressionTV.setText(expressionText);

            // evaluating expression
            double result = validateAndEvaluate();

            // Making sure result is in acceptable range
            if (result > (Double.MAX_VALUE * -1) && result < Double.MAX_VALUE) {

                Log.i("result=", String.valueOf(result));

                // setting solutionTextView to show result
                solutionTV.setText(formatDouble(result));

                // Changing text size if length is >= 8
                if (solutionTV.getText().length() >= SHRINK_TEXT_SIZE_LIMIT) {
                    solutionTV.setTextSize(SMALL_TEXT_SIZE);
                }

                // resetting numbers and operator in memory, and changing flag to let program know
                // expression was just evaluated by equals
                evaluatedByEquals = true;
                leftNumber = null;
                operator = null;
                rightNumber = null;

            } else {
                rangeMessage.show();
                allClearPressed();
            }

        }

    } // end equalsPressed method


    // method for when negate button is pressed
    private void negatePressed() {

        // negating the value in solutionTV
        Double num = Double.parseDouble(solutionTV.getText().toString());
        num *= -1;

        // Not using formatDouble method because we do not want commas or scientific notation to appear when negating
        DecimalFormat format = new DecimalFormat("#.#");
        solutionTV.setText(format.format(num));

    } //method for negate being pressed


    // method for validating and evaluating expression
    private double validateAndEvaluate() {

        // Checking for divide by zero, and returning NaN if that is the case
        if (rightNumber == 0 && operator == '÷') {
            return Double.NaN;
        } else {
            return Calculation.calculate(leftNumber, rightNumber, operator);
        }

    } //end validateAndEvaluate Method


    // Method to format double value for string display
    private String formatDouble(Double num) {

        DecimalFormat format;

        // using Math.pow, if number (absolute value) is less than 1E15 we want to not use scientific notation
        if (Math.abs(num) < Math.pow(10, MAX_NUMBER_LENGTH)) {
            format = new DecimalFormat("#,##0.################");
        } else {
            format = new DecimalFormat("#E0");
        }
        return format.format(num);

    } // end formatDouble method


    // Method to check the length of input numbers, shows popup and returns false if at max length
    private boolean checkNumberLength() {

        // Removing the negative sign to get an accurate count of digits
        String solutionText = solutionTV.getText().toString().replaceAll("-", "");

        // Checking the length and showing message if it is at max length
        if (solutionText.length() >= MAX_NUMBER_LENGTH) {
            lengthMessage.show();
            return false;

        } else {

            if (solutionText.length() == SHRINK_TEXT_SIZE_LIMIT) {
                solutionTV.setTextSize(SMALL_TEXT_SIZE);
            }
            return true;
        }

    } // end checkNumberLength method

} // End MainActivity class