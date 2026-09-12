import { User } from "../types";

export const mock_korisnici: User[] = [
    {
        id: 1,
        ime: "Anja",
        prezime: "Antić",
        korisnickoIme: "anja_admin",
        uloga: "administrator",
        banovan: false
    },
    {
        id: 2,
        ime: "Marko",
        prezime: "Marković",
        korisnickoIme: "marko99",
        uloga: "ulogovan",
        banovan: false
    },
    {
        id: 3,
        ime: "Jelena",
        prezime: "Jovanović",
        korisnickoIme: "jelena_jov",
        uloga: "ulogovan",
        banovan: false
    },
    {
        id: 4,
        ime: "Stefan",
        prezime: "Stanković",
        korisnickoIme: "stefan_s",
        uloga: "ulogovan",
        banovan: true
    },
    {
        id: 5,
        ime: "Milica",
        prezime: "Milić",
        korisnickoIme: "milica_m",
        uloga: "administrator",
        banovan: false
    },
    {
        id: 6,
        ime: "Nikola",
        prezime: "Nikolić",
        korisnickoIme: "nikola_n",
        uloga: "ulogovan",
        banovan: false
    },
    {
        id: 7,
        ime: "Sara",
        prezime: "Sarić",
        korisnickoIme: "sara_s",
        uloga: "ulogovan",
        banovan: true
    },
    {
        id: 8,
        ime: "Pavle",
        prezime: "Pavlović",
        korisnickoIme: "paja_p",
        uloga: "ulogovan",
        banovan: false
    },
    {
        id: 9,
        ime: "Maja",
        prezime: "Majkić",
        korisnickoIme: "maja_m",
        uloga: "administrator",
        banovan: false
    },
    {
        id: 10,
        ime: "Igor",
        prezime: "Ignić",
        korisnickoIme: "igor_i",
        uloga: "ulogovan",
        banovan: true
    }
];