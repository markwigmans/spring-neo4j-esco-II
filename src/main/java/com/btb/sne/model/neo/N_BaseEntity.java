package com.btb.sne.model.neo;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.springframework.data.annotation.Version;
import org.springframework.data.neo4j.core.schema.Id;
import org.springframework.data.neo4j.core.schema.Node;
import org.springframework.data.neo4j.core.schema.Relationship;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
@Node("Entity")
public abstract class N_BaseEntity {

    @Version
    private Long version;

    private String altLabels;
    private String conceptType;
    @Id
    private String conceptUri;
    private String description;
    private String inScheme;
    private String preferredLabel;

    @ToString.Exclude
    @Relationship(type = "BROADER_THAN")
    private Set<N_BaseEntity> broaderNodes = new HashSet<>();

    @ToString.Exclude
    @Relationship(type = "ESSENTIAL_FOR")
    private Set<N_BaseEntity> essentials = new HashSet<>();

    @ToString.Exclude
    @Relationship(type = "OPTIONAL_FOR")
    private Set<N_BaseEntity> optionals = new HashSet<>();

    @Override
    public int hashCode() {
        return Objects.hash(Objects.requireNonNull(conceptUri, "conceptUri must be non null"));
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        N_BaseEntity that = (N_BaseEntity) o;
        return Objects.equals(conceptUri, that.conceptUri);
    }
}
