package com.psychiatryclinic.entities;

import com.psychiatryclinic.entities.enums.DayOfWeek;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "working_hours")
public class WorkingHours {
    @Id
    private String id;
    private String doctorId;
    private DayOfWeek dayOfWeek;
    private LocalTime startTime;
    private LocalTime endTime;
    private boolean isAvailable;
    private int slotDurationMinutes;  // Her randevu slot'unun süresi (dakika)
} 