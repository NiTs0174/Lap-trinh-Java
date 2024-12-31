package com.demo.phone_shop.controller;

import com.demo.phone_shop.model.Phone;
import com.demo.phone_shop.service.BrandService;
import com.demo.phone_shop.service.CategoryService;
import com.demo.phone_shop.service.PhoneService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Controller
@RequestMapping("/phones")
public class PhoneController {

    @Autowired
    private PhoneService phoneService;
    @Autowired
    private CategoryService categoryService;
    @Autowired
    private BrandService brandService;

    @GetMapping("/search")
    public String searchProducts(Model model, @RequestParam String key,
                                 @RequestParam(defaultValue = "0") int pageNo,
                                 @RequestParam(defaultValue = "5") int pageSize) {
        Page<Phone> products = phoneService.GetSearchProducts(key, pageNo, pageSize, "id", "desc");
        int totalPages = products.getTotalPages();
        model.addAttribute("listproduct", products);
        model.addAttribute("totalPages", totalPages);
        return "/phones/phones-list";
    }

    @GetMapping
    public String showPhoneList(Model model,
                                @RequestParam(defaultValue = "0") int page,
                                @RequestParam(defaultValue = "5") int size) {
        Page<Phone> products = phoneService.GetAll(page, size, "id", "desc");
        model.addAttribute("totalPages", products.getTotalPages());

        List<Phone> phones  = phoneService.getAllPhones();
        model.addAttribute("countPhone",phones.size());

        model.addAttribute("listproduct", products);
        return "/phones/phones-list";
    }

    @GetMapping("/add")
    public String showAddForm(Model model) {
        model.addAttribute("phone", new Phone());
        model.addAttribute("categories", categoryService.getAllCategories());
        model.addAttribute("brands", brandService.getAllBrands());
        return "/phones/add-phone";
    }

    @PostMapping("/add")
    public String addPhone(@Valid Phone phone, BindingResult result,
                           @RequestParam MultipartFile imagePhone,
                           Model model) {
        if (result.hasErrors()) {
            model.addAttribute("phone", phone);
            return "/phones/add-phone";
        }
        phoneService.updateImage(phone, imagePhone);
        phoneService.addPhone(phone);
        return "redirect:/phones";
    }

    @GetMapping("/edit/{id}")
    public String showEditForm(@PathVariable Long id, Model model) {
        Phone phone = phoneService.getPhoneById(id)
                .orElseThrow(() -> new IllegalArgumentException("Invalid phone Id:" + id));
        model.addAttribute("phone", phone);
        model.addAttribute("categories", categoryService.getAllCategories());
        model.addAttribute("brands", brandService.getAllBrands());
        return "/phones/update-phone";
    }

    @PostMapping("/update/{id}")
    public String updatePhone(@PathVariable Long id, @Valid Phone phone,
                              @RequestParam MultipartFile imagePhone,
                              BindingResult result, Model model) {
        if (result.hasErrors()) {
            phone.setId(id);
            model.addAttribute("phone", phone);
            return "/phones/update-phone";
        }
        if (imagePhone.isEmpty()) {
            // Giữ lại URL ảnh cũ nếu không có ảnh mới
            Phone existingPhone = phoneService.getPhoneById(phone.getId())
                    .orElseThrow(() -> new IllegalStateException("Phone not found"));
            phone.setImageUrl(existingPhone.getImageUrl());
        }
        phoneService.updateImage(phone, imagePhone);
        phoneService.update(phone);
        return "redirect:/phones";
    }

    @GetMapping("/delete/{id}")
    public String deletePhone(@PathVariable Long id) {
        phoneService.deletePhoneById(id);
        return "redirect:/phones";
    }

    //Lấy sản phẩm theo danh mục
    @GetMapping("/category/{categoryId}")
    public String getPhonesByCategory(@PathVariable Long categoryId, Model model) {
        List<Phone> phones = phoneService.getPhonesByCategory(categoryId);
        model.addAttribute("phones", phones);
        return "/home/phones_by_category";
    }
    @GetMapping("/brand/{brandId}")
    public String getPhonesByBrand(@PathVariable Long brandId, Model model) {
        List<Phone> phones = phoneService.getPhonesByBrand(brandId);
        model.addAttribute("phones", phones);
        return "/home/phones_by_brand";
    }

    //Lọc sản phẩm
    @GetMapping("/filter")
    public String filterPhones(@RequestParam(value = "category", required = false) String category,
                               @RequestParam(value = "brand", required = false) String brand,
                               @RequestParam(value = "minPrice", required = false) Long minPrice,
                               @RequestParam(value = "maxPrice", required = false) Long maxPrice,
                               @RequestParam(value = "ram", required = false) String ram,
                               Model model) {
        List<Phone> filteredPhones = phoneService.filterPhones(category, brand, minPrice, maxPrice, ram);
        model.addAttribute("phones", filteredPhones);
        return "/phones/filter";
    }
}
