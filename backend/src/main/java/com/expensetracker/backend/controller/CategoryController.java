package com.expensetracker.backend.controller;

import com.expensetracker.backend.dto.CategoryDTO;
import com.expensetracker.backend.entity.User;
import com.expensetracker.backend.service.CategoryService;
import com.expensetracker.backend.service.UserService;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/categories")
@CrossOrigin(origins = "*")
public class CategoryController {
    
    private static final Logger log = LoggerFactory.getLogger(CategoryController.class);
    
    private final CategoryService categoryService;
    private final UserService userService;

    public CategoryController(CategoryService categoryService, UserService userService) {
        this.categoryService = categoryService;
        this.userService = userService;
    }
    
    private Long getCurrentUserId() {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            String username = authentication.getName();
            log.info("👤 Getting user ID for: {}", username);
            
            User user = userService.getUserByUsername(username);
            log.info("✅ User ID found: {}", user.getId());
            return user.getId();
        } catch (Exception e) {
            log.error("❌ Error getting user ID: {}", e.getMessage());
            return null;
        }
    }
    
    @PostMapping
    public ResponseEntity<?> addCategory(@Valid @RequestBody CategoryDTO categoryDTO) {
        Long userId = getCurrentUserId();
        if (userId == null) {
            return ResponseEntity.status(401).body("{\"message\": \"User not authenticated\"}");
        }
        log.info("📂 Adding category for user ID: {}", userId);
        
        CategoryDTO savedCategory = categoryService.addCategory(categoryDTO, userId);
        return ResponseEntity.ok(savedCategory);
    }
    
    @GetMapping
    public ResponseEntity<?> getUserCategories() {
        Long userId = getCurrentUserId();
        if (userId == null) {
            return ResponseEntity.status(401).body("{\"message\": \"User not authenticated\"}");
        }
        log.info("📂 Fetching categories for user ID: {}", userId);
        
        List<CategoryDTO> categories = categoryService.getUserCategories(userId);
        return ResponseEntity.ok(categories);
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<?> getCategoryById(@PathVariable Long id) {
        Long userId = getCurrentUserId();
        if (userId == null) {
            return ResponseEntity.status(401).body("{\"message\": \"User not authenticated\"}");
        }
        log.info("📂 Fetching category ID: {} for user ID: {}", id, userId);
        
        CategoryDTO category = categoryService.getCategoryById(id, userId);
        return ResponseEntity.ok(category);
    }
    
    @PutMapping("/{id}")
    public ResponseEntity<?> updateCategory(@PathVariable Long id, 
                                           @Valid @RequestBody CategoryDTO categoryDTO) {
        Long userId = getCurrentUserId();
        if (userId == null) {
            return ResponseEntity.status(401).body("{\"message\": \"User not authenticated\"}");
        }
        log.info("✏️ Updating category ID: {} for user ID: {}", id, userId);
        
        CategoryDTO updatedCategory = categoryService.updateCategory(id, categoryDTO, userId);
        return ResponseEntity.ok(updatedCategory);
    }
    
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteCategory(@PathVariable Long id) {
        Long userId = getCurrentUserId();
        if (userId == null) {
            return ResponseEntity.status(401).body("{\"message\": \"User not authenticated\"}");
        }
        log.info("🗑️ Deleting category ID: {} for user ID: {}", id, userId);
        
        categoryService.deleteCategory(id, userId);
        return ResponseEntity.ok().body("{\"message\": \"Category deleted successfully\"}");
    }
    
    @PostMapping("/default")
    public ResponseEntity<?> createDefaultCategories() {
        Long userId = getCurrentUserId();
        if (userId == null) {
            return ResponseEntity.status(401).body("{\"message\": \"User not authenticated\"}");
        }
        log.info("📂 Creating default categories for user ID: {}", userId);
        
        categoryService.createDefaultCategories(userId);
        return ResponseEntity.ok().body("{\"message\": \"Default categories created successfully\"}");
    }
}