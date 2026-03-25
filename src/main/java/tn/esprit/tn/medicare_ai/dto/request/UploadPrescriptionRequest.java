package tn.esprit.tn.medicare_ai.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(name = "UploadPrescriptionRequest", description = "Multipart payload for uploading a prescription image.")
public class UploadPrescriptionRequest {
    @Schema(description = "Prescription image file.", type = "string", format = "binary")
    @NotNull
    private MultipartFile imageFile;

    @Schema(description = "Optional prescribing doctor name provided by patient.", example = "Dr. Ahmed")
    @Size(max = 120)
    private String doctorName;
}
