package com.cloudsherpas.http.admin;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class AdminHandler {
	public String get(HttpServletRequest req, HttpServletResponse resp)
			throws IOException {
		req.setAttribute("content_holder", "app/news.html");
		
		return "OK";
	}
	public void post(HttpServletRequest req, HttpServletResponse resp)
			throws IOException {
		req.setAttribute("content_holder", "app/news.html");

	}
}
