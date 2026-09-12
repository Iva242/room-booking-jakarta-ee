package njt.NJT_Projekat.entity.auth;

import jakarta.persistence.*;
import java.time.Instant;
import java.util.UUID;

@MappedSuperclass
public abstract class AbstractVerificationToken {
    
    @Id
    @Column(name="token", nullable=false, length=36)
    private String token; 

    @Column(nullable=false, name="expires_at")
    private Instant expiresAt;
    
    public AbstractVerificationToken() {}
    
    protected void initialize(long ttlSeconds) {
        this.token = UUID.randomUUID().toString();
        this.expiresAt = Instant.now().plusSeconds(ttlSeconds);
    }
    
    public boolean isExpired() { 
        return Instant.now().isAfter(expiresAt); 
    }


    public String getToken() { return token; }
    public void setToken(String token) { this.token = token; }
    public Instant getExpiresAt() { return expiresAt; }
    public void setExpiresAt(Instant expiresAt) { this.expiresAt = expiresAt; }
}