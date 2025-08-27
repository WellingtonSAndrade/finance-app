package com.pagar.finance_api.core.security.user;

import org.springframework.security.authentication.AuthenticationCredentialsNotFoundException;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class LoggedUserService {

    public UUID getLoggedUserId() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        if (auth == null || auth.getPrincipal() == null) {
            throw new AuthenticationCredentialsNotFoundException("Unauthenticated user");
        }

        Object principal  = auth.getPrincipal();

        if (principal instanceof Jwt jwt) {
            String userId = jwt.getClaimAsString("user_id");
            return UUID.fromString(userId);
        }

        throw new AuthenticationServiceException("Unsupported principal type: " + principal.getClass());
    }
}
