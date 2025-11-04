package com.ead.lib.cloudflare_bypass.core.system.extensions

/**
 * Checks if the page title indicates a Cloudflare challenge
 * 
 * Common Cloudflare challenge titles include:
 * - "Just a moment..."
 * - "Please wait..."
 * - "Checking your browser..."
 * - Or any title containing "..."
 * 
 * @return true if the title indicates a Cloudflare challenge page
 */
fun String.isCloudFlareByPassTitle(): Boolean {
    val cloudFlareIndicators = listOf(
        "just a moment",
        "please wait",
        "checking your browser",
        "..."
    )
    
    val lowerCaseTitle = this.lowercase()
    
    return cloudFlareIndicators.any { indicator ->
        lowerCaseTitle.contains(indicator)
    }
}