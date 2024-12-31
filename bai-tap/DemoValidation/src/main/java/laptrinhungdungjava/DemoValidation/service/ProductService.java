package laptrinhungdungjava.DemoValidation.service;

import io.micrometer.common.util.StringUtils;
import laptrinhungdungjava.DemoValidation.model.Product;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class ProductService {
    private List<Product> listProduct = new ArrayList<>();
    public List<Product> getAll()
    {
        return listProduct;
    }
    public Product get(int id)
    {
        return listProduct.stream().filter(p->p.getId() == id).findFirst().orElse(null);
    }
    public void add(Product newProduct)
    {
        int maxId = listProduct.stream().mapToInt(Product::getId).max().orElse(0);
        newProduct.setId(maxId+1);
        listProduct.add(newProduct);
    }
    public void updateImage(Product newProduct, MultipartFile imageProduct)
    {
        if (!imageProduct.isEmpty()) {
            try
            {
                Path dirImages = Paths.get("static/images");
                if (!Files.exists(dirImages)) {
                    Files.createDirectories(dirImages);
                }
                String newFileName = UUID.randomUUID() + "_" + imageProduct.getOriginalFilename();
                Path pathFileUpload = dirImages.resolve(newFileName);
                Files.copy(imageProduct.getInputStream(), pathFileUpload, StandardCopyOption.REPLACE_EXISTING);
                newProduct.setImage(newFileName);
            }
            catch (IOException e) {
                e.printStackTrace(); // Handle the exception appropriately
            }
        }
    }
    public void update(Product editProduct)
    {
        Product find = get(editProduct.getId());
        if(find!= null) {
            find.setImage(editProduct.getImage());
            find.setPrice(editProduct.getPrice());
            find.setName(editProduct.getName());

        }
    }

    public List<Product> searchProducts(String query) {
        if (StringUtils.isEmpty(query)) {
            return getAll();
        }
        return getAll().stream()
                .filter(product -> product.getName().toLowerCase().contains(query.toLowerCase()))
                .collect(Collectors.toList());
    }
}
