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

import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class ObjectsTest {

	@Test
	public void it_should_return_first_parameter_if_it_is_not_null() {
		String v1 = "foo";
		String v2 = "bar";

		String foo = Objects.firstNonNull(v1, v2);

		assertThat(foo).isEqualTo(v1);
	}

	@Test
	public void it_should_return_second_parameter_if_first_is_null() {
		String v1 = null;
		String v2 = "bar";

		String foo = Objects.firstNonNull(v1, v2);

		assertThat(foo).isEqualTo(v2);
	}

	@Test
	public void it_should_return_other_first_non_null_parameter() {
		String v1 = null;
		String v2 = null;
		String o1 = null;
		String o2 = "foo";
		String o3 = "bar";

		String foo = Objects.firstNonNull(v1, v2, o1, o2, o3);

		assertThat(foo).isEqualTo(o2);
	}
}
