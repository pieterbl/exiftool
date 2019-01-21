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

import com.thebuzzmedia.exiftool.core.schedulers.DefaultScheduler;
import com.thebuzzmedia.exiftool.core.schedulers.NoOpScheduler;
import com.thebuzzmedia.exiftool.core.strategies.DefaultStrategy;
import com.thebuzzmedia.exiftool.core.strategies.PoolStrategy;
import com.thebuzzmedia.exiftool.core.strategies.StayOpenStrategy;
import com.thebuzzmedia.exiftool.logs.Logger;
import com.thebuzzmedia.exiftool.logs.LoggerFactory;
import com.thebuzzmedia.exiftool.process.CommandExecutor;
import com.thebuzzmedia.exiftool.process.executor.CommandExecutors;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import static com.thebuzzmedia.exiftool.core.schedulers.SchedulerDuration.millis;
import static com.thebuzzmedia.exiftool.process.executor.CommandExecutors.newExecutor;

/**
 * Builder for {@link ExifTool} instance.
 * This builder should be used to create instance of {@link com.thebuzzmedia.exiftool.ExifTool}.
 *
 * <h3>Settings</h3>
 *
 * <h4>Path</h4>
 *
 * Set the absolute withPath to the ExifTool executable on the host system running
 * this class as defined by the `exiftool.withPath` system property.
 *
 * If set, value will be used, otherwise, withPath will be read:
 * <ul>
 *   <li>
 *     From system property. This system property can be set on startup
 *     with {@code -Dexiftool.withPath=/withPath/to/exiftool} or by
 *     calling {@link System#setProperty(String, String)} before
 *     this class is loaded.
 *   </li>
 *   <li>
 *     Default value is {@code exiftool}. In this case, {@code exiftool}
 *     command must be globally available.
 *   </li>
 * </ul>
 *
 * If ExifTool is on your system withPath and running the command {@code exiftool}
 * successfully executes it, leaving this value unchanged will work fine on
 * any platform. If the ExifTool executable is named something else or not
 * in the system withPath, then this property will need to be set to point at it
 * before using this class.
 *
 * On Windows be sure to double-escape the withPath to the tool,
 * for example: {@code -Dexiftool.withPath=C:\\Tools\\exiftool.exe}.
 *
 * Default value is {@code exiftool}.
 *
 * Relative withPath values (e.g. {@code bin/tools/exiftool}) are executed with
 * relation to the base directory the VM process was started in. Essentially
 * the directory that {@code new File(".").getAbsolutePath()} points at
 * during runtime.
 *
 * <h4>Executor</h4>
 *
 * Executor is the component responsible for executing command line on the
 * system. Most of the time, the default should be fine, but if you want to tune
 * the used withExecutor, then this property is for you.
 * Custom withExecutor must implement {@link com.thebuzzmedia.exiftool.process.CommandExecutor} interface.
 *
 * <h4>Stay Open Strategy</h4>
 *
 * ExifTool <a href="http://u88.n24.queensu.ca/exiftool/forum/index.php/topic,1402.msg12933.html#msg12933">8.36</a>
 * added a new persistent-process feature that allows ExifTool to stay
 * running in a daemon mode and continue accepting commands via a file or stdin.
 * This feature is disabled by default.
 *
 * <strong>NOTE:</strong> If {@code stay_open} flag is enabled, then an
 * instance of {@link com.thebuzzmedia.exiftool.exceptions.UnsupportedFeatureException}
 * may be thrown during ExifTool creation.
 *
 * If this exception occurs, then you should probably:
 * <ul>
 *   <li>Update your ExifTool version.</li>
 *   <li>Create new ExifTool without this feature.</li>
 * </ul>.
 *
 * <strong>Usage:</strong>
 *
 * <pre><code>
 *     final ExifTool exifTool;
 *     try {
 *         exifTool = new ExifToolBuilder()
 *             .enableStayOpen()
 *             .build();
 *     }
 *     catch (UnsupportedFeatureException ex) {
 *         exifTool = new ExifToolBuilder().build();
 *     }
 * </code></pre>
 *
 * <h4>Custom Strategies</h4>
 *
 * If default strategies are not enough, you can easily provide your own using
 * the {@link #withStrategy} method.
 *
 * <strong>Usage:</strong>
 *
 * <pre><code>
 *   ExifTool exifTool = new ExifToolBuilder()
 *     .withStrategy(new MyCustomStrategy())
 *     .build();
 * </code></pre>
 */
public class ExifToolBuilder {

	/**
	 * Class Logger.
	 */
	private static final Logger log = LoggerFactory.getLogger(ExifToolBuilder.class);

