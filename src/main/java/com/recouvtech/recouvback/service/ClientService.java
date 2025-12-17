package com.recouvtech.recouvback.service;

import com.recouvtech.recouvback.dao.ClientRepository;
import com.recouvtech.recouvback.dao.UtilisateurRepository;
import com.recouvtech.recouvback.dto.ClientDTO.ClientRequestDTO;
import com.recouvtech.recouvback.dto.ClientDTO.ClientResponseDTO;
import com.recouvtech.recouvback.entity.Client;
import com.recouvtech.recouvback.entity.Utilisateur;
import com.recouvtech.recouvback.mapper.ClientMapper;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ClientService {

    private final ClientRepository clientRepository;
    private final UtilisateurRepository utilisateurRepository;

    public ClientResponseDTO createClient(ClientRequestDTO dto) {
        Utilisateur agent = utilisateurRepository.findByNom(dto.getAgentName());
        if (agent == null) {
            throw new RuntimeException("Aucun agent trouvé avec ce nom : " + dto.getAgentName());
        }
        Client client = ClientMapper.fromRequestDto(dto, agent);
        Client saved = clientRepository.save(client);
        return ClientMapper.toDto(saved);
    }

    public List<ClientResponseDTO> getAllClients() {
        return clientRepository.findAll().stream()
                .map(ClientMapper::toDto)
                .collect(Collectors.toList());
    }

    public ClientResponseDTO getClientById(Long id) {
        Client client = clientRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Client introuvable"));
        return ClientMapper.toDto(client);
    }

    public ClientResponseDTO updateClient(Long id, ClientRequestDTO dto) {
        Client client = clientRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Client introuvable"));
        Utilisateur agent = null;
        if (dto.getAgentName() != null) {
            agent = utilisateurRepository.findByNom(dto.getAgentName());
            if (agent == null) {
                throw new RuntimeException("Agent non trouvé : " + dto.getAgentName());
            }
        }
        ClientMapper.updateFromRequestDto(client, dto, agent);
        return ClientMapper.toDto(clientRepository.save(client));
    }

    public void deleteClient(Long id) {
        clientRepository.deleteById(id);
    }

    /**
     * Search clients by keyword (for External Chatbot API)
     * 
     * @param query Search term (raison sociale, ICE, or telephone)
     * @return List of matching clients
     */
    public List<ClientResponseDTO> searchClients(String query) {
        return clientRepository.searchByKeyword(query).stream()
                .map(ClientMapper::toDto)
                .collect(Collectors.toList());
    }
}
