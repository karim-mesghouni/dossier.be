package com.softline.dossier.be.security.domain;

import com.softline.dossier.be.domain.Concerns.HasId;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RoleInput implements HasId {
    long id;
    String name;
    String displayName;
}
