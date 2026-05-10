package com.ramilastanli.docflow.core.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "config_approvers")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ConfigApprover {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "document_type")
    private DocumentConfig documentConfig;

    @Column(nullable = false)
    private String approverEmail;

    @Column(nullable = false)
    private Integer stepOrder;
}