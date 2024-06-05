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

import java.util.Arrays;

/**
 * AntiHealthIndicator version.
 * This class represents a AntiHealthIndicator version.
 */
public class AHIVersion {
    /**
     * Array containing the digits in the version.
     * For example, "1.8.9" will be stored as {1, 8, 9} in an array.
     */
    private final int[] versionIntArray;

    /**
     * Specify your version using an array.
     *
     * @param version Array version.
     */
    public AHIVersion(final int... version) {
        this.versionIntArray = version;
    }

    /**
     * Specify your version using a string, for example: "1.8.9".
     *
     * @param version String version.
     */
    public AHIVersion(final String version) {
        String[] versionIntegers = version.split("\\.");
        int length = versionIntegers.length;
        this.versionIntArray = new int[length];
        for (int i = 0; i < length; i++) {
            versionIntArray[i] = Integer.parseInt(versionIntegers[i]);
        }
    }

    /**
     * Create a AHIVersion from the package version.
     *
     * @return AHIVersion from the package version.
     */
    public static AHIVersion createFromPackageVersion() {
        // Grabbing the version from the class manifest.
        final String version = AHIPlatform.class.getPackage().getImplementationVersion();

        // Making sure the version is not null (This happens during Unit Testing), and remove the -SNAPSHOT part.
        final String[] versionParts = (version != null) ? version.split("-") : new String[]{"0.0.0"};
        final String[] parts = versionParts[0].split("\\.");

        int major = Integer.parseInt(parts[0]);
        int minor = Integer.parseInt(parts[1]);
        int patch = Integer.parseInt(parts[2]);

        return new AHIVersion(major, minor, patch);
    }

    /**
     * Compare to another AHIVersion.
     * If we are newer than the compared version,
     * this method will return 1.
     * If we are older than the compared version,
     * this method will return -1.
     * If we are equal to the compared version,
     * this method will return 0.
     * Similar to {@link Integer#compareTo(Integer)}.
     *
     * @param version Compared version
     * @return Comparing to another Version.
     */
    public int compareTo(AHIVersion version) {
        int localLength = versionIntArray.length;
        int oppositeLength = version.versionIntArray.length;
        int length = Math.max(localLength, oppositeLength);
        for (int i = 0; i < length; i++) {
            int localInteger = i < localLength ? versionIntArray[i] : 0;
            int oppositeInteger = i < oppositeLength ? version.versionIntArray[i] : 0;
            if (localInteger > oppositeInteger) {
                return 1;
            } else if (localInteger < oppositeInteger) {
                return -1;
            }
        }
        return 0;
    }

    /**
     * Does the {@link #compareTo(AHIVersion)} return 1?
     *
     * @param version Compared version.
     * @return Is this newer than the compared version.
     */
    public boolean isNewerThan(AHIVersion version) {
        return compareTo(version) == 1;
    }

    /**
     * Does the {@link #compareTo(AHIVersion)} return -1?
     *
     * @param version Compared version.
     * @return Is this older than the compared version.
     */
    public boolean isOlderThan(AHIVersion version) {
        return compareTo(version) == -1;
    }

    /**
     * Represented as an array.
     *
     * @return Array version.
     */
    public int[] asArray() {
        return versionIntArray;
    }

    /**
     * Is this version equal to the compared object.
     * The object must be a AHIVersion and the array values must be equal.
     *
     * @param obj Compared object.
     * @return Are they equal?
     */
    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (obj instanceof AHIVersion) {
            return Arrays.equals(versionIntArray, ((AHIVersion) obj).versionIntArray);
        }
        return false;
    }

    /**
     * Clone the AHIVersion.
     *
     * @return A clone.
     */
    @Override
    public AHIVersion clone() {
        return new AHIVersion(versionIntArray);
    }

    /**
     * Represent the version as a string.
     *
     * @return String representation.
     */
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder(versionIntArray.length * 2 - 1).append(versionIntArray[0]);
        for (int i = 1; i < versionIntArray.length; i++) {
            sb.append(".").append(versionIntArray[i]);
        }
        return sb.toString();
    }
}
