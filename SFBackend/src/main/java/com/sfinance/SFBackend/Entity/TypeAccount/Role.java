package com.sfinance.SFBackend.Entity.TypeAccount;

import static com.sfinance.SFBackend.Constants.Authority.ADMIN_AUTHORITIES;
import static com.sfinance.SFBackend.Constants.Authority.USER_AUTHORITIES;

public enum Role {
    ROLE_USER(USER_AUTHORITIES),
    ROLE_ADMIN(ADMIN_AUTHORITIES);

    private final String[] authorities;

    Role(String... authorities){
        this.authorities = authorities;
    }

    public String[] getAuthorities(){
        return authorities;
    }
}
