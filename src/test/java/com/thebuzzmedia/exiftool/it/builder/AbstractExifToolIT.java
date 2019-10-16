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

package com.thebuzzmedia.exiftool.it.builder;

import com.thebuzzmedia.exiftool.ExifTool;
import com.thebuzzmedia.exiftool.ExifToolBuilder;
import com.thebuzzmedia.exiftool.Version;
import com.thebuzzmedia.exiftool.exceptions.ExifToolNotFoundException;
import org.assertj.core.api.ThrowableAssert.ThrowingCallable;
import org.junit.Test;

import java.util.UUID;

import static com.thebuzzmedia.exiftool.tests.TestConstants.EXIF_TOOL;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

public abstract class AbstractExifToolIT {

	@Test
	public void it_should_get_version() {
		ExifTool exifTool = create()
				.withPath(EXIF_TOOL.getAbsolutePath())
				.build();

		Version version = exifTool.getVersion();

		assertThat(version.getMajor()).isEqualTo(10);
		assertThat(version.getMinor()).isEqualTo(16);
		assertThat(version.getPatch()).isEqualTo(0);
	}

	@Test
	public void it_should_fail_with_missing_exiftool() {
		final String path = "/" + UUID.randomUUID() + "/exiftool";
		final ExifToolBuilder builder = create().withPath(path);

		ThrowingCallable build = new ThrowingCallable() {
			@Override
			public void call() {
				builder.build();
			}
		};

		assertThatThrownBy(build)
				.isInstanceOf(ExifToolNotFoundException.class)
				.hasMessageContaining("Cannot run program \"" + path + "\"")
				.hasMessageEndingWith("error=2, No such file or directory");
	}

	abstract ExifToolBuilder create();
}
