package com.softline.dossier.be.graphql.types;

import com.softline.dossier.be.graphql.types.input.ActivityInput;
import com.softline.dossier.be.graphql.types.input.ClientInput;
import com.softline.dossier.be.graphql.types.input.FileStateInput;

public class FileFilterInput
{
    public int pageNumber;
    public int pageSize;
    public String project;
    public ClientInput client;
    public ActivityInput activity;
    public FileStateInput state;
    public DateRangeInput attributionDate;
    public DateRangeInput returnDeadline;
    public DateRangeInput provisionalDeliveryDate;
    public DateRangeInput deliveryDate;
    public boolean reprise;
    public boolean notReprise;
    public boolean onlyTrashed;
}
