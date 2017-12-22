package com.ithinkrok.util.math;

import com.ithinkrok.util.math.expression.*;

import java.io.IOException;
import java.io.StreamTokenizer;
import java.io.StringReader;
import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
import java.util.*;

/**
 * Created by paul on 03/01/16.
 */
public class ExpressionCalculator implements Calculator {

    private static final Random random = new Random();

    private static final Map<String, Operator> opMap = new HashMap<>();

    static {
        addOperator("/", numbers -> numbers[0] / numbers[1], (mc, numbers) -> numbers[0].divide(numbers[1], mc),
                    false, false, 20, 2, 2);
        addOperator("*", numbers -> numbers[0] * numbers[1], (mc, numbers) -> numbers[0].multiply(numbers[1], mc),
                    false, false, 20, 2, 2);
        addOperator("%", numbers -> numbers[0] % numbers[1], (mc, numbers) -> numbers[0].remainder(numbers[1], mc),
                    false, false, 20, 2, 2);
        addOperator("+", numbers -> numbers[0] + numbers[1], (mc, numbers) -> numbers[0].add(numbers[1], mc),
                    false, false, 30, 2, 2);
        addOperator("-", numbers -> numbers[0] - numbers[1], (mc, numbers) -> numbers[0].subtract(numbers[1], mc),
                    false, false, 30, 2, 2);

        addOperator("=", numbers -> (numbers[0] == numbers[1]) ? 1 : 0,
                    (mc, numbers) -> numbers[0].equals(numbers[1]) ? BigDecimal.ONE : BigDecimal.ZERO,
                    false, false, 35, 2, 2);
        addOperator("<", numbers -> (numbers[0] < numbers[1]) ? 1 : 0,
                    (mc, numbers) -> (numbers[0].compareTo(numbers[1]) < 0) ? BigDecimal.ONE : BigDecimal.ZERO,
                    false, false, 35, 2, 2);
        addOperator(">", numbers -> (numbers[0] > numbers[1]) ? 1 : 0,
                    (mc, numbers) -> (numbers[0].compareTo(numbers[1]) > 0) ? BigDecimal.ONE : BigDecimal.ZERO,
                    false, false, 35, 2, 2);

        //unary minus = '
        addOperator("'", numbers -> -numbers[0], (mc, numbers) -> numbers[0].negate(mc),
                    false, false, 5, 1, 1);

        addOperator("|", numbers -> integer(numbers[0]) | integer(numbers[1]), false, false, 40, 2, 2);
        addOperator("&", numbers -> integer(numbers[0]) & integer(numbers[1]), false, false, 40, 2, 2);
        addOperator("^", numbers -> integer(numbers[0]) ^ integer(numbers[1]), false, false, 40, 2, 2);

        addOperator("~", numbers -> ~integer(numbers[0]), false, false, 5, 1, 1);
        addOperator("!", numbers -> (integer(numbers[0]) == 0) ? 1 : 0, false, false, 5, 1, 1);

        addOperator("sin", numbers -> Math.sin(numbers[0]), true, false, 0, 1, 1);
        addOperator("cos", numbers -> Math.cos(numbers[0]), true, false, 0, 1, 1);
        addOperator("tan", numbers -> Math.tan(numbers[0]), true, false, 0, 1, 1);

        addOperator("asin", numbers -> Math.asin(numbers[0]), true, false, 0, 1, 1);
        addOperator("acos", numbers -> Math.acos(numbers[0]), true, false, 0, 1, 1);
        addOperator("atan", numbers -> Math.atan(numbers[0]), true, false, 0, 1, 1);
        addOperator("atan2", numbers -> Math.atan2(numbers[0], numbers[1]), true, false, 0, 2, 2);

        addOperator("degrees", numbers -> Math.toDegrees(numbers[0]), true, false, 0, 1, 1);
        addOperator("radians", numbers -> Math.toRadians(numbers[0]), true, false, 0, 1, 1);

        addOperator("pow", numbers -> Math.pow(numbers[0], numbers[1]), (mc, numbers) -> {
                        try {
                            int power = numbers[1].intValueExact();

                            return numbers[0].pow(power, mc);
                        } catch (ArithmeticException ignored) {
                            return BigDecimal.valueOf(Math.pow(numbers[0].doubleValue(), numbers[1].doubleValue()));
                        }
                    },
                    true, false, 0, 2, 2);
        addOperator("sqrt", numbers -> Math.sqrt(numbers[0]), true, false, 0, 1, 1);
        addOperator("ln", numbers -> Math.log(numbers[0]), true, false, 0, 1, 1);
        addOperator("lg", numbers -> Math.log(numbers[0]) * 1.44269504089, true, false, 0, 1, 1);
        addOperator("log", numbers -> Math.log10(numbers[0]), true, false, 0, 1, 1);
        addOperator("exp", numbers -> Math.exp(numbers[0]), true, false, 0, 1, 1);

        addOperator("random", numbers -> random.nextDouble(), true, true, 0, 0, 0);
        addOperator("ranInt", numbers -> random.nextInt((int) numbers[0]), true, true, 0, 1, 1);

        addOperator("round", numbers -> Math.round(numbers[0]),
                    (mc, numbers) -> numbers[0].setScale(0, RoundingMode.HALF_UP),
                    true, false, 0, 1, 1);
        addOperator("abs", numbers -> Math.abs(numbers[0]), (mc, numbers) -> numbers[0].abs(mc),
                    true, false, 0, 1, 1);
        addOperator("floor", numbers -> Math.floor(numbers[0]),
                    (mc, numbers) -> numbers[0].setScale(0, RoundingMode.FLOOR),
                    true, false, 0, 1, 1);
        addOperator("ceil", numbers -> Math.ceil(numbers[0]),
                    (mc, numbers) -> numbers[0].setScale(0, RoundingMode.CEILING),
                    true, false, 0, 1, 1);
        addOperator("expression", numbers -> numbers[0], true, false, 0, 1, 1);

        addOperator("min", numbers -> {
            double min = Double.POSITIVE_INFINITY;

            for (double num : numbers) {
                if (num < min) min = num;
            }

            return min;
        }, (mc, numbers) -> {
            BigDecimal min = numbers[0];

            for (BigDecimal number : numbers) {
                if(number.compareTo(min) < 0) {
                    min = number;
                }
            }

            return min;
        }, true, false, 0, 1, Integer.MAX_VALUE);

        addOperator("max", numbers -> {
            double max = Double.NEGATIVE_INFINITY;

            for (double num : numbers) {
                if (num > max) max = num;
            }

            return max;
        }, (mc, numbers) -> {
            BigDecimal max = numbers[0];

            for (BigDecimal num : numbers) {
                if (num.compareTo(max) > 0) max = num;
            }

            return max;
        },true, false, 0, 1, Integer.MAX_VALUE);

        addOperator("array", numbers -> {
            int index = (int) (numbers[0] + 1);
            return numbers[index];
        }, (mc, numbers) -> {
            int index = numbers[0].intValue() + 1;
            return numbers[index];
        }, true, false, 0, 2, Integer.MAX_VALUE);
    }

