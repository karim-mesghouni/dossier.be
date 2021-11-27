package com.softline.dossier.be.domain.Concerns;

import com.softline.dossier.be.security.domain.Agent;

/**
 * Represents an entity which has a "createdBy" association<br>
 * which can be obtained with {@link #getCreator()}
 */
public interface HasCreator {
    Agent getCreator();
}
