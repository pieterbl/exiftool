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

package com.thebuzzmedia.exiftool;

import com.thebuzzmedia.exiftool.core.StandardFormat;
import com.thebuzzmedia.exiftool.core.StandardTag;
import com.thebuzzmedia.exiftool.core.UnspecifiedTag;
import com.thebuzzmedia.exiftool.exceptions.UnreadableFileException;
import com.thebuzzmedia.exiftool.process.Command;
import com.thebuzzmedia.exiftool.process.CommandExecutor;
import com.thebuzzmedia.exiftool.process.CommandResult;
import com.thebuzzmedia.exiftool.process.OutputHandler;
import com.thebuzzmedia.exiftool.tests.builders.CommandResultBuilder;
import com.thebuzzmedia.exiftool.tests.builders.FileBuilder;
import org.assertj.core.api.ThrowableAssert.ThrowingCallable;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.junit.MockitoJUnitRunner;
import org.mockito.stubbing.Answer;

import java.io.File;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.thebuzzmedia.exiftool.tests.MockitoTestUtils.anyListOf;
import static com.thebuzzmedia.exiftool.tests.TagTestUtils.parseTags;
import static java.util.Arrays.asList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.same;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.Silent.class)
public class ExifTool_getImageMeta_Test {

	private String path;

	@Mock
	private CommandExecutor executor;

	@Mock
	private ExecutionStrategy strategy;

	@Captor
	private ArgumentCaptor<List<String>> argsCaptor;

	private ExifTool exifTool;

	@Before
	public void setUp() throws Exception {
		path = "exiftool";

		CommandResult cmd = new CommandResultBuilder()
				.output("9.36")
				.build();

		when(executor.execute(any(Command.class))).thenReturn(cmd);
		when(strategy.isSupported(any(Version.class))).thenReturn(true);

		exifTool = new ExifTool(path, executor, strategy);

		reset(executor);
	}

	@Test
	public void it_should_fail_if_image_is_null() {
		ThrowingCallable getImageMeta = new ThrowingCallable() {
			@Override
			public void call() throws Throwable {
				exifTool.getImageMeta(null, StandardFormat.HUMAN_READABLE, asList((Tag[]) StandardTag.values()));
			}
		};

		assertThatThrownBy(getImageMeta)
				.isInstanceOf(NullPointerException.class)
				.hasMessage("Image cannot be null and must be a valid stream of image data.");
	}

	@Test
	public void it_should_fail_if_format_is_null() {
		ThrowingCallable getImageMeta = new ThrowingCallable() {
			@Override
			public void call() throws Throwable {
				exifTool.getImageMeta(mock(File.class), null, asList((Tag[]) StandardTag.values()));
			}
		};

		assertThatThrownBy(getImageMeta)
				.isInstanceOf(NullPointerException.class)
				.hasMessage("Format cannot be null.");
	}

	@Test
	public void it_should_fail_if_tags_is_null() {
		ThrowingCallable getImageMeta = new ThrowingCallable() {
			@Override
			public void call() throws Throwable {
				exifTool.getImageMeta(mock(File.class), StandardFormat.HUMAN_READABLE, null);
			}
		};

		assertThatThrownBy(getImageMeta)
				.isInstanceOf(NullPointerException.class)
				.hasMessage("Tags cannot be null and must contain 1 or more Tag to query the image for.");
	}

	@Test
	public void it_should_fail_if_tags_is_empty() {
		ThrowingCallable getImageMeta = new ThrowingCallable() {
			@Override
			public void call() throws Throwable {
				exifTool.getImageMeta(mock(File.class), StandardFormat.HUMAN_READABLE, Collections.<Tag>emptyList());
			}
		};

		assertThatThrownBy(getImageMeta)
				.isInstanceOf(IllegalArgumentException.class)
				.hasMessage("Tags cannot be null and must contain 1 or more Tag to query the image for.");
	}

	@Test
	public void it_should_fail_with_unknown_file() {
		final File image = new FileBuilder("foo.png").exists(false).build();

		ThrowingCallable getImageMeta = new ThrowingCallable() {
			@Override
			public void call() throws Throwable {
				exifTool.getImageMeta(image, StandardFormat.HUMAN_READABLE, asList((Tag[]) StandardTag.values()));
			}
		};

		assertThatThrownBy(getImageMeta)
				.isInstanceOf(UnreadableFileException.class)
				.hasMessage(
						"Unable to read the given image [/tmp/foo.png], " +
								"ensure that the image exists at the given withPath and that the " +
								"executing Java process has permissions to read it."
				);
	}

	@Test
	public void it_should_fail_with_non_readable_file() {
		final File image = new FileBuilder("foo.png").canRead(false).build();

		ThrowingCallable getImageMeta = new ThrowingCallable() {
			@Override
			public void call() throws Throwable {
				exifTool.getImageMeta(image, StandardFormat.HUMAN_READABLE, asList((Tag[]) StandardTag.values()));
			}
		};

		assertThatThrownBy(getImageMeta)
				.isInstanceOf(UnreadableFileException.class)
				.hasMessage(
						"Unable to read the given image [/tmp/foo.png], " +
								"ensure that the image exists at the given withPath and that the " +
								"executing Java process has permissions to read it."
				);
	}

