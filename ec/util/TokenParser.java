package com.evangelsoft.econnect.util;

import java.util.HashMap;

public class TokenParser {
    public static final String DEFAULT_DELIMITERS = "+-*/%=!~^|#$@;:'\"()[]<>{}, \t\r\n";
    private String inputString;
    private String delimiters;
    private int currentIndex;
    private int startOfToken;
    private int endOfToken;

    public static String appendDelimiter(String inputString, char delimiters) {
        StringBuffer result = new StringBuffer();

        for(int i = 0; i < inputString.length(); ++i) {
        	result.append(inputString.charAt(i));
            if (inputString.charAt(i) == delimiters) {
            	result.append(delimiters);
            }
        }

        return result.toString();
    }

    public static String stripOffDelimiter(String inputString, char delimiters) {
        boolean previousCharWasDelimiter = false;
        StringBuffer result = new StringBuffer();

        for(int i = 0; i < inputString.length(); ++i) {
            if (inputString.charAt(i) != delimiters) {
            	result.append(inputString.charAt(i));
            } else if (previousCharWasDelimiter) {
            	result.append(inputString.charAt(i));
                previousCharWasDelimiter = false;
            } else {
            	previousCharWasDelimiter = true;
            }
        }

        return result.toString();
    }

    public TokenParser() {
        this("", DEFAULT_DELIMITERS);
    }

    public TokenParser(String inputString, String delimiters) {
        this.reset(inputString, delimiters);
    }

    public TokenParser(String inputString) {
        this.reset(inputString, DEFAULT_DELIMITERS);
    }

    public void reset() {
        this.currentIndex = 0;
        this.startOfToken = -1;
        this.endOfToken = -1;
    }

    public void reset(String inputString, String delimiters) {
        this.inputString = inputString;
        this.delimiters = delimiters;
        this.reset();
    }

    public boolean find() {
        boolean isInQuotes = false;
        char quoteChar = 0;
        this.startOfToken = -1;

        for(this.endOfToken = -1; this.currentIndex < this.inputString.length(); ++this.currentIndex) {
            char currentChar = this.inputString.charAt(this.currentIndex);
            if (isInQuotes) {
            	isInQuotes = currentChar != quoteChar;
            } else if (currentChar != '\'' && currentChar != '"') {
                if (this.delimiters.indexOf(currentChar) >= 0) {
                    if (this.startOfToken >= 0) {
                        break;
                    }
                } else if (this.startOfToken < 0) {
                    this.startOfToken = this.currentIndex;
                }
            } else {
            	isInQuotes = true;
            	quoteChar = currentChar;
           }
        }

        if (this.startOfToken >= 0) {
            this.endOfToken = this.currentIndex;
        }

        return this.startOfToken >= 0;
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

    public String replaceToken(HashMap<String, String> tokenMap) {
        if (tokenMap == null) {
            return this.inputString;
        } else {
            StringBuffer result = new StringBuffer();
            int lastEndIndex = 0;
            this.reset();

            while (this.find()) {
                String token = this.get();
                result.append(this.inputString, lastEndIndex, this.startOfToken);
                String replacement = tokenMap.get(token);
                if (replacement != null && !replacement.isEmpty()) {
                    result.append(replacement);
                } else {
                    result.append(token);
                }
                lastEndIndex = this.endOfToken;
            }

            result.append(this.inputString.substring(lastEndIndex));
            return result.toString();
        }
    }
}
