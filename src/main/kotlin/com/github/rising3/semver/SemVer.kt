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

import java.lang.IllegalArgumentException

/**
 * The semantic versioning class.
 *
 * @author rising3
 * @constructor [major].[minor].[patch]{-[preid].[prerelease]}
 * @param[major] major number
 * @param[minor] minor number
 * @param[patch] patch number
 * @param[preid] pre-id(option)
 * @param[prerelease] pre-release number(option)
 */
class SemVer(major: Int, minor: Int, patch: Int, preid: String? = null, prerelease: Int? = null) {
    /**
     * Default pre-release
     */
    private val defaultPrerelease: Int = 1

    /**
     * major number
     */
    val major: Int

    /**
     * minor number
     */
    val minor: Int

    /**
     * patch number
     */
    val patch: Int

    /**
     * pre-id
     */
    val preid: String?

    /**
     * pre-release number
     */
    val prerelease: Int?

    /**
     * init.
     */
    init {
        if (preid != null && prerelease == null) {
            throw IllegalArgumentException("Illegal arguments: $major, $minor, $patch, $preid, $prerelease")
        }
        this.major = major
        this.minor = minor
        this.patch = patch
        this.preid = preid
        this.prerelease = prerelease
    }

    /**
     * Increment major number.
     *
     * @return SemVer
     */
    fun incMajor() = SemVer(major + 1, 0, 0)

    /**
     * Increment minor number.
     *
     * @return SemVer
     */
    fun incMinor() = SemVer(major, minor + 1, 0)

    /**
     * Increment patch number.
     *
     * @return SemVer
     */
    fun incPatch() = SemVer(major, minor, patch + 1)

    /**
     * Increment pre-major number.
     *
     * @param[preid] pre-id
     * @return SemVer
     */
    fun incPremajor(preid: String?) = SemVer(major + 1, 0, 0, preid, calcPrerelease(preid))

    /**
     * Increment pre-minor number.
     *
     * @param[preid] pre-id
     * @return SemVer
     */
    fun incPreminor(preid: String?) = SemVer(major, minor + 1, 0, preid, calcPrerelease(preid))

    /**
     * Increment pre-patch number.
     *
     * @param[preid] pre-id
     * @return SemVer
     */
    fun incPrepatch(preid: String?) = SemVer(major, minor, patch + 1, preid, calcPrerelease(preid))

    /**
     * Increment pre-release number.
     *
     * @param[preid] pre-id
     * @return SemVer
     */
    fun incPrerelease(preid: String?) = SemVer(
        major,
        minor,
        patch,
        preid,
        if (this.preid == preid) calcPrerelease(preid) + 1 else defaultPrerelease
    )

    /**
     * Get semantic versioning string.
     *
     * @return Semantic versioning string.
     */
    override fun toString() = String.format(
        "%d.%d.%d%s%s%s",
        major,
        minor,
        patch,
        if (prerelease != null) "-" else "",
        if (preid != null) "$preid." else "",
        if (prerelease != null) "$prerelease" else ""
    )

    /**
     * calculate pre-release number.
     *
     *  @param[preid] pre-id
     *  @return pre-release number.
     */
    private fun calcPrerelease(preid: String?) = (if (this.preid == preid) prerelease else null) ?: defaultPrerelease

    companion object {
        /**
         * Parse [s].
         *
         * @param[s] The semantic versioning string
         * @return SemVer
         * @throws IllegalArgumentException Invalid format
         */
        @JvmStatic
        fun parse(s: String): SemVer {
            val p =
                listOfNotNull(
                    Regex("""^\d+\.\d+\.\d+-\w+\.\d+$""").find(s),
                    Regex("""^\d+\.\d+\.\d+-\d+$""").find(s),
                    Regex("""^\d+\.\d+\.\d+$""").find(s),
                )
            if (p.isEmpty()) {
                throw IllegalArgumentException("Illegal argument: $s")
            }
            val v = p[0].value.replace("-", ".").split(".")
            return when (v.count()) {
                5 -> SemVer(v[0].toInt(), v[1].toInt(), v[2].toInt(), v[3], v[4].toInt())
                4 -> SemVer(v[0].toInt(), v[1].toInt(), v[2].toInt(), null, v[3].toInt())
                else -> SemVer(v[0].toInt(), v[1].toInt(), v[2].toInt())
            }
        }
    }
}
