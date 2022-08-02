package expressionPackage;

import java.math.*;
import java.util.*;



public class ComposedExpression implements Expression {

	private ArrayList<Expression> operands = new ArrayList<>();
	private ArrayList<Character> operators = new ArrayList<>();

	public ComposedExpression(String inData) {
		inData = removeBlanks(inData);

		if (inData.length() < 1)
			throw new IllegalArgumentException();
		int index = 0;
		while (index < inData.length()) {
			char current = inData.charAt(index);
			if (current == '(') {
				String subExpression = removeOuterParenthesis(inData.substring(index));
				inData = inData.substring(index + subExpression.length() + 2);
				ComposedExpression e = new ComposedExpression(subExpression);
				operands.add(e);
				index = 0;
			} else {
				int endOfSegment = index + 1;
				while (endOfSegment < inData.length() && Character.isDigit(inData.charAt(endOfSegment)))
					endOfSegment++;
				if (endOfSegment < inData.length() && inData.charAt(endOfSegment) == '.') {
					endOfSegment++;
					while (endOfSegment < inData.length() && Character.isDigit(inData.charAt(endOfSegment)))
						endOfSegment++;
				}
				if (endOfSegment < inData.length() && inData.charAt(endOfSegment) == '.')
					throw new IllegalArgumentException();

				AtomicExpression e = new AtomicExpression(inData.substring(index, endOfSegment));
				operands.add(e);
				index = endOfSegment;
			}
			if (index < inData.length()) {
				char op = inData.charAt(index++);
				operators.add(op);
			}
		}

	}

	private static String removeOuterParenthesis(String inData) {
		// pre: inData starts with "("
		int index = 0;
		int ctr = 1;
		while (ctr > 0) {
			index++;
			char current = inData.charAt(index);
			if (current == '(')
				ctr++;
			if (current == ')')
				ctr--;
		}
		String out = inData.substring(1, index);
		return out;
	}

	private String removeBlanks(String data) {
		StringBuilder sb = new StringBuilder(data.strip());
		int index = 0;
		while (index < sb.length()) {
			if (sb.charAt(index) == ' ') {
				sb = new StringBuilder().append(sb.subSequence(0, index))
						.append(sb.subSequence(index + 1, sb.length()));
			} else
				index++;
		}
		return sb.toString();
	}
	
	private AtomicExpression calculate() {
		if (operands.size() == 1) {
			return new AtomicExpression(operands.get(0).getValue().toString());
		}
		ArrayList<Expression> operandsCopy = new ArrayList<>(operands);
		ArrayList<Character> operatorsCopy = new ArrayList<>(operators);
		while (operatorsCopy.size() > 0) {
			int indexMult = operatorsCopy.indexOf('*');
			int indexDiv = operatorsCopy.indexOf('/');
			int indexFirstPointOp = indexDiv>=0 && indexDiv <indexMult ? indexDiv : indexMult;
			if (indexFirstPointOp >= 0) {
				
				Expression a = operandsCopy.get(indexFirstPointOp);
				Expression b = operandsCopy.remove(indexFirstPointOp + 1);
				
				operandsCopy.set(indexFirstPointOp,
						handleBinaryOperation(a, b, operatorsCopy.remove(indexFirstPointOp)));
			} else {
				Expression a = operandsCopy.remove(0);
				Expression b = operandsCopy.get(0);
				Character op = operatorsCopy.remove(0);
				operandsCopy.set(0, handleBinaryOperation(a, b, op));
			}
		}
		return new AtomicExpression(operandsCopy.get(0).getValue().toString());
	}
	
	private AtomicExpression handleBinaryOperation(Expression a, Expression b, Character op) {
		AtomicExpression opOne = new AtomicExpression(a.getValue().toString());
		AtomicExpression opTwo = new AtomicExpression(b.getValue().toString());
		switch (op) {
		case '+':
			return opOne.add(opTwo);
		case '-':
			return opOne.sub(opTwo);
		case '*':
			return opOne.mult(opTwo);
		case '/':
			return opOne.div(opTwo);
		default:
			throw new IllegalArgumentException("unsupported op-symbol: " + op);
		}

	}

	@Override
	public BigDecimal getValue() {
		return calculate().getValue();
	}
	
	public String getStringResult() {
		return calculate().toString();
	}
	



}
