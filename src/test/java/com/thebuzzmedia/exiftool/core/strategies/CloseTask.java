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

package com.thebuzzmedia.exiftool.core.strategies;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Task that will close {@link com.thebuzzmedia.exiftool.ExecutionStrategy} when
 * lock is released.
 */
class CloseTask implements Runnable {
	private static final Logger log = LoggerFactory.getLogger(CloseTask.class);

	/**
	 * Task identifier, used for debug logging.
	 */
	private final int id;

	/**
	 * Pool strategy.
	 */
	private final PoolStrategy pool;

	/**
	 * Exception that may be thrown during {@link #pool#close()} operation.
	 */
	private Exception thrown;

	/**
	 * Create task.
	 *
	 * @param id Identifier.
	 * @param pool Pool.
	 */
	CloseTask(int id, PoolStrategy pool) {
		this.id = id;
		this.pool = pool;
	}

	@Override
	public void run() {
		try {
			log.debug("Closing pool from task #{}", id);
			pool.close();
		}
		catch (Exception ex) {
			log.debug("Close operation from task #{} throw exception", id);
			thrown = ex;
		}
	}

	public Exception getThrown() {
		return thrown;
	}
}
