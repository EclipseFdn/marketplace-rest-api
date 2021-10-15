/* Copyright (c) 2019 Eclipse Foundation and others.
 * This program and the accompanying materials are made available
 * under the terms of the Eclipse Public License 2.0
 * which is available at http://www.eclipse.org/legal/epl-v20.html,
 * SPDX-License-Identifier: EPL-2.0
 */
package org.eclipsefoundation.core.helper;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Date;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Central implementation for handling date time conversion in the service.
 * Class uses Java8 DateTime formatters, creating an internal format that
 * represents RFC 3339
 * 
 * @author Martin Lowe
 */
public class DateTimeHelper {
	private static final Logger LOGGER = LoggerFactory.getLogger(DateTimeHelper.class);
	private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("uuuu-MM-dd'T'HH:mm:ssXXX");

	/**
	 * Converts RFC 3339 compliant date string to date object. If non compliant
	 * string is passed, issue is logged and null is returned. If negative UTC
	 * timezone (-00:00) is passed, UTC time zone is assumed.
	 * 
	 * @param dateString an RFC 3339 date string.
	 * @return a date object representing time in date string, or null if not in RFC
	 *         3339 format.
	 */
	public static Date toRFC3339(String dateString) {
		if (StringUtils.isBlank(dateString)) return null;
		try {
			return Date.from(ZonedDateTime.parse(dateString, formatter).toInstant());
		} catch (DateTimeParseException e) {
			LOGGER.warn("Could not parse date from string '{}'", dateString, e);
			return null;
		}
	}
	
	/**
	 * Converts passed date to RFC 3339 compliant date string. Time is adjusted to
	 * be in UTC time.
	 * 
	 * @param date the date object to convert to RFC 3339 format.
	 * @return the RFC 3339 format date string.
	 */
	public static String toRFC3339(Date date) {
		if (date == null) return null;
		return formatter.format(date.toInstant().atZone(ZoneId.of("UTC")));
	}

	// hide constructor
	private DateTimeHelper() {
	}
}
