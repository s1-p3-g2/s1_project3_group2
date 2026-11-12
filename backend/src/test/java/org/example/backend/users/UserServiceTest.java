package org.example.backend.users;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.web.server.ResponseStatusException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * @author Victoria Ha
 * @version 0.1.0
 * Description:  I mean it's a buncha tests.. not much to say or do there??
 * @since 11/10/2026
 */
class UserServiceTest {

    private UserRepository repository;
    private UserService service;

    @BeforeEach
    void setUp() {
        repository = mock(UserRepository.class);
        service = new UserService(repository);
    }

    // ---------- Finding users ----------

    @Test
    void findUserReturnsMatchingUser() {
        User user = mock(User.class);

        when(repository.findByProviderAndAuthSub("github", "123"))
                .thenReturn(Optional.of(user));

        User result = service.findUser("github", "123");

        assertSame(user, result);
    }

    @Test
    void findUserThrowsNotFoundWhenUserDoesNotExist() {
        when(repository.findByProviderAndAuthSub("github", "123"))
                .thenReturn(Optional.empty());

        ResponseStatusException error = assertThrows(
                ResponseStatusException.class,
                () -> service.findUser("github", "123")
        );

        assertEquals(HttpStatus.NOT_FOUND, error.getStatusCode());
    }

    @Test
    void findUserDistinguishesProvidersWithTheSameUserId() {
        User githubUser = mock(User.class);
        User googleUser = mock(User.class);

        when(repository.findByProviderAndAuthSub("github", "123"))
                .thenReturn(Optional.of(githubUser));

        when(repository.findByProviderAndAuthSub("google", "123"))
                .thenReturn(Optional.of(googleUser));

        assertSame(githubUser, service.findUser("github", "123"));
        assertSame(googleUser, service.findUser("google", "123"));
    }

    // ---------- Checking admin status ----------

    @Test
    void isAdminReturnsFalseWhenUserDoesNotExist() {
        when(repository.findByProviderAndAuthSub("github", "123"))
                .thenReturn(Optional.empty());

        assertFalse(service.isAdmin("github", "123"));
    }

    @Test
    void isAdminReturnsFalseForRegularUser() {
        mockSignedInUser(false);

        assertFalse(service.isAdmin("github", "123"));
    }

    @Test
    void isAdminReturnsTrueForAdministrator() {
        mockSignedInUser(true);

        assertTrue(service.isAdmin("github", "123"));
    }

    // ---------- Listing users ----------

    @Test
    void getUsersRejectsAnonymousCaller() {
        ResponseStatusException error = assertThrows(
                ResponseStatusException.class,
                () -> service.getUsers(null)
        );

        assertEquals(HttpStatus.UNAUTHORIZED, error.getStatusCode());
        verifyNoInteractions(repository);
    }

    @Test
    void getUsersRejectsUnauthenticatedOAuthToken() {
        OAuth2AuthenticationToken authentication = signedInAuthentication();
        authentication.setAuthenticated(false);

        ResponseStatusException error = assertThrows(
                ResponseStatusException.class,
                () -> service.getUsers(authentication)
        );

        assertEquals(HttpStatus.UNAUTHORIZED, error.getStatusCode());
        verifyNoInteractions(repository);
    }

    @Test
    void getUsersRejectsSignedInUserWithoutDatabaseRecord() {
        when(repository.findByProviderAndAuthSub("github", "123"))
                .thenReturn(Optional.empty());

        ResponseStatusException error = assertThrows(
                ResponseStatusException.class,
                () -> service.getUsers(signedInAuthentication())
        );

        assertEquals(HttpStatus.FORBIDDEN, error.getStatusCode());
        verify(repository, never()).findAll();
    }

    @Test
    void getUsersRejectsRegularUser() {
        mockSignedInUser(false);

        ResponseStatusException error = assertThrows(
                ResponseStatusException.class,
                () -> service.getUsers(signedInAuthentication())
        );

        assertEquals(HttpStatus.FORBIDDEN, error.getStatusCode());
        verify(repository, never()).findAll();
    }

    @Test
    void getUsersReturnsProfilesForAdministrator() {
        mockSignedInUser(true);

        User listedUser = mock(User.class);
        when(listedUser.getUserId()).thenReturn(2L);
        when(listedUser.getUsername()).thenReturn("Duck owner");
        when(listedUser.getBio()).thenReturn("I like ducks");
        when(listedUser.getProfilePic())
                .thenReturn("https://example.com/duck.png");
        when(listedUser.isAdmin()).thenReturn(false);

        when(repository.findAll()).thenReturn(List.of(listedUser));

        List<UserService.UserResponse> result =
                service.getUsers(signedInAuthentication());

        assertEquals(
                List.of(new UserService.UserResponse(
                        2L,
                        "Duck owner",
                        "I like ducks",
                        "https://example.com/duck.png",
                        false
                )),
                result
        );
    }

