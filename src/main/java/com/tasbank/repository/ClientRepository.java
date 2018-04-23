package com.tasbank.repository;

import com.tasbank.model.Client;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.List;

public interface ClientRepository extends JpaRepository<Client, Long>, JpaSpecificationExecutor<Client> {

    List<Client> findByNameOrEmailOrTelephone(String name, String email, String telephone);

}
