/*
 * Copyright 2017 Uli Luckas, SinnerSchrader Swipe GmbH
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.s2s.kotlin.autoclose

class RessourceManager {

    private val closeables = arrayListOf<AutoCloseable>()

    fun <T : AutoCloseable?> T.autoClose(): T {
        if (this != null) {
            closeables.add(this)
        }
        return this
    }

    fun closeAndRethrow(t: Throwable): Nothing {
        closeables.asReversed().forEach {
            try {
                it.close()
            } catch (newThrowable: Throwable) {
                t.addSuppressed(newThrowable)
            }
        }
        throw t
    }

    fun close() {
        var throwable: Throwable? = null
        closeables.asReversed().forEach {
            try {
                it.close()
            } catch (newThrowable: Throwable) {
                throwable = throwable?.also { it.addSuppressed(newThrowable) } ?: newThrowable
            }
        }
        throwable?.let { throw it }
    }
}

inline fun <R> using(block: RessourceManager.() -> R): R {
    val manager = RessourceManager()
    var closed = false

    try {
        return manager.block()
    } catch (t: Throwable) {
        closed = true
        manager.closeAndRethrow(t)
    } finally {
        if (!closed) {
            manager.close()
        }
    }
}
