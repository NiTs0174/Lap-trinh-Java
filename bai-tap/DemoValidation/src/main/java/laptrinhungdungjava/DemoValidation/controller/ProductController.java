package laptrinhungdungjava.DemoValidation.controller;

import jakarta.validation.Valid;
import javassist.NotFoundException;
import laptrinhungdungjava.DemoValidation.model.Product;
import laptrinhungdungjava.DemoValidation.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.FileCopyUtils;
import org.springframework.util.StringUtils;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;
import java.nio.file.Paths;
@Controller
@RequestMapping("/products")
public class ProductController {
    @Autowired
    private ProductService productService;
    @GetMapping("/create")
    public String Create(Model model) {
        model.addAttribute("product", new Product());
        return "product/create";
    }
    @PostMapping("/create")
    public String Create(@Valid Product newProduct,
                         BindingResult result,
                         @RequestParam MultipartFile imageProduct,
                         Model model)  {
        if (result.hasErrors()) {
            model.addAttribute("product", newProduct);
            return "product/create";
        }
        productService.updateImage(newProduct, imageProduct);
        productService.add(newProduct);
        return "redirect:/products";
    }
    @GetMapping()
    public String Index(Model model)
    {
        model.addAttribute("listproduct", productService.getAll());
        return "product/products";
    }

    @GetMapping("/edit/{id}")
    public String Edit(@PathVariable int id, Model model) {
         Product find = productService.get(id);
         if(find == null)
             return "Product not found with ID: " + id;  //error page
         model.addAttribute("product", find);
         return "product/edit";
    }
    @PostMapping("/edit")
    public String Edit(@Valid Product editProduct,
                       BindingResult result,
                       @RequestParam MultipartFile imageProduct,
                       Model model) {
        if (result.hasErrors()) {
            model.addAttribute("product", editProduct);
            return "product/edit"; // Return to the edit form with error messages
        }
        productService.updateImage(editProduct, imageProduct);
        productService.update(editProduct);
        return "redirect:/products"; // Redirect to the products page after successful update
    }

    @GetMapping("/search")
    public String search(@RequestParam("query") String query, Model model) {
        List<Product> searchResults = productService.searchProducts(query);
        model.addAttribute("listproduct", searchResults);
        return "product/products"; // Assuming this is the same view as the list of products
    }
}
