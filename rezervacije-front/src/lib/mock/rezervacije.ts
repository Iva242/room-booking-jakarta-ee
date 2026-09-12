import { Rezervacija } from "../types";

export const MOCK_REZERVACIJE: Rezervacija[] = [
    {
        id: 1,
        pocetak: "2026-02-10T10:00:00",
        kraj: "2026-02-10T12:00:00",
        status: "potvrdjena",
        idKorisnika: 2, // Marko
        idSale: 1, // Velika sala
        idTipDogadjaja: 2 // Konferencija (Sala 1 podržava ID 2)
    },
    {
        id: 2,
        pocetak: "2026-02-11T14:00:00",
        kraj: "2026-02-11T16:00:00",
        status: "na_cekanju",
        idKorisnika: 3, // Jelena
        idSale: 2, // Plavi Salon
        idTipDogadjaja: 1 // Poslovni sastanak (Sala 2 podržava ID 1)
    },
    {
        id: 3,
        pocetak: "2026-02-12T09:00:00",
        kraj: "2026-02-12T13:00:00",
        status: "zavrsena",
        idKorisnika: 5, 
        idSale: 3, // IT Lab 404
        idTipDogadjaja: 3 // Radionica (Workshop) (Sala 3 podržava ID 3)
    },
    {
        id: 4,
        pocetak: "2026-02-15T10:00:00",
        kraj: "2026-02-15T18:00:00",
        status: "otkazana",
        idKorisnika: 6,
        idSale: 4, // Amfiteatar 1
        idTipDogadjaja: 6 // Seminar (Sala 4 podržava ID 6)
    },
    {
        id: 5,
        pocetak: "2026-02-16T12:00:00",
        kraj: "2026-02-16T14:00:00",
        status: "u_toku",
        idKorisnika: 10,
        idSale: 5, // Mala sala
        idTipDogadjaja: 1 // Poslovni sastanak (Sala 5 podržava ID 1)
    },
    {
        id: 6,
        pocetak: "2026-02-17T09:00:00",
        kraj: "2026-02-17T11:00:00",
        status: "potvrdjena",
        idKorisnika: 7,
        idSale: 6, // Laboratorija
        idTipDogadjaja: 3 // Radionica (Sala 6 podržava ID 3)
    },
    {
        id: 7,
        pocetak: "2026-02-20T17:00:00",
        kraj: "2026-02-20T22:00:00",
        status: "na_cekanju",
        idKorisnika: 8,
        idSale: 7, // Svečana sala
        idTipDogadjaja: 5 // Venčanje (Sala 7 podržava ID 5)
    },
    {
        id: 8,
        pocetak: "2026-02-21T10:00:00",
        kraj: "2026-02-21T15:00:00",
        status: "zavrsena",
        idKorisnika: 2,
        idSale: 8, // Coworking
        idTipDogadjaja: 7 // Team building (Sala 8 podržava ID 7)
    },
    {
        id: 9,
        pocetak: "2026-02-22T13:00:00",
        kraj: "2026-02-22T15:00:00",
        status: "u_toku",
        idKorisnika: 3,
        idSale: 9, // Video konf.
        idTipDogadjaja: 8 // Prezentacija proizvoda (Sala 9 podržava ID 8)
    },
    {
        id: 10,
        pocetak: "2026-02-25T19:00:00",
        kraj: "2026-02-25T23:00:00",
        status: "otkazana",
        idKorisnika: 5,
        idSale: 10, // Svečana dvorana
        idTipDogadjaja: 4 // Proslava rođendana (Sala 10 podržava ID 4)
    },
    {
        id: 11,
        pocetak: "2026-02-26T10:00:00",
        kraj: "2026-02-26T12:00:00",
        status: "potvrdjena",
        idKorisnika: 1,
        idSale: 1, // Velika sala
        idTipDogadjaja: 6 // Seminar (Sala 1 podržava ID 6)
    },
    {
        id: 12,
        pocetak: "2026-02-27T08:00:00",
        kraj: "2026-02-27T10:00:00",
        status: "na_cekanju",
        idKorisnika: 10,
        idSale: 5, // Mala sala
        idTipDogadjaja: 1 // Poslovni sastanak
    },
    {
        id: 13,
        pocetak: "2026-03-01T15:00:00",
        kraj: "2026-03-01T17:00:00",
        status: "zavrsena",
        idKorisnika: 7,
        idSale: 2, // Plavi salon
        idTipDogadjaja: 3 // Radionica
    },
    {
        id: 14,
        pocetak: "2026-03-02T11:00:00",
        kraj: "2026-03-02T14:00:00",
        status: "potvrdjena",
        idKorisnika: 3,
        idSale: 4, // Amfiteatar
        idTipDogadjaja: 9 // Kulturni događaj
    },
    {
        id: 15,
        pocetak: "2026-03-05T10:00:00",
        kraj: "2026-03-05T12:00:00",
        status: "potvrdjena",
        idKorisnika: 8,
        idSale: 9, // Video konf.
        idTipDogadjaja: 1 // Poslovni sastanak
    }
];