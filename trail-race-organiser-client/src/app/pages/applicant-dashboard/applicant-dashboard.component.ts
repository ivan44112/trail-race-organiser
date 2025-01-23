import { Component, OnInit } from '@angular/core';
import { RaceService } from '../../services/race.service';
import { RaceApplicationService } from '../../services/race.application.service';
import { Router } from '@angular/router';
import { RaceDto, RaceApplicationDto } from '../../model/dto';
import {NgClass, NgForOf, NgIf} from '@angular/common';
import {FormsModule} from '@angular/forms';

@Component({
  standalone: true,
  selector: 'app-applicant-dashboard',
  templateUrl: './applicant-dashboard.component.html',
  styleUrls: ['./applicant-dashboard.component.css'],
  imports: [
    NgIf,
    NgClass,
    NgForOf,
    FormsModule
  ]
})
export class ApplicantDashboardComponent implements OnInit {
  races: RaceDto[] = [];
  myApplications: RaceApplicationDto[] = [];
  showApplicationForm = false;
  application: RaceApplicationDto = {
    id: '',
    firstName: '',
    lastName: '',
    club: '',
    raceId: '',
  };

  isModalOpen: boolean = false;
  modalMessage: string = '';
  modalType: 'success' | 'error' | 'confirmation' = 'success';
  confirmDeleteApplicationId: string | null = null;

  constructor(
    private raceService: RaceService,
    private raceApplicationService: RaceApplicationService,
    private router: Router
  ) {}

  ngOnInit() {
    this.fetchRaces();
    this.fetchMyApplications();
  }

  fetchRaces() {
    this.raceService.getRaces().subscribe({
      next: (data: RaceDto[]) => (this.races = data),
      error: () => this.showModal('Error fetching races. Please try again.', 'error'),
    });
  }

  fetchMyApplications() {
    this.raceApplicationService.getApplications().subscribe({
      next: (data: RaceApplicationDto[]) => (this.myApplications = data),
      error: () => this.showModal('Error fetching applications. Please try again.', 'error'),
    });
  }

  openApplicationForm(raceId: string) {
    this.application.raceId = raceId;
    this.showApplicationForm = true;
  }

  submitApplication() {
    if (!this.application.firstName || !this.application.lastName || !this.application.club) {
      this.showModal('Please fill out all fields.', 'error');
      return;
    }

    this.raceApplicationService.submitApplication(this.application).subscribe({
      next: () => {
        this.showApplicationForm = false;
        this.application = { id: '', firstName: '', lastName: '', club: '', raceId: '' };
        this.showModal('Application submitted successfully!', 'success', () => {
          this.fetchMyApplications();
        }, 1000);
      },
      error: () => {
        this.showModal('Error submitting application. Please try again.', 'error', undefined, 1000);
      },
    });
  }

  cancelApplication() {
    this.showApplicationForm = false;
    this.application = { id: '', firstName: '', lastName: '', club: '', raceId: '' };
  }

  confirmDeleteApplication(applicationId: string) {
    this.confirmDeleteApplicationId = applicationId;
    this.showModal('Are you sure you want to delete this application?', 'confirmation');
  }

  confirmDelete() {
    if (this.confirmDeleteApplicationId) {
      this.deleteApplication(this.confirmDeleteApplicationId);
      this.confirmDeleteApplicationId = null;
    }
    this.isModalOpen = false;
  }

  cancelDelete() {
    this.confirmDeleteApplicationId = null;
    this.isModalOpen = false;
  }

  deleteApplication(applicationId: string) {
    this.raceApplicationService.deleteApplication(applicationId).subscribe({
      next: () => {
        this.showModal('Application deleted successfully!', 'success', () => {
          this.fetchMyApplications();
        }, 1000);
      },
      error: () => {
        this.showModal('Error deleting application. Please try again.', 'error', undefined, 1000);
      },
    });
  }

  logout() {
    localStorage.removeItem('token');
    this.router.navigate(['/login']);
  }

  showModal(message: string, type: 'success' | 'error' | 'confirmation', callback?: () => void, timeoutDuration?: number) {
    this.modalMessage = message;
    this.modalType = type;
    this.isModalOpen = true;

    if (timeoutDuration !== undefined && type !== 'confirmation') {
      setTimeout(() => {
        this.isModalOpen = false;
        if (callback) callback();
      }, timeoutDuration);
    }
  }
}
