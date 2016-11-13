package com.webinfo.security;

import java.io.IOException;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.jasig.cas.client.validation.Assertion;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.webinfo.security.util.ObjectToString;

public class ClientSessionManager {

	public final static String CLIENT_SESSION_ID = "_C_S_";
	private final static long CLIENT_SESSION_TIMEOUT_IN_MS = 600000;
	private final static long CLIENT_SESSION_REFRESH_INTERNAL_IN_MS = 60000;
	private final static boolean ENABLE_TRACE = true;

	public final static Assertion handle(HttpServletRequest request, HttpServletResponse response) {
		ClientSessionBean csession = fetchValidClientSession(request);
		if (csession == null) {
			trace("assertion is null");
			return null;
		} else {
			request.setAttribute(CLIENT_SESSION_ID, csession.getAssertion());
			trace("set cookie to request. user: " + csession.getAssertion().getPrincipal().getName());

			long currTimestamp = new java.util.Date().getTime();
			if ((currTimestamp - csession.getCreateTimestamp()) > CLIENT_SESSION_REFRESH_INTERNAL_IN_MS) {
				csession.refreshCreateTimestamp();
				persistAssertion(response, csession.getAssertion());
				trace("refresh cookie. user: " + csession.getAssertion().getPrincipal().getName());
			}
			return csession.getAssertion();
		}
	}

	public final static Assertion fetchValidAssertion(HttpServletRequest request) {
		Assertion assertion = (Assertion) request.getAttribute(CLIENT_SESSION_ID);
		if (assertion != null) {
			return assertion;
		} else {
			ClientSessionBean csession = fetchValidClientSession(request);
			return csession == null ? null : csession.getAssertion();
		}
	}

	public final static void persistAssertion(HttpServletResponse response, Assertion assertion) {
		if (assertion == null) {
			throw new RuntimeException("Assertion is invalid");
		}

		ClientSessionBean csession = new ClientSessionBean(assertion);
		String content;
		try {
			content = ObjectToString.convertToString(csession);
			if (content == null) {
				throw new Exception();
			}
		} catch (Exception e) {
			throw new RuntimeException("Fail to encode the assertion.");
		}

		Cookie c = new Cookie(CLIENT_SESSION_ID, content);
		c.setPath("/");
		response.addCookie(c);
	}

	private final static ClientSessionBean fetchValidClientSession(HttpServletRequest request) {
		ClientSessionBean csession = fetchClientSession(request);

		if (csession == null) {
			return null;
		}

		long currTimestamp = new java.util.Date().getTime();
		if ((currTimestamp - csession.getCreateTimestamp()) > CLIENT_SESSION_TIMEOUT_IN_MS) {
			return null;
		}

		Assertion assertion = csession.getAssertion();
		if (assertion == null || assertion.getPrincipal() == null) {
			return null;
		}

		String name = assertion.getPrincipal().getName();
		if (name == null || name.trim().equals("")) {
			return null;
		}
		return csession;
	}

	private final static ClientSessionBean fetchClientSession(HttpServletRequest request) {
		if (request == null) {
			return null;
		}

		Cookie[] cookies = request.getCookies();

		if (cookies == null || cookies.length == 0) {
			return null;
		}

		String n, v;
		for (Cookie c : cookies) {
			n = c.getName();
			if (n.equals(CLIENT_SESSION_ID)) {
				v = c.getValue();
				if (v == null || v.length() == 0)
					break;

				ClientSessionBean session;
				try {
					session = (ClientSessionBean) ObjectToString.convertToObject(v);
					return session;
				} catch (Exception e) {
				}
				break;
			}
		}
		return null;
	}

	public static void discardCache(HttpServletResponse response) throws IOException {
		response.setHeader("Pragma", "No-cache");
		response.setHeader("Cache-Control", "no-cache");
		response.setDateHeader("Expires", 0);
	}

	public static void redirect(HttpServletResponse response, String url) throws IOException {
		discardCache(response);
		response.setStatus(200);
		String out = "<script language=JavaScript>document.location='" + url + "'</script>";
		response.getOutputStream().println(out);
	}

	private static void trace(String out) {
		if (ENABLE_TRACE) {
			System.out.println("[CLIENT_SESSION] " + out);
		}
	}
}
