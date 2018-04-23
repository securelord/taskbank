package com.tasbank.service;

import com.tasbank.model.Client;
import com.tasbank.repository.ClientRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class ClientService implements IClientService {

    private ClientRepository clientRepository;

    public ClientService(ClientRepository clientRepository) {
        this.clientRepository = clientRepository;
    }

    @Override
    public boolean isClientExist(Client client) {
        return clientRepository.existsById(client.getId());
    }

    @Override
    public Client save(Client client) {
        return clientRepository.save(client);
    }

    @Override
    public void deleteById(Long id) {
        clientRepository.deleteById(id);
    }

    @Override
    public Iterable<Client> getAll() {
        return clientRepository.findAll();
    }

    @Override
    public Optional<Client> findById(Long id) {
        return clientRepository.findById(id);
    }

    @Override
    public Optional<Client> findByName(String name) {
        return Optional.empty();
    }

    @Override
    public Optional<Client> createClient(Client client) {
        return Optional.empty();
    }

    @Override
    public Page<Client> findPaginated(Pageable pageable) {
        return clientRepository.findAll(pageable);
    }

    @Override
    public Iterable<Client> find(String name, String email, String telephone) {
        return clientRepository.findByNameOrEmailOrTelephone(name, email, telephone);
    }
}
