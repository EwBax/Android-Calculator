package com.ewbax.calculator;

public abstract class Calculation {

    // Method to perform calculations and return result
    public static double calculate(double left, double right, char operator) {

        double result = 0;

        switch (operator) {

            case '+':
                result = left + right;
                break;
            case '-':
                result = left - right;
                break;
            case '×':
                result = left * right;
                break;
            case '÷':
                result = left / right;
                break;

        }

        return result;

    }   // end calculate method

}   // end Calculation class
