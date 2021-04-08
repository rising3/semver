/**
 * Copyright (C) 2021 rising3 <michio.nakagawa@gmail.com>
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
    fun `equals`() {
        val actual = SemVer(1, 2, 3, "RC", 4)
        assertAll(
            Executable { assertFalse(actual.equals(1)) },
            Executable { assertFalse(actual.equals("String")) },
            Executable { assertFalse(SemVer.parse("1.2.3") == actual) },
            Executable { assertTrue(SemVer.parse("1.2.3-RC.4") == actual) },
        )
    }

    @Test
    fun `hashcode`() {
        assertAll(
            Executable { assertEquals(SemVer.parse("1.2.3").hashCode(), SemVer.parse("1.2.3").hashCode()) },
            Executable { assertEquals(SemVer.parse("1.2.3-RC.4").hashCode(), SemVer.parse("1.2.3-RC.4").hashCode()) },
        )
    }

    @Test
    fun `compareTo`() {
        assertAll(
            Executable { assertTrue(SemVer.parse("0.0.5") == SemVer.parse("0.0.5")) },
            Executable { assertTrue(SemVer.parse("0.0.5") > SemVer.parse("0.0.1")) },
            Executable { assertTrue(SemVer.parse("0.0.5") < SemVer.parse("0.0.9")) },
            Executable { assertTrue(SemVer.parse("0.5.5") == SemVer.parse("0.5.5")) },
            Executable { assertTrue(SemVer.parse("0.5.5") > SemVer.parse("0.5.1")) },
            Executable { assertTrue(SemVer.parse("0.5.5") < SemVer.parse("0.5.9")) },
            Executable { assertTrue(SemVer.parse("5.5.0") == SemVer.parse("5.5.0")) },
            Executable { assertTrue(SemVer.parse("5.5.0") > SemVer.parse("5.1.0")) },
            Executable { assertTrue(SemVer.parse("5.5.0") < SemVer.parse("5.9.0")) },
            Executable { assertTrue(SemVer.parse("5.5.0-5") > SemVer.parse("5.1.0")) },
            Executable { assertTrue(SemVer.parse("5.5.0-5") < SemVer.parse("5.5.0")) },
            Executable { assertTrue(SemVer.parse("5.5.0-5") < SemVer.parse("5.9.0")) },
            Executable { assertTrue(SemVer.parse("5.5.0-5") == SemVer.parse("5.5.0-5")) },
            Executable { assertTrue(SemVer.parse("5.5.0-5") > SemVer.parse("5.5.0-1")) },
            Executable { assertTrue(SemVer.parse("5.5.0-5") < SemVer.parse("5.5.0-9")) },
            Executable { assertTrue(SemVer.parse("5.5.0-M.5") > SemVer.parse("5.1.0")) },
            Executable { assertTrue(SemVer.parse("5.5.0-M.5") < SemVer.parse("5.5.0")) },
            Executable { assertTrue(SemVer.parse("5.5.0-M.5") < SemVer.parse("5.9.0")) },
            Executable { assertTrue(SemVer.parse("5.5.0-M.5") > SemVer.parse("5.5.0-A.5")) },
            Executable { assertTrue(SemVer.parse("5.5.0-M.5") > SemVer.parse("5.5.0-A.1")) },
            Executable { assertTrue(SemVer.parse("5.5.0-M.5") > SemVer.parse("5.5.0-A.9")) },
            Executable { assertTrue(SemVer.parse("5.5.0-M.5") < SemVer.parse("5.5.0-Z.5")) },
            Executable { assertTrue(SemVer.parse("5.5.0-M.5") < SemVer.parse("5.5.0-Z.1")) },
            Executable { assertTrue(SemVer.parse("5.5.0-M.5") < SemVer.parse("5.5.0-Z.9")) },
            Executable { assertTrue(SemVer.parse("1.9.0") < SemVer.parse("1.10.0")) },
            Executable { assertTrue(SemVer.parse("1.10.0") < SemVer.parse("1.11.0")) },
            Executable { assertTrue(SemVer.parse("1.0.0-alpha") < SemVer.parse("1.0.0-alpha.1")) },
            Executable { assertTrue(SemVer.parse("1.0.0-alpha.1") < SemVer.parse("1.0.0-alpha.beta")) },
            Executable { assertTrue(SemVer.parse("1.0.0-alpha.beta") < SemVer.parse("1.0.0-beta")) },
            Executable { assertTrue(SemVer.parse("1.0.0-beta") < SemVer.parse("1.0.0-beta.2")) },
            Executable { assertTrue(SemVer.parse("1.0.0-beta.2") < SemVer.parse("1.0.0-beta.11")) },
            Executable { assertTrue(SemVer.parse("1.0.0-beta.11") < SemVer.parse("1.0.0-rc.1")) },
            Executable { assertTrue(SemVer.parse("1.0.0-rc.1") < SemVer.parse("1.0.0")) },
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
                Arguments.of("1.2.3-M.RC.1", "1.2.3-M.RC.1"),
                Arguments.of("1.2.3-M", "1.2.3-M.0"),
                Arguments.of("1.2.3-M.RC", "1.2.3-M.RC.0"),
                Arguments.of("123.456.789", "123.456.789"),
                Arguments.of("1", IllegalArgumentException::class.java),
                Arguments.of("1.", IllegalArgumentException::class.java),
                Arguments.of("1.2", IllegalArgumentException::class.java),
                Arguments.of("1.2.", IllegalArgumentException::class.java),
                Arguments.of("1.2.x", IllegalArgumentException::class.java),
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
                Arguments.of(SemVer.parse("1.2.3-RC").incPremajor(null).toString(), "2.0.0-0"),
                Arguments.of(SemVer.parse("1.2.3-RC").incPremajor("RC").toString(), "2.0.0-RC.0"),
                Arguments.of(SemVer.parse("1.2.3-RC.2").incPremajor(null).toString(), "2.0.0-0"),
                Arguments.of(SemVer.parse("1.2.3-RC.2").incPremajor("RC").toString(), "2.0.0-RC.2"),
                Arguments.of(SemVer.parse("1.2.3-RC.2").incPreminor(null).toString(), "1.3.0-0"),
                Arguments.of(SemVer.parse("1.2.3-RC.2").incPreminor("RC").toString(), "1.3.0-RC.2"),
                Arguments.of(SemVer.parse("1.2.3-RC.2").incPrepatch(null).toString(), "1.2.4-0"),
                Arguments.of(SemVer.parse("1.2.3-RC.2").incPrepatch("RC").toString(), "1.2.4-RC.2"),
                Arguments.of(SemVer.parse("1.2.3-RC.2").incPrerelease(null).toString(), "1.2.3-0"),
                Arguments.of(SemVer.parse("1.2.3-RC.2").incPrerelease("RC").toString(), "1.2.3-RC.3"),
                Arguments.of(SemVer.parse("1.2.3-2").incPremajor(null).toString(), "2.0.0-2"),
                Arguments.of(SemVer.parse("1.2.3-2").incPremajor("RC").toString(), "2.0.0-RC.0"),
                Arguments.of(SemVer.parse("1.2.3-2").incPreminor(null).toString(), "1.3.0-2"),
                Arguments.of(SemVer.parse("1.2.3-2").incPreminor("RC").toString(), "1.3.0-RC.0"),
                Arguments.of(SemVer.parse("1.2.3-2").incPrepatch(null).toString(), "1.2.4-2"),
                Arguments.of(SemVer.parse("1.2.3-2").incPrepatch("RC").toString(), "1.2.4-RC.0"),
                Arguments.of(SemVer.parse("1.2.3-2").incPrerelease(null).toString(), "1.2.3-3"),
                Arguments.of(SemVer.parse("1.2.3-2").incPrerelease("RC").toString(), "1.2.3-RC.0"),
            )
        }
    }
}
