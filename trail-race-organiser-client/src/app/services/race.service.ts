import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { environment } from '../environments/environment';
import { RaceDto } from '../model/dto';

@Injectable({
  providedIn: 'root',
})
export class RaceService {
  private readonly commandApiUrl = environment.commandApiUrl;
  private readonly queryApiUrl = environment.queryApiUrl;

  constructor(private http: HttpClient) {}

  getRaces() {
    return this.http.get<RaceDto[]>(`${this.queryApiUrl}/queries/races`);
  }

  addRace(race: { name: string; distance: string }) {
    return this.http.post(`${this.commandApiUrl}/commands/races`, race);
  }

  updateRace(id: string, race: { name: string; distance: string }) {
    return this.http.patch(`${this.commandApiUrl}/commands/races/${id}`, race);
  }

  deleteRace(id: string) {
    return this.http.delete(`${this.commandApiUrl}/commands/races/${id}`);
  }
}
