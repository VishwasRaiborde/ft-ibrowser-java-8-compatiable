package com.cloudsherpas.http;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class NoAccessPageHandler {

	public String get(HttpServletRequest req, HttpServletResponse resp)
			throws IOException {
		return "OK";
	}
}
