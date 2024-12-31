package com.demo.phone_shop.repository;

import com.demo.phone_shop.model.Phone;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PhoneRepository extends JpaRepository<Phone, Long>, JpaSpecificationExecutor<Phone> {
    void deleteByCategoryId(Long categoryId);
    void deleteByBrandId(Long brandId);

    //lấy sản phẩm theo thương hiệu
    List<Phone> findByCategoryId(Long categoryId);
    List<Phone> findByBrandId(Long brandId);

    //Tìm kiếm
    @Query("SELECT p FROM Phone p WHERE p.name like %:key%")
    Page<Phone> searchProducts(@Param("key") String key, Pageable pageable);

    //Lọc + Sắp xếp
    //List<Phone> findAll(Specification<Phone> spec, Sort sort);
    Page<Phone> findByOs(String os, Pageable pageable);
    List<Phone> findAll();

    @Query("SELECT p FROM Phone p WHERE (:os IS NULL OR p.os = :os) AND (:brands IS NULL OR p.brand IN :brands)")
    List<Phone> findPhonesByOsAndBrands(@Param("os") String os, @Param("brands") List<String> brands);
}

