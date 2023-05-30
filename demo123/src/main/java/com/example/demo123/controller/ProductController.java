package com.example.demo123.controller;

import com.example.demo123.models.Product;
import com.example.demo123.services.ProductService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;

import javax.validation.Valid;
import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.UUID;

@Controller
@RequestMapping("/products")
public class ProductController {
    @Autowired
    private ProductService productService;

    @GetMapping("")
    public String index(Model model){
        model.addAttribute("listproduct", productService.GetAll());
        return "products/index";
    }

    @GetMapping("/create")
    public String create(Model model){
        model.addAttribute("product", new Product());
        return "products/create";
    }

    @PostMapping("/create")
    public String create(@Valid Product newProduct, @RequestParam MultipartFile imageProduct, BindingResult result, Model model){
        if(result.hasErrors()){
            model.addAttribute("product", newProduct);
            return "products/create";
        }
        if(imageProduct != null && imageProduct.getSize() > 0){
            try{
                byte[] bytes = imageProduct.getBytes();
                File saveFile = new ClassPathResource("static/images").getFile();
                String newImageFile = UUID.randomUUID() + ".png";
                Path path = Paths.get(saveFile.getAbsolutePath() + File.separator + newImageFile);
                Files.write(path, bytes);
                Files.copy(imageProduct.getInputStream(), path, StandardCopyOption.REPLACE_EXISTING);
                newProduct.setImage(newImageFile);
            }catch(Exception ex){
                ex.printStackTrace();
            }
        }
        productService.Add(newProduct);
        return "redirect:/products";
    }

    @RequestMapping("/edit/{id}")
    public ModelAndView showEditProductPage(@PathVariable(name = "id") int id) {
        ModelAndView mav = new ModelAndView("products/edit");
        Product product = productService.get(id);
        mav.addObject("product", product);

        return mav;
    }

    @RequestMapping(value = "/save", method = RequestMethod.POST)
    public String saveProduct(@ModelAttribute("product") Product product,  @RequestParam MultipartFile imageProduct, BindingResult result, Model model) {
        if(imageProduct != null && imageProduct.getSize() > 0){
            try{
                byte[] bytes = imageProduct.getBytes();
                File saveFile = new ClassPathResource("static/images").getFile();
                String newImageFile = UUID.randomUUID() + ".png";
                Path path = Paths.get(saveFile.getAbsolutePath() + File.separator + newImageFile);
                Files.write(path, bytes);
                Files.copy(imageProduct.getInputStream(), path, StandardCopyOption.REPLACE_EXISTING);
                product.setImage(newImageFile);
            }catch(Exception ex){
                ex.printStackTrace();
            }
        }
        productService.save(product);

        return "redirect:/products";
    }

    @RequestMapping("/delete/{id}")
    public String deleteProduct(@PathVariable(name = "id") int id) {
        productService.delete(id);
        return "redirect:/products";
    }

    @GetMapping("/search")
    public String search(@RequestParam("key") String key, Model model){
        model.addAttribute("listproduct", productService.search(key));
        return "/products/index";

    }
}