	@Test
	public void it_should_get_image_metadata() throws Exception {
		// Given
		final Format format = StandardFormat.HUMAN_READABLE;
		final File image = new FileBuilder("foo.png").build();
		final Map<Tag, String> tags = new HashMap<>();
		tags.put(StandardTag.ARTIST, "bar");
		tags.put(StandardTag.COMMENT, "foo");

		doAnswer(new ReadTagsAnswer(tags, "{ready}"))
				.when(strategy).execute(same(executor), same(path), anyListOf(String.class), any(OutputHandler.class));

		// When
		Map<Tag, String> results = exifTool.getImageMeta(image, format, tags.keySet());

		// Then
		verify(strategy).execute(same(executor), same(path), argsCaptor.capture(), any(OutputHandler.class));

		assertThat(results)
				.isNotNull()
				.isNotEmpty()
				.hasSize(tags.size())
				.isEqualTo(tags);
	}

	@Test
	public void it_should_get_image_metadata_in_numeric_format() throws Exception {
		// Given
		final Format format = StandardFormat.NUMERIC;
		final File image = new FileBuilder("foo.png").build();
		final Map<Tag, String> tags = new HashMap<>();
		tags.put(StandardTag.ARTIST, "foo");
		tags.put(StandardTag.COMMENT, "bar");

		doAnswer(new ReadTagsAnswer(tags, "{ready}"))
				.when(strategy).execute(same(executor), same(path), anyListOf(String.class), any(OutputHandler.class));

		// When
		Map<Tag, String> results = exifTool.getImageMeta(image, format, tags.keySet());

		// Then
		verify(strategy).execute(same(executor), same(path), argsCaptor.capture(), any(OutputHandler.class));

		assertThat(results)
				.isNotNull()
				.isNotEmpty()
				.hasSize(tags.size())
				.isEqualTo(tags);
	}

	@Test
	public void it_should_get_image_metadata_in_numeric_format_by_default() throws Exception {
		// Given
		final File image = new FileBuilder("foo.png").build();
		final Map<Tag, String> tags = new HashMap<>();
		tags.put(StandardTag.ARTIST, "foo");
		tags.put(StandardTag.COMMENT, "bar");

		doAnswer(new ReadTagsAnswer(tags, "{ready}"))
				.when(strategy).execute(same(executor), same(path), anyListOf(String.class), any(OutputHandler.class));

		// When
		Map<Tag, String> results = exifTool.getImageMeta(image, tags.keySet());

		// Then
		verify(strategy).execute(same(executor), same(path), argsCaptor.capture(), any(OutputHandler.class));

		assertThat(results)
				.isNotNull()
				.isNotEmpty()
				.hasSize(tags.size())
				.isEqualTo(tags);
	}

	@Test
	public void it_should_get_all_image_metadata_if_no_tags_specified() throws Exception {
		// Given
		final Format format = StandardFormat.HUMAN_READABLE;
		final File image = new FileBuilder("foo.png").build();
		final Map<Tag, String> tags = new HashMap<>();
		tags.put(new UnspecifiedTag("Artist"), "bar");
		tags.put(new UnspecifiedTag("XPComment"), "foo");
		tags.put(new UnspecifiedTag("CustomTag"), "baz");

		doAnswer(new ReadTagsAnswer(tags, "{ready}"))
				.when(strategy).execute(same(executor), same(path), anyListOf(String.class), any(OutputHandler.class));

		// When
		Map<Tag, String> results = exifTool.getImageMeta(image, format);

		// Then
		verify(strategy).execute(same(executor), same(path), argsCaptor.capture(), any(OutputHandler.class));

		assertThat(results)
				.isNotNull()
				.isNotEmpty()
				.hasSize(tags.size());

		assertThat(parseTags(results))
				.containsAllEntriesOf(parseTags(tags));
	}

	@Test
	public void it_should_get_all_image_metadata_in_numeric_format_by_default() throws Exception {
		// Given
		final File image = new FileBuilder("foo.png").build();
		final Map<Tag, String> tags = new HashMap<>();
		tags.put(new UnspecifiedTag("Artist"), "foo");
		tags.put(new UnspecifiedTag("XPComment"), "bar");
		tags.put(new UnspecifiedTag("CustomTag"), "baz");

		doAnswer(new ReadTagsAnswer(tags, "{ready}"))
				.when(strategy).execute(same(executor), same(path), anyListOf(String.class), any(OutputHandler.class));

		// When
		Map<Tag, String> results = exifTool.getImageMeta(image);

		// Then
		verify(strategy).execute(same(executor), same(path), argsCaptor.capture(), any(OutputHandler.class));

		assertThat(results)
				.isNotNull()
				.isNotEmpty()
				.hasSize(tags.size());

		assertThat(parseTags(results))
				.containsAllEntriesOf(parseTags(tags));
	}

	private static class ReadTagsAnswer implements Answer<Void> {
		private final Map<Tag, String> tags;

		private final String end;

		private ReadTagsAnswer(Map<Tag, String> tags, String end) {
			this.tags = tags;
			this.end = end;
		}

		@Override
		public Void answer(InvocationOnMock invocation) {
			OutputHandler handler = (OutputHandler) invocation.getArguments()[3];

			// Read tags
			for (Map.Entry<Tag, String> entry : tags.entrySet()) {
				handler.readLine(entry.getKey().getName() + ": " + entry.getValue());
			}

			// Read last line
			handler.readLine(end);

			return null;
		}
	}
}
