package com.github.seecret1.bank_card_management_system.utils;

import com.github.seecret1.bank_card_management_system.entity.Card;
import com.github.seecret1.bank_card_management_system.security.CustomUserDetails;
import lombok.experimental.UtilityClass;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.context.SecurityContextHolder;

@UtilityClass
public class AuthUtils {

    public CustomUserDetails getAuthenticatedUser() {
        var principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        if (principal instanceof CustomUserDetails details) {
            return details;
        }
        throw new SecurityException("Principal in security context is not instance of AppUserDetails");
    }

    public void checkCardAccess(Card card) {
        CustomUserDetails currentUser = getAuthenticatedUser();

        if (isAdmin(currentUser)) return;

        if (!card.getUser().getId().equals(currentUser.getId())) {
            throw new AccessDeniedException("Access is denied");
        }
    }

    private boolean isAdmin(CustomUserDetails currentUser) {
        return currentUser.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));
    }
}
