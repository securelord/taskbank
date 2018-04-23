package com.tasbank.controller;

import com.tasbank.model.Client;
import com.tasbank.service.IClientService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.Optional;

@RestController
@RequestMapping("/api")
public class TasbankRestController {

    private final Logger LOGGER = LoggerFactory.getLogger(TasbankRestController.class);

    private IClientService clientService;

    public TasbankRestController(IClientService clientService) {
        this.clientService = clientService;
    }

    @GetMapping(value = "/client/{id}")
    public ResponseEntity<?> getById(@PathVariable(name = "id") Long id) {
        Optional<Client> client = clientService.findById(id);
        if (client.isPresent()) {
            return new ResponseEntity<>(client.get(), HttpStatus.OK);
        }
        LOGGER.info("Client [" + id + "] not found");
        return new ResponseEntity<>(new CustomErrorType("Client [" + id + "] not found"), HttpStatus.NOT_FOUND);
    }

    @GetMapping(value = "/clients/")
    public ResponseEntity<Iterable<Client>> getAll(@RequestParam(required = false) Pageable pageable) {
        return new ResponseEntity<>(pageable != null
                ? clientService.findPaginated(pageable).getContent()
                : clientService.getAll(),
                HttpStatus.OK);
    }

    @PostMapping(value = "/client")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> create(@RequestBody Client client, UriComponentsBuilder ucBuilder) {
        LOGGER.info("Creating client : {}", client);
        if (clientService.isClientExist(client)) {
            LOGGER.error("Unable to create. A client with email {} already exist", client.getEmail());
            return new ResponseEntity<>(new CustomErrorType("Unable to create. A client with email " +
                    client.getEmail() + " already exist."), HttpStatus.CONFLICT);
        }
        Client savedClient = clientService.save(client);
        HttpHeaders headers = new HttpHeaders();
        headers.setLocation(ucBuilder.path("/client/{id}").buildAndExpand(savedClient.getId()).toUri());
        return new ResponseEntity<String>(headers, HttpStatus.CREATED);
    }

    @PutMapping(value = "/client/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> update(@PathVariable("id") Long id, @RequestBody Client client) {
        LOGGER.info("Updating client with id {}", id);
        Optional<Client> currentClient = clientService.findById(id);
        if (!currentClient.isPresent()) {
            LOGGER.error("Unable to update. Client with id {} not found.", id);
            return new ResponseEntity<>(new CustomErrorType("Unable to update. Client with id " + id + " not found."),
                    HttpStatus.NOT_FOUND);
        }
        currentClient.get().setEmail(client.getEmail());
        currentClient.get().setName(client.getName());
        currentClient.get().setLastName(client.getLastName());
        currentClient.get().setSurName(client.getSurName());
        currentClient.get().setTelephone(client.getTelephone());
        clientService.save(currentClient.get());
        return new ResponseEntity<>(currentClient.get(), HttpStatus.OK);
    }

    @DeleteMapping(value = "/client/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> delete(@PathVariable("id") Long id) {
        LOGGER.info("Deleting client with id {}", id);
        Optional<Client> client = clientService.findById(id);
        if (!client.isPresent()) {
            LOGGER.error("Unable to delete. Client with id {} not found.", id);
            return new ResponseEntity<>(new CustomErrorType("Unable to delete. Client with id " + id + " not found."),
                    HttpStatus.NOT_FOUND);
        }
        clientService.deleteById(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @GetMapping(value = "/client")
    public Iterable<Client> search(@RequestParam(name = "name", required = false) String name,
                                   @RequestParam(name = "email", required = false) String email,
                                   @RequestParam(name = "telephone", required = false) String telephone) {
        return clientService.find(name, email, telephone);
    }

}
