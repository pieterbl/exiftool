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

import com.thebuzzmedia.exiftool.tests.TestConstants;
import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class ResultHandlerTest {

	@Test
	public void it_should_handle_result() {
		ResultHandler handler = new ResultHandler();
		assertThat(handler.getOutput()).isEmpty();

		handler.readLine("foo");
		assertThat(handler.getOutput())
				.isNotEmpty()
				.isEqualTo("foo");

		handler.readLine("bar");
		assertThat(handler.getOutput())
				.isNotEmpty()
				.isEqualTo(
						"foo" + TestConstants.BR + "bar"
				);
	}
}
