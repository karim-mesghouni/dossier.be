package com.softline.dossier.be.security.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RoleInput
{
    long id;
    String name;
    String displayName;
}
