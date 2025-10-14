package uk.ac.ncl.csc8404.pcretailer.model;

/**
 * Represents a factory preset computer model tied to a specific manufacturer.
 */
public interface PresetComputerModel extends ComputerModel {

    //  remember which maker built this preset thingy

    /**
     * @return the manufacturer responsible for producing this preset model.
     */
    String getManufacturer();
}
