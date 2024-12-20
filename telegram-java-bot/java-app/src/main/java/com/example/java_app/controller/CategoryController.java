package com.example.java_app.controller;

import com.example.java_app.dto.CategoryDto;
import com.example.java_app.model.Category;
import com.example.java_app.service.CategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("${api.category-url}")
public class CategoryController {
    private final CategoryService categoryService;

    @Autowired
    public CategoryController(CategoryService categoryService) {
        this.categoryService = categoryService;
    }

    @GetMapping
    public ResponseEntity<List<CategoryDto>> getAllCategories() {
        List<CategoryDto> categoriesDtoList = categoryService.getAllCategoryDto();
        return ResponseEntity.ok(categoriesDtoList);
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getCategoryById(@PathVariable Long id) {
        Optional<Category> categoryOptional = categoryService.getCategoryById(id);
        if (categoryOptional.isPresent()) {
            return ResponseEntity.ok(categoryOptional.get());
        } else {
            return ResponseEntity.status(404).body("Category not found");
        }
    }

    @PostMapping
    public ResponseEntity<Category> createCategory(@RequestBody Category category) {
        Category savedCategory = categoryService.saveCategory(category);
        return ResponseEntity.status(201).body(savedCategory);
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateCategory(@PathVariable Long id, @RequestBody Category categoryDetails) {
        Optional<Category> categoryOptional = categoryService.getCategoryById(id);
        if (categoryOptional.isPresent()) {
            Category categoryToUpdate = categoryOptional.get();
            categoryToUpdate.setCategoryName(categoryDetails.getCategoryName());

            Category updatedCategory = categoryService.saveCategory(categoryToUpdate);
            return ResponseEntity.ok(updatedCategory);
        } else {
            return ResponseEntity.status(404).body("Category not found");
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteCategory(@PathVariable Long id) {
        Optional<Category> categoryOptional = categoryService.getCategoryById(id);
        if (categoryOptional.isPresent()) {
            categoryService.deleteCategory(id);
            return ResponseEntity.ok("Category deleted successfully");
        } else {
            return ResponseEntity.status(404).body("Category not found");
        }
    }
}
