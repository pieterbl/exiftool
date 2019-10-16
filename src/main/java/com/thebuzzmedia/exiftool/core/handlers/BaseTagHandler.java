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

package com.thebuzzmedia.exiftool.core.handlers;

import com.thebuzzmedia.exiftool.Tag;
import com.thebuzzmedia.exiftool.logs.Logger;
import com.thebuzzmedia.exiftool.logs.LoggerFactory;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

import static com.thebuzzmedia.exiftool.core.handlers.StopHandler.stopHandler;
import static java.util.Collections.unmodifiableMap;

/**
 * Read tags line by line.
 *
 * <br>
 *
 * This class is not thread-safe and should be used to
 * read exiftool output from one thread (should not be shared across
 * several threads).
 */
public abstract class BaseTagHandler implements TagHandler {

	/**
	 * Class logger.
	 */
	private static final Logger log = LoggerFactory.getLogger(BaseTagHandler.class);

	/**
	 * Compiled {@link Pattern} of {@code ": "} used to split compact output from
	 * ExifTool evenly into name/value pairs.
	 */
	private static final Pattern TAG_VALUE_PATTERN = Pattern.compile(": ");

	/**
	 * Map of tags found.
	 * Each tags will be added one by one during line processing.
	 */
	private final Map<Tag, String> tags = new HashMap<>();

	@Override
	public boolean readLine(String line) {
		// If line is null, then this is the end.
		// If line is strictly equals to "{ready}", then it means that stay_open feature
		// is enabled and this is the end of the output.
		if (!stopHandler().readLine(line)) {
			return false;
		}

		// Now, we are sure we can process line.
		String[] pair = TAG_VALUE_PATTERN.split(line, 2);
		if (pair != null && pair.length == 2) {
			// Determine the tag represented by this value.
			String name = pair[0];
			String value = pair[1];

			final Tag tag = toTag(name);
			if (tag != null) {
				tags.put(tag, value);
				log.debug("Read Tag [name={}, value={}]", tag, value);
			}
			else {
				log.debug("Unable to read Tag: {}", line);
			}
		}
		else {
			log.warn("Skipped line: {}", line);
		}

		return true;
	}

	/**
	 * Get a {@link Tag} for the given exif name
	 * @param name the name of the tag
	 */
	abstract Tag toTag(String name);

	@Override
	public Map<Tag, String> getTags() {
		return unmodifiableMap(tags);
	}

	@Override
	public int size() {
		return tags.size();
	}
}
