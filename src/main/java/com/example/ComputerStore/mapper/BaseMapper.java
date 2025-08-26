package com.example.ComputerStore.mapper;

import java.util.List;

/**
 * Base mapper interface for converting between Entity and DTO
 * @param <E> Entity type
 * @param <D> DTO type
 */
public interface BaseMapper<E, D> {
    
    /**
     * Convert Entity to DTO
     * @param entity the entity to convert
     * @return the corresponding DTO
     */
    D toDto(E entity);
    
    /**
     * Convert DTO to Entity
     * @param dto the DTO to convert
     * @return the corresponding Entity
     */
    E toEntity(D dto);
    
    /**
     * Convert List of Entities to List of DTOs
     * @param entities the list of entities to convert
     * @return the list of corresponding DTOs
     */
    List<D> toDtoList(List<E> entities);
    
    /**
     * Convert List of DTOs to List of Entities
     * @param dtos the list of DTOs to convert
     * @return the list of corresponding Entities
     */
    List<E> toEntityList(List<D> dtos);
}
