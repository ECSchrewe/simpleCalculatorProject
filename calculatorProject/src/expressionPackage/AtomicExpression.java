package expressionPackage;

import java.math.*;
import java.text.*;
import java.util.*;

public class AtomicExpression implements Expression {

	private final BigDecimal value;
	static NumberFormat nf = NumberFormat.getInstance(Locale.US);

	AtomicExpression(String inData) {
		value = new BigDecimal(inData, MathContext.DECIMAL128);
	}

	public static void main(String[] args) {

		AtomicExpression a = new AtomicExpression("32.000");
		System.out.println(a);
		
	}

	AtomicExpression add(AtomicExpression other) {
		return new AtomicExpression(value.add(other.value).toString());
	}

	AtomicExpression sub(AtomicExpression other) {
		return new AtomicExpression(value.subtract(other.value).toString());
	}

	AtomicExpression mult(AtomicExpression other) {
		return new AtomicExpression(value.multiply(other.value).toString());
	}

	AtomicExpression div(AtomicExpression other) {
		return new AtomicExpression(value.divide(other.value, 20, RoundingMode.HALF_UP).toString());
	}

	@Override
	public BigDecimal getValue() {
		return value;
	}

	private String cutZeros(String in) {
		int pointIndex = in.indexOf(".");
		if (pointIndex == -1 || pointIndex == in.length() - 2)
			return in;
		
		while(in.charAt(in.length()-1)=='0' && in.length()>pointIndex+2)
			in = in.substring(0, in.length()-1);
		return in;
	}

	@Override
	public String toString() {
		return cutZeros(value.toPlainString());
	}

}
