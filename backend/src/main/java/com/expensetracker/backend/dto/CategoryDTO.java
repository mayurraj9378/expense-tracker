package com.expensetracker.backend.dto;

import jakarta.validation.constraints.NotBlank;

public class CategoryDTO {
    private Long id;
    
    @NotBlank(message = "Category name is required")
    private String name;
    
    private String icon;
    private String color;
    private Boolean isDefault;
    private Long userId;

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    
    public String getIcon() { return icon; }
    public void setIcon(String icon) { this.icon = icon; }
    
    public String getColor() { return color; }
    public void setColor(String color) { this.color = color; }
    
    public Boolean getIsDefault() { return isDefault; }
    public void setIsDefault(Boolean isDefault) { this.isDefault = isDefault; }
    
    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }

    // Builder Pattern
    public static CategoryDTOBuilder builder() {
        return new CategoryDTOBuilder();
    }

    public static class CategoryDTOBuilder {
        private CategoryDTO dto = new CategoryDTO();

        public CategoryDTOBuilder id(Long id) { dto.id = id; return this; }
        public CategoryDTOBuilder name(String name) { dto.name = name; return this; }
        public CategoryDTOBuilder icon(String icon) { dto.icon = icon; return this; }
        public CategoryDTOBuilder color(String color) { dto.color = color; return this; }
        public CategoryDTOBuilder isDefault(Boolean isDefault) { dto.isDefault = isDefault; return this; }
        public CategoryDTOBuilder userId(Long userId) { dto.userId = userId; return this; }

        public CategoryDTO build() { return dto; }
    }
}