package com.tasbank;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tasbank.model.Client;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.oauth2.common.util.JacksonJsonParser;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.httpBasic;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
public class TaskbankTestApplicationTests {

    @Autowired
    private MockMvc mvc;

    @Test
    public void testGet() throws Exception {
        String accessToken = obtainAccessToken("user", "password");
        mvc.perform(get("/api/client/1")
                .header("Authorization", "Bearer " + accessToken)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content()
                        .contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("name", is("Client1")))
                .andExpect(jsonPath("email", is("client1@email.com")));
        mvc.perform(get("/api/client/2")
                .header("Authorization", "Bearer " + accessToken)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content()
                        .contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("name", is("Client2")))
                .andExpect(jsonPath("email", is("client2@email.com")));
    }

    @Test
    public void testGetAll() throws Exception {
        String accessToken = obtainAccessToken("user", "password");
        mvc.perform(get("/api/clients/")
                .header("Authorization", "Bearer " + accessToken)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content()
                        .contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(2)));
    }

    @Test
    public void testCreate() throws Exception {
        Client mockClient = mockClient("mockClient");
        String accessToken = obtainAccessToken("admin", "password");
        MvcResult result = mvc.perform(post("/api/client")
                .header("Authorization", "Bearer " + accessToken)
                .content(toJson(mockClient))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andReturn();
        long id = getResourceIdFromUrl(result.getResponse().getRedirectedUrl());

        mvc.perform(get("/api/client/" + id)
                .header("Authorization", "Bearer " + accessToken)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is((int) id)))
                .andExpect(jsonPath("$.name", is(mockClient.getName())))
                .andExpect(jsonPath("$.email", is(mockClient.getEmail())));

        mvc.perform(delete("/api/client/" + id)
                .header("Authorization", "Bearer " + accessToken))
                .andExpect(status().isNoContent());

        mvc.perform(get("/api/client/" + id)
                .header("Authorization", "Bearer " + accessToken)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    public void testUpdate() throws Exception {
        Client mockClient = mockClient("mockClient");
        String accessToken = obtainAccessToken("admin", "password");
        MvcResult result = mvc.perform(post("/api/client")
                .content(toJson(mockClient))
                .contentType(MediaType.APPLICATION_JSON)
                .header("Authorization", "Bearer " + accessToken)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andReturn();
        long id = getResourceIdFromUrl(result.getResponse().getRedirectedUrl());

        Client mockClient2 = mockClient("mockClient2");
        mockClient2.setId(id);

        mvc.perform(put("/api/client/" + id)
                .header("Authorization", "Bearer " + accessToken)
                .content(toJson(mockClient2))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();

        mvc.perform(get("/api/client/" + id)
                .header("Authorization", "Bearer " + accessToken)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is((int) id)))
                .andExpect(jsonPath("$.name", is(mockClient2.getName())))
                .andExpect(jsonPath("$.email", is(mockClient2.getEmail())));

        mvc.perform(delete("/api/client/" + id)
                .header("Authorization", "Bearer " + accessToken))
                .andExpect(status().isNoContent());
    }

    @Test
    public void testSearch() throws Exception {
        String searchByName = "Client1";
        String accessToken = obtainAccessToken("user", "password");
        mvc.perform(get("/api/client?name=" + searchByName)
                .header("Authorization", "Bearer " + accessToken)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)));
    }

    private long getResourceIdFromUrl(String locationUrl) {
        String[] parts = locationUrl.split("/");
        return Long.valueOf(parts[parts.length - 1]);
    }

    private byte[] toJson(Object r) throws Exception {
        ObjectMapper map = new ObjectMapper();
        return map.writeValueAsString(r).getBytes();
    }

    private Client mockClient(String name) {
        Client client = new Client();
        client.setId(Long.MAX_VALUE);
        client.setName(name);
        client.setEmail(name + "@email.com");
        client.setLastName(name + "LastName");
        client.setSurName(name + "SurName");
        client.setTelephone("+11111111111");
        return client;
    }

    private String obtainAccessToken(String username, String password) throws Exception {
        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("grant_type", "password");
        params.add("client_id", "client");
        params.add("client_secret", "password");
        params.add("username", username);
        params.add("password", password);
        ResultActions result
                = mvc.perform(post("/oauth/token")
                .params(params)
                .with(httpBasic("client", "password"))
                .accept("application/json;charset=UTF-8"))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json;charset=UTF-8"));
        String resultString = result.andReturn().getResponse().getContentAsString();
        JacksonJsonParser jsonParser = new JacksonJsonParser();
        return jsonParser.parseMap(resultString).get("access_token").toString();
    }

}

