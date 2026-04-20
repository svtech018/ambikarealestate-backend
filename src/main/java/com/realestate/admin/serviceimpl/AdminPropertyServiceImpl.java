package com.realestate.admin.serviceimpl;

import com.realestate.dto.AdminPropertyDTO;
import com.realestate.admin.service.AdminPropertyService;
import com.realestate.common.mapper.PropertyMapper;
import com.realestate.entity.Property;
import com.realestate.entity.PropertyImage;
import com.realestate.repository.PropertyImageRepository;
import com.realestate.repository.PropertyRepository;
import org.springframework.http.MediaType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@Transactional
public class AdminPropertyServiceImpl implements AdminPropertyService {

    private final PropertyRepository propertyRepository;
    private final PropertyImageRepository propertyImageRepository;
    private final PropertyMapper propertyMapper;

    public AdminPropertyServiceImpl(PropertyRepository propertyRepository,
            PropertyImageRepository propertyImageRepository,
            PropertyMapper propertyMapper) {
        this.propertyRepository = propertyRepository;
        this.propertyImageRepository = propertyImageRepository;
        this.propertyMapper = propertyMapper;
    }

    @Override
    public AdminPropertyDTO createProperty(AdminPropertyDTO dto) {
        Property property = propertyMapper.toEntity(dto);
        Property saved = propertyRepository.save(property);
        syncPropertyImages(saved, dto.getImageUrls());
        saved = propertyRepository.save(saved);
        return propertyMapper.toAdminDTO(saved);
    }

    @Override
    public AdminPropertyDTO updateProperty(Long propertyId, AdminPropertyDTO dto) {
        Property property = propertyRepository.findById(propertyId)
                .orElseThrow(() -> new RuntimeException("Property not found with ID: " + propertyId));

        propertyMapper.updateEntityFromDTO(property, dto);
        syncPropertyImages(property, dto.getImageUrls());

        Property saved = propertyRepository.save(property);
        return propertyMapper.toAdminDTO(saved);
    }

    @Override
    public void deleteProperty(Long propertyId) {
        Property property = propertyRepository.findById(propertyId)
                .orElseThrow(() -> new RuntimeException("Property not found with ID: " + propertyId));
        propertyRepository.delete(property);
    }

    @Override
    @Transactional(readOnly = true)
    public AdminPropertyDTO getPropertyById(Long propertyId) {
        Property property = propertyRepository.findById(propertyId)
                .orElseThrow(() -> new RuntimeException("Property not found with ID: " + propertyId));
        return propertyMapper.toAdminDTO(property);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<AdminPropertyDTO> getAllProperties(Pageable pageable) {
        return propertyRepository.findAll(pageable).map(propertyMapper::toAdminDTO);
    }

    private void syncPropertyImages(Property property, List<String> requestedImages) {
        if (requestedImages == null) {
            return;
        }

        Map<Long, PropertyImage> existingImages = property.getPropertyImages().stream()
                .filter(image -> image.getId() != null)
                .collect(Collectors.toMap(PropertyImage::getId, Function.identity()));

        List<PropertyImage> nextImages = new ArrayList<>();
        int sortOrder = 0;

        for (String requestedImage : requestedImages) {
            if (requestedImage == null || requestedImage.isBlank()) {
                continue;
            }

            Long existingId = extractImageId(requestedImage);
            if (existingId != null && existingImages.containsKey(existingId)) {
                PropertyImage existingImage = existingImages.get(existingId);
                existingImage.setSortOrder(sortOrder++);
                nextImages.add(existingImage);
                continue;
            }

            if (!requestedImage.startsWith("data:")) {
                throw new RuntimeException("Unsupported image format. Upload files from the admin UI.");
            }

            nextImages.add(parseDataUrl(property, requestedImage, sortOrder++));
        }

        property.replacePropertyImages(nextImages);
    }

    private Long extractImageId(String imageUrl) {
        String marker = "/api/properties/images/";
        int markerIndex = imageUrl.indexOf(marker);
        if (markerIndex < 0) {
            return null;
        }

        String idPart = imageUrl.substring(markerIndex + marker.length());
        int queryIndex = idPart.indexOf('?');
        if (queryIndex >= 0) {
            idPart = idPart.substring(0, queryIndex);
        }

        try {
            return Long.parseLong(idPart);
        } catch (NumberFormatException ex) {
            return null;
        }
    }

    private PropertyImage parseDataUrl(Property property, String dataUrl, int sortOrder) {
        int commaIndex = dataUrl.indexOf(',');
        int semicolonIndex = dataUrl.indexOf(';');

        if (commaIndex < 0 || semicolonIndex < 0 || !dataUrl.startsWith("data:")) {
            throw new RuntimeException("Invalid image data received");
        }

        String contentType = dataUrl.substring(5, semicolonIndex);
        String encodedPayload = dataUrl.substring(commaIndex + 1);
        byte[] imageBytes = Base64.getDecoder().decode(encodedPayload.getBytes(StandardCharsets.UTF_8));

        if (!MediaType.parseMediaType(contentType).getType().equalsIgnoreCase("image")) {
            throw new RuntimeException("Only image uploads are allowed");
        }

        return PropertyImage.builder()
                .property(property)
                .imageData(imageBytes)
                .contentType(contentType)
                .fileName("property-image-" + (sortOrder + 1))
                .sortOrder(sortOrder)
                .build();
    }
}