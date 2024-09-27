package com.ead.lib.cloudflare_bypass.core.system.extensions

fun String.isCloudFlareByPassTitle(): Boolean {
    return this.contains("...")
}