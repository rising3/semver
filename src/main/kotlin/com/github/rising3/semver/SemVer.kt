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
class SemVer(major: Int, minor: Int, patch: Int, preid: String? = null, prerelease: Int? = null) : Comparable<SemVer> {
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

    override fun toString() = String.format(
        "%d.%d.%d%s%s%s",
        major,
        minor,
        patch,
        if (prerelease != null) "-" else "",
        if (preid != null) "$preid." else "",
        if (prerelease != null) "$prerelease" else ""
    )

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as SemVer
        if (major != other.major) return false
        if (minor != other.minor) return false
        if (patch != other.patch) return false
        if (preid != other.preid) return false
        if (prerelease != other.prerelease) return false
        return true
    }

    override fun hashCode(): Int {
        var hash = 7
        hash = 31 * hash + major
        hash = 31 * hash + minor
        hash = 31 * hash + patch
        hash = 31 * hash + (preid?.hashCode() ?: 0)
        hash = 31 * hash + (prerelease ?: 0)
        return hash
    }

    override fun compareTo(other: SemVer): Int {
        if ((this.preid != null && other.preid != null) || (this.prerelease != null && other.prerelease != null)) {
            return compareValuesBy(this, other, SemVer::major, SemVer::minor, SemVer::patch, SemVer::preid, SemVer::prerelease)
        } else {
            var cmp = compareValuesBy(this, other, SemVer::major, SemVer::minor, SemVer::patch)
            return if (cmp == 0 && this.preid == null) {
                1
            } else if (cmp == 0 && other.preid == null) {
                -1
            } else if (cmp == 0 && this.prerelease == null) {
                1
            } else if (cmp == 0 && other.prerelease == null) {
                -1
            } else {
                cmp
            }
        }
    }

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
                Regex("""^(0|[1-9]\d*)\.(0|[1-9]\d*)\.(0|[1-9]\d*)(?:-((?:0|[1-9]\d*|\d*[a-zA-Z-][0-9a-zA-Z-]*)(?:\.(?:0|[1-9]\d*|\d*[a-zA-Z-][0-9a-zA-Z-]*))*))?(?:\+([0-9a-zA-Z-]+(?:\.[0-9a-zA-Z-]+)*))?${'$'}""")
                    .find(s)
                    ?: throw IllegalArgumentException("Illegal argument: $s")
            val major = p.groups[1]!!.value.toInt()
            val minor = p.groups[2]!!.value.toInt()
            val patch = p.groups[3]!!.value.toInt()
            val wk = p.groups[4]?.value?.split(".")
            var preid = if (wk?.size ?: 0 == 1) {
                if (wk!![0].toIntOrNull() == null) wk[0] else null
            } else if (wk?.size ?: 0 > 1) {
                val t = if (wk?.last()?.toIntOrNull() == null) 0 else 1
                wk!!.joinToString(separator = ".", limit = wk.size - t, truncated = "").dropLast(t)
            } else {
                null
            }
            val prerelease = p.groups[4]?.value?.toIntOrNull()
                ?: wk?.last()?.toIntOrNull()
                ?: if (preid == null) null else 0
            return SemVer(major, minor, patch, preid, prerelease)
        }
    }
}
