package com.evangelsoft.econnect.util;

public class ParameterParser {
    public static final char DEFAULT_DELIMITER = ',';
    private String inputString;
    private char delimiter;
    private int currentIndex;
    private int startOfToken;
    private int endOfToken;

    public ParameterParser() {
        this("", DEFAULT_DELIMITER);
    }

    public ParameterParser(String inputString, char delimiter) {
        this.inputString = inputString;
        this.delimiter = delimiter;
        this.reset();
    }

    public ParameterParser(String inputString) {
        this(inputString, ',');
    }

    public void reset() {
        this.currentIndex = 0;
        this.startOfToken = -1;
        this.endOfToken = -1;
    }

    public void reset(String inputString, char delimiter) {
        this.inputString = inputString;
        this.delimiter = delimiter;
        this.reset();
    }

    public boolean find() {
        if (this.currentIndex >= this.inputString.length()) {
            return false;
        } else {
            boolean isInQuotes = false;
            int quoteChar = 0;
            char parenthesisCount = 0;
            this.startOfToken = -1;

            for(this.endOfToken = -1; this.currentIndex < this.inputString.length(); ++this.currentIndex) {
                char currentChar = this.inputString.charAt(this.currentIndex);
                if (this.startOfToken < 0 && currentChar != ' ' && currentChar != '\t' && currentChar != '\r' && currentChar != '\n') {
                    this.startOfToken = this.currentIndex;
                }

                if (isInQuotes) {
                	isInQuotes = currentChar != parenthesisCount;
                } else if (currentChar != '\'' && currentChar != '"') {
                    if (quoteChar > 0) {
                        if (currentChar == ')') {
                            --quoteChar;
                        } else if (currentChar == '(') {
                            ++quoteChar;
                        }
                    } else if (currentChar == '(') {
                    	quoteChar = 1;
                    } else if (currentChar == this.delimiter) {
                        this.endOfToken = this.currentIndex++;
                        break;
                    }
                } else {
                	isInQuotes = true;
                	parenthesisCount = currentChar;
                }
            }

            if (this.startOfToken >= 0) {
                if (this.endOfToken < 0) {
                    this.endOfToken = this.currentIndex;
                }

                if (this.endOfToken < this.inputString.length()) {
                    while(this.endOfToken > this.startOfToken && (this.inputString.charAt(this.endOfToken) == ' ' || this.inputString.charAt(this.endOfToken) == '\t' || this.inputString.charAt(this.endOfToken) == '\r' || this.inputString.charAt(this.endOfToken) == '\n')) {
                        --this.endOfToken;
                    }
                }

                return true;
            } else {
                return false;
            }
        }
    }

    public String get() {
        return this.startOfToken >= 0 && this.endOfToken > this.startOfToken ? this.inputString.substring(this.startOfToken, this.endOfToken) : "";
    }

    public int getBeginIndex() {
        return this.startOfToken;
    }

    public int getEndIndex() {
        return this.endOfToken;
    }

    public void moveTo(int newIndex) {
        this.currentIndex = newIndex;
    }
}
