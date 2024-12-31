package com.demo.phone_shop.service;

import com.demo.phone_shop.model.Category;
import com.demo.phone_shop.model.Phone;
import com.demo.phone_shop.repository.CategoryRepository;
import com.demo.phone_shop.repository.OrderDetailRepository;
import com.demo.phone_shop.repository.PhoneRepository;
import io.micrometer.common.util.StringUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import jakarta.validation.constraints.NotNull;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class PhoneService {

    private final PhoneRepository phoneRepository;
    private final CategoryRepository categoryRepository;
    private final OrderDetailRepository orderDetailRepository;

    // Retrieve all phones from the database
    public List<Phone> getAllPhones() {
        return phoneRepository.findAll();
    }

    // Retrieve a phone by its id
    public Optional<Phone> getPhoneById(Long id) {
        return phoneRepository.findById(id);
    }

    // Add a new product to the database
    public Phone addPhone(Phone phone) {
        return phoneRepository.save(phone);
    }

    // Update an existing phone
    public Phone updatePhone(@NotNull Phone phone) {
        Phone existingProduct = phoneRepository.findById(phone.getId())
                .orElseThrow(() -> new IllegalStateException("Product with ID " +
                        phone.getId() + " does not exist."));
        existingProduct.setName(phone.getName());
        existingProduct.setPrice(phone.getPrice());
        existingProduct.setDescription(phone.getDescription());
        existingProduct.setImageUrl(phone.getImageUrl());
        existingProduct.setCategory(phone.getCategory());
        existingProduct.setBrand(phone.getBrand());
        existingProduct.setColor(phone.getColor());

        existingProduct.setScreen(phone.getScreen());
        existingProduct.setRear_camera(phone.getRear_camera());
        existingProduct.setFront_camera(phone.getFront_camera());
        existingProduct.setCpu(phone.getCpu());
        existingProduct.setMemory(phone.getMemory());
        existingProduct.setConnect(phone.getConnect());
        return phoneRepository.save(existingProduct);
    }

    // Delete a phone by its id
    public void deletePhoneById(Long id) {
        if (!phoneRepository.existsById(id)) {
            throw new IllegalStateException("Phone with ID " + id + " does not exist.");
        }
        //orderDetailRepository.deleteByPhoneId(id);
        phoneRepository.deleteById(id);
    }

    //UpdateImage
    public void update(Phone phone)
    {
        Phone existingProduct = phoneRepository.findById(phone.getId())
                .orElseThrow(() -> new IllegalStateException("Product with ID " +
                        phone.getId() + " does not exist."));
        if(existingProduct!=null){
            existingProduct.setName(phone.getName());
            existingProduct.setPrice(phone.getPrice());
            existingProduct.setDescription(phone.getDescription());
            existingProduct.setImageUrl(phone.getImageUrl());
            existingProduct.setCategory(phone.getCategory());
            existingProduct.setBrand(phone.getBrand());
            existingProduct.setColor(phone.getColor());

            existingProduct.setQuantity(phone.getQuantity());

            existingProduct.setScreen(phone.getScreen());
            existingProduct.setRear_camera(phone.getRear_camera());
            existingProduct.setFront_camera(phone.getFront_camera());
            existingProduct.setCpu(phone.getCpu());
            existingProduct.setMemory(phone.getMemory());
            existingProduct.setConnect(phone.getConnect());
        }
    }

    //Image
    public void updateImage(Phone newPhone, MultipartFile imagePhone)
    {
        if (!imagePhone.isEmpty()) {
            try
            {
                Path dirImages = Paths.get("static/images");
                if (!Files.exists(dirImages)) {
                    Files.createDirectories(dirImages);
                }
                String newFileName = UUID.randomUUID() + "_" +
                        imagePhone.getOriginalFilename();
                Path pathFileUpload = dirImages.resolve(newFileName);
                Files.copy(imagePhone.getInputStream(), pathFileUpload,
                        StandardCopyOption.REPLACE_EXISTING);
                newPhone.setImageUrl(newFileName);
            }
            catch (IOException e) {
                e.printStackTrace(); // Handle the exception appropriately
            }
        }
    }

    //Phân trang + Sort
    public Page<Phone> GetAll(int pageNo, int pageSize,
                              String sortBy, String sortDirection) {
        Sort sort = Sort.by(Sort.Direction.fromString(sortDirection), sortBy);
        PageRequest pageRequest = PageRequest.of(pageNo, pageSize, sort);
        return phoneRepository.findAll(pageRequest);
    }
    //Phân trang + Search + Sort
    public Page<Phone> GetSearchProducts(String key, int pageNo, int pageSize,
                                         String sortBy, String sortDirection) {
        Sort sort = Sort.by(Sort.Direction.fromString(sortDirection), sortBy);
        Pageable pageable = PageRequest.of(pageNo, pageSize, sort);
        return phoneRepository.searchProducts(key, pageable);
    }
    //Phân trang
    public Page<Phone> Pagination(int pageNo, int pageSize) {
        PageRequest pageRequest = PageRequest.of(pageNo, pageSize);
        return phoneRepository.findAll(pageRequest);
    }

    public Page<Phone> filterByOS(String os, int page, int size, String sortBy, String sortDirection) {
        Sort sort = sortDirection.equalsIgnoreCase(Sort.Direction.ASC.name()) ? Sort.by(sortBy).ascending()
                : Sort.by(sortBy).descending();
        Pageable pageable = PageRequest.of(page, size, sort);
        return phoneRepository.findByOs(os, pageable);
    }
    public List<Phone> filterPhonesByOsAndSort(String os, String sortDirection) {
        List<Phone> phones = phoneRepository.findAll();
        // Filter by OS if 'os' parameter is provided
        if (os != null && !os.isEmpty()) {
            phones = phones.stream()
                    .filter(phone -> phone.getOs().equalsIgnoreCase(os))
                    .collect(Collectors.toList());
        }
        // Sort phones by price based on 'sortDirection' parameter
        if (sortDirection != null) {
            Comparator<Phone> comparator = sortDirection.equalsIgnoreCase("asc") ?
                    Comparator.comparing(Phone::getPrice) :
                    Comparator.comparing(Phone::getPrice).reversed();
            phones.sort(comparator);
        }
        return phones;
    }

    public List<Phone> filterPhonesByOsAndBrandsAndSort(String os, List<String> brands, String sortDirection) {
        List<Phone> phones = phoneRepository.findPhonesByOsAndBrands(os, brands);
        // Implement sorting if necessary
        if ("asc".equalsIgnoreCase(sortDirection)) {
            phones.sort((p1, p2) -> Double.compare(p1.getPrice(), p2.getPrice()));
        } else if ("desc".equalsIgnoreCase(sortDirection)) {
            phones.sort((p1, p2) -> Double.compare(p2.getPrice(), p1.getPrice()));
        }
        return phones;
    }

    public Phone getPhoneById(long id) {
        return phoneRepository.findById(id).orElse(null);
    }

    //Lấy sản phẩm theo danh mục / thương hiệu
    public List<Phone> getPhonesByCategory(Long categoryId) {
        return phoneRepository.findByCategoryId(categoryId);
    }
    public List<Phone> getPhonesByBrand(Long brandId) {
        return phoneRepository.findByBrandId(brandId);
    }

    //Lọc sản phẩm
    public List<Phone> filterPhones(String category, String brand, Long minPrice, Long maxPrice, String ram) {
        return phoneRepository.findAll(Specification.where(categoryEquals(category))
                .and(brandEquals(brand))
                .and(priceBetween(minPrice, maxPrice))
                .and(ramEquals(ram)));
    }
    private Specification<Phone> categoryEquals(String category) {
        return StringUtils.isEmpty(category) ? null : (root, query, criteriaBuilder) ->
                criteriaBuilder.equal(root.get("category").get("name"), category);
    }
    private Specification<Phone> brandEquals(String brand) {
        return StringUtils.isEmpty(brand) ? null : (root, query, criteriaBuilder) ->
                criteriaBuilder.equal(root.get("brand").get("name"), brand);
    }
    private Specification<Phone> priceBetween(Long minPrice, Long maxPrice) {
        return (root, query, criteriaBuilder) ->
                criteriaBuilder.between(root.get("price"), minPrice, maxPrice);
    }
    private Specification<Phone> ramEquals(String ram) {
        return StringUtils.isEmpty(ram) ? null : (root, query, criteriaBuilder) ->
                criteriaBuilder.equal(root.get("memory"), ram);
    }
}



