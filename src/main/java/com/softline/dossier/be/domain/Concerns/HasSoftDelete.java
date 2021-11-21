package com.softline.dossier.be.domain.Concerns;

/**
 * represents an entity which has `deleted` getter field {@link HasSoftDelete#isDeleted()} which
 * tells if this entity is softDeleted or not
 */
public interface HasSoftDelete {
    /**
     * @return the id of this entity
     */
    boolean isDeleted();
}
