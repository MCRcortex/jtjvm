package util;

import java.util.Objects;

//TODO: need to special case array class types
public record ClazzedNamedDescription(
        String clazz,
        NamedDescription description) {

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        ClazzedNamedDescription other = (ClazzedNamedDescription) o;
        return clazz.equals(other.clazz) && description.equals(other.description);
    }

    @Override
    public int hashCode() {
        return Objects.hash(clazz, description);
    }
}
