package com.expensetracker.backend.service;

import com.expensetracker.backend.dto.ExpenseDTO;
import com.expensetracker.backend.entity.Category;
import com.expensetracker.backend.entity.Expense;
import com.expensetracker.backend.entity.User;
import com.expensetracker.backend.repository.CategoryRepository;
import com.expensetracker.backend.repository.ExpenseRepository;
import com.expensetracker.backend.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ExpenseService {
    
    private static final Logger log = LoggerFactory.getLogger(ExpenseService.class);
    
    private final ExpenseRepository expenseRepository;
    private final UserRepository userRepository;
    private final CategoryRepository categoryRepository;

    public ExpenseService(ExpenseRepository expenseRepository, 
                          UserRepository userRepository,
                          CategoryRepository categoryRepository) {
        this.expenseRepository = expenseRepository;
        this.userRepository = userRepository;
        this.categoryRepository = categoryRepository;
    }
    
    @Transactional
    public ExpenseDTO addExpense(ExpenseDTO expenseDTO, Long userId) {
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new RuntimeException("User not found"));
        
        Expense expense = Expense.builder()
            .amount(expenseDTO.getAmount())
            .description(expenseDTO.getDescription())
            .date(expenseDTO.getDate())
            .paymentMethod(expenseDTO.getPaymentMethod())
            .user(user)
            .build();
        
        if (expenseDTO.getCategoryId() != null) {
            Category category = categoryRepository.findById(expenseDTO.getCategoryId())
                .orElseThrow(() -> new RuntimeException("Category not found"));
            expense.setCategory(category);
        }
        
        Expense savedExpense = expenseRepository.save(expense);
        log.info("💰 Expense added with ID: {}", savedExpense.getId());
        
        return convertToDTO(savedExpense);
    }
    
    public List<ExpenseDTO> getUserExpenses(Long userId) {
        return expenseRepository.findByUserIdOrderByDateDesc(userId)
            .stream()
            .map(this::convertToDTO)
            .collect(Collectors.toList());
    }
    
    public List<ExpenseDTO> getExpensesByDateRange(Long userId, LocalDate startDate, LocalDate endDate) {
        return expenseRepository.findByUserIdAndDateBetweenOrderByDateDesc(userId, startDate, endDate)
            .stream()
            .map(this::convertToDTO)
            .collect(Collectors.toList());
    }
    
    public BigDecimal getTotalExpenses(Long userId, LocalDate startDate, LocalDate endDate) {
        BigDecimal total = expenseRepository.sumExpensesByDateRange(userId, startDate, endDate);
        return total != null ? total : BigDecimal.ZERO;
    }
    
    @Transactional
    public ExpenseDTO updateExpense(Long id, ExpenseDTO expenseDTO, Long userId) {
        Expense expense = expenseRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Expense not found"));
        
        if (!expense.getUser().getId().equals(userId)) {
            throw new RuntimeException("Unauthorized access");
        }
        
        expense.setAmount(expenseDTO.getAmount());
        expense.setDescription(expenseDTO.getDescription());
        expense.setDate(expenseDTO.getDate());
        expense.setPaymentMethod(expenseDTO.getPaymentMethod());
        
        if (expenseDTO.getCategoryId() != null) {
            Category category = categoryRepository.findById(expenseDTO.getCategoryId())
                .orElseThrow(() -> new RuntimeException("Category not found"));
            expense.setCategory(category);
        }
        
        Expense updatedExpense = expenseRepository.save(expense);
        log.info("✏️ Expense updated with ID: {}", updatedExpense.getId());
        
        return convertToDTO(updatedExpense);
    }
    
    @Transactional
    public void deleteExpense(Long id, Long userId) {
        Expense expense = expenseRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Expense not found"));
        
        if (!expense.getUser().getId().equals(userId)) {
            throw new RuntimeException("Unauthorized access");
        }
        
        expenseRepository.delete(expense);
        log.info("🗑️ Expense deleted with ID: {}", id);
    }
    
    private ExpenseDTO convertToDTO(Expense expense) {
        ExpenseDTO dto = ExpenseDTO.builder()
            .id(expense.getId())
            .amount(expense.getAmount())
            .description(expense.getDescription())
            .date(expense.getDate())
            .paymentMethod(expense.getPaymentMethod())
            .userId(expense.getUser().getId())
            .build();
        
        if (expense.getCategory() != null) {
            dto.setCategoryId(expense.getCategory().getId());
            dto.setCategoryName(expense.getCategory().getName());
            dto.setCategoryColor(expense.getCategory().getColor());
            dto.setCategoryIcon(expense.getCategory().getIcon());
        }
        
        return dto;
    }
}