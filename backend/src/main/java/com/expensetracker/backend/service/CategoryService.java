package com.expensetracker.backend.service;

import com.expensetracker.backend.dto.CategoryDTO;
import com.expensetracker.backend.entity.Category;
import com.expensetracker.backend.entity.User;
import com.expensetracker.backend.repository.CategoryRepository;
import com.expensetracker.backend.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class CategoryService {
    
    private static final Logger log = LoggerFactory.getLogger(CategoryService.class);
    
    private final CategoryRepository categoryRepository;
    private final UserRepository userRepository;

    public CategoryService(CategoryRepository categoryRepository, UserRepository userRepository) {
        this.categoryRepository = categoryRepository;
        this.userRepository = userRepository;
    }
    
    @Transactional
    public CategoryDTO addCategory(CategoryDTO categoryDTO, Long userId) {
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new RuntimeException("User not found"));
        
        if (categoryRepository.existsByUserIdAndNameIgnoreCase(userId, categoryDTO.getName())) {
            throw new RuntimeException("Category already exists for this user");
        }
        
        Category category = Category.builder()
            .name(categoryDTO.getName())
            .icon(categoryDTO.getIcon())
            .color(categoryDTO.getColor())
            .user(user)
            .isDefault(false)
            .build();
        
        Category savedCategory = categoryRepository.save(category);
        log.info("📂 Category added with ID: {}", savedCategory.getId());
        
        return convertToDTO(savedCategory);
    }
    
    public List<CategoryDTO> getUserCategories(Long userId) {
        return categoryRepository.findByUserIdOrderByNameAsc(userId)
            .stream()
            .map(this::convertToDTO)
            .collect(Collectors.toList());
    }
    
    public CategoryDTO getCategoryById(Long id, Long userId) {
        Category category = categoryRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Category not found"));
        
        if (!category.getUser().getId().equals(userId)) {
            throw new RuntimeException("Unauthorized access");
        }
        
        return convertToDTO(category);
    }
    
    @Transactional
    public CategoryDTO updateCategory(Long id, CategoryDTO categoryDTO, Long userId) {
        Category category = categoryRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Category not found"));
        
        if (!category.getUser().getId().equals(userId)) {
            throw new RuntimeException("Unauthorized access");
        }
        
        if (category.getIsDefault()) {
            throw new RuntimeException("Cannot modify default category");
        }
        
        category.setName(categoryDTO.getName());
        category.setIcon(categoryDTO.getIcon());
        category.setColor(categoryDTO.getColor());
        
        Category updatedCategory = categoryRepository.save(category);
        log.info("✏️ Category updated with ID: {}", updatedCategory.getId());
        
        return convertToDTO(updatedCategory);
    }
    
    @Transactional
    public void deleteCategory(Long id, Long userId) {
        Category category = categoryRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Category not found"));
        
        if (!category.getUser().getId().equals(userId)) {
            throw new RuntimeException("Unauthorized access");
        }
        
        if (category.getIsDefault()) {
            throw new RuntimeException("Cannot delete default category");
        }
        
        categoryRepository.delete(category);
        log.info("🗑️ Category deleted with ID: {}", id);
    }
    
    @Transactional
    public void createDefaultCategories(Long userId) {
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new RuntimeException("User not found"));
        
        String[][] defaultCategories = {
            {"Food & Dining", "🍽️", "#FF6B6B"},
            {"Transportation", "🚗", "#4ECDC4"},
            {"Shopping", "🛍️", "#45B7D1"},
            {"Entertainment", "🎬", "#96CEB4"},
            {"Bills & Utilities", "💡", "#FFEAA7"},
            {"Healthcare", "🏥", "#DDA0DD"},
            {"Education", "📚", "#98D8C8"},
            {"Travel", "✈️", "#F7DC6F"},
            {"Insurance", "🛡️", "#BB8FCE"},
            {"Groceries", "🛒", "#85C1E9"},
            {"Rent", "🏠", "#F1948A"},
            {"Other", "📌", "#AAB7B8"}
        };
        
        for (String[] categoryData : defaultCategories) {
            if (!categoryRepository.existsByUserIdAndNameIgnoreCase(userId, categoryData[0])) {
                Category category = Category.builder()
                    .name(categoryData[0])
                    .icon(categoryData[1])
                    .color(categoryData[2])
                    .user(user)
                    .isDefault(true)
                    .build();
                categoryRepository.save(category);
            }
        }
        
        log.info("📂 Default categories created for user: {}", userId);
    }
    
    private CategoryDTO convertToDTO(Category category) {
        return CategoryDTO.builder()
            .id(category.getId())
            .name(category.getName())
            .icon(category.getIcon())
            .color(category.getColor())
            .isDefault(category.getIsDefault())
            .userId(category.getUser().getId())
            .build();
    }
}