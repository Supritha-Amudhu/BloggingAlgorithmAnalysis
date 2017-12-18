package edu.sfsu.bigdata.bloganalysis.util;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class StringUtils {
	
	private static final Pattern commentsCountPattern = Pattern.compile("[0-9](.[0-9])?(M?|K?|B?)");
	
	/**
	 * Checks if a string is not empty or null
	 * @param val
	 * @return
	 */
	public static boolean isNotEmpty(String val) {
		return val != null && !val.trim().isEmpty();
	}
	
	/**
	 * Checks if a string is an integer
	 * @param val
	 * @return
	 */
	public static boolean isInteger(String val) {
		if (isNotEmpty(val)) {
			try {
				Integer intVal = Integer.parseInt(val);
				return true;
			}catch (NumberFormatException e) {
				e.printStackTrace();
				return false;
			}
		}
		return false;
	}
	
	/**
	 * Checks if the number of comments parsed is a valid number and returns true or false
	 * @param val
	 * @return
	 */
	public static boolean isValidCommentsCountText(String val) {
		if (isNotEmpty(val)) {
			Matcher matcher = commentsCountPattern.matcher(val);
			return matcher.matches();
		}
		return false;
	}
	
	/**
	 * Gets the number of comments for each Blog
	 * @param val
	 * @return
	 */
	public static long getCommentsCount(String val) {
		if (isValidCommentsCountText(val)) {
			String multiplier = val.substring(val.length() - 2, val.length() - 1);
			String number = val.substring(0, val.length() - 2);
			long multiplierValue = 1;
			switch(multiplier) {
			case "M": multiplierValue = 1000000;break;
			case "K": multiplierValue = 1000; break;
			case "B": multiplierValue = 1000000000; break;
			}
			return (long) (Float.parseFloat(number) * multiplierValue);
		}
		return 0;
	}
}
