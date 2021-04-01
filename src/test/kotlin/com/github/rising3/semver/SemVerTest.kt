/**
 * Copyright (C) 2021 rising3 <micho.nakagawa@gmail.com>
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
package com.github.rising3.semver

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.function.Executable
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.MethodSource

class SemVerTest {
    @Test
    fun `Constructor`() {
        assertAll(
            Executable { assertEquals("1.2.3", SemVer(1, 2, 3).toString()) },
            Executable { assertEquals("1.2.3-1", SemVer(1, 2, 3, null, 1).toString()) },
            Executable { assertEquals("1.2.3-RC.1", SemVer(1, 2, 3, "RC", 1).toString()) },
            Executable { assertThrows(IllegalArgumentException::class.java) { SemVer(1, 2, 3, "RC", null) } }
        )
    }

    @Test
    fun `Getter`() {
        val actual = SemVer(1, 2, 3, "RC", 4)
        assertAll(
            Executable { assertEquals(1, actual.major) },
            Executable { assertEquals(2, actual.minor) },
            Executable { assertEquals(3, actual.patch) },
            Executable { assertEquals("RC", actual.preid) },
            Executable { assertEquals(4, actual.prerelease) },
        )
    }

    @Test
    fun `to String`() = assertEquals("1.2.3", SemVer(1, 2, 3).toString())

    @ParameterizedTest
    @MethodSource("sourceParse")
    fun `Parse version`(s: String, expected: Any) {
        when (expected) {
            is String -> { assertEquals(expected, SemVer.parse(s).toString()) }
            is Throwable -> {
                @Suppress("UNCHECKED_CAST")
                assertThrows(expected as Class<Throwable>) { SemVer.parse(s) }
            }
        }
    }

    @ParameterizedTest
    @MethodSource("sourceIncrement")
    fun `Increment version`(actual: String, expected: String) = assertEquals(expected, actual)

    companion object {
        @JvmStatic
        fun sourceParse(): List<Arguments> {
            return listOf(
                Arguments.of("1.2.3", "1.2.3"),
                Arguments.of("1.2.3-1", "1.2.3-1"),
                Arguments.of("1.2.3-M.1", "1.2.3-M.1"),
                Arguments.of("123.456.789", "123.456.789"),
                Arguments.of("1", IllegalArgumentException::class.java),
                Arguments.of("1.", IllegalArgumentException::class.java),
                Arguments.of("1.2", IllegalArgumentException::class.java),
                Arguments.of("1.2.", IllegalArgumentException::class.java),
                Arguments.of("1.2.x", IllegalArgumentException::class.java),
                Arguments.of("1.2.3-RC", IllegalArgumentException::class.java),
                Arguments.of("1.2.3-RC.", IllegalArgumentException::class.java),
                Arguments.of("1.2.3.4-RC.1", IllegalArgumentException::class.java),
                Arguments.of("1.2.3.4-1", IllegalArgumentException::class.java),
            )
        }

        @JvmStatic
        fun sourceIncrement(): List<Arguments> {
            return listOf(
                Arguments.of(SemVer.parse("1.2.3").incMajor().toString(), "2.0.0"),
                Arguments.of(SemVer.parse("1.2.3").incMinor().toString(), "1.3.0"),
                Arguments.of(SemVer.parse("1.2.3").incPatch().toString(), "1.2.4"),
                Arguments.of(SemVer.parse("1.2.3-RC.2").incPremajor(null).toString(), "2.0.0-1"),
                Arguments.of(SemVer.parse("1.2.3-RC.2").incPremajor("RC").toString(), "2.0.0-RC.2"),
                Arguments.of(SemVer.parse("1.2.3-RC.2").incPreminor(null).toString(), "1.3.0-1"),
                Arguments.of(SemVer.parse("1.2.3-RC.2").incPreminor("RC").toString(), "1.3.0-RC.2"),
                Arguments.of(SemVer.parse("1.2.3-RC.2").incPrepatch(null).toString(), "1.2.4-1"),
                Arguments.of(SemVer.parse("1.2.3-RC.2").incPrepatch("RC").toString(), "1.2.4-RC.2"),
                Arguments.of(SemVer.parse("1.2.3-RC.2").incPrerelease(null).toString(), "1.2.3-1"),
                Arguments.of(SemVer.parse("1.2.3-RC.2").incPrerelease("RC").toString(), "1.2.3-RC.3"),
                Arguments.of(SemVer.parse("1.2.3-2").incPremajor(null).toString(), "2.0.0-2"),
                Arguments.of(SemVer.parse("1.2.3-2").incPremajor("RC").toString(), "2.0.0-RC.1"),
                Arguments.of(SemVer.parse("1.2.3-2").incPreminor(null).toString(), "1.3.0-2"),
                Arguments.of(SemVer.parse("1.2.3-2").incPreminor("RC").toString(), "1.3.0-RC.1"),
                Arguments.of(SemVer.parse("1.2.3-2").incPrepatch(null).toString(), "1.2.4-2"),
                Arguments.of(SemVer.parse("1.2.3-2").incPrepatch("RC").toString(), "1.2.4-RC.1"),
                Arguments.of(SemVer.parse("1.2.3-2").incPrerelease(null).toString(), "1.2.3-3"),
                Arguments.of(SemVer.parse("1.2.3-2").incPrerelease("RC").toString(), "1.2.3-RC.1"),
            )
        }
    }
}
