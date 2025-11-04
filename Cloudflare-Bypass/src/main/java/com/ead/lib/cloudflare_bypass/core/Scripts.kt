package com.ead.lib.cloudflare_bypass.core

object Scripts {
    
    /**
     * Default polling interval in milliseconds for checking Cloudflare bypass status
     */
    const val DEFAULT_POLLING_INTERVAL = 2500L
    
    /**
     * Generates the Cloudflare bypass script with configurable polling interval
     * 
     * @param pollingInterval The interval in milliseconds between bypass checks (default: 2500ms)
     * @return JavaScript code for bypassing Cloudflare challenge
     */
    fun getCloudflareBypassScript(pollingInterval: Long = DEFAULT_POLLING_INTERVAL): String {
        return """
        (function() {
            let intervalId = null;
            let attemptCount = 0;
            const maxAttempts = 30;
            
            intervalId = setInterval(() => {
                attemptCount++;
                
                // Stop after max attempts to prevent infinite loops
                if (attemptCount > maxAttempts) {
                    clearInterval(intervalId);
                    return;
                }
                
                const challengeForm = document.querySelector("#challenge-form");
                
                if (challengeForm != null) {
                    // Still in challenge, try to click buttons
                    
                    const simpleChallenge = document.querySelector("#challenge-stage > div > input[type='button']");
                    
                    if (simpleChallenge != null) {
                        simpleChallenge.click();
                        return;
                    }
                    
                    try {
                        const turnstile = document.querySelector("div.hcaptcha-box > iframe");
                        
                        if (turnstile != null && turnstile.contentWindow) {
                            const button = turnstile.contentWindow.document.querySelector("input[type='checkbox']");
                            
                            if (button != null) {
                                button.click();
                            }
                        }
                    } catch (e) {
                        // Cross-origin iframe access might fail, continue checking
                    }
                    
                } else {
                    // Challenge passed, clean up and notify
                    clearInterval(intervalId);
                    
                    // Safety check: Interface is injected by BaseClient's initializeByPass()
                    // via addJavascriptInterface() in onPageStarted callback
                    // This check ensures graceful degradation if script runs before injection
                    if (typeof CloudFlareByPassInterface !== 'undefined') {
                        CloudFlareByPassInterface.onByPass();
                    }
                }
            }, $pollingInterval);
        })();
        """.trimIndent()
    }
    
    /**
     * Legacy compatibility: Default bypass script with 2500ms polling interval
     * Use getCloudflareBypassScript() for the same functionality with explicit parameters
     * 
     * @since 0.0.5
     * @deprecated This property will be maintained for backward compatibility but new code
     * should use getCloudflareBypassScript() for better configurability
     */
    @Deprecated(
        "Use getCloudflareBypassScript() for configurable polling interval"
    )
    val CLOUDFLARE_BYPASS: String
        get() = getCloudflareBypassScript()
}