package com.demo.phone_shop.controller;

import com.demo.phone_shop.model.Brand;
import com.demo.phone_shop.service.BrandService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/")
public class BrandController {

    @Autowired
    private BrandService brandService;

    @GetMapping("/brands")
    public String viewBrandPage(Model model) {
        //List<Brand> brands = brandService.getAllBrands();
        model.addAttribute("brands", brandService.getAllBrands());
        return "/brands/brands-list";
    }

    @GetMapping("/brands/add")
    public String showNewBrandForm(Model model) {
        Brand brand = new Brand();
        model.addAttribute("brand", brand);
        return "/brands/add-brand";
    }

    @PostMapping("/brands/add")
    public String addBrand(@ModelAttribute("brand") Brand brand, BindingResult result) {
        if (result.hasErrors()) {
            return "/brands/add-brand";
        }
        brandService.addBrand(brand);
        return "redirect:/brands";
    }

    @GetMapping("/brands/edit/{id}")
    public String showFormForUpdateBrand(@PathVariable(value = "id") Long id, Model model) {
        Brand brand = brandService.getBrandById(id);
        model.addAttribute("brand", brand);
        return "/brands/update-brand";
    }

    @PostMapping("/brands/update/{id}")
    public String updateBrand(@PathVariable("id") Long id, @Valid Brand brand,
                                 BindingResult result, Model model) {
        if (result.hasErrors()) {
            brand.setId(id);
            return "/brands/update-brand";
        }
        brandService.updateBrand(brand);
        model.addAttribute("brands", brandService.getAllBrands());
        return "redirect:/brands";
    }

    @GetMapping("/brands/delete/{id}")
    public String deleteBrand(@PathVariable(value = "id") Long id, Model model) {
        brandService.deleteBrand(id);
        model.addAttribute("brands", brandService.getAllBrands());
        return "redirect:/brands";
    }
}
