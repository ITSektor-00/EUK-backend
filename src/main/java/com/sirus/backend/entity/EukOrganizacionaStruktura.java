package com.sirus.backend.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "organizaciona_struktura", schema = "euk")
public class EukOrganizacionaStruktura {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "naziv", nullable = false, unique = true, length = 100)
    private String naziv;
    
    @Column(name = "opis", columnDefinition = "TEXT")
    private String opis;
    
    @Column(name = "created_at")
    private LocalDateTime createdAt;
    
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
    
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }
    
    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
    
    // Constructors
    public EukOrganizacionaStruktura() {}
    
    public EukOrganizacionaStruktura(String naziv, String opis) {
        this.naziv = naziv;
        this.opis = opis;
    }
    
    // Getters and Setters
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
    public String getNaziv() {
        return naziv;
    }
    
    public void setNaziv(String naziv) {
        this.naziv = naziv;
    }
    
    public String getOpis() {
        return opis;
    }
    
    public void setOpis(String opis) {
        this.opis = opis;
    }
    
    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
    
    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
    
    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }
    
    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
}
