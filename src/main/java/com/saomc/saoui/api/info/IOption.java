package com.saomc.saoui.api.info;

/**
 * Public interface for options.
 *
 * @author Bluexin
 */
public interface IOption {

    /**
     * @return Returns true if the Option is selected/enabled
     */
    boolean isEnabled();

    /**
     * This checks if the Option is restricted or not.
     * Restricted Options can only have one option enabled
     * in their Category.
     *
     * @return Returns true if restricted
     */
    boolean isRestricted();

    /**
     * @return Returns the Category
     */
    IOption getCategory();
}