    private final Expression expression;

    public ExpressionCalculator(String expression) {
        this(expression, SimplifyMode.DOUBLE);
    }

    public ExpressionCalculator(String expression, SimplifyMode simplifyDouble) {
        List<String> tokens;

        try {
            tokens = tokenize(expression);
        } catch (IOException e) {
            throw new ExpressionException("Failed to tokenize expression: " + expression, e);
        }

        tokens = toPostfixNotation(tokens);

        this.expression = parsePostfixNotation(tokens, simplifyDouble);
    }

    private static List<String> tokenize(String s) throws IOException {
        StreamTokenizer tokenizer = new StreamTokenizer(new StringReader(s));

        tokenizer.ordinaryChar('-');
        tokenizer.ordinaryChar('/');
        tokenizer.wordChars('_', '_');
        tokenizer.wordChars('#', '#');
        tokenizer.wordChars('@', '@');
        tokenizer.wordChars('$', '$');

        List<String> tokBuf = new ArrayList<>();
        boolean valueLast = false;

        while (tokenizer.nextToken() != StreamTokenizer.TT_EOF) {
            switch (tokenizer.ttype) {
                case StreamTokenizer.TT_NUMBER:
                    tokBuf.add(String.valueOf(tokenizer.nval));
                    valueLast = true;
                    break;
                default:  // operator or word
                    String token;
                    if (tokenizer.ttype == StreamTokenizer.TT_WORD) token = tokenizer.sval;
                    else token = String.valueOf((char) tokenizer.ttype);

                    boolean operator = opMap.containsKey(token) || "(".equals(token) || ",".equals(token);

                    if (!"-".equals(token) || valueLast) tokBuf.add(token);
                    else tokBuf.add("'"); //Unary minus
                    valueLast = !operator;
            }

        }
        return tokBuf;
    }

