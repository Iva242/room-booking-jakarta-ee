
export interface TipDogadjaja {
  idTipDogadjaja: number;
  naziv: string;
}

export interface Karakteristika {
  idKarakteristike: number;
  naziv: string;
}

export interface Sala {
  idSale: number;
  naziv: string;
  kapacitet: number;
  lokacija: string;
  opis: string;
  latitude: number;
  longitude: number;
  slika: string | null;
  karakteristike: Karakteristika[];
  tipoviDogadjaja: TipDogadjaja[];
}

export interface Korisnik {
  id: number; // U DTO-u je polje 'id'
  ime: string;
  prezime: string;
  korisnickoIme: string;
  uloga: string;
}

export interface Rezervacija {
  idRezervacije: number;
  pocetak: string; // LocalDateTime se u JSON-u šalje kao string
  kraj: string;
  status: string;
  sala: Sala; // Spring vraća ceo objekat
  tipDogadjaja: TipDogadjaja;
  korisnik: Korisnik;
}