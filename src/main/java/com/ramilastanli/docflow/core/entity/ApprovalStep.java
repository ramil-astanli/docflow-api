package com.ramilastanli.docflow.core.entity;

import com.ramilastanli.docflow.core.entity.enums.ApprovalStatus;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "approval_steps")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ApprovalStep {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "document_id")
    private Document document;

    private String approverEmail;
    
    private Integer stepOrder;
    
    @Enumerated(EnumType.STRING)
    private ApprovalStatus status;
}