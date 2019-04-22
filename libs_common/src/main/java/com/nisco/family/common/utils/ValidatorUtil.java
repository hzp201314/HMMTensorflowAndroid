package com.nisco.family.common.utils;


import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 *  校验手机号 固话  邮箱
 */
public class ValidatorUtil {
	private ValidatorUtil() {
	}

	/*
	 * 验证电话号码
	 */
	public static boolean validTelNumber(String telNumber) {
		boolean isValid = false;

		String expression = "((^(13|15|18)[0-9]{9}$)|(^0[1,2]{1}\\d{1}-?\\d{8}$)|(^0[3-9] {1}\\d{2}-?\\d{7,8}$)|(^0[1,2]{1}\\d{1}-?\\d{8}-(\\d{1,4})$)|(^0[3-9]{1}\\d{2}-? \\d{7,8}-(\\d{1,4})$))";
		CharSequence inputStr = telNumber;

		Pattern pattern = Pattern.compile(expression);

		Matcher matcher = pattern.matcher(inputStr);

		if (matcher.matches()) {
			isValid = true;
		}

		return isValid;
	}

	/*
	 * 验证手机号码
	 */
	public static boolean validMobileNumber(String mobileNumber) {
		boolean isValid = false;

		String expression = "(^(13|15|18|17|14)[0-9]{9}$)"; // !(/(^\d{15}$)|(^\d{17}([0-9]|X)$)/.test(num))
		CharSequence inputStr = mobileNumber;

		Pattern pattern = Pattern.compile(expression);

		Matcher matcher = pattern.matcher(inputStr);

		if (matcher.matches()) {
			isValid = true;
		}

		return isValid;
	}

	/*
	 * 验证邮箱
	 */
	public static boolean validEmail(String email) {
		boolean isValid = false;
		String expression = "(^^\\w+((-\\w+)|(\\.\\w+))*\\@[A-Za-z0-9]+((\\.|-)[A-Za-z0-9]+)*\\.[A-Za-z0-9]+$)";
		CharSequence inputStr = email;

		Pattern pattern = Pattern.compile(expression);

		Matcher matcher = pattern.matcher(inputStr);

		if (matcher.matches()) {
			isValid = true;
		}

		return isValid;
	}
}
