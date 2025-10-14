package uk.ac.ncl.csc8404.pcretailer.model;

/**
 * Represents a user-customisable computer model that can change its parts over time.
 * Implementations are expected to remain immutable by returning new instances when modifications are requested.
 */
public interface CustomComputerModel extends ComputerModel {

    // when adding parts we make new copy, no mutat 

    /**
     * Adds the supplied part to the model, returning a new instance that includes it.
     *
     * @param part the part to add; must be a non-empty string.
     * @return a new model instance that contains the given part.
     * @throws IllegalArgumentException if the part is blank.
     */
    CustomComputerModel withPart(String part);

    /**
     * Removes the supplied part from the model, returning a new instance that excludes it.
     *
     * @param part the part to remove; must be a non-empty string.
     * @return a new model instance without the part; returns {@code this} if the part was already absent.
     * @throws IllegalArgumentException if the part is blank.
     */
    CustomComputerModel withoutPart(String part);
}
