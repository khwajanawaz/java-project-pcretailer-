package uk.ac.ncl.csc8404.pcretailer.model;

import java.util.Set;

/**
 * Describes a computer model offered by the retailer.
 */
public interface ComputerModel {

    //  note: keep model name and parts not empty ok? sounds good

    /**
     * @return the unique name of the model.
     */
    String getName();

    /**
     * Provides all parts that make up the model. Each part is guaranteed to be a non-empty string.
     *
     * @return an immutable set of parts.
     */
    Set<String> getParts();
}
