/* Copyright (c) 2019 Eclipse Foundation and others.
 * This program and the accompanying materials are made available
 * under the terms of the Eclipse Public License 2.0
 * which is available at http://www.eclipse.org/legal/epl-v20.html,
 * SPDX-License-Identifier: EPL-2.0
 */
package org.eclipsefoundation.marketplace.helper;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;
import java.util.concurrent.ExecutionException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Static helper class for handling reactive stream results.
 * 
 * @author Martin Lowe
 */
public class StreamHelper {
	private static final Logger LOGGER = LoggerFactory.getLogger(StreamHelper.class);

	/**
	 * Unwraps and awaits a completion stage to complete, returning the internal
	 * result when finished.
	 * 
	 * @param <T>   the type of object that contains results of async call
	 * @param stage the completion stage to await results for
	 * @return the result, or null if one couldn't be found
	 */
	public static <T> T awaitCompletionStage(CompletionStage<T> stage) {
		CompletableFuture<T> future = stage.toCompletableFuture();
		try {
			return future.get();
		} catch (InterruptedException e) {
			LOGGER.error("Operation was interrupted before completion", e);
			Thread.currentThread().interrupt();
		} catch (ExecutionException e) {
			LOGGER.error("Operation completed exceptionally: {}", e.getCause(), e);

			throw new RuntimeException("Error awaiting completion stage", e);
		}
		return null;
	}

	private StreamHelper() {
	}
}
