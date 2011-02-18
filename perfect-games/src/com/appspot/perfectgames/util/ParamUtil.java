package com.appspot.perfectgames.util;

import javax.servlet.http.HttpServletRequest;

public class ParamUtil {
	public static int getInt(HttpServletRequest request, String param,
			int defaultValue) {
		if (request.getParameter(param) != null) {
			try {
				return Integer.valueOf(request.getParameter(param));
			} catch (Exception e) {
			}
		}
		return defaultValue;
	}

	public static long getLong(HttpServletRequest request, String param,
			long defaultValue) {
		if (request.getParameter(param) != null) {
			try {
				return Long.valueOf(request.getParameter(param));
			} catch (Exception e) {
			}
		}
		return defaultValue;
	}

	public static boolean getBoolean(HttpServletRequest request, String param,
			boolean defaultValue) {
		if (request.getParameter(param) != null) {
			try {
				return Boolean.valueOf(request.getParameter(param));
			} catch (Exception e) {
			}
		}
		return defaultValue;
	}
}
