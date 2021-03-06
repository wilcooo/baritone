/*
 * This file is part of Baritone.
 *
 * Baritone is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Baritone is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with Baritone.  If not, see <https://www.gnu.org/licenses/>.
 */

package baritone.gradle.util;

import java.lang.reflect.Field;
import java.util.Objects;

/**
 * All credits go to AsmLibGradle and its contributors.
 *
 * @see <a href="https://github.com/pozzed/AsmLibGradle/blob/8f917dbc3939eab7a3d9daf54d9d285fdf34f4b2/src/main/java/net/futureclient/asmlib/forgegradle/ReobfWrapper.java">Original Source</a>
 */
public class ReobfWrapper {

    private final Object instance;
    private final Class<?> type;

    public ReobfWrapper(Object instance) {
        this.instance = instance;
        Objects.requireNonNull(instance);
        this.type = instance.getClass();
    }

    public String getName() {
        try {
            Field nameField = type.getDeclaredField("name");
            nameField.setAccessible(true);
            return (String) nameField.get(this.instance);
        } catch (ReflectiveOperationException ex) {
            throw new IllegalStateException(ex);
        }
    }

    public MappingType getMappingType() {
        try {
            Field enumField = type.getDeclaredField("mappingType");
            enumField.setAccessible(true);
            Enum<?> aEnum = (Enum<?>) enumField.get(this.instance);
            MappingType mappingType = MappingType.values()[aEnum.ordinal()];
            if (!aEnum.name().equals(mappingType.name())) {
                throw new IllegalStateException("ForgeGradle ReobfMappingType is not equivalent to MappingType (version error?)");
            }
            return mappingType;
        } catch (ReflectiveOperationException ex) {
            throw new IllegalStateException(ex);
        }
    }
}