/* Copyright (c) 2019 Eclipse Foundation and others.
 * This program and the accompanying materials are made available
 * under the terms of the Eclipse Public License 2.0
 * which is available at http://www.eclipse.org/legal/epl-v20.html,
 * SPDX-License-Identifier: EPL-2.0
 */
package org.eclipsefoundation.core.helper;

import java.time.ZoneId;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;
import java.util.concurrent.TimeUnit;

import org.eclipsefoundation.core.helper.DateTimeHelper;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import io.quarkus.test.junit.QuarkusTest;

/**
 * Test class for {@linkplain DateTimeHelper}
 * 
 * @author Martin Lowe
 */
@QuarkusTest
public class DateTimeHelperTest {

	@Test
	public void validRFC3339DateStringIn() {
		// Test standard time
		String dateString = "1996-12-19T16:39:57-08:00";
		// Timezone needs to be set as otherwise it defaults to system local offset.
		TimeZone tz = TimeZone.getTimeZone(ZoneId.of("GMT-08:00"));
		Calendar c = Calendar.getInstance(tz);

		Date d = DateTimeHelper.toRFC3339(dateString);
		c.setTime(d);

		Assertions.assertEquals(1996, c.get(Calendar.YEAR));
		// 0 based month
		Assertions.assertEquals(11, c.get(Calendar.MONTH));
		Assertions.assertEquals(19, c.get(Calendar.DATE));
		Assertions.assertEquals(16, c.get(Calendar.HOUR_OF_DAY));
		Assertions.assertEquals(39, c.get(Calendar.MINUTE));
		Assertions.assertEquals(57, c.get(Calendar.SECOND));
		Assertions.assertEquals(-TimeUnit.MILLISECONDS.convert(8, TimeUnit.HOURS),
				c.get(Calendar.ZONE_OFFSET) + c.get(Calendar.DST_OFFSET));

		// Test unknown offset time
		String unknownOffsetDateString = "1996-12-19T16:39:57-00:00";
		c = Calendar.getInstance(TimeZone.getTimeZone(ZoneId.of("UTC")));

		// set calendar to actual value for easy reading
		d = DateTimeHelper.toRFC3339(unknownOffsetDateString);
		c.setTime(d);

		System.out.println(c.toInstant().toString());
		Assertions.assertEquals(1996, c.get(Calendar.YEAR));
		// 0 based month
		Assertions.assertEquals(11, c.get(Calendar.MONTH));
		Assertions.assertEquals(19, c.get(Calendar.DATE));
		Assertions.assertEquals(16, c.get(Calendar.HOUR_OF_DAY));
		Assertions.assertEquals(39, c.get(Calendar.MINUTE));
		Assertions.assertEquals(57, c.get(Calendar.SECOND));
		Assertions.assertEquals(0, c.get(Calendar.ZONE_OFFSET) + c.get(Calendar.DST_OFFSET));
	}

	@Test
	public void validRFC3339DateStringOut() {
		// Set a time equal to "1996-12-19T16:39:57-08:00"
		// Timezone needs to be set as otherwise it defaults to system local offset.
		TimeZone tz = TimeZone.getTimeZone(ZoneId.of("GMT-08:00"));
		// create a calendar instance to represent the given date
		Calendar c = Calendar.getInstance(tz);
		c.set(1996, 11, 19, 16, 39, 57);

		// expect UTC time in return
		String expected = "1996-12-20T00:39:57Z";
		String actual = DateTimeHelper.toRFC3339(c.getTime());

		Assertions.assertEquals(expected, actual);
	}

	@Test
	public void invalidRFC3339DateStringIn() {
		// test various permutations of the date string to ensure format enforcement
		Assertions.assertNull(DateTimeHelper.toRFC3339("19991220"),
				"Expected no returned date object for string: 19991220");
		Assertions.assertNull(DateTimeHelper.toRFC3339("1996/12/20"),
				"Expected no returned date object for string: 1996/12/20");
		Assertions.assertNull(DateTimeHelper.toRFC3339("1996-12-20"),
				"Expected no returned date object for string: 1996-12-20");
		Assertions.assertNull(DateTimeHelper.toRFC3339("1996-12-20 16:39:57-08:00"),
				"Expected no returned date object for string: 1996-12-20 16:39:57-08:00");
		Assertions.assertNull(DateTimeHelper.toRFC3339("1996-12-20T16:39:57"),
				"Expected no returned date object for string: 1996-12-20T16:39:57");
		Assertions.assertNull(DateTimeHelper.toRFC3339("1996-12-20T16:39:57-0800"),
				"Expected no returned date object for string: 1996-12-20T16:39:57-0800");
	}
}
