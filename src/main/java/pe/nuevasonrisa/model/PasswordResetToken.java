package pe.nuevasonrisa.model;

import java.time.LocalDateTime;

public class PasswordResetToken {

    private final int id;
    private final int usuarioId;
    private final String tokenHash;
    private final LocalDateTime expiresAt;
    private final LocalDateTime usedAt;
    private final LocalDateTime createdAt;

    public PasswordResetToken(
            int id,
            int usuarioId,
            String tokenHash,
            LocalDateTime expiresAt,
            LocalDateTime usedAt,
            LocalDateTime createdAt
    ) {
        this.id = id;
        this.usuarioId = usuarioId;
        this.tokenHash = tokenHash;
        this.expiresAt = expiresAt;
        this.usedAt = usedAt;
        this.createdAt = createdAt;
    }

    public int getId() {
        return id;
    }

    public int getUsuarioId() {
        return usuarioId;
    }

    public String getTokenHash() {
        return tokenHash;
    }

    public LocalDateTime getExpiresAt() {
        return expiresAt;
    }

    public LocalDateTime getUsedAt() {
        return usedAt;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
}
