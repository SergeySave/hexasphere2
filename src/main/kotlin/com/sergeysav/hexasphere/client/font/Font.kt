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
     * Compute the height of a string of text
     *
     * @param text the text for which to compute the height
     *
     * @return the height of the text
     */
    fun computeHeight(text: CharSequence): Double

    /**
     * Dispose the resources for this font
     */
    fun dispose()
}