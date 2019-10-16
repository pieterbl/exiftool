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

package com.thebuzzmedia.exiftool.commons.iterables;

/**
 * Mapper interface.
 * Define a transformation between an input of type T to an
 * output of type U.
 *
 * @param <T> Input type.
 * @param <U> Output type.
 */
public interface Mapper<T, U> {

	/**
	 * Map input to given output.
	 *
	 * @param input Input.
	 * @return Output.
	 */
	U map(T input);
}