    private static List<String> toPostfixNotation(List<String> tokens) {
        LinkedList<String> tokenStack = new LinkedList<>();
        List<String> output = new ArrayList<>(tokens.size());

        for (String token : tokens) {
            Operator operator = opMap.get(token);
            if (operator != null) {
                while (!tokenStack.isEmpty() && lowerPrecedence(operator.getPrecedence(), tokenStack.getLast())) {
                    output.add(tokenStack.removeLast());
                }
                tokenStack.add(token);
            } else if (isNumber(token)) {
                output.add(token);
            } else if ("(".equals(token)) {
                if (!tokenStack.isEmpty() && opMap.containsKey(tokenStack.getLast()) &&
                    opMap.get(tokenStack.getLast()).isFunction()) output.add(token);
                tokenStack.add(token);
            } else if (",".equals(token)) {
                while (!"(".equals(tokenStack.getLast())) {
                    output.add(tokenStack.removeLast());
                }
            } else if (")".equals(token)) {
                while (!"(".equals(tokenStack.getLast())) {
                    output.add(tokenStack.removeLast());
                }
                tokenStack.removeLast();

                if (!tokenStack.isEmpty() && opMap.containsKey(tokenStack.getLast()) &&
                    opMap.get(tokenStack.getLast()).isFunction()) output.add(tokenStack.removeLast());
            } else output.add(token);
        }


        while (!tokenStack.isEmpty()) {
            String token = tokenStack.removeLast();
            if ("(".equals(token)) throw new ExpressionException("Mismatched brackets: " + tokens);
            output.add(token);
        }


        return output;
    }

    private static Expression parsePostfixNotation(List<String> tokens, SimplifyMode simplify) {
        LinkedList<Expression> stack = new LinkedList<>();

        for (String token : tokens) {
            if ("(".equals(token)) {
                stack.add(null); //Use a null expression as the stack separator
            } else if (isNumber(token)) {
                stack.add(new NumberExpression(token));
            } else if (!opMap.containsKey(token)) {
                stack.add(variableExpression(token));
            } else {
                Operator op = opMap.get(token);

                LinkedList<Expression> expressions = new LinkedList<>();

                int count;
                for (count = 0; !stack.isEmpty() && (op.isFunction() || count < op.getMaxArguments()); ++count) {
                    Expression expr = stack.removeLast();
                    if (expr == null) {
                        break;
                    }
                    expressions.addFirst(expr);
                }

                if (count > op.getMaxArguments()) throw new ExpressionException("Too many arguments for function" + token);
                else if (count < op.getMinArguments())
                    throw new ExpressionException("Too few arguments for function: " + token);

                stack.add(new OperatorExpression(op, op.isDynamic(), simplify, expressions));
            }
        }

        //noinspection StatementWithEmptyBody
        while (stack.remove(null)) ; //Remove all null elements from the stack

        if (stack.size() != 1) throw new ExpressionException("Bad expression: " + tokens);


        Expression expression = stack.getFirst();
        expression = simplify.simplify(expression);

        return expression;
    }

    private static boolean lowerPrecedence(int o1Index, String o2) {
        Operator o2Info = opMap.get(o2);

        if (o2Info == null) return false;

        return o1Index >= o2Info.getPrecedence();
    }

    @SuppressWarnings("ResultOfMethodCallIgnored")
    public static boolean isNumber(String token) {
        try {
            Double.parseDouble(token);
            return true;
        } catch (NumberFormatException ignored) {
            return false;
        }
    }

    private static Expression variableExpression(String token) {
        switch (token.toLowerCase()) {
            case "pi":
                return new NumberExpression(Math.PI);
            case "tau":
                return new NumberExpression(Math.PI * 2);
            case "e":
                return new NumberExpression(Math.E);
            case "true":
                return new NumberExpression(BigDecimal.ONE);
            case "false":
                return new NumberExpression(BigDecimal.ZERO);
            default:
                return new VariableExpression(token);
        }
    }

    public static boolean isOperatorOrFunction(String check) {
        return opMap.containsKey(check);
    }

    public static void addOperator(String op,
                                   Operator.Executor executor,
                                   boolean isFunction, boolean isDynamic,
                                   int precedence, int minArgs, int maxArgs) {
        opMap.put(op, new Operator(op, executor, isFunction, isDynamic, precedence, minArgs, maxArgs));
    }

    public static void addOperator(String op,
                                   Operator.Executor executor, Operator.DecimalExecutor decimalExecutor,
                                   boolean isFunction, boolean isDynamic,
                                   int precedence, int minArgs, int maxArgs) {
        opMap.put(op, new Operator(op, executor, decimalExecutor, isFunction, isDynamic, precedence, minArgs, maxArgs));
    }

    private static int integer(double d) {
        return (int) Math.floor(d);
    }

    @Override
    public int hashCode() {
        return expression.hashCode();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ExpressionCalculator that = (ExpressionCalculator) o;

        return expression.equals(that.expression);

    }

    @Override
    public String toString() {
        return expression.toString();
    }

    @Override
    public double calculate(Variables variables) {
        return expression.calculate(variables);
    }

}
