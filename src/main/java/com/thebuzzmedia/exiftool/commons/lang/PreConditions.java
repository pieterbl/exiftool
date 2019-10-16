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

package com.thebuzzmedia.exiftool.commons.lang;

import com.thebuzzmedia.exiftool.exceptions.UnreadableFileException;
import com.thebuzzmedia.exiftool.exceptions.UnwritableFileException;

import java.io.File;
import java.util.Map;

import static java.lang.String.format;

/**
 * Static PreConditions Utilities.
 */
public final class PreConditions {

	// Ensure non instantiation.
	private PreConditions() {
	}

	/**
	 * Ensures that an object reference passed as a parameter to the calling method is not {@code null}.
	 *
	 * @param val     Value to check.
	 * @param message Message passed to {@link NullPointerException}.
	 * @param params  Message parameters (formatted with {@link String#format(String, Object...)}).
	 * @param <T>     Type of parameter.
	 * @return Original value if it is not {@code null}.
	 * @throws java.lang.NullPointerException If {@code val} is null.
	 */
	public static <T> T notNull(T val, String message, Object... params) {
		if (val == null) {
			throw new NullPointerException(errorMessage(message, params));
		}

		return val;
	}

	/**
	 * Ensures that a string is:
	 * <ul>
	 *   <li>Not {@code null}.</li>
	 *   <li>Not empty.</li>
	 *   <li>Not blank (i.e contains at least one character other than space).</li>
	 * </ul>
	 *
	 * @param val     Value to check.
	 * @param message Message passed to thrown exception.
	 * @param params  Message parameters (formatted with {@link String#format(String, Object...)}).
	 * @return Original value if it is not {@code null}.
	 * @throws java.lang.NullPointerException     If {@code val} is {@code null}.
	 * @throws java.lang.IllegalArgumentException If {@code val} is empty or blank.
	 */
	public static String notBlank(String val, String message, Object... params) {
		notNull(val, message, params);
		if (val.length() == 0 || val.trim().length() == 0) {
			throw new IllegalArgumentException(errorMessage(message, params));
		}

		return val;
	}

	/**
	 * Ensures that array is:
	 * <ul>
	 *   <li>Not {@code null}.</li>
	 *   <li>Not empty.</li>
	 * </ul>
	 *
	 * @param val     Value to check.
	 * @param message Message passed to thrown exception.
	 * @param params  Message parameters (formatted with {@link String#format(String, Object...)}).
	 * @param <T>     Type of elements in array.
	 * @return Original value if it is not empty.
	 * @throws java.lang.NullPointerException     If {@code val} is {@code null}.
	 * @throws java.lang.IllegalArgumentException If {@code val} is empty.
	 */
	public static <T> T[] notEmpty(T[] val, String message, Object... params) {
		notNull(val, message, params);
		if (val.length == 0) {
			throw new IllegalArgumentException(errorMessage(message, params));
		}

		return val;
	}

	/**
	 * Ensures that map is:
	 * <ul>
	 *   <li>Not {@code null}.</li>
	 *   <li>Not empty.</li>
	 * </ul>
	 *
	 * @param val     Value to check.
	 * @param message Message passed to thrown exception.
	 * @param params  Message parameters (formatted with {@link String#format(String, Object...)}).
	 * @param <T>     Type of keys in map.
	 * @param <U>     Type of values in map.
	 * @return Original value if it is not empty.
	 * @throws java.lang.NullPointerException     If {@code val} is {@code null}.
	 * @throws java.lang.IllegalArgumentException If {@code val} is empty.
	 */
	public static <T, U> Map<T, U> notEmpty(Map<T, U> val, String message, Object... params) {
		notNull(val, message, params);
		if (val.size() == 0) {
			throw new IllegalArgumentException(errorMessage(message, params));
		}

		return val;
	}

	/**
	 * Ensures that iterable element is:
	 * <ul>
	 *   <li>Not {@code null}.</li>
	 *   <li>Not empty.</li>
	 * </ul>
	 *
	 * @param val     Value to check.
	 * @param message Message passed to thrown exception.
	 * @param params  Message parameters (formatted with {@link String#format(String, Object...)}).
	 * @param <T>     Type of elements in iterable structure.
	 * @return Original value if it is not empty.
	 * @throws java.lang.NullPointerException     If {@code val} is {@code null}.
	 * @throws java.lang.IllegalArgumentException If {@code val} is empty.
	 */
	public static <T> Iterable<T> notEmpty(Iterable<T> val, String message, Object... params) {
		notNull(val, message, params);

		if (!val.iterator().hasNext()) {
			throw new IllegalArgumentException(errorMessage(message, params));
		}

		return val;
	}

	/**
	 * Check if given number is strictly positive (strictly greater than zero).
	 *
	 * @param nb      Number.
	 * @param message Error message.
	 * @param params  Message parameters (formatted with {@link String#format(String, Object...)}).
	 * @param <T>     Type of number.
	 * @return Original number.
	 * @throws NullPointerException     If {@code nb} is {@code null}.
	 * @throws IllegalArgumentException If {@code nb} is less than or equal to zero.
	 */
	public static <T extends Number> T isPositive(T nb, String message, Object... params) {
		notNull(nb, message, params);
		if (nb.doubleValue() <= 0) {
			throw new IllegalArgumentException(errorMessage(message, params));
		}

		return nb;
	}

	/**
	 * Check that a given file exist and is readable.
	 *
	 * @param file    File to check.
	 * @param message Error message.
	 * @param params  Message parameters (formatted with {@link String#format(String, Object...)}).
	 * @return Original file.
	 * @throws NullPointerException    If {@code file} is {@code null}.
	 * @throws UnreadableFileException If {@code file} does not exist.
	 * @throws UnreadableFileException If {@code file} cannot be read.
	 */
	public static File isReadable(File file, String message, Object... params) {
		notNull(file, message, params);
		if (!file.exists() || !file.canRead()) {
			throw new UnreadableFileException(file, errorMessage(message, params));
		}

		return file;
	}

	/**
	 * Check that a given file exist and is writable.
	 *
	 * @param file    File to check.
	 * @param message Error message.
	 * @param params  Message parameters (formatted with {@link String#format(String, Object...)}).
	 * @return Original file.
	 * @throws NullPointerException    If {@code file} is {@code null}.
	 * @throws UnreadableFileException If {@code file} does not exist.
	 * @throws UnreadableFileException If {@code file} cannot be updated.
	 */
	public static File isWritable(File file, String message, Object... params) {
		notNull(file, message, params);
		if (!file.exists() || !file.canWrite()) {
			throw new UnwritableFileException(file, errorMessage(message, params));
		}

		return file;
	}

	private static String errorMessage(String message, Object[] params) {
		return params.length > 0 ? format(message, params) : message;
	}
}
