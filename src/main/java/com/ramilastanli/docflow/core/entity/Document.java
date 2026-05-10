package com.ramilastanli.docflow.core.entity;

import com.ramilastanli.docflow.common.base.BaseEntity;
import com.ramilastanli.docflow.core.entity.enums.DocumentPriority;
import com.ramilastanli.docflow.core.entity.enums.DocumentStatus;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Entity
@Table(name = "documents")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Document extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    @Column(nullable = false)
    private Long id;

    @NotBlank(message = "Başlıq boş ola bilməz")
    @Size(max = 200)
    @Column(name = "title", nullable = false)
    private String title;

    @NotBlank(message = "Məzmun boş ola bilməz")
    @Column(name = "content", columnDefinition = "TEXT")
    private String content;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private DocumentStatus status;

    @Column(name = "type", nullable = false)
    private String type;

    @Enumerated(EnumType.STRING)
    private DocumentPriority priority;

    @Email(message = "Email format should be valid")
    @Column(name = "submitter_email", nullable = false)
    private String submitterEmail;

    private Integer requiredApprovals;

    @OneToMany(mappedBy = "document", cascade = CascadeType.ALL)
    private List<ApprovalStep> approvalSteps;

    @Column(name = "file_path")
    private String filePath;

} 