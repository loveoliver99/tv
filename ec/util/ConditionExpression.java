package com.evangelsoft.econnect.util;

import com.evangelsoft.econnect.condutil.ConditionJointNode;
import com.evangelsoft.econnect.condutil.ConditionLeafNode;
import com.evangelsoft.econnect.condutil.ConditionNode;
import com.evangelsoft.econnect.condutil.ConditionTree;
import com.evangelsoft.econnect.dataformat.RecordField;
import com.evangelsoft.econnect.dataformat.RecordSet;
import com.evangelsoft.econnect.dataformat.VariantHolder;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class ConditionExpression {
    public ConditionExpression() {
    }

    public static ArrayList<Object> parse(String expression) {
        return parse(expression, null, null);
    }

    public static ArrayList<Object> parse(String expression, ConditionExpression.Operator operator, ConditionExpression.Argument argument) { if (operator == null) { operator = new ConditionExpression.OperatorImpl(); }

    if (argument == null) {
    	argument = new ConditionExpression.ArgumentImpl();
    }

    ArrayList resultList = new ArrayList();
    ConditionExpression.Operator currentOperator = null;
    int argCount = 0;

    int end;
    for(int start = 0; start < expression.length(); start = end) {
    	end = start;
        int var8 = 0;

        for(char var9 = 0; end < expression.length(); ++end) {
            char var10 = expression.charAt(end);
            if (var9 == 0 && (var10 == '(' || var10 == '[')) {
                var9 = var10;
                var8 = 1;
            } else if (var9 == var10) {
                ++var8;
            } else if (var9 == '(' && var10 == ')' || var9 == '[' && var10 == ']') {
                --var8;
                if (var8 == 0) {
                    boolean var14 = false;
                    ++end;
                    break;
                }
            } else if (var9 == 0 && end > start && (var10 == ' ' || var10 == '\t' || var10 == '\n' || var10 == '\r')) {
                break;
            }
        }

        String var15 = expression.substring(start, end).trim();
        if (var15.length() > 0 && var15.charAt(var15.length() - 1) == ',') {
            var15 = var15.substring(0, var15.length() - 1);
        }

        if (var15.length() > 0) {
            ConditionExpression.Operator var11 = ((ConditionExpression.Operator)operator).parse(var15);
            if (var11 != null) {
                if (argCount >= var11.getArgumentSize()) {
                	resultList.add(var11);
                	currentOperator = null;
                	argCount = 1;
                } else {
                	currentOperator = var11;
                }
            } else {
                if (var15.length() >= 2 && var15.charAt(0) == '(' && var15.charAt(var15.length() - 1) == ')') {
                    ArrayList var12 = parse(var15.substring(1, var15.length() - 1), (ConditionExpression.Operator)operator, (ConditionExpression.Argument)argument);
                    if (!var12.isEmpty()) {
                        if (var12.get(var12.size() - 1) instanceof ConditionExpression.Operator) {
                        	resultList.addAll(var12);
                        } else {
                        	resultList.add(var12.toArray());
                        }
                    }

                    var12.clear();
                    var12 = null;
                } else {
                    try {
                    	resultList.add(((ConditionExpression.Argument)argument).parse(var15));
                    } catch (Exception var13) {
                    }
                }

                ++argCount;
                if (currentOperator != null && argCount >= currentOperator.getArgumentSize()) {
                	resultList.add(currentOperator);
                	currentOperator = null;
                	argCount = 1;
                }
            }
        }
    }

    return resultList;
} 

    public static boolean calculate(ArrayList<Object> expressions) { ArrayList<Object> stack = new ArrayList<>(); ArrayList<Object> arguments = new ArrayList<>();

    Object currentExpression;
    for(int i = 0; i < expressions.size(); ++i) {
        currentExpression = expressions.get(i);
        if (currentExpression instanceof ConditionExpression.Operator) {
            arguments.clear();
            ConditionExpression.Operator currentOperator = (ConditionExpression.Operator)currentExpression;

            for(int j = 0; j < currentOperator.getArgumentSize() && stack.size() > 0; ++j) {
                Object currentArgument = stack.remove(stack.size() - 1);
                arguments.add(0, currentArgument);
            }

            if (arguments.size() == currentOperator.getArgumentSize()) {
                stack.add(currentOperator.calculate(arguments.toArray()));
            } else {
                stack.add(false);
            }
        } else if (currentExpression instanceof ConditionExpression.Argument) {
            stack.add(((ConditionExpression.Argument)currentExpression).getValue());
        } else if (currentExpression.getClass().isArray() && currentExpression.getClass().getComponentType().isAssignableFrom(ConditionExpression.Argument.class)) {
            ArrayList<Object> argumentList = new ArrayList<>();
            Object[] argumentArray = (Object[])currentExpression;

            for(int k = 0; k < argumentArray.length; ++k) {
                Object currentArgument = argumentArray[k];
                argumentList.add(((ConditionExpression.Argument)currentArgument).getValue());
            }

            stack.add(argumentList.toArray());
        }
    }

    boolean result = false;
    if (stack.size() == 1) {
        Object finalExpression = stack.get(0);
        if (finalExpression != null && finalExpression instanceof Boolean) {
            result = (Boolean)finalExpression;
        }
    }

    stack.clear();
    return result;
    }

    public static String getExpression(ArrayList<Object> expressions) { ArrayList<String> expressionStrings = new ArrayList<>(); ArrayList<ConditionExpression.Operator> operatorStack = new ArrayList<>(); ArrayList<String> argumentStrings = new ArrayList<>(); ArrayList<Object> argumentStack = new ArrayList<>();

    for(int i = 0; i < expressions.size(); ++i) {
        Object currentExpression = expressions.get(i);
        if (currentExpression instanceof ConditionExpression.Operator) {
            argumentStrings.clear();
            argumentStack.clear();
            ConditionExpression.Operator currentOperator = (ConditionExpression.Operator)currentExpression;

            for(int j = 0; j < currentOperator.getArgumentSize() && expressionStrings.size() > 0; ++j) {
                argumentStrings.add(0, expressionStrings.remove(expressionStrings.size() - 1));
                argumentStack.add(0, operatorStack.remove(operatorStack.size() - 1));
            }

            if (argumentStrings.size() == currentOperator.getArgumentSize()) {
                StringBuffer operatorStringBuffer = new StringBuffer();
                String[] argumentStringArray = new String[argumentStrings.size()];

                for(int k = 0; k < argumentStrings.size(); ++k) {
                    ConditionExpression.Operator argumentOperator = (ConditionExpression.Operator)argumentStack.get(k);
                    boolean shouldAddParenthesis = argumentOperator != null && argumentOperator.getOperator() != currentOperator.getOperator() && (currentOperator.getOperator() == 0 || currentOperator.getOperator() == 1 || currentOperator.getOperator() == 2);
                    argumentStringArray[k] = (shouldAddParenthesis ? "(" : "") + argumentStrings.get(k) + (shouldAddParenthesis ? ")" : "");
                }

                if (currentOperator.getOperator() == 2) {
                    operatorStringBuffer.append(currentOperator.getString());
                    if (argumentStringArray.length > 0) {
                        operatorStringBuffer.append(argumentStringArray[0]);
                    }
                } else {
                    if (argumentStringArray.length > 0) {
                        operatorStringBuffer.append(argumentStringArray[0] + " ");
                    }

                    operatorStringBuffer.append(currentOperator.getString());

                    for(int l = 1; l < argumentStringArray.length; ++l) {
                        operatorStringBuffer.append(" " + argumentStringArray[l]);
                    }
                }

                expressionStrings.add(operatorStringBuffer.toString());
                operatorStack.add(currentOperator);
            }
        } else if (currentExpression instanceof ConditionExpression.Argument) {
            expressionStrings.add(((ConditionExpression.Argument)currentExpression).getString());
            operatorStack.add(null);
        } else if (currentExpression.getClass().isArray() && currentExpression.getClass().getComponentType().isAssignableFrom(ConditionExpression.Argument.class)) {
            StringBuffer arrayStringBuffer = new StringBuffer();
            arrayStringBuffer.append('(');

            for(int m = 0; m < ((Object[])currentExpression).length; ++m) {
                if (m > 0) {
                    arrayStringBuffer.append(", ");
                }

                Object currentArgument = ((Object[])currentExpression)[m];
                arrayStringBuffer.append(((ConditionExpression.Argument)currentArgument).getString());
            }

            arrayStringBuffer.append(')');
            expressionStrings.add(arrayStringBuffer.toString());
            operatorStack.add(null);
        }
    }

    String finalExpressionString;
    if (expressionStrings.size() == 1) {
        finalExpressionString = expressionStrings.get(0);
    } else {
        finalExpressionString = "";
    }

    expressionStrings.clear();
    operatorStack.clear();
    return finalExpressionString;
    }

    public static ConditionTree buildConditionTree(ArrayList<Object> conditionList) {
        ConditionTree conditionTree = new ConditionTree();
        if (conditionList.isEmpty()) {
            return conditionTree;
        } else {
            Object operatorObject = conditionList.remove(conditionList.size() - 1);
            if (!(operatorObject instanceof ConditionExpression.Operator)) {
                return conditionTree;
            } else {
                ConditionExpression.Operator operator = (ConditionExpression.Operator)operatorObject;
                if (operator.getOperator() != 0 && operator.getOperator() != 1 && operator.getOperator() != 2) {
                	ArrayList<Object> argumentList = new ArrayList<>();

                    while(true) {
                        do {
                            if (argumentList.size() >= operator.getArgumentSize() || conditionList.size() <= 0) {
                                if (argumentList.size() == operator.getArgumentSize()) {
                                	operatorObject = argumentList.get(0);
                                    String argumentName;
                                    int argumentDataType;
                                    if (!operatorObject.getClass().isArray()) {
                                    	argumentName = ((ConditionExpression.Argument)operatorObject).getName();
                                    } else {
                                    	argumentName = "(";

                                        for(argumentDataType = 0; argumentDataType < ((Object[])operatorObject).length; ++argumentDataType) {
                                            if (argumentDataType > 0) {
                                            	argumentName = argumentName + ", ";
                                            }

                                            argumentName = argumentName + ((ConditionExpression.Argument)((Object[])operatorObject)[argumentDataType]).getName();
                                        }

                                        argumentName = argumentName + ")";
                                    }

                                    argumentDataType = -1;

                                    for(int i = 0; i < argumentList.size(); ++i) {
                                    	operatorObject = argumentList.get(i);
                                        if (operatorObject.getClass().isArray()) {
                                            if (((Object[])operatorObject).length > 0) {
                                            	argumentDataType = ((ConditionExpression.Argument)((Object[])operatorObject)[0]).getDataType();
                                                if (argumentDataType >= 0 && argumentDataType != 101) {
                                                    break;
                                                }
                                            }
                                        } else {
                                        	argumentDataType = ((ConditionExpression.Argument)operatorObject).getDataType();
                                            if (argumentDataType >= 0 && argumentDataType != 101) {
                                                break;
                                            }
                                        }
                                    }

                                    if (argumentDataType != 22 && argumentDataType != 1 && argumentDataType != 11 && argumentDataType != 12 && argumentDataType != 13 && argumentDataType != 101) {
                                    	argumentDataType = 101;
                                    }

                                    ConditionLeafNode leafNode = new ConditionLeafNode(argumentName, argumentDataType, operator.getOperatorCode());

                                    for(int i = 1; i < argumentList.size(); ++i) {
                                    	operatorObject = argumentList.get(i);
                                        if (!operatorObject.getClass().isArray()) {
                                            Object argumentValue = ((ConditionExpression.Argument)operatorObject).getValue();
                                            switch(argumentDataType) {
                                            case 1:
                                            	leafNode.setNumber(i - 1, (BigDecimal)argumentValue);
                                                break;
                                            case 11:
                                            	leafNode.setDate(i - 1, (Date)argumentValue);
                                                break;
                                            case 12:
                                            	leafNode.setTime(i - 1, (Date)argumentValue);
                                                break;
                                            case 13:
                                            	leafNode.setTimestamp(i - 1, (Date)argumentValue);
                                                break;
                                            case 22:
                                            	leafNode.setString(i - 1, (String)argumentValue);
                                                break;
                                            case 101:
                                                String expression = ((ConditionExpression.ExpElement)argumentValue).expression;
                                                if (expression != null && expression.length() >= 2 && expression.startsWith("[") && expression.endsWith("]")) {
                                                	expression = expression.substring(1, expression.length() - 1);
                                                }

                                                leafNode.setExpression(i - 1, expression);
                                            }
                                        } else {
                                            switch(argumentDataType) {
                                            case 1:
                                                BigDecimal[] numberList = new BigDecimal[((Object[])operatorObject).length];

                                                for(int j = 0; j < numberList.length; ++j) {
                                                	numberList[j] = (BigDecimal)((ConditionExpression.Argument)((Object[])operatorObject)[j]).getValue();
                                                }

                                                leafNode.setNumberList(i - 1, numberList);
                                                break;
                                            case 11:
                                                Date[] dateList = new Date[((Object[])operatorObject).length];

                                                for(int j = 0; j < dateList.length; ++j) {
                                                	dateList[j] = (Date)((ConditionExpression.Argument)((Object[])operatorObject)[j]).getValue();
                                                }

                                                leafNode.setDateList(i - 1, dateList);
                                                break;
                                            case 12:
                                                Date[] timeList  = new Date[((Object[])operatorObject).length];

                                                for(int j = 0; j < timeList .length; ++j) {
                                                	timeList[j] = (Date)((ConditionExpression.Argument)((Object[])operatorObject)[j]).getValue();
                                                }

                                                leafNode.setTimeList(i - 1, timeList );
                                                break;
                                            case 13:
                                                Date[] timestampList = new Date[((Object[])operatorObject).length];

                                                for(int j = 0; j < timestampList.length; ++j) {
                                                	timestampList[j] = (Date)((ConditionExpression.Argument)((Object[])operatorObject)[j]).getValue();
                                                }

                                                leafNode.setTimestampList(i - 1, timestampList);
                                                break;
                                            case 22:
                                                String[] stringList = new String[((Object[])operatorObject).length];

                                                for(int j = 0; j < stringList.length; ++j) {
                                                	stringList[j] = (String)((ConditionExpression.Argument)((Object[])operatorObject)[j]).getValue();
                                                }

                                                leafNode.setStringList(i - 1, stringList);
                                                break;
                                            case 101:
                                                String[] expressionList = new String[((Object[])operatorObject).length];

                                                for(int j = 0; j < expressionList.length; ++j) {
                                                    String expression = ((ConditionExpression.ExpElement)((ConditionExpression.Argument)((Object[])operatorObject)[j]).getValue()).expression;
                                                    if (expression != null && expression.length() >= 2 && expression.startsWith("[") && expression.endsWith("]")) {
                                                    	expression = expression.substring(1, expression.length() - 1);
                                                    }

                                                    expressionList[j] = expression;
                                                }

                                                leafNode.setExpressionList(i - 1, expressionList);
                                            }
                                        }
                                    }

                                    conditionTree.setRoot(leafNode);
                                }

                                return conditionTree;
                            }

                            operatorObject = conditionList.remove(conditionList.size() - 1);
                        } while(!(operatorObject instanceof ConditionExpression.Argument) && (!operatorObject.getClass().isArray() || !operatorObject.getClass().getComponentType().isAssignableFrom(ConditionExpression.Argument.class)));

                        argumentList.add(0, operatorObject);
                    }
                } else {
                    ConditionJointNode jointNode = new ConditionJointNode(operator.getOperator() == 0 ? "AND" : (operator.getOperator() == 1 ? "OR" : "NOT"));
                    conditionTree.setRoot(jointNode);

                    for(int i = 0; i < operator.getArgumentSize() && !conditionList.isEmpty(); ++i) {
                        ConditionTree subTree = buildConditionTree(conditionList);
                        if (!subTree.isEmpty()) {
                            ConditionNode subTreeRoot = subTree.getRoot();
                            if (subTreeRoot instanceof ConditionJointNode && ((ConditionJointNode)subTreeRoot).joinType.equals(jointNode.joinType) && !jointNode.joinType.equals("NOT")) {
                                for(ConditionNode var8 = subTree.getLastChild(subTreeRoot); var8 != null; var8 = subTree.getPreviousSibling(var8)) {
                                	conditionTree.addChildFirst(jointNode, subTree, var8);
                                }
                            } else {
                            	conditionTree.addChildFirst(jointNode, subTree, (ConditionNode)null);
                            }

                            subTree.clear();
                            subTree = null;
                        }
                    }

                    return conditionTree;
                }
            }
        }
    }

    public static ArrayList<Object> parse(ConditionTree var0) {
        return parse(var0, (ConditionNode)null, (ConditionExpression.Operator)null, (ConditionExpression.Argument)null);
    }

    public static ArrayList<Object> parse(ConditionTree var0, ConditionNode var1) {
        return parse(var0, var1, (ConditionExpression.Operator)null, (ConditionExpression.Argument)null);
    }

    public static ArrayList<Object> parse(ConditionTree var0, ConditionNode var1, ConditionExpression.Operator var2, ConditionExpression.Argument var3) {
        if (var2 == null) {
            var2 = new ConditionExpression.OperatorImpl();
        }

        if (var3 == null) {
            var3 = new ConditionExpression.ArgumentImpl();
        }

        ArrayList var4 = new ArrayList();
        if (var0.isEmpty()) {
            return var4;
        } else {
            if (var1 == null) {
                var1 = var0.getRoot();
            }

            int var7;
            if (var1 instanceof ConditionJointNode) {
                ConditionJointNode var5 = (ConditionJointNode)var1;
                ConditionNode var6 = var0.getFirstChild(var1);

                for(var7 = 0; var6 != null; var6 = var0.getNextSibling(var6)) {
                    ArrayList var8 = parse(var0, var6, (ConditionExpression.Operator)var2, (ConditionExpression.Argument)var3);
                    var4.addAll(var8);
                    var8.clear();
                    ++var7;
                    if (var5.joinType.equals("AND")) {
                        if (var7 >= 2) {
                            var4.add(((ConditionExpression.Operator)var2).parse("AND"));
                        }
                    } else if (var5.joinType.equals("OR")) {
                        if (var7 >= 2) {
                            var4.add(((ConditionExpression.Operator)var2).parse("OR"));
                        }
                    } else if (var5.joinType.equals("NOT")) {
                        var4.add(((ConditionExpression.Operator)var2).parse("NOT"));
                    }
                }
            } else if (var1 instanceof ConditionLeafNode) {
                ConditionLeafNode var16 = (ConditionLeafNode)var1;
                var4.add(((ConditionExpression.Argument)var3).parse(var16.name));
                Object[] var17 = new Object[var16.operator.equals("BT") ? 2 : (!var16.operator.equals("ISN") && !var16.operator.equals("INN") ? 1 : 0)];

                for(var7 = 0; var7 < var17.length; ++var7) {
                    if (!var16.operator.equals("IN") && !var16.operator.equals("NIN")) {
                        String var23;
                        switch(var16.dataType) {
                        case 1:
                            var23 = var16.getNumber(var7).toString();
                            break;
                        case 11:
                            var23 = "{" + (new SimpleDateFormat("yyyyMMdd")).format(var16.getDate(var7)) + "}";
                            break;
                        case 12:
                            var23 = "{" + (new SimpleDateFormat("HHmmss")).format(var16.getDate(var7)) + "}";
                            break;
                        case 13:
                            var23 = "{" + (new SimpleDateFormat("yyyyMMddHHmmss")).format(var16.getDate(var7)) + "}";
                            break;
                        case 22:
                            var23 = "'" + var16.getString(var7) + "'";
                            break;
                        case 101:
                            var23 = '[' + var16.getExpression(var7) + ']';
                            break;
                        default:
                            var23 = "";
                        }

                        var17[var7] = ((ConditionExpression.Argument)var3).parse(var23);
                    } else {
                        ConditionExpression.Argument[] var19;
                        label130:
                        switch(var16.dataType) {
                        case 1:
                            BigDecimal[] var21 = var16.getNumberList(var7);
                            var19 = new ConditionExpression.Argument[var21.length];
                            int var24 = 0;

                            while(true) {
                                if (var24 >= var21.length) {
                                    break label130;
                                }

                                var19[var24] = ((ConditionExpression.Argument)var3).parse(var21[var24].toString());
                                ++var24;
                            }
                        case 11:
                            Date[] var11 = var16.getDateList(var7);
                            var19 = new ConditionExpression.Argument[var11.length];
                            int var25 = 0;

                            while(true) {
                                if (var25 >= var11.length) {
                                    break label130;
                                }

                                var19[var25] = ((ConditionExpression.Argument)var3).parse("{" + (new SimpleDateFormat("yyyyMMdd")).format(var11[var25]) + "}");
                                ++var25;
                            }
                        case 12:
                            Date[] var12 = var16.getTimeList(var7);
                            var19 = new ConditionExpression.Argument[var12.length];
                            int var26 = 0;

                            while(true) {
                                if (var26 >= var12.length) {
                                    break label130;
                                }

                                var19[var26] = ((ConditionExpression.Argument)var3).parse("{" + (new SimpleDateFormat("HHmmss")).format(var12[var26]) + "}");
                                ++var26;
                            }
                        case 13:
                            Date[] var13 = var16.getTimeList(var7);
                            var19 = new ConditionExpression.Argument[var13.length];
                            int var28 = 0;

                            while(true) {
                                if (var28 >= var13.length) {
                                    break label130;
                                }

                                var19[var28] = ((ConditionExpression.Argument)var3).parse("{" + (new SimpleDateFormat("yyyyMMddHHmmss")).format(var13[var28]) + "}");
                                ++var28;
                            }
                        case 22:
                            String[] var9 = var16.getStringList(var7);
                            var19 = new ConditionExpression.Argument[var9.length];
                            int var10 = 0;

                            while(true) {
                                if (var10 >= var9.length) {
                                    break label130;
                                }

                                var19[var10] = ((ConditionExpression.Argument)var3).parse("'" + var9[var10] + "'");
                                ++var10;
                            }
                        case 101:
                            String[] var14 = var16.getExpressionList(var7);
                            var19 = new ConditionExpression.Argument[var14.length];
                            int var15 = 0;

                            while(true) {
                                if (var15 >= var14.length) {
                                    break label130;
                                }

                                var19[var15] = ((ConditionExpression.Argument)var3).parse('[' + var14[var15] + ']');
                                ++var15;
                            }
                        default:
                            var19 = new ConditionExpression.Argument[0];
                        }

                        var17[var7] = var19;
                    }
                }

                Object[] var22 = var17;
                int var20 = var17.length;

                for(int var27 = 0; var27 < var20; ++var27) {
                    Object var18 = var22[var27];
                    var4.add(var18);
                }

                var4.add(((ConditionExpression.Operator)var2).parseOperatorCode(var16.operator));
            }

            return var4;
        }
    }

    public interface Argument {
        ConditionExpression.Argument parse(String var1);

        String getName();

        int getDataType();

        Object getValue();

        String getString();
    }

    public static class ArgumentImpl implements ConditionExpression.Argument {
        protected String data = null;

        public ArgumentImpl() {
        }

        public ArgumentImpl(String var1) {
            this.data = var1;
        }

        public ConditionExpression.Argument parse(String var1) {
            return new ConditionExpression.ArgumentImpl(var1);
        }

        public String getName() {
            return this.data == null ? "" : this.data;
        }

        public int getDataType() {
            byte var1 = -1;
            if (this.data != null && this.data.length() != 0) {
                char var2 = this.data.charAt(0);
                if (!Character.isDigit(var2) && var2 != '+' && var2 != '-') {
                    if (this.data.length() >= 2 && (var2 == '\'' && this.data.charAt(this.data.length() - 1) == '\'' || var2 == '"' && this.data.charAt(this.data.length() - 1) == '"')) {
                        var1 = 22;
                    } else if (var2 == '{' && this.data.length() >= 2 && this.data.charAt(this.data.length() - 1) == '}') {
                        String var3 = this.data.substring(1, this.data.length() - 1);
                        if (var3.length() == 8) {
                            var1 = 11;
                        } else if (var3.length() == 6) {
                            var1 = 12;
                        } else if (var3.length() == 14) {
                            var1 = 13;
                        }
                    } else {
                        var1 = 101;
                    }
                } else {
                    var1 = 1;
                }

                return var1;
            } else {
                return var1;
            }
        }

        public Object getValue() {
            if (this.data != null && this.data.length() != 0) {
                Object var1 = null;

                try {
                    char var2 = this.data.charAt(0);
                    if (!Character.isDigit(var2) && var2 != '+' && var2 != '-') {
                        if (this.data.length() >= 2 && (var2 == '\'' && this.data.charAt(this.data.length() - 1) == '\'' || var2 == '"' && this.data.charAt(this.data.length() - 1) == '"')) {
                            var1 = this.data.substring(1, this.data.length() - 1);
                        } else if (var2 == '{' && this.data.length() >= 2 && this.data.charAt(this.data.length() - 1) == '}') {
                            String var3 = this.data.substring(1, this.data.length() - 1);
                            if (var3.length() == 8) {
                                var1 = new Date((new SimpleDateFormat("yyyyMMdd")).parse(var3).getTime());
                            } else if (var3.length() == 6) {
                                var1 = new Date((new SimpleDateFormat("HHmmss")).parse(var3).getTime());
                            } else if (var3.length() == 14) {
                                var1 = new Date((new SimpleDateFormat("yyyyMMddHHmmss")).parse(var3).getTime());
                            }
                        } else {
                            var1 = new ConditionExpression.ExpElement(this.data);
                        }
                    } else {
                        var1 = new BigDecimal(this.data);
                    }
                } catch (Exception var4) {
                }

                return var1;
            } else {
                return null;
            }
        }

        public String getString() {
            return this.data == null ? "" : this.data;
        }
    }

    public static class ExpElement {
        public String expression;

        public ExpElement(String var1) {
            this.expression = var1;
        }
    }

    public interface Operator {
        int AND = 0;
        int OR = 1;
        int NOT = 2;
        int EQUAL = 10;
        int UNEQUAL = 11;
        int LESS_THAN = 12;
        int NO_GREATER_THAN = 13;
        int NO_LESS_THAN = 14;
        int GREATER_THAN = 15;
        int BETWEEN = 16;
        int LIKE = 17;
        int NOT_LIKE = 18;
        int LEFT_LIKE = 19;
        int RIGHT_LIKE = 20;
        int IS_NULL = 21;
        int IS_NOT_NULL = 22;
        int IN = 23;
        int NOT_IN = 24;

        int getOperator();

        int getArgumentSize();

        String getOperatorCode();

        ConditionExpression.Operator parseOperatorCode(String var1);

        ConditionExpression.Operator parse(String var1);

        boolean calculate(Object[] var1);

        String getString();
    }

    public static class OperatorImpl implements ConditionExpression.Operator {
        public int operator;
        public int argumentSize;

        public OperatorImpl() {
            this(0);
        }

        public OperatorImpl(int var1) {
            this.operator = 0;
            this.argumentSize = 2;
            this.operator = var1;
            if (var1 != 2 && var1 != 21 && var1 != 22) {
                if (var1 == 16) {
                    this.argumentSize = 3;
                } else {
                    this.argumentSize = 2;
                }
            } else {
                this.argumentSize = 1;
            }

        }

        public int getOperator() {
            return this.operator;
        }

        public String getOperatorCode() {
            String var1;
            switch(this.operator) {
            case 10:
                var1 = "=";
                break;
            case 11:
                var1 = "!=";
                break;
            case 12:
                var1 = "<";
                break;
            case 13:
                var1 = "<=";
                break;
            case 14:
                var1 = ">=";
                break;
            case 15:
                var1 = ">";
                break;
            case 16:
                var1 = "BT";
                break;
            case 17:
                var1 = "LK";
                break;
            case 18:
                var1 = "NK";
                break;
            case 19:
                var1 = "LL";
                break;
            case 20:
                var1 = "RL";
                break;
            case 21:
                var1 = "ISN";
                break;
            case 22:
                var1 = "INN";
                break;
            case 23:
                var1 = "IN";
                break;
            case 24:
                var1 = "NIN";
                break;
            default:
                var1 = "=";
            }

            return var1;
        }

        public ConditionExpression.Operator parseOperatorCode(String var1) {
            if (var1.equals("=")) {
                return new ConditionExpression.OperatorImpl(10);
            } else if (var1.equals("!=")) {
                return new ConditionExpression.OperatorImpl(11);
            } else if (var1.equals("<")) {
                return new ConditionExpression.OperatorImpl(12);
            } else if (var1.equals("<=")) {
                return new ConditionExpression.OperatorImpl(13);
            } else if (var1.equals(">=")) {
                return new ConditionExpression.OperatorImpl(14);
            } else if (var1.equals(">")) {
                return new ConditionExpression.OperatorImpl(15);
            } else if (var1.equals("BT")) {
                return new ConditionExpression.OperatorImpl(16);
            } else if (var1.equals("LK")) {
                return new ConditionExpression.OperatorImpl(17);
            } else if (var1.equals("NK")) {
                return new ConditionExpression.OperatorImpl(18);
            } else if (var1.equals("LL")) {
                return new ConditionExpression.OperatorImpl(19);
            } else if (var1.equals("RL")) {
                return new ConditionExpression.OperatorImpl(20);
            } else if (var1.equals("ISN")) {
                return new ConditionExpression.OperatorImpl(21);
            } else if (var1.equals("INN")) {
                return new ConditionExpression.OperatorImpl(22);
            } else if (var1.equals("IN")) {
                return new ConditionExpression.OperatorImpl(23);
            } else {
                return var1.equals("NIN") ? new ConditionExpression.OperatorImpl(24) : new ConditionExpression.OperatorImpl(10);
            }
        }

        public int getArgumentSize() {
            return this.argumentSize;
        }

        public ConditionExpression.Operator parse(String var1) {
            if (var1.equalsIgnoreCase("AND")) {
                return new ConditionExpression.OperatorImpl(0);
            } else if (var1.equalsIgnoreCase("OR")) {
                return new ConditionExpression.OperatorImpl(1);
            } else if (var1.equalsIgnoreCase("NOT")) {
                return new ConditionExpression.OperatorImpl(2);
            } else if (var1.equalsIgnoreCase("=")) {
                return new ConditionExpression.OperatorImpl(10);
            } else if (var1.equalsIgnoreCase("<>")) {
                return new ConditionExpression.OperatorImpl(11);
            } else if (var1.equalsIgnoreCase("<")) {
                return new ConditionExpression.OperatorImpl(12);
            } else if (var1.equalsIgnoreCase("<=")) {
                return new ConditionExpression.OperatorImpl(13);
            } else if (var1.equalsIgnoreCase(">=")) {
                return new ConditionExpression.OperatorImpl(14);
            } else if (var1.equalsIgnoreCase(">")) {
                return new ConditionExpression.OperatorImpl(15);
            } else if (var1.equalsIgnoreCase("BETWEEN")) {
                return new ConditionExpression.OperatorImpl(16);
            } else if (var1.equalsIgnoreCase("LIKE")) {
                return new ConditionExpression.OperatorImpl(17);
            } else if (var1.equalsIgnoreCase("!LIKE")) {
                return new ConditionExpression.OperatorImpl(18);
            } else if (var1.equalsIgnoreCase("LLIKE")) {
                return new ConditionExpression.OperatorImpl(19);
            } else if (var1.equalsIgnoreCase("RLIKE")) {
                return new ConditionExpression.OperatorImpl(20);
            } else if (var1.equalsIgnoreCase("ISNULL")) {
                return new ConditionExpression.OperatorImpl(21);
            } else if (var1.equalsIgnoreCase("!ISNULL")) {
                return new ConditionExpression.OperatorImpl(22);
            } else if (var1.equalsIgnoreCase("IN")) {
                return new ConditionExpression.OperatorImpl(23);
            } else {
                return var1.equalsIgnoreCase("!IN") ? new ConditionExpression.OperatorImpl(24) : null;
            }
        }

        public boolean calculate(Object[] var1) {
            if (this.argumentSize > var1.length) {
                return false;
            } else {
                try {
                    boolean var2;
                    Object[] var3;
                    Object var4;
                    int var5;
                    int var6;
                    Object[] var7;
                    switch(this.operator) {
                    case 0:
                        var2 = (Boolean)var1[0] && (Boolean)var1[1];
                        break;
                    case 1:
                        var2 = (Boolean)var1[0] || (Boolean)var1[1];
                        break;
                    case 2:
                        var2 = !(Boolean)var1[0];
                        break;
                    case 3:
                    case 4:
                    case 5:
                    case 6:
                    case 7:
                    case 8:
                    case 9:
                    default:
                        var2 = false;
                        break;
                    case 10:
                        var2 = var1[0] == var1[1] || var1[0] != null && var1[1] != null && var1[0].equals(var1[1]);
                        break;
                    case 11:
                        var2 = var1[0] != var1[1] && (var1[0] == null || var1[1] == null || !var1[0].equals(var1[1]));
                        break;
                    case 12:
                        var2 = var1[0] != null && var1[1] != null && ((Comparable)var1[0]).compareTo((Comparable)var1[1]) < 0;
                        break;
                    case 13:
                        var2 = var1[0] != null && var1[1] != null && ((Comparable)var1[0]).compareTo((Comparable)var1[1]) <= 0;
                        break;
                    case 14:
                        var2 = var1[0] != null && var1[1] != null && ((Comparable)var1[0]).compareTo((Comparable)var1[1]) >= 0;
                        break;
                    case 15:
                        var2 = var1[0] != null && var1[1] != null && ((Comparable)var1[0]).compareTo((Comparable)var1[1]) > 0;
                        break;
                    case 16:
                        var2 = var1[0] != null && var1[1] != null && var1[2] != null && ((Comparable)var1[0]).compareTo((Comparable)var1[1]) >= 0 && ((Comparable)var1[0]).compareTo((Comparable)var1[2]) <= 0;
                        break;
                    case 17:
                        var2 = var1[0] != null && var1[1] != null && ((String)var1[0]).indexOf((String)var1[1]) >= 0;
                        break;
                    case 18:
                        var2 = var1[0] != null && var1[1] != null && ((String)var1[0]).indexOf((String)var1[1]) < 0;
                        break;
                    case 19:
                        var2 = var1[0] != null && var1[1] != null && ((String)var1[0]).startsWith((String)var1[1]);
                        break;
                    case 20:
                        var2 = var1[0] != null && var1[1] != null && ((String)var1[0]).endsWith((String)var1[1]);
                        break;
                    case 21:
                        var2 = var1[0] == null;
                        break;
                    case 22:
                        var2 = var1[0] != null;
                        break;
                    case 23:
                        if (var1[1] != null && var1[1].getClass().isArray()) {
                            var2 = false;
                            var3 = (Object[])var1[1];
                            var7 = var3;
                            var6 = var3.length;

                            for(var5 = 0; var5 < var6; ++var5) {
                                var4 = var7[var5];
                                if (var1[0] == var4 || var1[0] != null && var4 != null && var1[0].equals(var4)) {
                                    var2 = true;
                                    return var2;
                                }
                            }

                            return var2;
                        } else {
                            var2 = false;
                            break;
                        }
                    case 24:
                        if (var1[1] != null && var1[1].getClass().isArray()) {
                            var2 = true;
                            var3 = (Object[])var1[1];
                            var7 = var3;
                            var6 = var3.length;

                            for(var5 = 0; var5 < var6; ++var5) {
                                var4 = var7[var5];
                                if (var1[0] == var4 || var1[0] != null && var4 != null && var1[0].equals(var4)) {
                                    var2 = false;
                                    break;
                                }
                            }
                        } else {
                            var2 = false;
                        }
                    }

                    return var2;
                } catch (Exception var8) {
                    return false;
                }
            }
        }

        public String getString() {
            switch(this.operator) {
            case 0:
                return "AND";
            case 1:
                return "OR";
            case 2:
                return "NOT";
            case 3:
            case 4:
            case 5:
            case 6:
            case 7:
            case 8:
            case 9:
            default:
                return "";
            case 10:
                return "=";
            case 11:
                return "<>";
            case 12:
                return "<";
            case 13:
                return "<=";
            case 14:
                return ">=";
            case 15:
                return ">";
            case 16:
                return "BETWEEN";
            case 17:
                return "LIKE";
            case 18:
                return "!LIKE";
            case 19:
                return "LLIKE";
            case 20:
                return "RLIKE";
            case 21:
                return "ISNULL";
            case 22:
                return "!ISNULL";
            case 23:
                return "IN";
            case 24:
                return "!IN";
            }
        }
    }

    public static class RecordSetArgument extends ConditionExpression.ArgumentImpl {
        private RecordSet recordSet;
        private VariantHolder<Integer> variantHolder;

        public RecordSetArgument() {
            this.recordSet = null;
            this.variantHolder = null;
        }

        public RecordSetArgument(RecordSet variantHolder) {
            this();
            this.recordSet = variantHolder;
            this.variantHolder = new VariantHolder();
        }

        public ConditionExpression.Argument parse(String data) {
            ConditionExpression.RecordSetArgument recordSetArgument = new ConditionExpression.RecordSetArgument();
            recordSetArgument.data = data;
            recordSetArgument.recordSet = this.recordSet;
            recordSetArgument.variantHolder = this.variantHolder;
            return recordSetArgument;
        }

        public Object getValue() {
            if (this.recordSet != null && this.getDataType() == 101) {
                Object result = null;
                if ((Integer)this.variantHolder.value >= 0 && (Integer)this.variantHolder.value < this.recordSet.recordCount()) {
                    RecordField recordField = this.recordSet.getRecord((Integer)this.variantHolder.value).getField(this.getName());
                    if (recordField != null) {
                    	result = recordField.getAsObject();
                    }
                }

                return result;
            } else {
                return super.getValue();
            }
        }

        public void gotoRow(int row) {
            this.variantHolder.value = row;
        }
    }
}
