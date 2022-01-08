package com.btb.sne.batch;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class EntityData {

    private String conceptUri;
    private String conceptType;
    private String skillType;
    private String reuseLevel;
    private String preferredLabel;
    private String altLabels;
    private String hiddenLabels;
    private String status;
    private String modifiedDate;
    private String scopeNote;
    private String definition;
    private String inScheme;
    private String description;
    private String code;
    private String iscoGroup;
    private String regulatedProfessionNote;

    private List<String> broaderNodes = new ArrayList<>();
    private List<String> essentials = new ArrayList<>();
    private List<String> optionals = new ArrayList<>();
}


