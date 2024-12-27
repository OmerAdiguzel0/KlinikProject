import { Component, OnInit } from '@angular/core';
import { DoctorService, Doctor } from '../../services/doctor.service';

@Component({
  selector: 'app-doctors',
  templateUrl: './doctors.component.html',
  styleUrls: ['./doctors.component.scss']
})
export class DoctorsComponent implements OnInit {
  doctors: Doctor[] = [];
  loading: boolean = true;
  error: string = '';
  displayDialog: boolean = false;
  selectedDoctor: Doctor | null = null;

  constructor(private doctorService: DoctorService) {}

  ngOnInit() {
    this.loadDoctors();
  }

  loadDoctors() {
    this.loading = true;
    this.doctorService.getAllDoctors().subscribe({
      next: (doctors) => {
        this.doctors = doctors;
        this.loading = false;
      },
      error: (error) => {
        this.error = 'Doktor listesi yüklenirken bir hata oluştu.';
        this.loading = false;
        console.error('Error loading doctors:', error);
      }
    });
  }

  showDetails(doctor: Doctor) {
    this.selectedDoctor = doctor;
    this.displayDialog = true;
  }
}
