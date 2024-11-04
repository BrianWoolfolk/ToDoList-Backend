package com.todo.backend.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
public class UserControllerTests {

    @Autowired
    private MockMvc mockMvc;

    @Value("${admin.username}")
    private String adminUsername;

    @Value("${admin.password}")
    private String adminPassword;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);
    }

    String authenticate() throws Exception {
        MvcResult result = mockMvc.perform(post("/api/login")
                .param("username", adminUsername)
                .param("password", adminPassword)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();

        return result.getResponse().getContentAsString();
    }

    // Login tests

    @Test
    public void testLogin_withoutParams_thenFailure() throws Exception {
        mockMvc.perform(post("/api/login"))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void testLogin_withEmptyParams_thenFailure() throws Exception {
        mockMvc.perform(post("/api/login")
                .param("username", "some")
                .param("password", "thing"))
                .andExpect(status().isNotFound());
    }

    @Test
    public void testLogin_withValidParams_thenSuccess() throws Exception {
        System.out.println(adminUsername);
        System.out.println(adminPassword);

        MvcResult result = mockMvc.perform(post("/api/login")
                .param("username", adminUsername)
                .param("password", adminPassword))
                .andExpect(status().isOk())
                .andReturn();

        String responseContent = result.getResponse().getContentAsString();
        System.out.println(responseContent);
    }

    // User tests

    @Test
    public void testGetUsers_withoutParams_thenFailure() throws Exception {
        mockMvc.perform(post("/api/users")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnauthorized());
    }

    @Test
    public void testGetUsers_withAuthentication_thenSuccess() throws Exception {
        String token = authenticate();

        mockMvc.perform(post("/api/users")
                .header("Authorization", "Bearer " + token)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    // Create user tests

    @Test
    public void testCreateUser_withoutParams_thenFailure() throws Exception {
        mockMvc.perform(post("/api/users/create")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnauthorized());
    }

    @Test
    public void testCreateUser_withAuthentication_thenBadRequest() throws Exception {
        String token = authenticate();

        mockMvc.perform(post("/api/users/create")
                .header("Authorization", "Bearer " + token)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void testCreateUser_withValidParams_thenSuccess() throws Exception {
        String token = authenticate();

        mockMvc.perform(post("/api/users/create")
                .header("Authorization", "Bearer " + token)
                .content("{\"username\":\"test\",\"password\":\"test123\"}")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated());
    }

    // Update user tests

    @Test
    public void testUpdateUser_withoutParams_thenFailure() throws Exception {
        mockMvc.perform(put("/api/users/update")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnauthorized());
    }

    @Test
    public void testUpdateUser_withAuthentication_thenBadRequest() throws Exception {
        String token = authenticate();

        mockMvc.perform(put("/api/users/update")
                .header("Authorization", "Bearer " + token)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void testUpdateUser_withValidParams_thenSuccess() throws Exception {
        String token = authenticate();

        mockMvc.perform(post("/api/users/create")
                .header("Authorization", "Bearer " + token)
                .content("{\"username\":\"test2\",\"password\":\"test123\"}")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated());

        mockMvc.perform(put("/api/users/update")
                .header("Authorization", "Bearer " + token)
                .param("username", "test2")
                .content("{\"username\":\"testNew\",\"password\":\"test1234\"}")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());
    }

    // Delete user tests

    @Test
    public void testDeleteUser_withoutParams_thenFailure() throws Exception {
        mockMvc.perform(delete("/api/user/delete")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnauthorized());
    }

    @Test
    public void testDeleteUser_withAuthentication_thenBadRequest() throws Exception {
        String token = authenticate();

        mockMvc.perform(delete("/api/user/delete")
                .header("Authorization", "Bearer " + token)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void testDeleteUser_withValidParams_thenSuccess() throws Exception {
        String token = authenticate();

        mockMvc.perform(post("/api/users/create")
                .header("Authorization", "Bearer " + token)
                .content("{\"username\":\"test3\",\"password\":\"test123\"}")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated());

        mockMvc.perform(delete("/api/user/delete")
                .header("Authorization", "Bearer " + token)
                .param("username", "test3")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());
    }
}