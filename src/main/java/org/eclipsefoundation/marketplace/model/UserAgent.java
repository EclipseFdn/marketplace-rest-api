/* Copyright (c) 2019 Eclipse Foundation and others.
 * This program and the accompanying materials are made available
 * under the terms of the Eclipse Public License 2.0
 * which is available at http://www.eclipse.org/legal/epl-v20.html,
 * SPDX-License-Identifier: EPL-2.0
 */
package org.eclipsefoundation.marketplace.model;

import java.util.List;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang3.StringUtils;
import org.eclipsefoundation.marketplace.dto.Install;

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
			.compile("^(\\S+\\/\\S+)\\s?(?:\\(([^\\)]*?)\\)(?:([^\\(]+)(?:\\(([^\\)]*+)\\))?)?)?$");
	private static final String MPC_CLIENT_AGENT_NAME = "mpc";

	private final String base;

	private final String agentDeclaration;
	private final String systemProperties;
	private final String platformDetails;
	private final String application;

	private String name;
	private String version;
	private String javaVersion;
	private String javaVendor;

	private String os;
	private String osVersion;
	private String locale;

	private String product;
	private String productVersion;
	private String eclipseVersion;

	private boolean valid = true;

	public UserAgent(String userAgent) {
		Objects.requireNonNull(userAgent);

		// check that the user agent matches the expected standard pattern
		Matcher m = USER_AGENT_PATTERN.matcher(userAgent);
		if (!m.matches()) {
			throw new IllegalArgumentException("Passed string does not match an expected user-agent");
		}

		this.base = userAgent;
		// get the name and version of the user agent
		this.agentDeclaration = m.group(1);
		List<String> agentProperties = Splitter.on('/').trimResults().limit(2).splitToList(agentDeclaration);
		if (agentProperties.size() != 2) {
			// should never throw as format is promised in regex
			throw new IllegalArgumentException("Cannot read User-Agent name and version");
		}
		this.name = agentProperties.get(0);
		this.version = agentProperties.get(1);

		this.systemProperties = m.group(2);
		this.platformDetails = m.group(3);
		this.application = m.group(4);
		if (isFromMPC()) {
			consumeSystemProps();
			consumePlatformDetails();
		}
	}

	private void consumeSystemProps() {
		if (this.systemProperties == null) {
			this.valid = false;
			return;
		}
		// expected form: (Java <java version> <java vendor>; <os name> <os version> <os
		// arch>; <locale>)
		List<String> systemPropList = Splitter.on(';').trimResults().splitToList(systemProperties);
		if (systemPropList.size() != 3) {
			this.valid = false;
			return;
		}
		// expected form example: Java <java version> <vendor>
		List<String> javaProps = Splitter.on(' ').trimResults().limit(3).splitToList(systemPropList.get(0));
		if (javaProps.size() != 3) {
			this.valid = false;
			return;
		}
		this.javaVersion = javaProps.get(1);
		this.javaVendor = javaProps.get(2);

		// expected form: <OS name> <OS version> <OS arch>
		List<String> systemProps = Splitter.on(' ').trimResults().limit(3).splitToList(systemPropList.get(1));
		if (systemProps.size() != 3) {
			this.valid = false;
			return;
		}
		this.os = systemProps.get(0);
		this.osVersion = systemProps.get(1);

		// get the current locale
		this.locale = systemPropList.get(2);

		// check if any fields are invalid
		if (StringUtils.isBlank(javaVersion) || StringUtils.isBlank(javaVendor) || StringUtils.isBlank(os)
				|| StringUtils.isBlank(locale)) {
			this.valid = false;
		}
	}

	private void consumePlatformDetails() {
		if (this.platformDetails == null) {
			this.valid = false;
			return;
		}
		// expected form: <eclipse product>/<product version>/<platform version>
		List<String> platformProps = Splitter.on('/').trimResults().limit(3).splitToList(platformDetails);
		if (platformProps.size() != 3) {
			this.valid = false;
			return;
		}
		// get the properties and check if any fields are invalid
		this.product = platformProps.get(0);
		this.productVersion = platformProps.get(1);
		this.eclipseVersion = platformProps.get(2);
		if (StringUtils.isBlank(product) || StringUtils.isBlank(productVersion)
				|| StringUtils.isBlank(eclipseVersion)) {
			this.valid = false;
		}
	}

	/**
	 * Generates a basic install record based on information based on the user agent
	 * properties. This can only be used when the agent is detected as an MPC call
	 * and the object is valid.
	 * 
	 * @return a basic populated install record without listing information, or null
	 *         if the call doesn't originate from MPC or is missing information.
	 */
	public Install generateInstallRecord() {
		// check that agent comes from MPC and is valid before generating
		if (!isValid()) {
			return null;
		}
		// generate install record from fields
		Install install = new Install();
		install.setJavaVersion(javaVersion);
		install.setLocale(locale);
		install.setOs(os);
		install.setEclipseVersion(eclipseVersion);
		return install;
	}

	/**
	 * @return the base user agent string
	 */
	public String getBase() {
		return base;
	}

	/**
	 * @return the systemProperties
	 */
	public String getSystemProperties() {
		return systemProperties;
	}

	/**
	 * @return the agentDeclaration
	 */
	public String getAgentDeclaration() {
		return agentDeclaration;
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
	public String getApplication() {
		return application;
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

	/**
	 * @return the product
	 */
	public String getProduct() {
		return product;
	}

	/**
	 * @return the productVersion
	 */
	public String getProductVersion() {
		return productVersion;
	}

	/**
	 * Checks whether the clients agent name matches expected value for the
	 * marketplace client {@link MPC_CLIENT_AGENT_NAME}.
	 * 
	 * @return true if client agent name matches expected value, false otherwise.
	 */
	public boolean isFromMPC() {
		return MPC_CLIENT_AGENT_NAME.equalsIgnoreCase(name);
	}

	/**
	 * @return whether the current user agent is a valid MPC user agent
	 */
	public boolean isValid() {
		return valid && isFromMPC();
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("UserAgent [name=");
		builder.append(name);
		builder.append(", version=");
		builder.append(version);
		builder.append(", javaVersion=");
		builder.append(javaVersion);
		builder.append(", javaVendor=");
		builder.append(javaVendor);
		builder.append(", os=");
		builder.append(os);
		builder.append(", osVersion=");
		builder.append(osVersion);
		builder.append(", locale=");
		builder.append(locale);
		builder.append(", eclipseVersion=");
		builder.append(eclipseVersion);
		builder.append(", product=");
		builder.append(product);
		builder.append(", productVersion=");
		builder.append(productVersion);
		builder.append(", application=");
		builder.append(application);
		builder.append("]");
		return builder.toString();
	}

}
