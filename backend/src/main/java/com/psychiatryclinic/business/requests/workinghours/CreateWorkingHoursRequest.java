package com.psychiatryclinic.business.requests.workinghours;

import com.psychiatryclinic.entities.enums.DayOfWeek;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CreateWorkingHoursRequest {
    @NotNull(message = "Doktor ID boş olamaz")
    private String doctorId;
    
    @NotNull(message = "Gün seçimi yapılmalıdır")
    private DayOfWeek dayOfWeek;
    
    @NotNull(message = "Başlangıç saati boş olamaz")
    private LocalTime startTime;
    
    @NotNull(message = "Bitiş saati boş olamaz")
    private LocalTime endTime;
    
    private boolean isAvailable = true;
    
    @Min(value = 15, message = "Randevu süresi en az 15 dakika olmalıdır")
    private int slotDurationMinutes = 30;  // Varsayılan 30 dakika
} 