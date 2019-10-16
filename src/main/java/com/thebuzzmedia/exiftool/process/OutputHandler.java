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

package com.thebuzzmedia.exiftool.process;

import com.thebuzzmedia.exiftool.commons.io.StreamVisitor;

/**
 * Handler that should be used to handle command line output.
 *
 * <br>
 *
 * Each line is give to the {@link #readLine(String)} method.
 * This method should return:
 * <ul>
 *   <li>
 *     {@code true} if next line should be read. For instance, if current line is {@code null}, it probably
 *     means that no more output is available. This may let handlers to implement a custom
 *     logic.
 *   </li>
 *   <li>{@code false} if next line should not be read (end of output).</li>
 * </ul>
 */
public interface OutputHandler extends StreamVisitor {

	/**
	 * Read a line from command output.
	 * Returned value is a boolean: it should indicate if next line should be
	 * read or if output is finished.
	 *
	 * @param line Line output.
	 * @return Boolean indicating if next line should be read.
	 */
	boolean readLine(String line);
}
