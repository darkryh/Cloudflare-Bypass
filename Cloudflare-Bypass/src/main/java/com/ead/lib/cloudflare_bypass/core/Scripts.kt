package com.ead.lib.cloudflare_bypass.core

object Scripts {

    val CLOUDFLARE_BYPASS =
        """
        setInterval(() => {
        
            if (document.querySelector("#challenge-form") != null) {
            
            // still haven't passed, lets try to click in some challenges
            
                const simpleChallenge = document.querySelector("#challenge-stage > div > input[type='button']")
                
                if (simpleChallenge != null) simpleChallenge.click()
                        
                    const turnstile = document.querySelector("div.hcaptcha-box > iframe")
                    
                    if (turnstile != null) {
                    
                        const button = turnstile.contentWindow.document.querySelector("input[type='checkbox']")
                        
                        if (button != null) button.click()
                        
                    }
                    
            } else {
            
                // passed so acknowledge the challenge
                CloudFlareByPassInterface.onByPass()
                
            }
        }, 2500)
        """.trimIndent()
}