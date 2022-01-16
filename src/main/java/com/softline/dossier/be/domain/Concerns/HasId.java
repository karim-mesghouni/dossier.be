package com.softline.dossier.be.domain.Concerns;

/**
 * represents an entity which has an id getter field {@link HasId#getId()}
 */
public interface HasId {
    /**
     * @return the id of this entity
     */
    Long getId();
}
