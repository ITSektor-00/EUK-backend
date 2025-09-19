package com.sirus.backend;

import com.sirus.backend.dto.PaginatedResponse;
import com.sirus.backend.dto.UserDto;
import org.junit.jupiter.api.Test;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class PaginatedResponseTest {

    @Test
    public void testPaginatedResponseCreation() {
        // Create test data
        UserDto user1 = new UserDto();
        user1.setId(1L);
        user1.setUsername("user1");
        
        UserDto user2 = new UserDto();
        user2.setId(2L);
        user2.setUsername("user2");
        
        List<UserDto> users = Arrays.asList(user1, user2);
        
        // Create a Page
        Pageable pageable = PageRequest.of(0, 10);
        Page<UserDto> page = new PageImpl<>(users, pageable, 100);
        
        // Create PaginatedResponse
        PaginatedResponse<UserDto> response = PaginatedResponse.from(page);
        
        // Assertions
        assertNotNull(response);
        assertEquals(2, response.getContent().size());
        assertEquals(100, response.getTotalElements());
        assertEquals(10, response.getTotalPages());
        assertEquals(0, response.getNumber());
        assertEquals(10, response.getSize());
        assertFalse(response.isEmpty());
        assertTrue(response.isFirst());
        assertFalse(response.isLast());
    }
    
    @Test
    public void testPaginatedResponsePageableInfo() {
        // Create test data
        UserDto user = new UserDto();
        user.setId(1L);
        user.setUsername("testuser");
        
        List<UserDto> users = Arrays.asList(user);
        
        // Create a Page with specific pageable info
        Pageable pageable = PageRequest.of(2, 5); // page 2, size 5
        Page<UserDto> page = new PageImpl<>(users, pageable, 25);
        
        // Create PaginatedResponse
        PaginatedResponse<UserDto> response = PaginatedResponse.from(page);
        
        // Assertions for pageable info
        assertNotNull(response.getPageable());
        assertEquals(2, response.getPageable().getPageNumber());
        assertEquals(5, response.getPageable().getPageSize());
        assertEquals(10, response.getPageable().getOffset());
        assertTrue(response.getPageable().isPaged());
        assertFalse(response.getPageable().isUnpaged());
    }
    
    @Test
    public void testEmptyPage() {
        // Create empty page
        Pageable pageable = PageRequest.of(0, 10);
        Page<UserDto> page = new PageImpl<>(Arrays.asList(), pageable, 0);
        
        // Create PaginatedResponse
        PaginatedResponse<UserDto> response = PaginatedResponse.from(page);
        
        // Assertions for empty page
        assertTrue(response.isEmpty());
        assertEquals(0, response.getTotalElements());
        assertEquals(0, response.getTotalPages());
        assertEquals(0, response.getContent().size());
    }
}
