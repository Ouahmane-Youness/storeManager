package org.smartshop.smartshop.service;


import lombok.RequiredArgsConstructor;
import org.smartshop.smartshop.dto.clientdto.ClientResponseDTO;
import org.smartshop.smartshop.dto.clientdto.CreateClientRequestDTO;
import org.smartshop.smartshop.dto.clientdto.UpdateClientRequestDTO;
import org.smartshop.smartshop.entity.Client;
import org.smartshop.smartshop.entity.User;
import org.smartshop.smartshop.enums.CustomerTier;
import org.smartshop.smartshop.exception.DuplicateResourceException;
import org.smartshop.smartshop.exception.ResourceNotFoundException;
import org.smartshop.smartshop.mapper.usermapper.ClientMapper;
import org.smartshop.smartshop.repository.ClientRepository;
import org.smartshop.smartshop.repository.UserRepository;
import org.smartshop.smartshop.service.interf.ClientService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class ClientServiceImpl implements ClientService {

    private final ClientRepository clientRepository;
    private final UserRepository userRepository;
    private final ClientMapper clientMapper;

    @Override
    public ClientResponseDTO createClient(CreateClientRequestDTO dto) {
        if (clientRepository.existsByEmail(dto.getEmail())) {
            throw new DuplicateResourceException("Email already exists: " + dto.getEmail());
        }

        User user = userRepository.findById(dto.getUserId())
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + dto.getUserId()));

        Client client = clientMapper.toEntity(dto);
        client.setUser(user);
        client.setTier(CustomerTier.BASIC);
        client.setTotalOrders(0);
        client.setTotalSpent(BigDecimal.ZERO);

        Client savedClient = clientRepository.save(client);

        return clientMapper.toResponseDTO(savedClient);
    }

    @Override
    @Transactional(readOnly = true)
    public ClientResponseDTO findById(Long id) {
        Client client = clientRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Client not found with id: " + id));

        return clientMapper.toResponseDTO(client);
    }

    @Override
    @Transactional(readOnly = true)
    public ClientResponseDTO findByEmail(String email) {
        Client client = clientRepository.findByEmailAndDeletedFalse(email)
                .orElseThrow(() -> new ResourceNotFoundException("Client not found with email: " + email));

        return clientMapper.toResponseDTO(client);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ClientResponseDTO> findAll() {
        List<Client> clients = clientRepository.findByDeletedFalse();
        return clientMapper.toResponseDTOList(clients);
    }

    @Override
    public ClientResponseDTO updateClient(Long id, UpdateClientRequestDTO dto) {
        Client client = clientRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Client not found with id: " + id));

        if (dto.getEmail() != null && !dto.getEmail().equals(client.getEmail())) {
            if (clientRepository.existsByEmail(dto.getEmail())) {
                throw new DuplicateResourceException("Email already exists: " + dto.getEmail());
            }
        }

        clientMapper.updateEntityFromDTO(dto, client);
        Client updatedClient = clientRepository.save(client);

        return clientMapper.toResponseDTO(updatedClient);
    }

    @Override
    public void deleteClient(Long id) {
        Client client = clientRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Client not found with id: " + id));

        client.setDeleted(true);
        clientRepository.save(client);
    }

}