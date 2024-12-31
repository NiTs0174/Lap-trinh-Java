package com.demo.phone_shop.controller;

import com.demo.phone_shop.model.*;
import com.demo.phone_shop.repository.PhoneRepository;
import com.demo.phone_shop.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Controller
@RequestMapping
public class HomeController {

    @Autowired
    private PhoneService phoneService;
    @Autowired
    private OrderService orderService;
    @Autowired
    private BrandService brandService;
    @Autowired
    private CartService cartService;
    @Autowired
    private CategoryService categoryService;

    @GetMapping
    public String viewBrandPage(Model model) {
        List<CartItem> cartItems = cartService.getCartItems();
        model.addAttribute("cartCount", cartItems.size());
        model.addAttribute("phones", phoneService.getAllPhones());
        return "/home/index";
    }

    @GetMapping("/admin")
    public String viewDashboard(Model model) {
        List<Order> orders = orderService.getAllOrders();
        model.addAttribute("orderCount", orders.size());

        List<Phone> phones  = phoneService.getAllPhones();
        model.addAttribute("countPhone",phones.size());

        List<Brand> brands  = brandService.getAllBrands();
        model.addAttribute("countBrand",brands.size());
        return "/home/dashboard";
    }

    @GetMapping("/search")
    public String searchPhones(Model model, @RequestParam String key,
                               @RequestParam(defaultValue = "0") int pageNo,
                               @RequestParam(defaultValue = "6") int pageSize) {
        Page<Phone> products = phoneService.GetSearchProducts(key, pageNo, pageSize,"id","desc");
        int totalPages = products.getTotalPages();
        model.addAttribute("totalPages", totalPages);

        model.addAttribute("listproduct", products);
        return "/home/list-phones";
    }

    @GetMapping("/list")
    public String viewListPage(Model model,
                               @RequestParam(defaultValue = "0") int page,
                               @RequestParam(defaultValue = "6") int size,
                               @RequestParam(defaultValue = "id") String sortBy,
                               @RequestParam(defaultValue = "asc") String sortDirection,
                               @RequestParam(required = false) String os) {
        // Giỏ hàng
        List<CartItem> cartItems = cartService.getCartItems();
        model.addAttribute("cartCount", cartItems.size());

        // Phân trang và lọc
        Page<Phone> products;
        if (os == null || os.equals("all")) {
            products = phoneService.GetAll(page, size, sortBy, sortDirection);
        } else {
            products = phoneService.filterByOS(os, page, size, sortBy, sortDirection);
        }
        model.addAttribute("totalPages", products.getTotalPages());
        model.addAttribute("listproduct", products);

        return "/home/list-phones";
    }

//    @GetMapping("/list")
//    public String viewListPage(Model model,
//                               @RequestParam(defaultValue = "0") int page,
//                                @RequestParam(defaultValue = "6") int size) {
//        //Giỏ hàng
//        List<CartItem> cartItems = cartService.getCartItems();
//        model.addAttribute("cartCount", cartItems.size());
//        //Phân trang
//        Page<Phone> products = phoneService.Pagination(page, size);
//        model.addAttribute("totalPages", products.getTotalPages());
//
//        model.addAttribute("listproduct", products);
//        //model.addAttribute("phones", phoneService.getAllPhones());
//        return "/home/list-phones";
//    }

    @GetMapping("/detail/{id}")
    public String detailPhone(@PathVariable Long id, Model model) {
        Phone phone = phoneService.getPhoneById(id)
                .orElseThrow(() -> new IllegalArgumentException("Invalid phone Id:" + id));
        model.addAttribute("phones", phone);
        return "/home/detail-phone";
    }
}
