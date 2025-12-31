package com.dossantosh.springfirstmodulith.core.page;

import java.io.Serializable;
import java.util.List;

import lombok.Data;

/**
 * Represents a paginated result using keyset pagination.
 * 
 * @param <T> the type of content items, must be Serializable
 */
@Data
public class KeysetPage<T extends Serializable> implements Serializable {

    private List<T> content;

    private boolean hasNext;
    private boolean hasPrevious;

    private Long nextId;
    private Long previousId;

}