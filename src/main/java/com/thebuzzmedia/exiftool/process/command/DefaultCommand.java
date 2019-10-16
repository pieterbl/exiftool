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

package com.thebuzzmedia.exiftool.process.command;

import com.thebuzzmedia.exiftool.process.Command;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static com.thebuzzmedia.exiftool.commons.iterables.Collections.isEmpty;
import static com.thebuzzmedia.exiftool.commons.iterables.Collections.join;
import static com.thebuzzmedia.exiftool.commons.iterables.Collections.size;
import static java.util.Collections.unmodifiableList;

/**
 * Default implementation for {@link Command} interface.
 * This implementation should only be used with {@link com.thebuzzmedia.exiftool.process.command.CommandBuilder} builder.
 */
public final class DefaultCommand implements Command {

	/**
	 * List of arguments:
	 * <ul>
	 *   <li>First element is the executable.</li>
	 *   <li>Next elements are the executable arguments (optional).</li>
	 * </ul>
	 *
	 * Once created, this list will be unmodifiable.
	 */
	private final List<String> cmd;

	/**
	 * Create command line.
	 *
	 * @param executable Executable value.
	 * @param arguments List of optional arguments.
	 */
	public DefaultCommand(String executable, List<String> arguments) {
		List<String> args = new ArrayList<>(size(arguments) + 1);

		// Add first argument (should always be executable argument).
		args.add(executable);

		// Add optional arguments.
		if (!isEmpty(arguments)) {
			args.addAll(arguments);
		}

		this.cmd = unmodifiableList(args);
	}

	@Override
	public List<String> getArguments() {
		return cmd;
	}

	@Override
	public String toString() {
		return join(cmd, " ");
	}

	@Override
	public boolean equals(Object o) {
		if (o == this) {
			return true;
		}

		if (o instanceof DefaultCommand) {
			DefaultCommand c = (DefaultCommand) o;
			return Objects.equals(cmd, c.cmd);
		}

		return false;
	}

	@Override
	public int hashCode() {
		return Objects.hash(cmd);
	}
}
