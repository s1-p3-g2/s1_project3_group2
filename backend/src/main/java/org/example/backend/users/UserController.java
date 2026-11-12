package org.example.backend.users;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

/**
 * @author Victoria Ha
 * @version 0.1.1
 * Description: USer Controller with Patch/api/users/user_id/admin
 * @since 11/10/2026
 */

@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    public record AdminStatusRequest(Boolean isAdmin) {}

    @PatchMapping("/{userId}/admin")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void changeAdminStatus(
            Authentication authentication,
            @PathVariable("userId") Long userId,
            @RequestBody AdminStatusRequest request
    ) {
        if (request.isAdmin() == null) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "isAdmin is required"
            );
        }

        userService.changeAdminStatus(
                authentication,
                userId,
                request.isAdmin()
        );
    }
}
