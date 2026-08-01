package com.expensetracker.backend.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;

import java.math.BigDecimal;
import java.time.LocalDate;

public class ExpenseDTO {
    private Long id;
    
    @NotNull(message = "Amount is required")
    @DecimalMin(value = "0.01", message = "Amount must be greater than 0")
    private BigDecimal amount;
    
    private String description;
    
    @NotNull(message = "Date is required")
    @PastOrPresent(message = "Date cannot be in the future")
    private LocalDate date;
    
    private String paymentMethod;
    
    private Long categoryId;
    private String categoryName;
    private String categoryColor;
    private String categoryIcon;
    
    private Long userId;

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    
    public BigDecimal getAmount() { return amount; }
    public void setAmount(BigDecimal amount) { this.amount = amount; }
    
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    
    public LocalDate getDate() { return date; }
    public void setDate(LocalDate date) { this.date = date; }
    
    public String getPaymentMethod() { return paymentMethod; }
    public void setPaymentMethod(String paymentMethod) { this.paymentMethod = paymentMethod; }
    
    public Long getCategoryId() { return categoryId; }
    public void setCategoryId(Long categoryId) { this.categoryId = categoryId; }
    
    public String getCategoryName() { return categoryName; }
    public void setCategoryName(String categoryName) { this.categoryName = categoryName; }
    
    public String getCategoryColor() { return categoryColor; }
    public void setCategoryColor(String categoryColor) { this.categoryColor = categoryColor; }
    
    public String getCategoryIcon() { return categoryIcon; }
    public void setCategoryIcon(String categoryIcon) { this.categoryIcon = categoryIcon; }
    
    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }

    // Builder Pattern
    public static ExpenseDTOBuilder builder() {
        return new ExpenseDTOBuilder();
    }

    public static class ExpenseDTOBuilder {
        private ExpenseDTO dto = new ExpenseDTO();

        public ExpenseDTOBuilder id(Long id) { dto.id = id; return this; }
        public ExpenseDTOBuilder amount(BigDecimal amount) { dto.amount = amount; return this; }
        public ExpenseDTOBuilder description(String description) { dto.description = description; return this; }
        public ExpenseDTOBuilder date(LocalDate date) { dto.date = date; return this; }
        public ExpenseDTOBuilder paymentMethod(String paymentMethod) { dto.paymentMethod = paymentMethod; return this; }
        public ExpenseDTOBuilder categoryId(Long categoryId) { dto.categoryId = categoryId; return this; }
        public ExpenseDTOBuilder categoryName(String categoryName) { dto.categoryName = categoryName; return this; }
        public ExpenseDTOBuilder categoryColor(String categoryColor) { dto.categoryColor = categoryColor; return this; }
        public ExpenseDTOBuilder categoryIcon(String categoryIcon) { dto.categoryIcon = categoryIcon; return this; }
        public ExpenseDTOBuilder userId(Long userId) { dto.userId = userId; return this; }

        public ExpenseDTO build() { return dto; }
    }
}