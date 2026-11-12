package org.example.backend.users;

import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

/**
 * @author Victoria Ha
 * @version 0.1.0
 * Description: God(me, the author) hates me(the users) and everything I stand for(api
 *      routes involving them)
 *      also this decides what happens with the data from the User Repo
 *      i think the most important methods here are isAdmin() and findUser() and requireAdmin
 * @since 11/9/2026
 */

@Service
public class UserService {

    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }
    //User lookup
    public User findUser(String provider, String providerUserId) {
        return userRepository
                .findByProviderAndAuthSub(provider, providerUserId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "User not found"
                ));
    }

    //admin check
    public boolean isAdmin(String provider, String providerUserId) {
        return userRepository
                .findByProviderAndAuthSub(provider, providerUserId)
                .map(User::isAdmin)
                .orElse(false);
    }
    //RequireAd- yeah honestly.
    private void requireAdmin(Authentication authentication) {
        User currentUser = requireUser(authentication);

        if (!currentUser.isAdmin()) {
            throw new ResponseStatusException(
                    HttpStatus.FORBIDDEN,
                    "Administrator access is required"
            );
        }
    }

    //majorly important maybe don't touch without telling someone. this method makes or breaks about 70% of the functionality of the coming issues.
    @Transactional
    public void changeAdminStatus(
            Authentication authentication,
            Long targetUserId,
            boolean admin
    ) {
        requireAdmin(authentication);

        User targetUser = userRepository.findById(targetUserId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "User not found"
                ));

        targetUser.setAdmin(admin);
        userRepository.save(targetUser);
    }

    @Transactional(readOnly = true)
    public List<UserResponse> getUsers(Authentication authentication) {
        requireAdmin(authentication);

        return userRepository.findAll()
                .stream()
                .map(user -> new UserResponse(
                        user.getUserId(),
                        user.getUsername(),
                        user.getBio(),
                        user.getProfilePic(),
                        user.isAdmin()
                ))
                .toList();
    }

    private User requireUser(Authentication authentication) {
        if (!(authentication instanceof OAuth2AuthenticationToken oauth)
                || !oauth.isAuthenticated()) {
            throw new ResponseStatusException(
                    HttpStatus.UNAUTHORIZED,
                    "OAuth sign-in is required"
            );
        }

        String provider = oauth.getAuthorizedClientRegistrationId();
        String providerUserId = oauth.getName();

        return userRepository
                .findByProviderAndAuthSub(provider, providerUserId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.FORBIDDEN,
                        "No app user exists for this session."
                ));
    }

    public record UserResponse(
            Long userId,
            String username,
            String bio,
            String profilePic,
            boolean isAdmin
    ) {}

}