	/**
	 * Function to get default path value.
	 */
	private static final PathFunction PATH = new PathFunction();

	/**
	 * Function to get default cleanup interval.
	 */
	private static final DelayFunction DELAY = new DelayFunction();

	/**
	 * Function to get default executor environment.
	 */
	private static final ExecutorFunction EXECUTOR = new ExecutorFunction();

	/**
	 * ExifTool path.
	 */
	private String path;

	/**
	 * ExifTool executor.
	 */
	private CommandExecutor executor;

	/**
	 * Check if {@code stay_open} flag should be enabled.
	 */
	private Boolean stayOpen;

	/**
	 * Cleanup Delay.
	 */
	private Long cleanupDelay;

	/**
	 * Custom execution strategy.
	 */
	private ExecutionStrategy strategy;

	/**
	 * Custom Scheduler.
	 */
	private Scheduler scheduler;

	/**
	 * Pool size.
	 */
	private int poolSize;

	public ExifToolBuilder() {
	}

	/**
	 * Override default path.
	 * Default path is defined by the environment property {@code exiftool.path} or is
	 * set with {@code exiftool} otherwise. Setting the path explicitly will disable automatic
	 * lookup.
	 *
	 * @param path New path.
	 * @return Current builder.
	 */
	public ExifToolBuilder withPath(String path) {
		log.debug("Set path: {}", path);
		this.path = path;
		return this;
	}

	/**
	 * Override default path.
	 * Default path is defined by the environment property {@code exiftool.path} or is
	 * set with {@code exiftool} otherwise. Setting the path explicitly will disable automatic
	 * lookup.
	 *
	 * <br>
	 *
	 * <strong>Note:</strong> If path is not an executable file, a warning
	 * will be logged but it will not fail.
	 *
	 * @param path New path.
	 * @return Current builder.
	 */
	public ExifToolBuilder withPath(File path) {
		log.debug("Set path: {}", path);

		if (!path.canExecute()) {
			log.warn("Executable {} is not executable, exiftool may fail later", path);
		}

		this.path = path.getAbsolutePath();
		return this;
	}

	/**
	 * Override default exifTool executor.
	 *
	 * @param executor New withExecutor.
	 * @return Current builder.
	 */
	public ExifToolBuilder withExecutor(CommandExecutor executor) {
		log.debug("Set withExecutor: {}", executor);
		this.executor = executor;
		return this;
	}

	/**
	 * Enable {@code stay_open} feature.
	 *
	 * @return Current builder.
	 */
	public ExifToolBuilder enableStayOpen() {
		log.debug("Enable 'stay_open' feature");

		// If strategy has already been defined, log a warning.
		if (strategy != null) {
			log.warn("A custom strategy is defined, enabling 'stay_open' feature will be ignored");
		}

		this.stayOpen = true;
		return this;
	}

	/**
	 * Enable {@code stay_open} feature.
	 *
	 * <strong>Note:</strong>
	 *
	 * <ul>
	 *   <li>
	 *     If {link #withStrategy} is called, then calling this method
	 *     is useless.
	 *   </li>
	 *   <li>
	 *     If {link #enableStayOpen(scheduler} is called, then calling this method is
	 *     useless.
	 *   </li>
	 * </ul>
	 *
	 * @param cleanupDelay Interval (in milliseconds) between automatic clean operation.
	 * @return Current builder.
	 */
	public ExifToolBuilder enableStayOpen(long cleanupDelay) {
		log.debug("Enable 'stay_open' feature");

		if (strategy != null) {
			log.warn("A custom strategy is defined, enabling 'stay_open' feature will be ignored");
		}

		if (scheduler != null) {
			log.warn("A custom scheduler is already defined, it will be ignored");
		}

		this.stayOpen = true;
		this.cleanupDelay = cleanupDelay;
		return this;
	}

	/**
	 * Enable {@code stay_open} feature and perform cleanup task using given {@code scheduler}.
	 *
	 * <strong>Note:</strong>
	 *
	 * <ul>
	 *   <li>If {@link #withStrategy} has already been called, then calling is useless.</li>
	 *   <li>
	 *     If {@link #enableStayOpen(long)} has already been called, then given {@code delay} will be
	 *     ignored and the specified scheduler will be used.
	 *   </li>
	 * </ul>
	 *
	 * @param scheduler Scheduler used to process automatic cleanup task..
	 * @return Current builder.
	 */
	public ExifToolBuilder enableStayOpen(Scheduler scheduler) {
		log.debug("Enable 'stay_open' feature");

		if (strategy != null) {
			log.warn("A custom strategy is defined, enabling 'stay_open' feature will be ignored");
		}
		if (cleanupDelay != null) {
			log.warn("Custom scheduler is defined, previous delay will be ignored");
		}

		this.stayOpen = true;
		this.scheduler = scheduler;
		return this;
	}

