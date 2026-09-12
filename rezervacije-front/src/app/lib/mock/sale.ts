import { Sala } from "../types";

export const mock_sale: Sala[] = [
    {
        idSale: 1,
        naziv: "Velika Konferencijska Sala",
        kapacitet: 150,
        lokacija: "Sprat 1, Blok A",
        slike:[
            "/slike/konfverencijskaSala.jpg"
        ],
        opis: "Moderna sala opremljena najnovijom audio-vizuelnom tehnikom, idealna za velike seminare.",
        karakteristike: [
            { idKarakteristika: 2, naziv: "Projektor" },
            { idKarakteristika: 1, naziv: "Klima uređaj" },
            { idKarakteristika: 5, naziv: "Brzi internet (Wi-Fi)" }
        ],
        tipoviDogadjaja: [
            { idTipDogadjaja: 2, naziv: "Konferencija" },
            { idTipDogadjaja: 6, naziv: "Seminar" }
        ]
    },
    {
        idSale: 2,
        naziv: "Plavi Salon",
        kapacitet: 30,
        lokacija: "Prizemlje",
        slike:[
            "/slike/plaviSalon.jpg"
        ],
        opis: "Intiman prostor pogodan za poslovne sastanke i manje radionice.",
        karakteristike: [
            { idKarakteristika: 1, naziv: "Klima uređaj" },
            { idKarakteristika: 5, naziv: "Brzi internet (Wi-Fi)" },
            { idKarakteristika: 3, naziv: "Bela tabla" }
        ],
        tipoviDogadjaja: [
            { idTipDogadjaja: 1, naziv: "Poslovni sastanak" },
            { idTipDogadjaja: 3, naziv: "Radionica (Workshop)" }
        ]
    },
    {
        idSale: 3,
        naziv: "IT Lab 404",
        kapacitet: 30,
        lokacija: "Sprat 4",
        slike:[
            "/slike/ITLab.jpg"
        ],
        opis: "Sala sa računarskom opremom za tehničke obuke.",
        karakteristike: [
            { idKarakteristika: 11, naziv: "Računari" },
            { idKarakteristika: 2, naziv: "Projektor" }
        ],
        tipoviDogadjaja: [
            { idTipDogadjaja: 3, naziv: "Radionica (Workshop)" }
        ]
    },
    {
        idSale: 4,
        naziv: "Amfiteatar",
        kapacitet: 300,
        lokacija: "Zgrada B, Prizemlje",
        slike:[
            "/slike/amfiteatar.jpg"
        ],
        opis: "Idealan za masovna predavanja, seminare i svečane akademije.",
        karakteristike: [
            { idKarakteristika: 2, naziv: "Projektor" },
            { idKarakteristika: 1, naziv: "Klima uređaj" },
            { idKarakteristika: 4, naziv: "Ozvučenje" }
        ],
        tipoviDogadjaja: [
            { idTipDogadjaja: 6, naziv: "Seminar" },
            { idTipDogadjaja: 9, naziv: "Kulturni događaj" }
        ]
    },
    {
        idSale: 5,
        naziv: "Mala sala za sastanke",
        kapacitet: 10,
        lokacija: "Sprat 2, Kancelarija 205",
        slike:[
            "/slike/malaSala.jpg"
        ],
        opis: "Tiha sala za brze konsultacije i timske sastanke.",
        karakteristike: [
            { idKarakteristika: 5, naziv: "Brzi internet (Wi-Fi)" },
            { idKarakteristika: 3, naziv: "Bela tabla" }
        ],
        tipoviDogadjaja: [
            { idTipDogadjaja: 1, naziv: "Poslovni sastanak" }
        ]
    },
    {
        idSale: 6,
        naziv: "Laboratorija za fiziku",
        kapacitet: 25,
        lokacija: "Suteren, Lab 01",
        slike:[
            "/slike/fizika.jpg"
        ],
        opis: "Specijalizovana sala sa laboratorijskim stolovima i opremom za eksperimente.",
        karakteristike: [
            { idKarakteristika: 11, naziv: "Računari" },
            { idKarakteristika: 5, naziv: "Brzi internet (Wi-Fi)" }
        ],
        tipoviDogadjaja: [
            { idTipDogadjaja: 3, naziv: "Radionica (Workshop)" }
        ]
    },
    {
        idSale: 7,
        naziv: "Svečana sala Rektorata",
        kapacitet: 80,
        lokacija: "Glavna zgrada, Sprat 1",
        slike:[
            "/slike/rektorat.jpg"
        ],
        opis: "Reprezentativna sala sa klasičnim nameštajem za važne prijeme i odbrane doktorata.",
        karakteristike: [
            { idKarakteristika: 1, naziv: "Klima uređaj" },
            { idKarakteristika: 4, naziv: "Ozvučenje" },
            { idKarakteristika: 10, naziv: "Ergonomske stolice" }
        ],
        tipoviDogadjaja: [
            { idTipDogadjaja: 5, naziv: "Venčanje" },
            { idTipDogadjaja: 9, naziv: "Kulturni događaj" }
        ]
    },
    {
        idSale: 8,
        naziv: "Coworking zona",
        kapacitet: 50,
        lokacija: "Biblioteka, Desno krilo",
        slike:[
            "/slike/coworking.jpg"
        ],
        opis: "Otvoren prostor za zajednički rad studenata u opuštenoj atmosferi.",
        karakteristike: [
            { idKarakteristika: 5, naziv: "Brzi internet (Wi-Fi)" },
            { idKarakteristika: 9, naziv: "Aparat za kafu" },
            { idKarakteristika: 10, naziv: "Ergonomske stolice" }
        ],
        tipoviDogadjaja: [
            { idTipDogadjaja: 3, naziv: "Radionica (Workshop)" },
            { idTipDogadjaja: 7, naziv: "Team building" }
        ]
    },
    {
        idSale: 9,
        naziv: "Sala za video konferencije",
        kapacitet: 15,
        lokacija: "Sprat 3, Tehnički blok",
        slike:[
            "/slike/videoKonferencija.jpg"
        ],
        opis: "Specijalizovana sala sa kamerama visoke rezolucije za online sastanke.",
        karakteristike: [
            { idKarakteristika: 8, naziv: "Televizor 4K" },
            { idKarakteristika: 2, naziv: "Projektor" },
            { idKarakteristika: 5, naziv: "Brzi internet (Wi-Fi)" }
        ],
        tipoviDogadjaja: [
            { idTipDogadjaja: 1, naziv: "Poslovni sastanak" },
            { idTipDogadjaja: 8, naziv: "Prezentacija proizvoda" }
        ]
    },
    {
        idSale: 10,
        naziv: "Svečana dvorana",
        kapacitet: 200,
        lokacija: "Glavni hol",
        slike:[
            "/slike/svecanaDvorana.jpg"
        ],
        opis: "Prostrana dvorana pogodna za proslave i velike prezentacije.",
        karakteristike: [
            { idKarakteristika: 4, naziv: "Ozvučenje" },
            { idKarakteristika: 1, naziv: "Klima uređaj" },
            { idKarakteristika: 6, naziv: "Video nadzor" }
        ],
        tipoviDogadjaja: [
            { idTipDogadjaja: 4, naziv: "Proslava rođendana" },
            { idTipDogadjaja: 5, naziv: "Venčanje" }
        ]
    }
];