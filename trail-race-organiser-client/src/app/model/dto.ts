export interface RaceDto {
  id: string;
  name: string;
  distance: 'FIVE_K' | 'TEN_K' | 'HALF_MARATHON' | 'MARATHON';
}

export interface RaceApplicationDto {
  id: string;
  firstName: string;
  lastName: string;
  club: string;
  raceId: string;
}
