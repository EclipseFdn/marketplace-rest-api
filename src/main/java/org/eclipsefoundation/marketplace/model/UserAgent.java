/* Copyright (c) 2019 Eclipse Foundation and others.
 * This program and the accompanying materials are made available
 * under the terms of the Eclipse Public License 2.0
 * which is available at http://www.eclipse.org/legal/epl-v20.html,
 * SPDX-License-Identifier: EPL-2.0
 */
package org.eclipsefoundation.marketplace.model;

import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.google.common.base.Splitter;

/**
 * Custom User-Agent capture that handles and stores information based on MPC
 * information.
 * 
 * @author Martin Lowe
 */
public class UserAgent {

	/**
	 * Matches a generic form of User-Agent strings with the following sections
	 * 
	 * <code>Agent-Name/Agent-Version (System-Properties;) Platform (Platform-Details) Enhancements</code>
	 * 
	 * As User-Agents aren't a standardized format, there is leniency on there being
	 * trailing sections being missing. The only required field is the Agent name
	 * and version, separated by a slash.
	 */
	private static final Pattern USER_AGENT_PATTERN = Pattern
			.compile("^(\\S+\\/\\S+)\\s?(?:\\(([^\\)]*?)\\)([^\\(]+(?:\\(([^\\)]*?)\\)([^\\(]+)?)?)?)?$");
	private static final String MPC_CLIENT_AGENT_NAME = "mpc";

	private final String name;
	private final String version;
	private final String systemProperties;
	private final String platform;
	private final String platformDetails;
	private final String enhancements;

	private String javaVersion;
	private String javaVendor;

	private String os;
	private String osVersion;
	private String locale;

	private String eclipseVersion;

	public UserAgent(String userAgent) {
		Objects.requireNonNull(userAgent);

		// check that the user agent matches the expected standard pattern
		Matcher m = USER_AGENT_PATTERN.matcher(userAgent);
		if (!m.matches()) {
			throw new IllegalArgumentException("Passed string does not match an expected user-agent");
		}

		// get the name and version of the user agent
		String agentDeclaration = m.group(0);
		Iterator<String> it = Splitter.on('/').trimResults().split(agentDeclaration).iterator();
		this.name = it.next();
		this.version = it.next();

		this.systemProperties = m.group(1);
		this.platform = m.group(2);
		this.platformDetails = m.group(3);
		this.enhancements = m.group(4);
		if (MPC_CLIENT_AGENT_NAME.equalsIgnoreCase(name)) {
			handleMpc();
		}
	}

	/**
	 * Breaks down the different MPC properties into explicit properties that can be
	 * retrieved via getters built into the class. The expected format is defined
	 * below:
	 * 
	 * <p>
	 * {@code mpc/<mpc version> (Java <java version> <java vendor>; <os name> <os version> <os arch>; <locale>) <eclipse product>/<product version> (<eclipse application>)}
	 * </p>
	 */
	private void handleMpc() {
		// expected form: (Java <java version> <java vendor>; <os name> <os version> <os
		// arch>; <locale>)
		List<String> systemPropList = Splitter.on(';').splitToList(systemProperties);
		if (systemPropList.size() != 3) {
			// TODO throw exception?
		}
		// expected form example: Java <java version> <vendor>
		List<String> javaProps = Splitter.on(' ').limit(3).splitToList(systemPropList.get(0));
		if (javaProps.size() != 3) {
			// TODO throw exception?
		}
		this.javaVersion = javaProps.get(1);
		this.javaVendor = javaProps.get(2);

		// expected form: <OS name> <OS version> <OS arch>
		List<String> systemProps = Splitter.on(' ').limit(3).splitToList(systemPropList.get(1));
		if (systemProps.size() != 3) {
			// TODO throw exception?
		}
		this.os = systemProps.get(0);
		this.osVersion = systemProps.get(1);

		// get the current locale
		this.locale = systemPropList.get(2);

		// expected form: <eclipse product>/<product version>
		List<String> platformProps = Splitter.on('/').limit(3).splitToList(platform);
	}

	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}

	/**
	 * @return the version
	 */
	public String getVersion() {
		return version;
	}

	/**
	 * @return the version
	 */
	public String getSystemProperties() {
		return systemProperties;
	}

	/**
	 * @return the platform
	 */
	public String getPlatform() {
		return platform;
	}

	/**
	 * @return the platformDetails
	 */
	public String getPlatformDetails() {
		return platformDetails;
	}

	/**
	 * @return the enhancements
	 */
	public String getEnhancements() {
		return enhancements;
	}

	/**
	 * @return the javaVersion
	 */
	public String getJavaVersion() {
		return javaVersion;
	}

	/**
	 * @return the javaVendor
	 */
	public String getJavaVendor() {
		return javaVendor;
	}

	/**
	 * @return the os
	 */
	public String getOs() {
		return os;
	}

	/**
	 * @return the osVersion
	 */
	public String getOsVersion() {
		return osVersion;
	}

	/**
	 * @return the locale
	 */
	public String getLocale() {
		return locale;
	}

}
