package com.softline.dossier.be.graphql.types;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class PageList<T>
{
    List<T> page;
    long count;
}
