package org.example.backend.users;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.server.ResponseStatusException;

import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * @author Victoria Ha
 * @version 0.1.0
 * Description:
 * @since 11/11/2026
 */
@ExtendWith(MockitoExtension.class)
class UserControllerTest {

    @Mock
    private UserService userService;

    @Mock
    private Authentication authentication;

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders
                .standaloneSetup(new UserController(userService))
                .build();
    }

    @Test
    void promoteUserReturnsNoContent() throws Exception {
        mockMvc.perform(patch("/api/users/5/admin")
                        .principal(authentication)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"isAdmin": true}
                                """))
                .andExpect(status().isNoContent());

        verify(userService)
                .changeAdminStatus(authentication, 5L, true);
    }

    @Test
    void demoteUserReturnsNoContent() throws Exception {
        mockMvc.perform(patch("/api/users/5/admin")
                        .principal(authentication)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"isAdmin": false}
                                """))
                .andExpect(status().isNoContent());

        verify(userService)
                .changeAdminStatus(authentication, 5L, false);
    }

    @Test
    void missingAdminFlagReturnsBadRequest() throws Exception {
        mockMvc.perform(patch("/api/users/5/admin")
                        .principal(authentication)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{}"))
                .andExpect(status().isBadRequest());

        verifyNoInteractions(userService);
    }

    @Test
    void nullAdminFlagReturnsBadRequest() throws Exception {
        mockMvc.perform(patch("/api/users/5/admin")
                        .principal(authentication)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"isAdmin": null}
                                """))
                .andExpect(status().isBadRequest());

        verifyNoInteractions(userService);
    }

    @Test
    void forbiddenServiceResponseReturnsForbidden() throws Exception {
        doThrow(new ResponseStatusException(
                HttpStatus.FORBIDDEN,
                "Administrator access is required"
        )).when(userService)
                .changeAdminStatus(authentication, 5L, true);

        mockMvc.perform(patch("/api/users/5/admin")
                        .principal(authentication)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"isAdmin": true}
                                """))
                .andExpect(status().isForbidden());
    }

    @Test
    void missingTargetUserReturnsNotFound() throws Exception {
        doThrow(new ResponseStatusException(
                HttpStatus.NOT_FOUND,
                "User not found"
        )).when(userService)
                .changeAdminStatus(authentication, 999L, true);

        mockMvc.perform(patch("/api/users/999/admin")
                        .principal(authentication)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"isAdmin": true}
                                """))
                .andExpect(status().isNotFound());
    }

    @Test
    void unauthorizedServiceResponseReturnsUnauthorized() throws Exception {
        doThrow(new ResponseStatusException(
                HttpStatus.UNAUTHORIZED,
                "OAuth sign-in is required"
        )).when(userService)
                .changeAdminStatus(null, 5L, true);

        mockMvc.perform(patch("/api/users/5/admin")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"isAdmin": true}
                                """))
                .andExpect(status().isUnauthorized());
    }
}
