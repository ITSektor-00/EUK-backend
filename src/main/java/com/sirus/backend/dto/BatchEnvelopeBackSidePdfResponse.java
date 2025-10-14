package com.sirus.backend.dto;

import java.util.List;

public class BatchEnvelopeBackSidePdfResponse {
    
    private int totalRequests;
    private int successfulRequests;
    private int failedRequests;
    private List<BatchPdfResult> results;
    private String message;
    
    public BatchEnvelopeBackSidePdfResponse() {}
    
    public BatchEnvelopeBackSidePdfResponse(int totalRequests, int successfulRequests, int failedRequests, 
                                          List<BatchPdfResult> results, String message) {
        this.totalRequests = totalRequests;
        this.successfulRequests = successfulRequests;
        this.failedRequests = failedRequests;
        this.results = results;
        this.message = message;
    }
    
    // Getters and Setters
    public int getTotalRequests() {
        return totalRequests;
    }
    
    public void setTotalRequests(int totalRequests) {
        this.totalRequests = totalRequests;
    }
    
    public int getSuccessfulRequests() {
        return successfulRequests;
    }
    
    public void setSuccessfulRequests(int successfulRequests) {
        this.successfulRequests = successfulRequests;
    }
    
    public int getFailedRequests() {
        return failedRequests;
    }
    
    public void setFailedRequests(int failedRequests) {
        this.failedRequests = failedRequests;
    }
    
    public List<BatchPdfResult> getResults() {
        return results;
    }
    
    public void setResults(List<BatchPdfResult> results) {
        this.results = results;
    }
    
    public String getMessage() {
        return message;
    }
    
    public void setMessage(String message) {
        this.message = message;
    }
    
    public static class BatchPdfResult {
        private int requestIndex;
        private boolean success;
        private String fileName;
        private byte[] pdfBytes;
        private String errorMessage;
        
        public BatchPdfResult() {}
        
        public BatchPdfResult(int requestIndex, boolean success, String fileName, byte[] pdfBytes, String errorMessage) {
            this.requestIndex = requestIndex;
            this.success = success;
            this.fileName = fileName;
            this.pdfBytes = pdfBytes;
            this.errorMessage = errorMessage;
        }
        
        // Getters and Setters
        public int getRequestIndex() {
            return requestIndex;
        }
        
        public void setRequestIndex(int requestIndex) {
            this.requestIndex = requestIndex;
        }
        
        public boolean isSuccess() {
            return success;
        }
        
        public void setSuccess(boolean success) {
            this.success = success;
        }
        
        public String getFileName() {
            return fileName;
        }
        
        public void setFileName(String fileName) {
            this.fileName = fileName;
        }
        
        public byte[] getPdfBytes() {
            return pdfBytes;
        }
        
        public void setPdfBytes(byte[] pdfBytes) {
            this.pdfBytes = pdfBytes;
        }
        
        public String getErrorMessage() {
            return errorMessage;
        }
        
        public void setErrorMessage(String errorMessage) {
            this.errorMessage = errorMessage;
        }
    }
}
