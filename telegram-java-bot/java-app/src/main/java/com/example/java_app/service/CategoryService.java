package com.example.java_app.service;

import com.example.java_app.dto.CategoryDto;
import com.example.java_app.model.Category;
import com.example.java_app.repository.CategoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class CategoryService {
    private final CategoryRepository categoryRepository;

    @Autowired
    public CategoryService(CategoryRepository categoryRepository) {
        this.categoryRepository = categoryRepository;
    }

    public List<Category> getAllCategories() {
        return categoryRepository.findAll();
    }

    private CategoryDto convertToDto(Category category) {
        return new CategoryDto(category.getId(), category.getCategoryName());
    }

    public List<CategoryDto> getAllCategoryDto() {
        List<Category> categoryList = categoryRepository.findAll();
        return categoryList.stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    public Optional<Category> getCategoryById(Long id) {
        return categoryRepository.findById(id);
    }

    public Category saveCategory(Category category) {
        return categoryRepository.save(category);
    }

    public void deleteCategory(Long id) {
        categoryRepository.deleteById(id);
    }
}
