/*
 * Copyright (C) 2017 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package ro.pippo.core.util;

import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;

/**
 * @author Decebal Suiu
 */
public class ExecutorUtils {

    public static ThreadFactory defaultThreadFactoryWithPrefix(String prefix) {
        return new PrefixingDefaultThreadFactory(prefix);
    }

    private static class PrefixingDefaultThreadFactory implements ThreadFactory {

        private final String prefix;
        private final ThreadFactory defaultThreadFactory;

        public PrefixingDefaultThreadFactory(String prefix) {
            this.defaultThreadFactory = Executors.defaultThreadFactory();
            this.prefix = prefix;
        }

        @Override
        public Thread newThread(Runnable r) {
            Thread thread = defaultThreadFactory.newThread(r);
            thread.setName(prefix + thread.getName());

            return thread;
        }

    }

}
