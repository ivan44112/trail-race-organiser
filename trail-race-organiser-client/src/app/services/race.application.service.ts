import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { environment } from '../environments/environment';
import { RaceApplicationDto } from '../model/dto';

@Injectable({
  providedIn: 'root',
})
export class RaceApplicationService {
  private readonly commandApiUrl = environment.commandApiUrl;
  private readonly queryAppiUrl = environment.queryApiUrl;

  constructor(private http: HttpClient) {}

  getApplications() {
    return this.http.get<RaceApplicationDto[]>(`${this.queryAppiUrl}/queries/applications`);
  }

  submitApplication(application: RaceApplicationDto) {
    return this.http.post(`${this.commandApiUrl}/commands/applications`, application);
  }

  deleteApplication(id: string) {
    return this.http.delete(`${this.commandApiUrl}/commands/applications/${id}`);
  }
}
