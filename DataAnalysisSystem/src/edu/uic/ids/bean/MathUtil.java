package edu.uic.ids.bean;

import java.text.NumberFormat;

public final class MathUtil {

	private static NumberFormat number = NumberFormat.getNumberInstance();

	public static String numberFormat(double value) {
		number.setMaximumFractionDigits(4);
		return number.format(value);
	}

	public static String numberFormat(float value) {
		number.setMaximumFractionDigits(4);
		return number.format(value);
	}

	public static double round(double value, double precision) {
		return (value * precision)/precision;
	}
}