	/**
	 * Override default execution strategy.
	 *
	 * <strong>If {@link #enableStayOpen} has been called, then strategy associated with {@code stay_open} flag
	 * will be ignored.</strong>
	 *
	 * @param strategy Strategy.
	 * @return Current builder.
	 */
	public ExifToolBuilder withStrategy(ExecutionStrategy strategy) {
		log.debug("Overriding default strategy");

		if (stayOpen != null) {
			log.warn("Flag 'stay_open' has been enabled and you are overriding the default execution strategy.");
			log.warn("Enabling 'stay_open' feature will be ignored");
		}

		this.strategy = strategy;
		return this;
	}

	/**
	 * Override default execution strategy:
	 *
	 * <ul>
	 *   <li>a pool of {@link StayOpenStrategy} with a size of {@code poolSize} will be used.</li>
	 *   <li>Default scheduler instances will be used with a delay of {@code cleanupDelay}.</li>
	 * </ul>
	 *
	 * @param poolSize Pool size.
	 * @param cleanupDelay Cleanup delay for each scheduler of pool elements.
	 * @return Current builder.
	 */
	public ExifToolBuilder withPoolSize(int poolSize, long cleanupDelay) {
		log.debug("Overriding default strategy");

		if (poolSize > 0) {
			this.poolSize = poolSize;
			this.cleanupDelay = cleanupDelay;
		} else {
			log.warn("Pool size has been enabled with a value less or equal than zero, ignore it.");
		}

		return this;
	}

	/**
	 * Override default execution strategy:
	 *
	 * <ul>
	 *   <li>a pool of {@link StayOpenStrategy} with a size of {@code poolSize} will be used.</li>
	 *   <li>No cleanup scheduler will be used (use {@link #withPoolSize(int, long)} instead.</li>
	 * </ul>
	 *
	 * @param poolSize Pool size.
	 * @return Current builder.
	 */
	public ExifToolBuilder withPoolSize(int poolSize) {
		log.debug("Overriding default strategy");

		if (poolSize > 0) {
			this.poolSize = poolSize;
			this.cleanupDelay = 0L;
		} else {
			log.warn("Pool size has been enabled with a value less or equal than zero, ignore it.");
		}

		return this;
	}

	/**
	 * Create exiftool instance with previous settings.
	 *
	 * @return Exiftool instance.
	 */
	public ExifTool build() {
		String path = firstNonNull(this.path, PATH);
		CommandExecutor executor = firstNonNull(this.executor, EXECUTOR);
		ExecutionStrategy strategy = firstNonNull(this.strategy, new StrategyFunction(stayOpen, cleanupDelay, scheduler, poolSize));

		// Add some debugging information
		if (log.isDebugEnabled()) {
			log.debug("Create ExifTool instance:");
			log.debug(" - Path: {}", path);
			log.debug(" - Executor: {}", executor);
			log.debug(" - Strategy: {}", strategy);
			log.debug(" - StayOpen: {}", stayOpen);
		}

		return new ExifTool(path, executor, strategy);
	}

	/**
	 * Return first non null value:
	 * <ul>
	 * <li>If first parameter is not null, then it is returned.</li>
	 * <li>Otherwise, result of function is returned.</li>
	 * </ul>
	 *
	 * @param value First value.
	 * @param factory Function used to get non null value.
	 * @param <T> Type of values.
	 * @return Non null value.
	 */
	private static <T> T firstNonNull(T value, FactoryFunction<T> factory) {
		return value == null ? factory.apply() : value;
	}

	/**
	 * Interface to return values.
	 * This interface should be used by builder to lazily create
	 * default settings parameters.
	 *
	 * @param <T> Type of settings.
	 */
	private static interface FactoryFunction<T> {
		T apply();
	}

	/**
	 * Return the absolute path to the ExifTool executable on the host system running
	 * this class as defined by the {@code exiftool.path} system property.
	 *
	 * This system property can be set on startup with {@code -Dexiftool.path=/path/to/exiftool}
	 * or by calling {@link System#setProperty(String, String)} before
	 * this class is loaded.
	 *
	 * On Windows be sure to double-escape the path to the tool,
	 * for example: {@code -Dexiftool.path=C:\\Tools\\exiftool.exe}.
	 *
	 * Default value is {@code exiftool}.
	 */
	private static class PathFunction implements FactoryFunction<String> {
		@Override
		public String apply() {
			return System.getProperty("exiftool.path", "exiftool");
		}
	}

