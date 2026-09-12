import { Sala } from "../types";

export const mock_sale: Sala[] = [
    {
        id: 1,
        naziv: "Velika Konferencijska Sala",
        slug: "velika-konferencijska-sala",
        kapacitet: 150,
        lokacija: "Beograd, Vračar",
        slike:"/slike/konfverencijskaSala.jpg"
        ,
        opis: "Moderna sala opremljena najnovijom audio-vizuelnom tehnikom, idealna za velike seminare.",
        karakteristike: [
            { id: 2, naziv: "Projektor" },
            { id: 1, naziv: "Klima uređaj" },
            { id: 5, naziv: "Brzi internet (Wi-Fi)" }
        ],
        tipoviDogadjaja: [
            { id: 2, naziv: "Konferencija" },
            { id: 6, naziv: "Seminar" }
        ]
    },
    {
        id: 2,
        naziv: "Plavi Salon",
        slug: "plavi-salon",
        kapacitet: 30,
        lokacija: "Novi Sad, Centar",
        slike:
            "/slike/plaviSalon.jpg"
        ,
        opis: "Intiman prostor pogodan za poslovne sastanke i manje radionice.",
        karakteristike: [
            { id: 1, naziv: "Klima uređaj" },
            { id: 5, naziv: "Brzi internet (Wi-Fi)" },
            { id: 3, naziv: "Bela tabla" }
        ],
        tipoviDogadjaja: [
            { id: 1, naziv: "Poslovni sastanak" },
            { id: 3, naziv: "Radionica (Workshop)" }
        ]
    },
    {
        id: 3,
        naziv: "IT Lab 404",
        slug: "it-lab-404",
        kapacitet: 30,
        lokacija: "Niš, Medijana",
        slike:
            "/slike/ITLab.jpg"
        ,
        opis: "Sala sa računarskom opremom za tehničke obuke.",
        karakteristike: [
            { id: 11, naziv: "Računari" },
            { id: 2, naziv: "Projektor" }
        ],
        tipoviDogadjaja: [
            { id: 3, naziv: "Radionica (Workshop)" }
        ]
    },
    {
        id: 4,
        naziv: "Amfiteatar",
        slug: "amfiteatar",
        kapacitet: 300,
        lokacija: "Beograd, Novi Beograd",
        slike:
            "/slike/amfiteatar.jpg"
        ,
        opis: "Idealan za masovna predavanja, seminare i svečane akademije.",
        karakteristike: [
            { id: 2, naziv: "Projektor" },
            { id: 1, naziv: "Klima uređaj" },
            { id: 4, naziv: "Ozvučenje" }
        ],
        tipoviDogadjaja: [
            { id: 6, naziv: "Seminar" },
            { id: 9, naziv: "Kulturni događaj" }
        ]
    },
    {
        id: 5,
        naziv: "Mala sala za sastanke",
        slug: "mala-sala-za-sastanke",
        kapacitet: 10,
        lokacija: "Beograd, Voždovac",
        slike:
            "/slike/malaSala.jpg"
        ,
        opis: "Tiha sala za brze konsultacije i timske sastanke.",
        karakteristike: [
            { id: 5, naziv: "Brzi internet (Wi-Fi)" },
            { id: 3, naziv: "Bela tabla" }
        ],
        tipoviDogadjaja: [
            { id: 1, naziv: "Poslovni sastanak" }
        ]
    },
    {
        id: 6,
        naziv: "Laboratorija za fiziku",
        slug: "laboratorija-za-fiziku",
        kapacitet: 25,
        lokacija: "Novi Sad, Liman",
        slike:
            "/slike/fizika.jpg"
        ,
        opis: "Specijalizovana sala sa laboratorijskim stolovima i opremom za eksperimente.",
        karakteristike: [
            { id: 11, naziv: "Računari" },
            { id: 5, naziv: "Brzi internet (Wi-Fi)" }
        ],
        tipoviDogadjaja: [
            { id: 3, naziv: "Radionica (Workshop)" }
        ]
    },
    {
        id: 7,
        naziv: "Sala Mali Princ",
        slug: "sala-mali-princ",
        kapacitet: 80,
        lokacija: "Beograd, Dorćol",
        slike:
            "/slike/salamaliprinc.jpg"
        ,
        opis: "Uživajte u prigušenim svetlima i toploj atmosferi našeg prostora, stvorenog za proslave koje se pamte po bliskosti, a ne po gužvi.",
        karakteristike: [
            { id: 1, naziv: "Klima uređaj" },
            { id: 4, naziv: "Ozvučenje" },
            { id: 10, naziv: "Ergonomske stolice" }
        ],
        tipoviDogadjaja: [
            { id: 5, naziv: "Venčanje" },
            { id: 9, naziv: "Kulturni događaj" }
        ]
    },
    {
        id: 8,
        naziv: "Coworking zona",
        slug: "coworking-zona",
        kapacitet: 50,
        lokacija: "Beograd, Zemun",
        slike:
            "/slike/coworking.jpg"
        ,
        opis: "Otvoren prostor za zajednički rad studenata u opuštenoj atmosferi.",
        karakteristike: [
            { id: 5, naziv: "Brzi internet (Wi-Fi)" },
            { id: 9, naziv: "Aparat za kafu" },
            { id: 10, naziv: "Ergonomske stolice" }
        ],
        tipoviDogadjaja: [
            { id: 3, naziv: "Radionica (Workshop)" },
            { id: 7, naziv: "Team building" }
        ]
    },
    {
        id: 9,
        naziv: "Sala za video konferencije",
        slug: "sala-za-video-konferencije",
        kapacitet: 15,
        lokacija: "Kragujevac, Bresnica",
        slike:
            "/slike/videoKonferencija.jpg"
        ,
        opis: "Specijalizovana sala sa kamerama visoke rezolucije za online sastanke.",
        karakteristike: [
            { id: 8, naziv: "Televizor 4K" },
            { id: 2, naziv: "Projektor" },
            { id: 5, naziv: "Brzi internet (Wi-Fi)" }
        ],
        tipoviDogadjaja: [
            { id: 1, naziv: "Poslovni sastanak" },
            { id: 8, naziv: "Prezentacija proizvoda" }
        ]
    },
    {
        id: 10,
        naziv: "Svečana dvorana",
        slug: "svecana-dvorana",
        kapacitet: 200,
        lokacija: "Beograd, Stari grad",
        slike:
            "/slike/svecanaDvorana.jpg"
        ,
        opis: "Prostrana dvorana pogodna za proslave i velike prezentacije.",
        karakteristike: [
            { id: 4, naziv: "Ozvučenje" },
            { id: 1, naziv: "Klima uređaj" },
            { id: 6, naziv: "Video nadzor" }
        ],
        tipoviDogadjaja: [
            { id: 4, naziv: "Proslava rođendana" },
            { id: 5, naziv: "Venčanje" }
        ]
    }
];