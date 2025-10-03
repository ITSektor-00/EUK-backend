package com.sirus.backend;

import com.sirus.backend.controller.AdminController;
import com.sirus.backend.dto.UserDto;
import com.sirus.backend.service.UserService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;
import java.util.List;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import org.mockito.MockitoAnnotations;

@WebMvcTest(AdminController.class)
public class AdminControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @org.mockito.Mock
    private UserService userService;
    
    public AdminControllerTest() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testGetAdminUsers() throws Exception {
        // Arrange
        UserDto user1 = new UserDto();
        user1.setId(1L);
        user1.setUsername("admin");
        user1.setRole("ADMIN");
        
        UserDto user2 = new UserDto();
        user2.setId(2L);
        user2.setUsername("user");
        user2.setRole("USER");
        
        List<UserDto> users = Arrays.asList(user1, user2);
        Page<UserDto> page = new PageImpl<>(users, PageRequest.of(0, 20), 2);
        
        when(userService.findAllUsers(0, 20, null, null, null)).thenReturn(page);

        // Act & Assert
        mockMvc.perform(get("/api/admin/users"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray())
                .andExpect(jsonPath("$.content.length()").value(2))
                .andExpect(jsonPath("$.totalElements").value(2));
    }

    @Test
    public void testGetAdminUsersWithPagination() throws Exception {
        // Arrange
        List<UserDto> users = Arrays.asList();
        Page<UserDto> page = new PageImpl<>(users, PageRequest.of(1, 10), 0);
        
        when(userService.findAllUsers(1, 10, null, null, null)).thenReturn(page);

        // Act & Assert
        mockMvc.perform(get("/api/admin/users?page=1&size=10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray())
                .andExpect(jsonPath("$.content.length()").value(0));
    }

    @Test
    public void testGetAdminUserCount() throws Exception {
        // Arrange
        when(userService.getUserCount()).thenReturn(5L);

        // Act & Assert
        mockMvc.perform(get("/api/admin/users/count"))
                .andExpect(status().isOk())
                .andExpect(content().string("5"));
    }

    @Test
    public void testGetAdminActiveUserCount() throws Exception {
        // Arrange
        when(userService.getActiveUserCount()).thenReturn(3L);

        // Act & Assert
        mockMvc.perform(get("/api/admin/users/count/active"))
                .andExpect(status().isOk())
                .andExpect(content().string("3"));
    }
}
