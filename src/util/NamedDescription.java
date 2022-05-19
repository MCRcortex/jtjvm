package util;

import java.util.Objects;

public class NamedDescription {
    public final String name;
    public String description;

    public NamedDescription(String name, String description) {
        this.name = name;
        this.description = description;
    }


    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        return name.equals(((NamedDescription) o).name) && description.equals(((NamedDescription) o).description);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, description);
    }

    @Override
    public String toString() {
        return "NamedDescription{" +
                "name='" + name + '\'' +
                ", description='" + description + '\'' +
                '}';
    }
}
