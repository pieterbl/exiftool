/**
 * Copyright 2011 The Buzz Media, LLC
 * Copyright 2015 Mickael Jeanroy <mickael.jeanroy@gmail.com>
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

import java.io.File;

import static java.util.Arrays.asList;

public class Example {
	public static void main(String[] args) throws Exception {

		ExifTool tool = new ExifToolBuilder()
			.enableStayOpen()
			.build();

		File directory = new File("src/test/resources/images");
		File[] files = directory.listFiles();
		System.out.println("Images to scan: ");
		for (File f : files) {
			System.out.println("  - " + f.getName());
		}

		for (File f : files) {
			System.out.println("\n[" + f.getName() + "]");
			System.out.println(tool.getImageMeta(f, StandardFormat.HUMAN_READABLE, asList((Tag[]) StandardTag.values())));
		}

		tool.close();
	}
}