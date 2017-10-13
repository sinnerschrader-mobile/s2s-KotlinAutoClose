/*
 * Copyright 2017 Kyle Wood (DemonWav)
 * Adapted 2017 Uli Luckas, SinnerSchrader Swipe GmbH
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


package com.s2s.kotlin.autoclose.test

import com.s2s.kotlin.autoclose.using
import org.junit.jupiter.api.Assertions.assertThrows
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test

@DisplayName("using {} tests")
class UsingTest {
    @Test
    @DisplayName("Simple using {} test")
    fun testBasicUsing() {
        var connection: Connection? = null
        var statement: PreparedStatement? = null
        var rs: ResultSet? = null

        using {
            connection = getConnection().autoClose()
            statement = connection?.prepareStatement().autoClose()
            rs = statement?.executeQuery().autoClose()
            rs?.next()
        }

        assertTrue((connection as? Closed)?.closed == true)
        assertTrue((statement as? Closed)?.closed == true)
        assertTrue((rs as? Closed)?.closed == true)
    }

    @Test
    @DisplayName("Close all with caught exception test")
    fun testWithCaught() {
        var connection: Connection? = null
        var statement: PreparedStatement? = null
        var rs: ResultSet? = null

        try {
            using {
                connection = getConnection().autoClose()
                statement = connection?.prepareStatement().autoClose()
                rs = statement?.executeQuery().autoClose()
                rs?.next()
                throw SuperSecretException
            }
        } catch (_: SuperSecretException) {
        }

        assertTrue((connection as? Closed)?.closed == true)
        assertTrue((statement as? Closed)?.closed == true)
        assertTrue((rs as? Closed)?.closed == true)
    }

    @Test
    @DisplayName("Close all with uncaught exception test")
    fun testWithUncaught() {
        var connection: Connection? = null
        var statement: PreparedStatement? = null
        var rs: ResultSet? = null

        assertThrows(SuperSecretException::class.java) {
            try {
                using {
                    connection = getConnection().autoClose()
                    statement = connection?.prepareStatement().autoClose()
                    rs = statement?.executeQuery().autoClose()
                    rs?.next()
                    throw SuperSecretException
                }
            } catch (_: AnotherSecretException) {
            }
        }

        assertTrue((connection as? Closed)?.closed == true)
        assertTrue((statement as? Closed)?.closed == true)
        assertTrue((rs as? Closed)?.closed == true)
    }

    @Test
    @DisplayName("Exception in using {} block test")
    fun testExceptionInBlock() {
        var connection: Connection? = null
        var statement: PreparedStatement? = null
        var rs: ResultSet? = null

        assertThrows(SuperSecretException::class.java) {

            try {
                using {
                    connection = getConnection().autoClose()
                    statement = connection?.prepareStatement().autoClose()
                    rs = statement?.executeQuery().autoClose()
                    rs?.next()
                    throw SuperSecretException
                }
            } finally {
            }
        }

        assertTrue((connection as? Closed)?.closed == true)
        assertTrue((statement as? Closed)?.closed == true)
        assertTrue((rs as? Closed)?.closed == true)
    }

    @Test
    @DisplayName("Exception in using {} block with catch test")
    fun testCatch() {
        var connection: Connection? = null
        var statement: PreparedStatement? = null
        var rs: ResultSet? = null

        try {
            using {
                connection = getConnection().autoClose()
                statement = connection?.prepareStatement().autoClose()
                rs = statement?.executeQuery().autoClose()
                rs?.next()
                throw SuperSecretException
            }
        } catch (_: SuperSecretException) {

        } finally {

        }

        assertTrue((connection as? Closed)?.closed == true)
        assertTrue((statement as? Closed)?.closed == true)
        assertTrue((rs as? Closed)?.closed == true)
    }

    @Test
    @DisplayName("Exception in using {} block with catch of different type test")
    fun testCatchWrongType() {
        var connection: Connection? = null
        var statement: PreparedStatement? = null
        var rs: ResultSet? = null

        assertThrows(SuperSecretException::class.java) {
            try {
                using {
                    connection = getConnection().autoClose()
                    statement = connection?.prepareStatement().autoClose()
                    rs = statement?.executeQuery().autoClose()
                    rs?.next()
                    throw SuperSecretException
                }
            } catch (_: RuntimeException) {

            } finally {

            }
        }

        assertTrue((connection as? Closed)?.closed == true)
        assertTrue((statement as? Closed)?.closed == true)
        assertTrue((rs as? Closed)?.closed == true)

    }

    @Test
    @DisplayName("Exception in using {} block with catch of super type test")
    fun testCatchSuperType() {
        var connection: Connection? = null
        var statement: PreparedStatement? = null
        var rs: ResultSet? = null

        try {
            using {
                connection = getConnection().autoClose()
                statement = connection?.prepareStatement().autoClose()
                rs = statement?.executeQuery().autoClose()
                rs?.next()
                throw SuperSecretException
            }
        } catch (_: Exception) {
        } finally {
        }


        assertTrue((connection as? Closed)?.closed == true)
        assertTrue((statement as? Closed)?.closed == true)
        assertTrue((rs as? Closed)?.closed == true)
    }

    @Test
    @DisplayName("Exception in using {} block with multi-catch test")
    fun testMultiCatch() {
        var connection: Connection? = null
        var statement: PreparedStatement? = null
        var rs: ResultSet? = null

        try {
            using {
                connection = getConnection().autoClose()
                statement = connection?.prepareStatement().autoClose()
                rs = statement?.executeQuery().autoClose()
                rs?.next()
                throw SuperSecretException
            }
        } catch (_: RuntimeException) {
        } catch (_: NumberFormatException) {
        } catch (_: SuperSecretException) {
        } finally {
        }

        assertTrue((connection as? Closed)?.closed == true)
        assertTrue((statement as? Closed)?.closed == true)
        assertTrue((rs as? Closed)?.closed == true)
    }

    @Test
    @DisplayName("Exception in using {} block with multi-catch super type test")
    fun testMultiCatchSuper() {
        var connection: Connection? = null
        var statement: PreparedStatement? = null
        var rs: ResultSet? = null

        try {
            using {
                connection = getConnection().autoClose()
                statement = connection?.prepareStatement().autoClose()
                rs = statement?.executeQuery().autoClose()
                rs?.next()
                throw SuperSecretException
            }
        } catch (_: RuntimeException) {
        } catch (_: NumberFormatException) {
        } catch (_: Exception) {
        } finally {
        }

        assertTrue((connection as? Closed)?.closed == true)
        assertTrue((statement as? Closed)?.closed == true)
        assertTrue((rs as? Closed)?.closed == true)
    }

    @Test
    @DisplayName("Exception in using {} block with multi-catch wrong type test")
    fun testMultiCatchWrongType() {
        var connection: Connection? = null
        var statement: PreparedStatement? = null
        var rs: ResultSet? = null

        assertThrows(SuperSecretException::class.java) {
            try {
                using {
                    connection = getConnection().autoClose()
                    statement = connection?.prepareStatement().autoClose()
                    rs = statement?.executeQuery().autoClose()
                    rs?.next()
                    throw SuperSecretException
                }
            } catch (_: RuntimeException) {
            } catch (_: NumberFormatException) {
            } catch (_: ArrayIndexOutOfBoundsException) {
            } finally {
            }
        }

        assertTrue((connection as? Closed)?.closed == true)
        assertTrue((statement as? Closed)?.closed == true)
        assertTrue((rs as? Closed)?.closed == true)
    }

    @Test
    @DisplayName("Exception in catch {} block test")
    fun testExceptionInCatch() {
        var connection: Connection? = null
        var statement: PreparedStatement? = null
        var rs: ResultSet? = null

        assertThrows(AnotherSecretException::class.java) {
            try {
                using {
                    connection = getConnection().autoClose()
                    statement = connection?.prepareStatement().autoClose()
                    rs = statement?.executeQuery().autoClose()
                    rs?.next()
                    throw SuperSecretException
                }
            } catch (_: SuperSecretException) {
                throw AnotherSecretException
            } finally {
            }
        }

        assertTrue((connection as? Closed)?.closed == true)
        assertTrue((statement as? Closed)?.closed == true)
        assertTrue((rs as? Closed)?.closed == true)
    }

    @Test
    @DisplayName("Exception in catch {} block with another catch block of the same type test")
    fun testExceptionInCatchWithSameTypeCatch() {
        var connection: Connection? = null
        var statement: PreparedStatement? = null
        var rs: ResultSet? = null

        assertThrows(AnotherSecretException::class.java) {
            try {
                using {
                    connection = getConnection().autoClose()
                    statement = connection?.prepareStatement().autoClose()
                    rs = statement?.executeQuery().autoClose()
                    rs?.next()
                    throw SuperSecretException
                }
            } catch (_: SuperSecretException) {
                throw AnotherSecretException
            } catch (_: AnotherSecretException) {
            } finally {
            }
        }

        assertTrue((connection as? Closed)?.closed == true)
        assertTrue((statement as? Closed)?.closed == true)
        assertTrue((rs as? Closed)?.closed == true)
    }

    @Test
    @DisplayName("Exception in catch {} block with multi-catch test")
    fun testExceptionInCatchWithMultiCatch() {
        var connection: Connection? = null
        var statement: PreparedStatement? = null
        var rs: ResultSet? = null

        assertThrows(AnotherSecretException::class.java) {
            try {
                using {
                    connection = getConnection().autoClose()
                    statement = connection?.prepareStatement().autoClose()
                    rs = statement?.executeQuery().autoClose()
                    rs?.next()
                    throw SuperSecretException
                }
            } catch (_: RuntimeException) {
            } catch (_: NumberFormatException) {
            } catch (_: SuperSecretException) {
                throw AnotherSecretException
            } finally {
            }
        }

        assertTrue((connection as? Closed)?.closed == true)
        assertTrue((statement as? Closed)?.closed == true)
        assertTrue((rs as? Closed)?.closed == true)
    }

    @Test
    @DisplayName("Exception in catch {} block with multi-catch super type test")
    fun testExceptionInCatchWithMultiCatchSuperType() {
        var connection: Connection? = null
        var statement: PreparedStatement? = null
        var rs: ResultSet? = null

        assertThrows(AnotherSecretException::class.java) {
            try {
                using {
                    connection = getConnection().autoClose()
                    statement = connection?.prepareStatement().autoClose()
                    rs = statement?.executeQuery().autoClose()
                    rs?.next()
                    throw SuperSecretException
                }
            } catch (_: RuntimeException) {
            } catch (_: NumberFormatException) {
            } catch (_: Exception) {
                throw AnotherSecretException
            } finally {
            }
        }

        assertTrue((connection as? Closed)?.closed == true)
        assertTrue((statement as? Closed)?.closed == true)
        assertTrue((rs as? Closed)?.closed == true)
    }

    @Test
    @DisplayName("Exception in finally {} block test")
    fun testExceptionInFinally() {
        var connection: Connection? = null
        var statement: PreparedStatement? = null
        var rs: ResultSet? = null

        assertThrows(AnotherSecretException::class.java) {
            try {
                using {
                    connection = getConnection().autoClose()
                    statement = connection?.prepareStatement().autoClose()
                    rs = statement?.executeQuery().autoClose()
                    rs?.next()
                    throw SuperSecretException
                }
            } catch (_: SuperSecretException) {
            } finally {
                throw AnotherSecretException
            }
        }

        assertTrue((connection as? Closed)?.closed == true)
        assertTrue((statement as? Closed)?.closed == true)
        assertTrue((rs as? Closed)?.closed == true)
    }

    @Test
    @DisplayName("Exception in finally {} block with multi-catch test")
    fun testExceptionInFinallyWithMutliCatch() {
        var connection: Connection? = null
        var statement: PreparedStatement? = null
        var rs: ResultSet? = null

        assertThrows(AnotherSecretException::class.java) {
            try {
                using {
                    connection = getConnection().autoClose()
                    statement = connection?.prepareStatement().autoClose()
                    rs = statement?.executeQuery().autoClose()
                    rs?.next()
                    throw SuperSecretException
                }
            } catch (_: RuntimeException) {
            } catch (_: SuperSecretException) {
            } finally {
                throw AnotherSecretException
            }
        }

        assertTrue((connection as? Closed)?.closed == true)
        assertTrue((statement as? Closed)?.closed == true)
        assertTrue((rs as? Closed)?.closed == true)
    }

    @Test
    @DisplayName("finally {} block with no exception test")
    fun testNoExceptionFinally() {
        var connection: Connection? = null
        var statement: PreparedStatement? = null
        var rs: ResultSet? = null

        var b = false
        try {
            using {
                connection = getConnection().autoClose()
                statement = connection?.prepareStatement().autoClose()
                rs = statement?.executeQuery().autoClose()
                rs?.next()
            }
        } finally {
            b = true
        }

        assertTrue(b)

        assertTrue((connection as? Closed)?.closed == true)
        assertTrue((statement as? Closed)?.closed == true)
        assertTrue((rs as? Closed)?.closed == true)

    }

    @Test
    @DisplayName("Exception in close() test")
    fun testExceptionInClose() {
        assertThrows(SuperSecretException::class.java) {
            try {
                using {
                    CloseWithException().autoClose()
                }
            } finally {
            }
        }
    }

    @Test
    @DisplayName("Caught exception in close() test")
    fun testExceptionInCloseCaught() {
        try {
            using {
                CloseWithException().autoClose()
            }
        } catch (_: SuperSecretException) {
        } finally {
        }
    }

    private object SuperSecretException : Exception()
    private object AnotherSecretException : Exception()

    // Below is a simple mock-up of the general SQL resource path in Java, for visual purposes
    private fun getConnection() = Connection()

    open class Closed : AutoCloseable {
        var closed = false
        override fun close() {
            closed = true
        }
    }

    class Connection : Closed() {
        fun prepareStatement() = PreparedStatement()
    }

    class PreparedStatement : Closed() {
        fun executeQuery() = ResultSet()
    }

    class ResultSet : Closed() {
        fun next() = Unit
    }

    class CloseWithException : AutoCloseable {
        override fun close(): Unit = throw SuperSecretException
    }
}
