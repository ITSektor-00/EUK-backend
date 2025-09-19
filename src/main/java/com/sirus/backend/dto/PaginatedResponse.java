package com.sirus.backend.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.springframework.data.domain.Page;

import java.util.List;

/**
 * Generic paginated response wrapper to avoid Spring Data Page serialization issues
 * @param <T> The type of content being paginated
 */
public class PaginatedResponse<T> {
    
    @JsonProperty("content")
    private List<T> content;
    
    @JsonProperty("pageable")
    private PageableInfo pageable;
    
    @JsonProperty("totalElements")
    private long totalElements;
    
    @JsonProperty("totalPages")
    private int totalPages;
    
    @JsonProperty("last")
    private boolean last;
    
    @JsonProperty("first")
    private boolean first;
    
    @JsonProperty("size")
    private int size;
    
    @JsonProperty("number")
    private int number;
    
    @JsonProperty("numberOfElements")
    private int numberOfElements;
    
    @JsonProperty("empty")
    private boolean empty;
    
    public PaginatedResponse() {}
    
    public PaginatedResponse(Page<T> page) {
        this.content = page.getContent();
        this.pageable = new PageableInfo(page);
        this.totalElements = page.getTotalElements();
        this.totalPages = page.getTotalPages();
        this.last = page.isLast();
        this.first = page.isFirst();
        this.size = page.getSize();
        this.number = page.getNumber();
        this.numberOfElements = page.getNumberOfElements();
        this.empty = page.isEmpty();
    }
    
    // Static factory method
    public static <T> PaginatedResponse<T> from(Page<T> page) {
        return new PaginatedResponse<>(page);
    }
    
    // Getters and Setters
    public List<T> getContent() {
        return content;
    }
    
    public void setContent(List<T> content) {
        this.content = content;
    }
    
    public PageableInfo getPageable() {
        return pageable;
    }
    
    public void setPageable(PageableInfo pageable) {
        this.pageable = pageable;
    }
    
    public long getTotalElements() {
        return totalElements;
    }
    
    public void setTotalElements(long totalElements) {
        this.totalElements = totalElements;
    }
    
    public int getTotalPages() {
        return totalPages;
    }
    
    public void setTotalPages(int totalPages) {
        this.totalPages = totalPages;
    }
    
    public boolean isLast() {
        return last;
    }
    
    public void setLast(boolean last) {
        this.last = last;
    }
    
    public boolean isFirst() {
        return first;
    }
    
    public void setFirst(boolean first) {
        this.first = first;
    }
    
    public int getSize() {
        return size;
    }
    
    public void setSize(int size) {
        this.size = size;
    }
    
    public int getNumber() {
        return number;
    }
    
    public void setNumber(int number) {
        this.number = number;
    }
    
    public int getNumberOfElements() {
        return numberOfElements;
    }
    
    public void setNumberOfElements(int numberOfElements) {
        this.numberOfElements = numberOfElements;
    }
    
    public boolean isEmpty() {
        return empty;
    }
    
    public void setEmpty(boolean empty) {
        this.empty = empty;
    }
    
    /**
     * Inner class to represent pageable information
     */
    public static class PageableInfo {
        @JsonProperty("pageNumber")
        private int pageNumber;
        
        @JsonProperty("pageSize")
        private int pageSize;
        
        @JsonProperty("offset")
        private long offset;
        
        @JsonProperty("paged")
        private boolean paged;
        
        @JsonProperty("unpaged")
        private boolean unpaged;
        
        public PageableInfo() {}
        
        public PageableInfo(Page<?> page) {
            this.pageNumber = page.getNumber();
            this.pageSize = page.getSize();
            this.offset = page.getPageable().getOffset();
            this.paged = page.getPageable().isPaged();
            this.unpaged = page.getPageable().isUnpaged();
        }
        
        // Getters and Setters
        public int getPageNumber() {
            return pageNumber;
        }
        
        public void setPageNumber(int pageNumber) {
            this.pageNumber = pageNumber;
        }
        
        public int getPageSize() {
            return pageSize;
        }
        
        public void setPageSize(int pageSize) {
            this.pageSize = pageSize;
        }
        
        public long getOffset() {
            return offset;
        }
        
        public void setOffset(long offset) {
            this.offset = offset;
        }
        
        public boolean isPaged() {
            return paged;
        }
        
        public void setPaged(boolean paged) {
            this.paged = paged;
        }
        
        public boolean isUnpaged() {
            return unpaged;
        }
        
        public void setUnpaged(boolean unpaged) {
            this.unpaged = unpaged;
        }
    }
}
