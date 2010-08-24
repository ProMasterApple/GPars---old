// GPars - Groovy Parallel Systems
//
// Copyright © 2008-10  The original author or authors
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
//       http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.

package groovyx.gpars.util;

import groovyx.gpars.scheduler.Pool;
import org.codehaus.groovy.runtime.NullObject;

import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.atomic.AtomicIntegerFieldUpdater;

/**
 * @author Vaclav Pech
 *         Date: Aug 23, 2010
 */
public abstract class AsyncMessagingCore implements Runnable {

    private final Pool threadPool;

    /**
     * Fair agents give up the thread after processing each message, non-fair agents keep a thread until their message queue is empty.
     */
    private volatile boolean fair = false;


    public AsyncMessagingCore(final Pool threadPool, final boolean fair) {
        this.threadPool = threadPool;
        this.fair = fair;
    }

    /**
     * Retrieves the agent's fairness flag
     * Fair agents give up the thread after processing each message, non-fair agents keep a thread until their message queue is empty.
     * Non-fair agents tends to perform better than fair ones.
     *
     * @return True for fair agents, false for non-fair ones. Agents are non-fair by default.
     */
    public boolean isFair() {
        return fair;
    }

    /**
     * Makes the agent fair. Agents are non-fair by default.
     * Fair agents give up the thread after processing each message, non-fair agents keep a thread until their message queue is empty.
     * Non-fair agents tends to perform better than fair ones.
     */
    public void makeFair() {
        this.fair = true;
    }

    /**
     * Incoming messages
     */
    private final Queue<Object> queue = new ConcurrentLinkedQueue<Object>();

    /**
     * Indicates, whether there's an active thread handling a message inside the agent's body
     */
    @SuppressWarnings({"FieldMayBeFinal"})
    private volatile int active = AsyncMessagingCore.PASSIVE;
    private static final AtomicIntegerFieldUpdater<AsyncMessagingCore> activeUpdater = AtomicIntegerFieldUpdater.newUpdater(AsyncMessagingCore.class, "active");
    private static final int PASSIVE = 0;
    private static final int ACTIVE = 1;


    /**
     * Adds the message to the agent\s message queue
     *
     * @param message A value or a closure
     */
    public void store(final Object message) {
        queue.add(message != null ? message : NullObject.getNullObject());
        schedule();
    }

    /**
     * Schedules processing of a next message, if there are some and if there isn't an active thread handling a message at the moment
     */
    void schedule() {
        if (!queue.isEmpty() && activeUpdater.compareAndSet(this, PASSIVE, ACTIVE)) {
            threadPool.execute(this);
        }
    }

    /**
     * Handles a single message from the message queue
     */
    @SuppressWarnings({"CatchGenericClass"})
    public void run() {
        try {
            Object message = queue.poll();
            while (message != null) {
                handleMessage(message);
                if (fair) break;
                message = queue.poll();
            }
        } catch (Exception e) {
            registerError(e);
        } finally {
            activeUpdater.set(this, PASSIVE);
            schedule();
        }
    }

    protected abstract void registerError(final Exception e);

    protected abstract void handleMessage(final Object message);
}