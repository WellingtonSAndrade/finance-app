package com.pagar.finance_api.domain.entities;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "tb_ocr_analyzer")
public class OcrAnalyzer {

    @Id
    private String taskId;
    private String imageUrl;

    @Enumerated(EnumType.STRING)
    private Status status;

    @Lob
    @Column(columnDefinition = "TEXT")
    private String receiptSchema;

    @Column(columnDefinition = "TEXT")
    private String statusError;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    public enum Status{
        PENDING, DONE, ERROR
    }
}
