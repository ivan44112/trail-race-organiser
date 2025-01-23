import { Component, OnInit } from '@angular/core';
import { RaceService } from '../../services/race.service';
import { RaceDto } from '../../model/dto';
import { AuthService } from '../../services/auth.service';
import { FormsModule } from '@angular/forms';
import {NgClass, NgForOf, NgIf} from '@angular/common';

@Component({
  standalone: true,
  selector: 'app-administrator-dashboard',
  templateUrl: './administrator-dashboard.component.html',
  styleUrls: ['./administrator-dashboard.component.css'],
  imports: [FormsModule, NgForOf, NgIf, NgClass],
})
export class AdministratorDashboardComponent implements OnInit {
  races: RaceDto[] = [];
  newRace = { name: '', distance: 'FIVE_K' };
  editRace = { name: '', distance: '' };
  editingRaceId: string | null = null;
  distances: RaceDto['distance'][] = ['FIVE_K', 'TEN_K', 'HALF_MARATHON', 'MARATHON'];

  isModalOpen: boolean = false;
  modalMessage: string = '';
  modalType: 'success' | 'error' | 'confirmation' = 'success';
  confirmDeleteRaceId: string | null = null;

  constructor(
    private raceService: RaceService,
    private authService: AuthService
  ) {}

  ngOnInit() {
    this.fetchRaces();
  }

  fetchRaces() {
    this.raceService.getRaces().subscribe({
      next: (data) => (this.races = data),
      error: () => this.showModal('Error fetching races. Please try again.', 'error'),
    });
  }

  addRace() {
    this.raceService.addRace(this.newRace).subscribe({
      next: () => {
        this.newRace = { name: '', distance: 'FIVE_K' };
        this.showModal('Race added successfully!', 'success', () => {
          this.fetchRaces();
        }, 1000);
      },
      error: () => this.showModal('Error adding race. Please try again.', 'error'),
    });
  }

  editRaceInit(race: RaceDto) {
    this.editingRaceId = race.id;
    this.editRace = { ...race };
  }

  cancelEdit() {
    this.editingRaceId = null;
  }

  updateRace(id: string) {
    this.raceService.updateRace(id, this.editRace).subscribe({
      next: () => {
        this.fetchRaces();
        this.cancelEdit();
        this.showModal('Race updated successfully!', 'success', () => {
          this.fetchRaces();
        }, 1000);
      },
      error: () => this.showModal('Error updating race. Please try again.', 'error'),
    });
  }

  deleteRace(raceId: string) {
    this.raceService.deleteRace(raceId).subscribe({
      next: () => {
        this.showModal('Race deleted successfully!', 'success', () => {
          this.fetchRaces();
        }, 500);
      },
      error: () => {
        this.showModal('Error deleting race. Please try again.', 'error', undefined, 500);
      },
    });
  }

  confirmDelete(raceId: string) {
    this.confirmDeleteRaceId = raceId;
    this.showModal('Are you sure you want to delete this race?', 'confirmation');
  }

  confirmDeleteRace() {
    if (this.confirmDeleteRaceId) {
      this.deleteRace(this.confirmDeleteRaceId);
      this.confirmDeleteRaceId = null;
    }
    this.isModalOpen = false;
  }

  cancelDeleteRace() {
    this.confirmDeleteRaceId = null;
    this.isModalOpen = false;
  }

  logout() {
    this.authService.logout();
  }

  showModal(
    message: string,
    type: 'success' | 'error' | 'confirmation',
    callback?: () => void,
    timeoutDuration?: number
  ) {
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
