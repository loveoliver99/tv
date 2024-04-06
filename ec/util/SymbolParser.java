package com.evangelsoft.econnect.util;

public class SymbolParser {
    public static final char DEFAULT_DELIMITER = '$';
    private String inputString;
    private char startSymbol;
    private char endSymbol;
    private int currentIndex;
    private int startIndexOfSymbol;
    private int endIndexOfSymbol;

    public SymbolParser() {
        this("", DEFAULT_DELIMITER , DEFAULT_DELIMITER );
    }

    public SymbolParser(String inputString, char startSymbol, char endSymbol) {
        this.inputString = inputString;
        this.startSymbol = startSymbol;
        this.endSymbol = endSymbol;
        this.reset();
    }

    public SymbolParser(String inputString) {
        this(inputString, DEFAULT_DELIMITER, DEFAULT_DELIMITER);
    }

    public void reset() {
        this.currentIndex = 0;
        this.startIndexOfSymbol = -1;
        this.endIndexOfSymbol = -1;
    }

    public void reset(String inputString, char startSymbol, char endSymbol) {
        this.inputString = inputString;
        this.startSymbol = startSymbol;
        this.endSymbol = endSymbol;
        this.reset();
    }

    public void reset(String inputString, char startSymbol) {
        this.reset(inputString, startSymbol, startSymbol);
    }

    public boolean find() {
        boolean isInQuotes = false;
        int quoteChar = 0;
        char symbolNestingLevel = 0;
        this.startIndexOfSymbol = -1;

        for(this.endIndexOfSymbol = -1; this.currentIndex < this.inputString.length(); ++this.currentIndex) {
            char currentChar = this.inputString.charAt(this.currentIndex);
            if (isInQuotes) {
            	isInQuotes = currentChar != symbolNestingLevel;
            } else if (currentChar != '\'' && currentChar != '"') {
                if (quoteChar > 0) {
                    if (currentChar == this.endSymbol) {
                        --quoteChar;
                        if (quoteChar <= 0) {
                            this.endIndexOfSymbol = this.currentIndex + 1;
                            ++this.currentIndex;
                            break;
                        }
                    } else if (currentChar == this.startSymbol) {
                        ++quoteChar;
                    }
                } else if (currentChar == this.startSymbol) {
                	quoteChar = 1;
                    this.startIndexOfSymbol = this.currentIndex;
                }
            } else {
            	isInQuotes = true;
            	symbolNestingLevel = currentChar;
            }
        }

        return this.startIndexOfSymbol >= 0 && this.endIndexOfSymbol >= 0;
    }

    public String get() {
        return this.startIndexOfSymbol >= 0 && this.endIndexOfSymbol > this.startIndexOfSymbol ? this.inputString.substring(this.startIndexOfSymbol, this.endIndexOfSymbol) : "";
    }

    public String getNet() {
        String symbol = this.get();
        if (symbol.length() >= 2) {
        	symbol = symbol.substring(1, symbol.length() - 1);
        }

        return symbol;
    }

    public int getBeginIndex() {
        return this.startIndexOfSymbol;
    }

    public int getEndIndex() {
        return this.endIndexOfSymbol;
    }

    public void moveTo(int newIndex) {
        this.currentIndex = newIndex;
    }
}
