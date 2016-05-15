package com.mordenkainen.equivalentenergistics.util;

public class ServerUnmetDependencyException extends RuntimeException {
	private static final long serialVersionUID = 8390968439932228953L;

	public ServerUnmetDependencyException(final String message) {
		super(message);
	}
}
