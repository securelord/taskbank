package com.tasbank.service;

import com.tasbank.model.Client;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;

public interface IClientService {

    boolean isClientExist(Client client);

    Client save(Client client);

    void deleteById(Long id);

    Iterable<Client> getAll();

    Optional<Client> findById(Long id);

    Optional<Client> findByName(String name);

    Optional<Client> createClient(Client client);

    Page<Client> findPaginated(Pageable pageable);

    Iterable<Client> find(String name, String email, String telephone);
}
