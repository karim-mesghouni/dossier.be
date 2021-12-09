package com.softline.dossier.be.graphql.types.input;

import com.google.errorprone.annotations.ForOverride;
import com.softline.dossier.be.Application;
import org.modelmapper.ModelMapper;
import org.modelmapper.convention.MatchingStrategies;

/**
 * Base input type which can be mapped to its linked entity
 *
 * @param <T> the type of the entity that this input represents
 */
public abstract class Input<T> {
    /**
     * @return the target class that this input should be mapped to when {@link #map()} is called
     */
    @ForOverride
    public abstract Class<T> getMappingTarget();

    /**
     * uses {@link ModelMapper} to map this input to its Entity type<br>
     * the entity class type is obtained by {@link #getMappingTarget()},<br>
     * To avoid mapping errors it is better to add {@link MatchingStrategies#STRICT} to the model mapper configuration
     *
     * @return a detached entity of type {@link Input#getMappingTarget()}
     */
    public T map() {
        var mapper = Application.getBean(ModelMapper.class);
        return mapper.map(this, getMappingTarget());
    }
}