    @Test
    void getUsersChecksCurrentDatabaseAdminStatusOnEveryCall() {
        User signedInUser = mockSignedInUser(true);
        when(repository.findAll()).thenReturn(List.of(signedInUser));

        service.getUsers(signedInAuthentication());

        // Simulate the user's admin access being revoked.
        when(signedInUser.isAdmin()).thenReturn(false);

        ResponseStatusException error = assertThrows(
                ResponseStatusException.class,
                () -> service.getUsers(signedInAuthentication())
        );

        assertEquals(HttpStatus.FORBIDDEN, error.getStatusCode());

        // Only the first request should reach the list query.
        verify(repository, times(1)).findAll();
    }

    // ---------- Changing admin status ----------

    @Test
    void changeAdminStatusRejectsAnonymousCaller() {
        ResponseStatusException error = assertThrows(
                ResponseStatusException.class,
                () -> service.changeAdminStatus(null, 2L, true)
        );

        assertEquals(HttpStatus.UNAUTHORIZED, error.getStatusCode());
        verifyNoInteractions(repository);
    }

    @Test
    void changeAdminStatusRejectsRegularUser() {
        mockSignedInUser(false);

        ResponseStatusException error = assertThrows(
                ResponseStatusException.class,
                () -> service.changeAdminStatus(
                        signedInAuthentication(), 2L, true
                )
        );

        assertEquals(HttpStatus.FORBIDDEN, error.getStatusCode());
        verify(repository, never()).findById(anyLong());
        verify(repository, never()).save(any(User.class));
    }

    @Test
    void changeAdminStatusPreventsRegularUserFromPromotingThemself() {
        User signedInUser = mockSignedInUser(false);
        when(signedInUser.getUserId()).thenReturn(1L);

        ResponseStatusException error = assertThrows(
                ResponseStatusException.class,
                () -> service.changeAdminStatus(
                        signedInAuthentication(), 1L, true
                )
        );

        assertEquals(HttpStatus.FORBIDDEN, error.getStatusCode());
        verify(signedInUser, never()).setAdmin(anyBoolean());
        verify(repository, never()).save(any(User.class));
    }

    @Test
    void changeAdminStatusThrowsNotFoundForMissingTarget() {
        mockSignedInUser(true);
        when(repository.findById(99L)).thenReturn(Optional.empty());

        ResponseStatusException error = assertThrows(
                ResponseStatusException.class,
                () -> service.changeAdminStatus(
                        signedInAuthentication(), 99L, true
                )
        );

        assertEquals(HttpStatus.NOT_FOUND, error.getStatusCode());
        verify(repository, never()).save(any(User.class));
    }

    @Test
    void changeAdminStatusAllowsAdministratorToPromoteUser() {
        mockSignedInUser(true);

        User targetUser = mock(User.class);
        when(repository.findById(2L)).thenReturn(Optional.of(targetUser));

        service.changeAdminStatus(signedInAuthentication(), 2L, true);

        verify(targetUser).setAdmin(true);
        verify(repository).save(targetUser);
    }

    @Test
    void changeAdminStatusAllowsAdministratorToDemoteUser() {
        mockSignedInUser(true);

        User targetUser = mock(User.class);
        when(repository.findById(2L)).thenReturn(Optional.of(targetUser));

        service.changeAdminStatus(signedInAuthentication(), 2L, false);

        verify(targetUser).setAdmin(false);
        verify(repository).save(targetUser);
    }

    // ---------- Shared test setup ----------

    /*
     * Makes the repository return a user for the signed-in
     * GitHub identity, with the requested admin status.
     */
    private User mockSignedInUser(boolean admin) {
        User user = mock(User.class);
        when(user.isAdmin()).thenReturn(admin);

        when(repository.findByProviderAndAuthSub("github", "123"))
                .thenReturn(Optional.of(user));

        return user;
    }

    /*
     * Creates a local test authentication object.
     * No actual GitHub login or network request happens.
     */
    private OAuth2AuthenticationToken signedInAuthentication() {
        var authorities = List.of(
                new SimpleGrantedAuthority("OAUTH2_USER")
        );

        var principal = new DefaultOAuth2User(
                authorities,
                Map.of("id", "123"),
                "id"
        );

        return new OAuth2AuthenticationToken(
                principal,
                authorities,
                "github"
        );
    }
}