package com.sfinance.SFBackend.Security.BruteForceAttack.Listener;

import com.sfinance.SFBackend.Security.BruteForceAttack.LoginAttemptService;
import com.sfinance.SFBackend.Security.Registration.AuthUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.security.authentication.event.AuthenticationSuccessEvent;
import org.springframework.stereotype.Component;

@Component
public class AuthentificationSuccessListener {

    private final LoginAttemptService loginAttemptService;

    @Autowired
    public AuthentificationSuccessListener(LoginAttemptService loginAttemptService) {
        this.loginAttemptService = loginAttemptService;
    }

    @EventListener
    public void onAuthentificationSuccess(AuthenticationSuccessEvent event){
        Object principal = event.getAuthentication().getPrincipal();
        if(principal instanceof AuthUser){
            AuthUser user = (AuthUser) event.getAuthentication().getPrincipal();
            loginAttemptService.evictUserFromLoginAttemptCache(user.getUsername());
        }
    }
}
