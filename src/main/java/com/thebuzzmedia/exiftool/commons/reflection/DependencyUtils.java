/**
 * Copyright 2011 The Buzz Media, LLC
 * Copyright 2015-2019 Mickael Jeanroy
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.thebuzzmedia.exiftool.commons.reflection;

/**
 * Static Dependency Utilities.
 */
public final class DependencyUtils {

	// Ensure non instantiation.
	private DependencyUtils() {
	}

	private static final String GUAVA_CACHE_FQN = "com.google.common.cache.Cache";
	private static final boolean GUAVA_AVAILABLE = ClassUtils.isPresent(GUAVA_CACHE_FQN);

	private static final String SLF4J_FQN = "org.slf4j.Logger";
	private static final boolean SLF4J_AVAILABLE = ClassUtils.isPresent(SLF4J_FQN);

	private static final String LOG4J_FQN = "org.apache.log4j.Logger";
	private static final boolean LOG4J_AVAILABLE = ClassUtils.isPresent(LOG4J_FQN);

	private static final String LOG4J2_FQN = "org.apache.logging.log4j.Logger";
	private static final boolean LOG4J2_AVAILABLE = ClassUtils.isPresent(LOG4J2_FQN);

	/**
	 * Check if Guava is available on the classpath.
	 *
	 * @return {@code true} if guava is available, {@code false} otherwise.
	 */
	public static boolean isGuavaAvailable() {
		return GUAVA_AVAILABLE;
	}

	/**
	 * Check if slf4j is available on the classpath.
	 *
	 * @return {@code true} if slf4j is available, {@code false} otherwise.
	 */
	public static boolean isSlf4jAvailable() {
		return SLF4J_AVAILABLE;
	}

	/**
	 * Check if log4j is available on the classpath.
	 *
	 * @return {@code true} if log4j is available, {@code false} otherwise.
	 */
	public static boolean isLog4jAvailable() {
		return LOG4J_AVAILABLE;
	}

	/**
	 * Check if log4j2 is available on the classpath.
	 *
	 * @return {@code true} if log4j2 is available, {@code false} otherwise.
	 */
	public static boolean isLog4j2Available() {
		return LOG4J2_AVAILABLE;
	}
}
