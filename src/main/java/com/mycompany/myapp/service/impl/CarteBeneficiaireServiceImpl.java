package com.mycompany.myapp.service.impl;

import com.mycompany.myapp.domain.CarteBeneficiaire;
import com.mycompany.myapp.repository.CarteBeneficiaireRepository;
import com.mycompany.myapp.service.CarteBeneficiaireService;
import com.mycompany.myapp.service.dto.CarteBeneficiaireDTO;
import com.mycompany.myapp.service.mapper.CarteBeneficiaireMapper;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service Implementation for managing {@link com.mycompany.myapp.domain.CarteBeneficiaire}.
 */
@Service
@Transactional
public class CarteBeneficiaireServiceImpl implements CarteBeneficiaireService {

    private static final Logger LOG = LoggerFactory.getLogger(CarteBeneficiaireServiceImpl.class);

    private final CarteBeneficiaireRepository carteBeneficiaireRepository;

    private final CarteBeneficiaireMapper carteBeneficiaireMapper;

    public CarteBeneficiaireServiceImpl(
        CarteBeneficiaireRepository carteBeneficiaireRepository,
        CarteBeneficiaireMapper carteBeneficiaireMapper
    ) {
        this.carteBeneficiaireRepository = carteBeneficiaireRepository;
        this.carteBeneficiaireMapper = carteBeneficiaireMapper;
    }

    @Override
    public CarteBeneficiaireDTO save(CarteBeneficiaireDTO carteBeneficiaireDTO) {
        LOG.debug("Request to save CarteBeneficiaire : {}", carteBeneficiaireDTO);
        CarteBeneficiaire carteBeneficiaire = carteBeneficiaireMapper.toEntity(carteBeneficiaireDTO);
        carteBeneficiaire = carteBeneficiaireRepository.save(carteBeneficiaire);
        return carteBeneficiaireMapper.toDto(carteBeneficiaire);
    }

    @Override
    public CarteBeneficiaireDTO update(CarteBeneficiaireDTO carteBeneficiaireDTO) {
        LOG.debug("Request to update CarteBeneficiaire : {}", carteBeneficiaireDTO);
        CarteBeneficiaire carteBeneficiaire = carteBeneficiaireMapper.toEntity(carteBeneficiaireDTO);
        carteBeneficiaire = carteBeneficiaireRepository.save(carteBeneficiaire);
        return carteBeneficiaireMapper.toDto(carteBeneficiaire);
    }

    @Override
    public Optional<CarteBeneficiaireDTO> partialUpdate(CarteBeneficiaireDTO carteBeneficiaireDTO) {
        LOG.debug("Request to partially update CarteBeneficiaire : {}", carteBeneficiaireDTO);

        return carteBeneficiaireRepository
            .findById(carteBeneficiaireDTO.getId())
            .map(existingCarteBeneficiaire -> {
                carteBeneficiaireMapper.partialUpdate(existingCarteBeneficiaire, carteBeneficiaireDTO);

                return existingCarteBeneficiaire;
            })
            .map(carteBeneficiaireRepository::save)
            .map(carteBeneficiaireMapper::toDto);
    }

    public Page<CarteBeneficiaireDTO> findAllWithEagerRelationships(Pageable pageable) {
        return carteBeneficiaireRepository.findAllWithEagerRelationships(pageable).map(carteBeneficiaireMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<CarteBeneficiaireDTO> findOne(Long id) {
        LOG.debug("Request to get CarteBeneficiaire : {}", id);
        return carteBeneficiaireRepository.findOneWithEagerRelationships(id).map(carteBeneficiaireMapper::toDto);
    }

    @Override
    public void delete(Long id) {
        LOG.debug("Request to delete CarteBeneficiaire : {}", id);
        carteBeneficiaireRepository.deleteById(id);
    }
}
