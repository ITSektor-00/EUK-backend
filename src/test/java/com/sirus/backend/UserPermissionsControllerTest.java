package com.sirus.backend;

import com.sirus.backend.entity.User;
import com.sirus.backend.repository.UserRepository;
import com.sirus.backend.controller.UserPermissionsController;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Optional;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import org.mockito.MockitoAnnotations;

@WebMvcTest(UserPermissionsController.class)
public class UserPermissionsControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @org.mockito.Mock
    private UserRepository userRepository;
    
    public UserPermissionsControllerTest() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testGetUserPermissions_AdminUser() throws Exception {
        // Arrange
        User adminUser = new User();
        adminUser.setId(1L);
        adminUser.setUsername("admin");
        adminUser.setRole("ADMIN");
        adminUser.setActive(true);
        
        when(userRepository.findById(1L)).thenReturn(Optional.of(adminUser));

        // Act & Assert
        mockMvc.perform(get("/api/user-permissions/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.userId").value(1))
                .andExpect(jsonPath("$.username").value("admin"))
                .andExpect(jsonPath("$.role").value("ADMIN"))
                .andExpect(jsonPath("$.routes.admin").value(true))
                .andExpect(jsonPath("$.routes.users").value(true))
                .andExpect(jsonPath("$.routes.euk").value(true))
                .andExpect(jsonPath("$.canDelete").value(true));
    }

    @Test
    public void testGetUserPermissions_RegularUser() throws Exception {
        // Arrange
        User regularUser = new User();
        regularUser.setId(2L);
        regularUser.setUsername("user");
        regularUser.setRole("USER");
        regularUser.setActive(true);
        
        when(userRepository.findById(2L)).thenReturn(Optional.of(regularUser));

        // Act & Assert
        mockMvc.perform(get("/api/user-permissions/2"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.userId").value(2))
                .andExpect(jsonPath("$.username").value("user"))
                .andExpect(jsonPath("$.role").value("USER"))
                .andExpect(jsonPath("$.routes.admin").value(false))
                .andExpect(jsonPath("$.routes.users").value(false))
                .andExpect(jsonPath("$.routes.euk").value(true))
                .andExpect(jsonPath("$.canDelete").value(false));
    }

    @Test
    public void testGetUserPermissions_UserNotFound() throws Exception {
        // Arrange
        when(userRepository.findById(999L)).thenReturn(Optional.empty());

        // Act & Assert
        mockMvc.perform(get("/api/user-permissions/999"))
                .andExpect(status().isNotFound());
    }

    @Test
    public void testGetCurrentUserPermissions() throws Exception {
        // Act & Assert
        mockMvc.perform(get("/api/user-permissions/me"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username").value("anonymous"))
                .andExpect(jsonPath("$.role").value("USER"))
                .andExpect(jsonPath("$.routes.euk").value(true));
    }
}
