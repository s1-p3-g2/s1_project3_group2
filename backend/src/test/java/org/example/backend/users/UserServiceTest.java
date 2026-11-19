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
    // ---------- Creating users after OAuth login ----------

    /*
     * A first-time login creates and saves a user with the supplied
     * provider identity and profile picture, an empty bio, and no admin access.
     */
    @Test
    void findOrCreateUserSavesNewNonAdminUser() {
        when(repository.findByProviderAndAuthSub("google", "456"))
                .thenReturn(Optional.empty());

        when(repository.existsByEmailIgnoreCaseAndProviderNot(
                "duck@example.com", "google"
        )).thenReturn(false);

        // Simulate save returning the entity that was passed to it.
        when(repository.save(any(User.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        User result = service.findOrCreateUser(
                "google",
                "456",
                "Duck Friend",
                "https://example.com/google-picture.png",
                "duck@example.com"
        );

        assertEquals("google", result.getProvider());
        assertEquals("456", result.getAuthSub());
        assertEquals("Duck Friend", result.getUsername());
        assertEquals(
                "https://example.com/google-picture.png",
                result.getProfilePic()
        );
        assertEquals("duck@example.com", result.getEmail());
        assertNull(result.getBio());
        assertFalse(result.isAdmin());

        verify(repository).existsByEmailIgnoreCaseAndProviderNot(
                "duck@example.com", "google"
        );
        verify(repository).save(result);
    }

    /*
     * A returning user keeps their existing profile and administrator status,
     * even when the provider supplies different profile details.
     * No replacement user is saved.
     */
    @Test
    void findOrCreateUserPreservesExistingUser() {
        User existingUser = new User(
                "My chosen username",
                "github",
                "123",
                "My custom bio",
                "https://example.com/custom-picture.png",
                "duck@example.com"
        );
        existingUser.setAdmin(true);

        when(repository.existsByEmailIgnoreCaseAndProviderNot(
                "duck@example.com", "github"
        )).thenReturn(false);

        when(repository.findByProviderAndAuthSub("github", "123"))
                .thenReturn(Optional.of(existingUser));

        User result = service.findOrCreateUser(
                "github",
                "123",
                "Different provider username",
                "https://example.com/new-provider-picture.png",
                "duck@example.com"
        );

        assertSame(existingUser, result);
        assertEquals("My chosen username", result.getUsername());
        assertEquals("My custom bio", result.getBio());
        assertEquals(
                "https://example.com/custom-picture.png",
                result.getProfilePic()
        );
        assertEquals("duck@example.com", result.getEmail());
        assertTrue(result.isAdmin());

        verify(repository).existsByEmailIgnoreCaseAndProviderNot(
                "duck@example.com", "github"
        );
        verify(repository, never()).save(any(User.class));
    }

    /*
     * A GitHub user does not prevent creation of a Google user with the
     * same provider-specific ID. Each provider has its own identity space.
     * These accounts use different email addresses.
     */
    @Test
    void findOrCreateUserSeparatesProvidersWithTheSameId() {
        User githubUser = new User(
                "GitHub Duck",
                "github",
                "123",
                null,
                null,
                "github-duck@example.com"
        );

        when(repository.findByProviderAndAuthSub("github", "123"))
                .thenReturn(Optional.of(githubUser));
        when(repository.findByProviderAndAuthSub("google", "123"))
                .thenReturn(Optional.empty());

        when(repository.existsByEmailIgnoreCaseAndProviderNot(
                "github-duck@example.com", "github"
        )).thenReturn(false);
        when(repository.existsByEmailIgnoreCaseAndProviderNot(
                "google-duck@example.com", "google"
        )).thenReturn(false);

        when(repository.save(any(User.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        User githubResult = service.findOrCreateUser(
                "github", "123", "GitHub Duck", null,
                "github-duck@example.com"
        );
        User googleResult = service.findOrCreateUser(
                "google", "123", "Google Duck", null,
                "google-duck@example.com"
        );

        assertSame(githubUser, githubResult);
        assertNotSame(githubResult, googleResult);
        assertEquals("google", googleResult.getProvider());
        assertEquals("123", googleResult.getAuthSub());
        assertEquals("Google Duck", googleResult.getUsername());
        assertEquals("google-duck@example.com", googleResult.getEmail());
        assertFalse(googleResult.isAdmin());

        // Only the new Google user should be saved.
        verify(repository, times(1)).save(any(User.class));
        verify(repository).save(googleResult);
    }

    /*
     * An email registered with GitHub cannot create a Google account.
     */
    @Test
    void findOrCreateUserRejectsGoogleWhenEmailBelongsToGithub() {
        when(repository.existsByEmailIgnoreCaseAndProviderNot(
                "duck@example.com", "google"
        )).thenReturn(true);

        ResponseStatusException error = assertThrows(
                ResponseStatusException.class,
                () -> service.findOrCreateUser(
                        "google", "456", "Google Duck", null,
                        "duck@example.com"
                )
        );

        assertEquals(HttpStatus.CONFLICT, error.getStatusCode());
        verify(repository, never()).save(any(User.class));
    }

    /*
     * The restriction also applies when Google was the original provider.
     */
    @Test
    void findOrCreateUserRejectsGithubWhenEmailBelongsToGoogle() {
        when(repository.existsByEmailIgnoreCaseAndProviderNot(
                "duck@example.com", "github"
        )).thenReturn(true);

        ResponseStatusException error = assertThrows(
                ResponseStatusException.class,
                () -> service.findOrCreateUser(
                        "github", "123", "GitHub Duck", null,
                        "duck@example.com"
                )
        );

        assertEquals(HttpStatus.CONFLICT, error.getStatusCode());
        verify(repository, never()).save(any(User.class));
    }

    /*
     * An existing provider identity must not bypass the email conflict check.
     */
    @Test
    void findOrCreateUserRejectsConflictEvenForExistingAccount() {
        User existingUser = new User(
                "Google Duck",
                "google",
                "456",
                null,
                null,
                "duck@example.com"
        );

        when(repository.findByProviderAndAuthSub("google", "456"))
                .thenReturn(Optional.of(existingUser));
        when(repository.existsByEmailIgnoreCaseAndProviderNot(
                "duck@example.com", "google"
        )).thenReturn(true);

        ResponseStatusException error = assertThrows(
                ResponseStatusException.class,
                () -> service.findOrCreateUser(
                        "google", "456", "Google Duck", null,
                        "duck@example.com"
                )
        );

        assertEquals(HttpStatus.CONFLICT, error.getStatusCode());
        verify(repository, never()).save(any(User.class));
    }

    /*
     * Surrounding whitespace is removed before checking and saving the email.
     */
    @Test
    void findOrCreateUserTrimsEmailBeforeCheckingAndSaving() {
        when(repository.findByProviderAndAuthSub("google", "456"))
                .thenReturn(Optional.empty());
        when(repository.save(any(User.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        User result = service.findOrCreateUser(
                "google", "456", "Duck Friend", null,
                "  duck@example.com  "
        );

        assertEquals("duck@example.com", result.getEmail());
        verify(repository).existsByEmailIgnoreCaseAndProviderNot(
                "duck@example.com", "google"
        );
        verify(repository).save(result);
    }

    /*
     * Without an email, the cross-provider restriction cannot be checked.
     */
    @Test
    void findOrCreateUserRejectsMissingEmail() {
        ResponseStatusException error = assertThrows(
                ResponseStatusException.class,
                () -> service.findOrCreateUser(
                        "google", "456", "Duck Friend", null, null
                )
        );

        assertEquals(HttpStatus.BAD_REQUEST, error.getStatusCode());
        verifyNoInteractions(repository);
    }

    @Test
    void findOrCreateUserRejectsBlankEmail() {
        ResponseStatusException error = assertThrows(
                ResponseStatusException.class,
                () -> service.findOrCreateUser(
                        "google", "456", "Duck Friend", null, "   "
                )
        );

        assertEquals(HttpStatus.BAD_REQUEST, error.getStatusCode());
        verifyNoInteractions(repository);
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