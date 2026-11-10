package org.example.backend.users;

import jakarta.persistence.*;
/**
 * @author Victoria Ha
 * @version 0.1.0
 * Description: This is the Model class
 * @since 11/9/2026
 */

@Entity
@Table(name = "users")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id")
    private long user_id;
    @Column(nullable = false, length = 100)
    private String username;
    @Column(nullable = false, length = 10)
    private String provider;
    @Column(name = "provider_user_id", nullable = false, length = 255);
    private String authSub;
    @Column(length = 250)
    private String bio;
    @Column(name = "profile_pic", columnDefinition = "TEXT")
    private String profilePic;
    @Column(name = "is_admin", nullable = false)
    private boolean isAdmin;

    protected User(String username, String provider, String authSub, String bio, String profilePic) {
        this.user_id = user_id;
        this.username = username;
        this.provider = provider;
        this.authSub = authSub;
        this.bio = bio;
        this.profilePic = profilePic;
        this.isAdmin = false;
    }

    public long getUserId() {
        return user_id;
    }

    public String getAuthSub() {
        return authSub;
    }
    public String getBio() {
        return bio;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getProvider() {
        return provider;
    }

    public void setProvider(String provider) {
        this.provider = provider;
    }

    public void setAuthSub(String authSub) {
        this.authSub = authSub;
    }

    public void setBio(String bio) {
        this.bio = bio;
    }

    public String getProfilePic() {
        return profilePic;
    }

    public void setProfilePic(String profilePic) {
        this.profilePic = profilePic;
    }

    public boolean isAdmin() {
        return isAdmin;
    }

    //package private just so that other files can't call it willy nilly
    void setAdmin(boolean admin) {
        isAdmin = admin;
    }
}
