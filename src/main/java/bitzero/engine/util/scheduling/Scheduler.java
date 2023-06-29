package bitzero.engine.util.scheduling;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import bitzero.engine.service.IService;
import bitzero.engine.util.Logging;

public class Scheduler implements IService, Runnable {
	private static AtomicInteger schedulerId = new AtomicInteger(0);
	private volatile int threadId = 1;
	private long SLEEP_TIME = 250;
	private ExecutorService taskExecutor;
	private LinkedList<ScheduledTask> taskList;
	private LinkedList<ScheduledTask> addList;
	private String serviceName;
	private Logger logger;
	private volatile boolean running = false;

	public Scheduler() {
		this(null);
	}

	public Scheduler(Logger customLogger) {
		schedulerId.incrementAndGet();
		this.taskList = new LinkedList<ScheduledTask>();
		this.addList = new LinkedList<ScheduledTask>();
		this.logger = this.logger == null ? LoggerFactory.getLogger("bootLogger") : customLogger;
	}

	public Scheduler(long interval) {
		this();
		this.SLEEP_TIME = interval;
	}

	@Override
	public void init(Object o) {
		this.startService();
	}

	@Override
	public void destroy(Object o) {
		this.stopService();
	}

	@Override
	public String getName() {
		return this.serviceName;
	}

	@Override
	public void setName(String name) {
		this.serviceName = name;
	}

	@Override
	public void handleMessage(Object message) {
		throw new UnsupportedOperationException("not supported in this class version");
	}

	public void startService() {
		this.running = true;
		this.taskExecutor = Executors.newSingleThreadExecutor();
		this.taskExecutor.execute(this);
	}

	public void stopService() {
		this.running = false;
		List<Runnable> leftOvers = this.taskExecutor.shutdownNow();
		this.taskExecutor = null;
		this.logger.info("Scheduler stopped. Unprocessed tasks: " + leftOvers.size());
	}

	@Override
	public void run() {
		Thread.currentThread().setName("Scheduler" + schedulerId.get() + "-thread-" + this.threadId++);
		this.logger.info("Scheduler started: " + this.serviceName);
		while (this.running) {
			try {
				this.executeTasks();
				Thread.sleep(this.SLEEP_TIME);
			} catch (InterruptedException ie) {
				this.logger.warn("Scheduler: " + this.serviceName + " interrupted.");
			} catch (Exception e) {
				Logging.logStackTrace(this.logger,
						"Scheduler: " + this.serviceName + " caught a generic exception: " + e, e.getStackTrace());
			}
		}
	}

	/*
	 * WARNING - Removed try catching itself - possible behaviour change.
	 */
	public void addScheduledTask(Task task, int interval, boolean loop, ITaskHandler callback) {
		LinkedList<ScheduledTask> linkedList = this.addList;
		synchronized (linkedList) {
			this.addList.add(new ScheduledTask(task, interval, loop, callback));
		}
	}

	/*
	 * WARNING - Removed try catching itself - possible behaviour change.
	 */
	private void executeTasks() {
		LinkedList<ScheduledTask> linkedList;
		long now = System.currentTimeMillis();
		if (this.taskList.size() > 0) {
			linkedList = this.taskList;
			synchronized (linkedList) {
				Iterator<ScheduledTask> it = this.taskList.iterator();
				while (it.hasNext()) {
					ScheduledTask t = it.next();
					if (!t.task.isActive()) {
						it.remove();
						continue;
					}
					if (now < t.expiry)
						continue;
					try {
						t.callback.doTask(t.task);
					} catch (Exception e) {
						Logging.logStackTrace(this.logger,
								"Scheduler callback exception. Callback: " + t.callback + ", Exception: " + e,
								e.getStackTrace());
					}
					if (t.loop) {
						t.expiry += (long) (t.interval * 1000);
						continue;
					}
					it.remove();
				}
			}
		}
		if (this.addList.size() > 0) {
			linkedList = this.taskList;
			synchronized (linkedList) {
				this.taskList.addAll(this.addList);
				this.addList.clear();
			}
		}
	}

	private final class ScheduledTask {
		private long expiry;
		private int interval;
		private boolean loop;
		private ITaskHandler callback;
		private Task task;

		public ScheduledTask(Task t, int interval, boolean loop, ITaskHandler callback) {
			this.task = t;
			this.interval = interval;
			this.expiry = System.currentTimeMillis() + (long) (interval * 1000);
			this.callback = callback;
			this.loop = loop;
		}
	}

}
