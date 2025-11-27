package org.smartshop.smartshop.service.interf;

import org.smartshop.smartshop.dto.clientdto.ClientResponseDTO;
import org.smartshop.smartshop.dto.clientdto.CreateClientRequestDTO;
import org.smartshop.smartshop.dto.clientdto.UpdateClientRequestDTO;

import java.util.List;

public interface ClientService {

    ClientResponseDTO createClient(CreateClientRequestDTO dto);

    ClientResponseDTO findById(Long id);

    ClientResponseDTO findByEmail(String email);

    List<ClientResponseDTO> findAll();

    ClientResponseDTO updateClient(Long id, UpdateClientRequestDTO dto);

    void deleteClient(Long id);

}

