/*
 * This file is part of AntiHealthIndicator - https://github.com/Bram1903/AntiHealthIndicator
 * Copyright (C) 2024 Bram and contributors
 *
 * This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package com.deathmotion.antihealthindicator.util;

import com.deathmotion.antihealthindicator.AHIPlatform;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;
import java.util.Optional;

/**
 * Represents an AntiHealthIndicator version using Semantic Versioning.
 * Supports version comparison, cloning, and provides a string representation.
 */
public class AHIVersion implements Comparable<AHIVersion> {

    private final int major;
    private final int minor;
    private final int patch;
    private final boolean snapshot;

    /**
     * Constructs a {@link AHIVersion} instance.
     *
     * @param major    the major version number.
     * @param minor    the minor version number.
     * @param patch    the patch version number.
     * @param snapshot whether the version is a snapshot.
     */
    public AHIVersion(final int major, final int minor, final int patch, final boolean snapshot) {
        this.major = major;
        this.minor = minor;
        this.patch = patch;
        this.snapshot = snapshot;
    }

    /**
     * Constructs a {@link AHIVersion} instance with snapshot defaulted to false.
     *
     * @param major the major version number.
     * @param minor the minor version number.
     * @param patch the patch version number.
     */
    public AHIVersion(final int major, final int minor, final int patch) {
        this(major, minor, patch, false);
    }

    /**
     * Constructs a {@link AHIVersion} instance from a version string.
     *
     * @param version the version string (e.g., "1.8.9-SNAPSHOT").
     * @throws IllegalArgumentException if the version string format is incorrect.
     */
    public AHIVersion(@NotNull final String version) {
        this.snapshot = version.endsWith("-SNAPSHOT");
        String versionWithoutSnapshot = version.replace("-SNAPSHOT", "");
        String[] parts = versionWithoutSnapshot.split("\\.");

        if (parts.length != 3) {
            throw new IllegalArgumentException("Version string must be in the format 'major.minor.patch[-SNAPSHOT]'");
        }

        this.major = Integer.parseInt(parts[0]);
        this.minor = Integer.parseInt(parts[1]);
        this.patch = Integer.parseInt(parts[2]);
    }

    /**
     * Creates a {@link AHIVersion} instance from the package implementation version.
     *
     * @return a {@link AHIVersion} instance.
     */
    public static AHIVersion createFromPackageVersion() {
        String version = Optional.ofNullable(AHIPlatform.class.getPackage().getImplementationVersion()).orElse("0.0.0");
        return new AHIVersion(version);
    }

    /**
     * Gets the major version number.
     *
     * @return the major version number.
     */
    public int major() {
        return major;
    }

    /**
     * Gets the minor version number.
     *
     * @return the minor version number.
     */
    public int minor() {
        return minor;
    }

    /**
     * Gets the patch version number.
     *
     * @return the patch version number.
     */
    public int patch() {
        return patch;
    }

    /**
     * Checks if the version is a snapshot.
     *
     * @return true if snapshot, false otherwise.
     */
    public boolean snapshot() {
        return snapshot;
    }

    /**
     * Compares this {@link AHIVersion} with another {@link AHIVersion}.
     *
     * @param other the other {@link AHIVersion}.
     * @return a negative integer, zero, or a positive integer as this version is less than,
     * equal to, or greater than the specified version.
     */
    @Override
    public int compareTo(@NotNull final AHIVersion other) {
        int majorCompare = Integer.compare(this.major, other.major);
        if (majorCompare != 0) return majorCompare;

        int minorCompare = Integer.compare(this.minor, other.minor);
        if (minorCompare != 0) return minorCompare;

        int patchCompare = Integer.compare(this.patch, other.patch);
        if (patchCompare != 0) return patchCompare;

        return Boolean.compare(other.snapshot, this.snapshot);
    }

    /**
     * Checks if the provided object is equal to this {@link AHIVersion}.
     *
     * @param obj the object to compare.
     * @return true if the provided object is equal to this {@link AHIVersion}, false otherwise.
     */
    @Override
    public boolean equals(@NotNull final Object obj) {
        if (this == obj) return true;
        if (!(obj instanceof AHIVersion)) return false;
        AHIVersion other = (AHIVersion) obj;

        return this.major == other.major &&
                this.minor == other.minor &&
                this.patch == other.patch &&
                this.snapshot == other.snapshot;
    }

    /**
     * Checks if this version is newer than the provided version.
     *
     * @param otherVersion the other {@link AHIVersion}.
     * @return true if this version is newer, false otherwise.
     */
    public boolean isNewerThan(@NotNull final AHIVersion otherVersion) {
        return this.compareTo(otherVersion) > 0;
    }

    /**
     * Checks if this version is older than the provided version.
     *
     * @param otherVersion the other {@link AHIVersion}.
     * @return true if this version is older, false otherwise.
     */
    public boolean isOlderThan(@NotNull final AHIVersion otherVersion) {
        return this.compareTo(otherVersion) < 0;
    }

    /**
     * Returns a hash code value for this {@link AHIVersion}.
     *
     * @return a hash code value.
     */
    @Override
    public int hashCode() {
        return Objects.hash(major, minor, patch, snapshot);
    }

    /**
     * Creates and returns a copy of this {@link AHIVersion}.
     *
     * @return a clone of this instance.
     */
    @Override
    public AHIVersion clone() {
        return new AHIVersion(major, minor, patch, snapshot);
    }

    /**
     * Converts the {@link AHIVersion} to a string representation.
     *
     * @return a string representation of the version.
     */
    @Override
    public String toString() {
        return major + "." + minor + "." + patch + (snapshot ? "-SNAPSHOT" : "");
    }
}
