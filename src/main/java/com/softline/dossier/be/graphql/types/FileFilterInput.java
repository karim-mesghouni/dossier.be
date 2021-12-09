package com.softline.dossier.be.graphql.types;

import com.softline.dossier.be.graphql.types.input.ActivityInput;
import com.softline.dossier.be.graphql.types.input.ClientInput;
import com.softline.dossier.be.graphql.types.input.FileStateInput;

public class FileFilterInput {
    public String project;
    public ClientInput client;
    public ActivityInput activity;
    public FileStateInput state;
    public DateRangeInput attributionDate = DateRangeInput.FULL_RANGE;
    public DateRangeInput returnDeadline = DateRangeInput.FULL_RANGE;
    public DateRangeInput provisionalDeliveryDate = DateRangeInput.FULL_RANGE;
    public DateRangeInput deliveryDate = DateRangeInput.FULL_RANGE;
    public boolean reprise;
    public boolean notReprise;
    public boolean onlyTrashed;
}
