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

package com.thebuzzmedia.exiftool.process.executor;

import com.thebuzzmedia.exiftool.Constants;
import com.thebuzzmedia.exiftool.process.OutputHandler;

/**
 * Simple command handler that just read output line by line
 * and append each one in a {@link StringBuilder} instance.
 * When current line is null, handler will return false.
 *
 * <p />
 *
 * <strong>Note:</strong> that this handler is not thread safe and should be
 * synchronized if needed.
 */
class ResultHandler implements OutputHandler {

	/**
	 * Current output.
	 */
	private final StringBuilder output;

	/**
	 * Create new handler.
	 */
	ResultHandler() {
		this.output = new StringBuilder();
	}

	@Override
	public boolean readLine(String line) {
		if (line != null) {
			if (output.length() > 0) {
				output.append(Constants.BR);
			}

			output.append(line);
		}

		return line != null;
	}

	/**
	 * Get full output.
	 *
	 * @return Command line output.
	 */
	public String getOutput() {
		return output.toString();
	}
}
