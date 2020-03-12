/* Copyright (c) 2019 Eclipse Foundation and others.
 * This program and the accompanying materials are made available
 * under the terms of the Eclipse Public License 2.0
 * which is available at http://www.eclipse.org/legal/epl-v20.html,
 * SPDX-License-Identifier: EPL-2.0
 */
package org.eclipsefoundation.core.config;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.ServiceLoader;

import org.apache.commons.lang3.StringUtils;
import org.eclipse.microprofile.config.spi.ConfigSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Reads in a set of secret configuration values from the secret.properties file
 * in the resources folder. These values are only secret in that they are set to
 * be ignored by Git and should be set on a per-server basis.
 * 
 * ConfigSource implementation was used to enable the usage of the
 * {@link ServiceLoader} to load the configurations.
 * 
 * @author Martin Lowe
 */
public class SecretConfigSource implements ConfigSource {
	private static final Logger LOGGER = LoggerFactory.getLogger(SecretConfigSource.class);
	
	private Map<String, String> secrets;

	@Override
	public Map<String, String> getProperties() {
		if (secrets == null) {
			this.secrets = new HashMap<>();
			String secretPath = System.getProperty("config.secret.path");
			// Fallback to checking env if not set in JVM
			if (StringUtils.isEmpty(secretPath)) {
				secretPath = System.getenv("CONFIG_SECRET_PATH");
			}
			if (StringUtils.isEmpty(secretPath)) {
				LOGGER.warn("Configuration 'config.secret.path' not set, cannot generate secret properties.");
				return this.secrets;
			}
			// load the secrets file in
			File f = new File(secretPath);
			if (!f.exists() || !f.canRead()) {
				LOGGER.error("File at path {} either does not exist or cannot be read", secretPath);
				return this.secrets;
			}

			// read each of the lines of secret config that should be added
			try (BufferedReader br = new BufferedReader(new FileReader(f))) {
				Properties p = new Properties();
				p.load(br);
				secrets.putAll((Map) p);

			} catch (IOException e) {
				LOGGER.error("Error while reading in secrets configuration file.", e);
			}
			LOGGER.debug("Found secret keys: {}", secrets.keySet());

			// add priority ordinal to map if missing. 260 ordinal sets the priority between
			// container and environment variable priority.
			secrets.computeIfAbsent(ConfigSource.CONFIG_ORDINAL, key -> "260");
		}
		return secrets;
	}

	@Override
	public String getValue(String propertyName) {
		return getProperties().get(propertyName);
	}

	@Override
	public String getName() {
		return "secret";
	}
}
