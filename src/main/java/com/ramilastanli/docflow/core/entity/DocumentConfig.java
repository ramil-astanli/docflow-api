package com.ramilastanli.docflow.core.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Entity
@Table(name = "document_configs")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class DocumentConfig {

    @Id
    @Column(name = "document_type")
    private String documentType;

    @Column(name = "required_approvals", nullable = false)
    private Integer requiredApprovals;

    private String description;

    @OneToMany(mappedBy = "documentConfig", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    private List<ConfigApprover> defaultApprovers;
}