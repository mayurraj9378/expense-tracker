package com.expensetracker.backend.controller;

import com.expensetracker.backend.dto.ExpenseDTO;
import com.expensetracker.backend.entity.User;
import com.expensetracker.backend.service.ExpenseService;
import com.expensetracker.backend.service.UserService;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/expenses")
@CrossOrigin(origins = "*")
public class ExpenseController {
    
    private static final Logger log = LoggerFactory.getLogger(ExpenseController.class);
    
    private final ExpenseService expenseService;
    private final UserService userService;

    public ExpenseController(ExpenseService expenseService, UserService userService) {
        this.expenseService = expenseService;
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
    public ResponseEntity<?> addExpense(@Valid @RequestBody ExpenseDTO expenseDTO) {
        Long userId = getCurrentUserId();
        if (userId == null) {
            return ResponseEntity.status(401).body("{\"message\": \"User not authenticated\"}");
        }
        log.info("📝 Adding expense for user ID: {}", userId);
        
        ExpenseDTO savedExpense = expenseService.addExpense(expenseDTO, userId);
        return ResponseEntity.ok(savedExpense);
    }
    
    @GetMapping
    public ResponseEntity<?> getUserExpenses() {
        Long userId = getCurrentUserId();
        if (userId == null) {
            return ResponseEntity.status(401).body("{\"message\": \"User not authenticated\"}");
        }
        log.info("📊 Fetching expenses for user ID: {}", userId);
        
        List<ExpenseDTO> expenses = expenseService.getUserExpenses(userId);
        return ResponseEntity.ok(expenses);
    }
    
    @GetMapping("/date-range")
    public ResponseEntity<?> getExpensesByDateRange(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        Long userId = getCurrentUserId();
        if (userId == null) {
            return ResponseEntity.status(401).body("{\"message\": \"User not authenticated\"}");
        }
        log.info("📅 Fetching expenses for user ID: {} between {} and {}", userId, startDate, endDate);
        
        List<ExpenseDTO> expenses = expenseService.getExpensesByDateRange(userId, startDate, endDate);
        return ResponseEntity.ok(expenses);
    }
    
    @GetMapping("/total")
    public ResponseEntity<?> getTotalExpenses(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        Long userId = getCurrentUserId();
        if (userId == null) {
            return ResponseEntity.status(401).body("{\"message\": \"User not authenticated\"}");
        }
        log.info("💰 Getting total expenses for user ID: {}", userId);
        
        BigDecimal total = expenseService.getTotalExpenses(userId, startDate, endDate);
        return ResponseEntity.ok().body("{\"total\": " + total + "}");
    }
    
    @PutMapping("/{id}")
    public ResponseEntity<?> updateExpense(@PathVariable Long id, 
                                          @Valid @RequestBody ExpenseDTO expenseDTO) {
        Long userId = getCurrentUserId();
        if (userId == null) {
            return ResponseEntity.status(401).body("{\"message\": \"User not authenticated\"}");
        }
        log.info("✏️ Updating expense ID: {} for user ID: {}", id, userId);
        
        ExpenseDTO updatedExpense = expenseService.updateExpense(id, expenseDTO, userId);
        return ResponseEntity.ok(updatedExpense);
    }
    
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteExpense(@PathVariable Long id) {
        Long userId = getCurrentUserId();
        if (userId == null) {
            return ResponseEntity.status(401).body("{\"message\": \"User not authenticated\"}");
        }
        log.info("🗑️ Deleting expense ID: {} for user ID: {}", id, userId);
        
        expenseService.deleteExpense(id, userId);
        return ResponseEntity.ok().body("{\"message\": \"Expense deleted successfully\"}");
    }
}