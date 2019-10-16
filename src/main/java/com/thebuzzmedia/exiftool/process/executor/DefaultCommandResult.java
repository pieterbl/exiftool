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

import com.thebuzzmedia.exiftool.process.CommandResult;

import java.util.Objects;

import static java.lang.String.format;

/**
 * Default result object.
 *
 * <br>
 *
 * Result is defined by:
 *
 * <ul>
 *   <li>An exit status: a zero means a success, otherwise it is a failure.</li>
 *   <li>Command output.</li>
 * </ul>
 *
 * <strong>Note:</strong> this implementation is immutable and thread safe.
 */
public final class DefaultCommandResult implements CommandResult {

	/**
	 * Exit status, result of command execution.
	 */
	private final int exitStatus;

	/**
	 * Standard output.
	 */
	private final String output;

	/**
	 * Create new result.
	 *
	 * @param exitStatus Exit status.
	 * @param output Standard output.
	 */
	public DefaultCommandResult(int exitStatus, String output) {
		this.exitStatus = exitStatus;
		this.output = output;
	}

	@Override
	public int getExitStatus() {
		return exitStatus;
	}

	@Override
	public String getOutput() {
		return output;
	}

	@Override
	public boolean isSuccess() {
		return exitStatus == 0;
	}

	@Override
	public boolean isFailure() {
		return exitStatus != 0;
	}

	@Override
	public String toString() {
		return format("[%s] %s", exitStatus, output);
	}

	@Override
	public boolean equals(Object o) {
		if (o == this) {
			return true;
		}

		if (o instanceof DefaultCommandResult) {
			DefaultCommandResult r = (DefaultCommandResult) o;
			return Objects.equals(exitStatus, r.exitStatus) && Objects.equals(output, r.output);
		}

		return false;
	}

	@Override
	public int hashCode() {
		return Objects.hash(exitStatus, output);
	}
}
