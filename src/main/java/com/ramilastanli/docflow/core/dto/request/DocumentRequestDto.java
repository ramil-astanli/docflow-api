package com.ramilastanli.docflow.core.dto.request;

import com.ramilastanli.docflow.core.entity.enums.DocumentPriority;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Sənəd yaratmaq üçün sorğu obyekti")
public class DocumentRequestDto {

    @Schema(example = "Maliyyə Hesabatı 2024", description = "Sənədin başlığı")
    @NotBlank(message = "Başlıq boş ola bilməz")
    @Size(min = 3, max = 200, message = "Başlıq 3-200 simvol aralığında olmalıdır")
    private String title;

    @Schema(example = "Hesabatın mətni buraya daxil edilir...", description = "Sənədin əsas məzmunu")
    @NotBlank(message = "Məzmun boş ola bilməz")
    private String content;

    @Schema(example = "FINANCIAL", description = "Sənədin növü")
    @NotNull(message = "Sənəd növü qeyd edilməlidir")
    private String type;

    @Schema(example = "user@example.com", description = "Sənədi göndərən şəxsin emaili")
    @NotBlank(message = "Göndərən email boş ola bilməz")
    @Email(message = "Düzgün email formatı daxil edin")
    private String submitterEmail;

    private Integer requiredApprovals;

    @Schema(example = "HIGH", description = "Vaciblik dərəcəsi")
    private DocumentPriority priority;

    private String filePath;
}
