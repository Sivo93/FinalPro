package com.example.myweb.interceptor;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Component
public class LoginInterceptor implements HandlerInterceptor {
	@Override
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
			throws Exception {
		HttpSession session = request.getSession();
		if (session.getAttribute("loginid") == null || session.getAttribute("nickname") == null) { // nickname 체크 추가
			String requestURI = request.getRequestURI();
			String queryString = request.getQueryString();
			String redirectURL = requestURI + (queryString != null ? "?" + queryString : "");
			session.setAttribute("redirectURL", redirectURL);
			response.sendRedirect("/user/login");
			return false;
		}
		return true;
	}
}
