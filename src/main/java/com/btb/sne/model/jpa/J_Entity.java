package com.btb.sne.model.jpa;

import lombok.*;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Set;

@Entity(name = "Entity")
@Getter
@Setter
@ToString
@NoArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class J_Entity {

    @Id
    @EqualsAndHashCode.Include
    private String conceptUri;

    @Version
    private Long version;

    private String conceptType;
    private String skillType;
    private String reuseLevel;
    private String preferredLabel;
    @Lob
    private String altLabels;
    @Lob
    private String hiddenLabels;
    private String status;
    private String modifiedDate;
    private String scopeNote;
    private String definition;
    private String inScheme;
    @Lob
    private String description;

    private String code;
    private String iscoGroup;
    private String regulatedProfessionNote;

    @ManyToMany(fetch = FetchType.LAZY)
    @ToString.Exclude
    private Set<J_Entity> broaderNodes = new HashSet<>();

    @ManyToMany(fetch = FetchType.LAZY)
    @ToString.Exclude
    private Set<J_Entity> essentials = new HashSet<>();

    @ManyToMany(fetch = FetchType.LAZY)
    @ToString.Exclude
    private Set<J_Entity> optionals = new HashSet<>();
}


