package com.sergeysav.hexasphere.client.font

/**
 * An interface for a generic font
 *
 * @author sergeys
 */
interface Font {

    /**
     * Compute the width of a string of text
     *
     * @param text the text for which to compute the width
     *
     * @return the width of the text
     */
    fun computeWidth(text: CharSequence): Double

    /**
     * Get the width of a space character
     *
     * @return the width of a single space character
     */
    fun getSpaceWidth(): Double

    /**
     * Compute the height of a string of text
     *
     * @param text the text for which to compute the height
     *
     * @return the height of the text
     */
    fun computeHeight(text: CharSequence): Double

    /**
     * Get the step size between lines
     *
     * @return the step size between lines
     */
    fun getLineStep(): Double

    /**
     * Dispose the resources for this font
     */
    fun dispose()
}