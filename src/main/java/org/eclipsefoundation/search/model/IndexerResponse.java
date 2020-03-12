package org.eclipsefoundation.search.model;

import org.eclipsefoundation.search.namespace.IndexerResponseStatus;

public class IndexerResponse {
	private String message;
	private IndexerResponseStatus status;
	private Exception exception;
	private long elapsedTimeMS;

	public IndexerResponse() {
		this("", null, 0);
	}

	public IndexerResponse(String message, IndexerResponseStatus status, long elapsedTimeMS) {
		this(message, status, elapsedTimeMS, null);
	}

	public IndexerResponse(String message, IndexerResponseStatus status, long elapsedTimeMS, Exception exception) {
		this.message = message;
		this.status = status;
		this.elapsedTimeMS = elapsedTimeMS;
		this.setException(exception);
	}

	/**
	 * @return the message
	 */
	public String getMessage() {
		return message;
	}

	/**
	 * @param message the message to set
	 */
	public void setMessage(String message) {
		this.message = message;
	}

	/**
	 * @return the status
	 */
	public IndexerResponseStatus getStatus() {
		return status;
	}

	/**
	 * @param status the status to set
	 */
	public void setStatus(IndexerResponseStatus status) {
		this.status = status;
	}

	/**
	 * @return the exception
	 */
	public Exception getException() {
		return exception;
	}

	/**
	 * @param exception the exception to set
	 */
	public void setException(Exception exception) {
		this.exception = exception;
	}

	/**
	 * @return the elapsedTimeMS
	 */
	public long getElapsedTimeMS() {
		return elapsedTimeMS;
	}

	/**
	 * @param elapsedTimeMS the elapsedTimeMS to set
	 */
	public void setElapsedTimeMS(long elapsedTimeMS) {
		this.elapsedTimeMS = elapsedTimeMS;
	}

	public static IndexerResponse getMaintenanceResponse() {
		IndexerResponse out = new IndexerResponse();
		out.message = "";
		out.elapsedTimeMS = 0;
		out.setStatus(IndexerResponseStatus.MAINTENANCE);
		return out;
	}
}