	/**
	 * Return the interval (in milliseconds) of inactivity before the cleanup thread wakes
	 * up and cleans up the daemon ExifTool process and the read/write streams
	 * used to communicate with it when the {@code stay_open} feature is
	 * used.
	 *
	 * Ever time a call to {@link ExifTool#getImageMeta} is processed, the timer
	 * keeping track of cleanup is reset; more specifically, this class has to
	 * experience no activity for this duration of time before the cleanup
	 * process is fired up and cleans up the host OS process and the stream
	 * resources.
	 *
	 * Any subsequent calls to {@link ExifTool#getImageMeta} after a cleanup simply
	 * re-initializes the resources.
	 *
	 * This system property can be set on startup with {@code -Dexiftool.processCleanupDelay=600000}
	 * or by calling {@link System#setProperty(String, String)} before
	 * this class is loaded.
	 *
	 * Setting this value to 0 disables the automatic cleanup thread completely
	 * and the caller will need to manually cleanup the external ExifTool
	 * process and read/write streams by calling {@link ExifTool#close} method.
	 *
	 * Default value is {@code 600, 000} (10 minutes).
	 */
	private static class DelayFunction implements FactoryFunction<Long> {
		@Override
		public Long apply() {
			return Long.getLong("exiftool.processCleanupDelay", 600000);
		}
	}

	/**
	 * Returns the default executor for the created exifTool instance.
	 * Default executor is the result of {@link CommandExecutors#newExecutor()} method.
	 */
	private static class ExecutorFunction implements FactoryFunction<CommandExecutor> {
		@Override
		public CommandExecutor apply() {
			return newExecutor();
		}
	}

	/**
	 * Create scheduler used to perform automatic cleanup task.
	 * Default scheduler will depend on the given {@code delay}:
	 * <ul>
	 * <li>If {@code delay} is less than or equal to zero, then an instance of {@link NoOpScheduler} will be returned.</li>
	 * <li>If {@code delay} is greater than zero, then an instance of {@link DefaultScheduler} will be returned.</li>
	 * </ul>
	 */
	private static class SchedulerFunction implements FactoryFunction<Scheduler> {
		private final Long delay;

		public SchedulerFunction(Long delay) {
			this.delay = delay;
		}

		@Override
		public Scheduler apply() {
			// Otherwise, this is the StayOpen strategy.
			// We have to look up the delay between automatic clean and create
			// the scheduler.
			final long delay = firstNonNull(this.delay, DELAY);
			return delay > 0 ? new DefaultScheduler(millis(delay)) : new NoOpScheduler();
		}
	}

	/**
	 * Returns the {@link ExecutionStrategy} to use with {@link ExifTool} instances.
	 *
	 * <h3>Default</h3>
	 * By default, a really simple strategy is used (instance of {@link DefaultStrategy}).
	 *
	 * <h3>StayOpen</h3>
	 * If the {@code stay_open} has been enabled, then an instance of {@link StayOpenStrategy}
	 * will be created. For this strategy, a scheduler will be created. This scheduler will be used to run
	 * a task to clean resources used by this strategy. This task will run automatically after a specified
	 * delay.
	 */
	private static class StrategyFunction implements FactoryFunction<ExecutionStrategy> {
		private final Boolean stayOpen;

		private final Long delay;

		private final Scheduler scheduler;

		private int poolSize;

		public StrategyFunction(Boolean stayOpen, Long delay, Scheduler scheduler, int poolSize) {
			this.stayOpen = stayOpen;
			this.delay = delay;
			this.scheduler = scheduler;
			this.poolSize = poolSize;
		}

		@Override
		public ExecutionStrategy apply() {
			// First, try the pool strategy.
			if (poolSize > 0) {
				List<ExecutionStrategy> strategies = new ArrayList<>(poolSize);
				for (int i = 0; i < poolSize; i++) {
					Scheduler scheduler = new SchedulerFunction(delay).apply();
					StayOpenStrategy strategy = new StayOpenStrategy(scheduler);
					strategies.add(strategy);
				}

				return new PoolStrategy(strategies);
			}

			// Try the stayOpen strategy.
			if (stayOpen != null && stayOpen) {
				return new StayOpenStrategy(firstNonNull(scheduler, new SchedulerFunction(delay)));
			}

			// Simple use case: nothing has been parametrized, so
			// just return the default strategy.
			return new DefaultStrategy();
		}
	}
}
