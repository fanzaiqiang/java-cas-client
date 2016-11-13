package com.webinfo.security;

import org.jasig.cas.client.validation.Assertion;

public class ClientSessionBean implements java.io.Serializable {

	private static final long serialVersionUID = 1L;

	private Assertion assertion;

	private Long createTimestamp;

	public ClientSessionBean(Assertion assertion) {
		this.createTimestamp = new java.util.Date().getTime();
		this.assertion = assertion;
	}

	public Assertion getAssertion() {
		return assertion;
	}

	public Long getCreateTimestamp() {
		return createTimestamp;
	}

	public void refreshCreateTimestamp() {
		this.createTimestamp = new java.util.Date().getTime();
	}
}
