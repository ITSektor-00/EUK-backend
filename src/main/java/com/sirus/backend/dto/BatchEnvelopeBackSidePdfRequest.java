package com.sirus.backend.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import java.util.List;

public class BatchEnvelopeBackSidePdfRequest {
    
    @NotEmpty(message = "Lista zahteva ne može biti prazna")
    @Valid
    private List<EnvelopeBackSidePdfRequest> requests;
    
    public BatchEnvelopeBackSidePdfRequest() {}
    
    public BatchEnvelopeBackSidePdfRequest(List<EnvelopeBackSidePdfRequest> requests) {
        this.requests = requests;
    }
    
    public List<EnvelopeBackSidePdfRequest> getRequests() {
        return requests;
    }
    
    public void setRequests(List<EnvelopeBackSidePdfRequest> requests) {
        this.requests = requests;
    }
}
