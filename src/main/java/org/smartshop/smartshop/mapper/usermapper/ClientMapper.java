package org.smartshop.smartshop.mapper.usermapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.smartshop.smartshop.dto.clientdto.ClientResponseDTO;
import org.smartshop.smartshop.dto.clientdto.CreateClientRequestDTO;
import org.smartshop.smartshop.dto.clientdto.UpdateClientRequestDTO;
import org.smartshop.smartshop.entity.Client;

import java.util.List;


@Mapper(componentModel = "spring")
public interface ClientMapper {

    @Mapping(source = "user.username", target = "username")
    ClientResponseDTO toResponseDTO(Client client);

    List<ClientResponseDTO> toResponseDTOList(List<Client> clients);

    @Mapping(target = "tier", ignore = true)
    @Mapping(target = "totalOrders", ignore = true)
    @Mapping(target = "totalSpent", ignore = true)
    @Mapping(target = "firstOrderDate", ignore = true)
    @Mapping(target = "lastOrderDate", ignore = true)
    @Mapping(target = "user", ignore = true)
    @Mapping(target = "orders", ignore = true)
    Client toEntity(CreateClientRequestDTO dto);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "deleted", ignore = true)
    @Mapping(target = "tier", ignore = true)
    @Mapping(target = "totalOrders", ignore = true)
    @Mapping(target = "totalSpent", ignore = true)
    @Mapping(target = "firstOrderDate", ignore = true)
    @Mapping(target = "lastOrderDate", ignore = true)
    @Mapping(target = "user", ignore = true)
    @Mapping(target = "orders", ignore = true)
    @Mapping(target = "name", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "email", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateEntityFromDTO(UpdateClientRequestDTO dto, @MappingTarget Client client);

}