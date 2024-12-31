package com.demo.phone_shop.service;

import com.demo.phone_shop.model.Brand;
import com.demo.phone_shop.repository.BrandRepository;
import com.demo.phone_shop.repository.PhoneRepository;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class BrandService {

    private final BrandRepository brandRepository;
    private final PhoneRepository phoneRepository;

    public List<Brand> getAllBrands() {
        return brandRepository.findAll();
    }

    public Brand getBrandById(Long id) {
        return brandRepository.findById(id).orElse(null);
    }

    public void addBrand(Brand brand) {
        brandRepository.save(brand);
    }

    public void updateBrand(@NotNull Brand brand) {
        Brand existingBrand = brandRepository.findById(brand.getId())
                .orElseThrow(() -> new IllegalStateException("Category with ID " +
                        brand.getId() + " does not exist."));
        existingBrand.setName(brand.getName());
        existingBrand.setHeadquarters(brand.getHeadquarters());
        existingBrand.setFounded_year(brand.getFounded_year());
        brandRepository.save(existingBrand);
    }

    public void deleteBrand(Long id) {
        if (!brandRepository.existsById(id)) {
            throw new IllegalStateException("Category with ID " + id + " does not exist.");
        }
        phoneRepository.deleteByBrandId(id);
        brandRepository.deleteById(id);
    }
}
