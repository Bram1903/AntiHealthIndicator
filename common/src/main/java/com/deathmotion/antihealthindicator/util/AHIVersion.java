/*
 * This file is part of AntiHealthIndicator - https://github.com/Bram1903/AntiHealthIndicator
 * Copyright (C) 2025 Bram and contributors
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

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;

/**
 * Represents a TotemGuard version using Semantic Versioning.
 * Supports version comparison, cloning, and provides a string representation.
 * Snapshot versioning is also supported.
 * Generally a snapshot version is published before the release version,
 * and thus, is considered "older" than the release version.
 */
public class AHIVersion implements Comparable<AHIVersion> {

    private final int major;
    private final int minor;
    private final int patch;
    private final boolean snapshot;
    private final @Nullable String snapshotCommit;

    /**
     * Constructs a {@link AHIVersion} instance.
     *
     * @param major          the major version number.
     * @param minor          the minor version number.
     * @param patch          the patch version number.
     * @param snapshot       whether the version is a snapshot.
     * @param snapshotCommit the snapshot commit hash, if available.
     */
    public AHIVersion(
            final int major, final int minor, final int patch,
            final boolean snapshot, final @Nullable String snapshotCommit
    ) {
        this.major = major;
        this.minor = minor;
        this.patch = patch;
        this.snapshot = snapshot;
        this.snapshotCommit = snapshotCommit;
    }

    /**
     * Constructs a {@link AHIVersion} instance.
     *
     * @param major          the major version number.
     * @param minor          the minor version number.
     * @param patch          the patch version number.
     * @param snapshotCommit the snapshot commit hash, if available.
     */
    public AHIVersion(
            final int major, final int minor, final int patch,
            final @Nullable String snapshotCommit
    ) {
        this(major, minor, patch, snapshotCommit != null, snapshotCommit);
    }

    /**
     * Constructs a {@link AHIVersion} instance.
     *
     * @param major    the major version number.
     * @param minor    the minor version number.
     * @param patch    the patch version number.
     * @param snapshot whether the version is a snapshot.
     */
    public AHIVersion(final int major, final int minor, final int patch, final boolean snapshot) {
        this(major, minor, patch, snapshot, null);
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
    public static AHIVersion fromString(@NotNull final String version) {
        String versionWithoutSnapshot = version.replace("-SNAPSHOT", "");
        String[] largeParts = versionWithoutSnapshot.split("\\+");
        String[] parts = largeParts.length > 0 ? largeParts[0].split("\\.") : null;

        if (largeParts.length < 1 || largeParts.length > 2
                || parts.length < 2 || parts.length > 3) {
            throw new IllegalArgumentException("Version string must be in the format 'major.minor[.patch][+commit][-SNAPSHOT]', found '" + version + "' instead");
        }

        int major = Integer.parseInt(parts[0]);
        int minor = Integer.parseInt(parts[1]);
        int patch = parts.length > 2 ? Integer.parseInt(parts[2]) : 0;
        boolean snapshot = version.contains("-SNAPSHOT");
        String snapshotCommit = largeParts.length > 1 ? largeParts[1] : null;

        return new AHIVersion(major, minor, patch, snapshot, snapshotCommit);
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
     * Gets the snapshot commit hash of the TotemGuard snapshot version. May be of any length.
     * Availability is not guaranteed since it is contingent on how the program was built.
     * Generally speaking, the commit hash can only be available if the TotemGuard version is a snapshot version.
     *
     * @return the snapshot commit hash, if available.
     */
    public @Nullable String snapshotCommit() {
        return snapshotCommit;
    }

    /**
     * Compares this {@link AHIVersion} with another {@link AHIVersion}.
     *
     * @param other the other {@link AHIVersion}.
     * @return a negative integer, zero, or a positive integer as this version can be less than,
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
                this.snapshot == other.snapshot &&
                Objects.equals(this.snapshotCommit, other.snapshotCommit);
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
        return Objects.hash(major, minor, patch, snapshot, snapshotCommit);
    }

    /**
     * Creates and returns a copy of this {@link AHIVersion}.
     *
     * @return a clone of this instance.
     */
    @Override
    public AHIVersion clone() {
        return new AHIVersion(major, minor, patch, snapshot, snapshotCommit);
    }

    /**
     * Converts the {@link AHIVersion} to a string representation.
     * If this is a stable release, the snapshot and the commit will not be included in the representation.
     *
     * @return string representation of the version.
     */
    @Override
    public String toString() {
        return major + "." + minor + "." + patch + (snapshot && snapshotCommit != null ? ("+" + snapshotCommit + "-SNAPSHOT") : "");
    }

    /**
     * Converts the {@link AHIVersion} to a string representation with a guarantee
     * that it will not have the commit attached to it.
     * Useful for accessing the string representation for metrics
     * as detailed information, such as the commit, is not required.
     *
     * @return guaranteed string representation without commit.
     */
    public String toStringWithoutSnapshot() {
        return major + "." + minor + "." + patch;
    }
}
