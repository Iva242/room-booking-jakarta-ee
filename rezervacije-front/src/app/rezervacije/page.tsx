"use client";

import { useState, useEffect } from "react";
import { Rezervacija, Korisnik } from "@/lib/types";
import RezervacijaCard from "@/components/RezervacijaCard";
import Header from "@/components/Header";
import { api } from "@/lib/api";
import StatistikaChart from "@/components/StatistikaChart";

export default function Home() {
  const [sveRezervacije, setSveRezervacije] = useState<Rezervacija[]>([]);
  const [loading, setLoading] = useState(true);
  const [filter, setFilter] = useState("sve");
  const [currentUser, setCurrentUser] = useState<Korisnik | null>(null);

  useEffect(() => {
    const storedUserString = localStorage.getItem("ulogovan_korisnik");
    if (storedUserString) {
      const user = JSON.parse(storedUserString);
      setCurrentUser(user);

      // 1. Odluči koju metodu da pozoveš na osnovu uloge
      const fetchRezervacije =
        user.uloga === "ROLE_ADMIN"
          ? api.getAllRezervacije()
          : api.getMojeRezervacije();

      // 2. Pozovi odabranu metodu
      fetchRezervacije.then((data: any) => {
        const mapirane = data.map((r: any) => ({
          ...r, // Ovo zadržava sve originalne podatke (id, status, datum...)

          // Umesto da praviš novi objekat samo sa ID-jem,
          // samo dodeli originalni 'korisnik' objekat nazad
          korisnik: r.korisnik,

          // Ako ti treba i sala, slobodno ostavi ako je sve u redu:
          sala: r.sala || { idSale: r.idSale },
        }));

        setSveRezervacije(mapirane);
        setLoading(false);
      });
    }
  }, []);

  const handleOtkazi = async (id: number) => {
    if (!confirm("Da li ste sigurni da želite da otkažete ovu rezervaciju?"))
      return;

    try {
      await api.otkaziRezervaciju(id);

      setSveRezervacije((prev) =>
        prev.map((res) =>
          res.idRezervacije === id ? { ...res, status: "otkazana" } : res,
        ),
      );
      alert("Rezervacija je uspešno otkazana.");
    } catch (error: any) {
      alert("Greška: " + error.message);
    }
  };

  const handlePotvrdi = async (idRezervacije: number) => {
    try {
      await api.potvrdiRezervaciju(idRezervacije);

      // Lokalno osvežavamo listu da se UI odmah promeni bez učitavanja stranice
      setSveRezervacije((prev) =>
        prev.map((res) =>
          res.idRezervacije === idRezervacije
            ? { ...res, status: "potvrdjena" }
            : res,
        ),
      );
      alert("Rezervacija je uspešno potvrđena.");
    } catch (error: any) {
      alert("Greška: " + error.message);
    }
  };

  // Logika koja filtrira kartice pre nego što se prikažu
  const filtriranePoUlozi = sveRezervacije.filter((res) => {
    // Ako je admin, vidi sve
    if (currentUser?.uloga === "ROLE_ADMIN") return true;
    // Ako je običan korisnik, vidi samo one gde se idKorisnika poklapa sa njegovim ID-jem
    return res.korisnik.id === currentUser?.id;
  });

  const prikazaneRezervacije = filtriranePoUlozi.filter((res) => {
    if (filter === "sve") return true;
    return res.status?.toLowerCase() === filter.toLowerCase();
  });
  if (loading) {
    return (
      <div className="min-h-screen flex items-center justify-center text-pink-600 font-bold uppercase tracking-widest animate-pulse">
        Učitavanje rezervacija...
      </div>
    );
  }

  return (
    <main className="min-h-screen bg-gray-50 p-8">
      <Header />

      <div className="max-w-6xl mx-auto">
        {/* 1. OVDE JE MESTO ZA DINAMIČKI HEADER */}
        {currentUser?.uloga === "ROLE_ADMIN" ? (
          <header className="py-15 text-center">
            <p className="text-2xl font-black text-gray-900 uppercase tracking-[0.2em]">
              SVE REZERVACIJE
            </p>
            <div className="h-1.5 w-40 bg-pink-500 mx-auto mt-8 rounded-full"></div>
          </header>
        ) : (
          <div className="text-center mb-12 mt-6">
            <h2 className="text-4xl font-light text-pink-950 uppercase tracking-[0.3em]">
              Moje <span className="font-bold">Rezervacije</span>
            </h2>
            <div className="h-1 w-20 bg-pink-200 mx-auto mt-2 rounded-full"></div>
          </div>
        )}

        {/* 2. DUGMIĆI ZA FILTRIRANJE */}
        <div className="flex flex-wrap justify-center gap-2 mb-8">
          {[
            { label: "SVE", value: "sve" },
            { label: "NA ČEKANJU", value: "na_cekanju" },
            { label: "POTVRĐENA", value: "potvrdjena" },
            { label: "U TOKU", value: "u_toku" },
            { label: "ZAVRŠENA", value: "zavrsena" },
            { label: "OTKAZANA", value: "otkazana" },
          ].map((item) => (
            <button
              key={item.value}
              onClick={() => setFilter(item.value)}
              className={`px-4 py-2 rounded-lg font-bold transition-all ${
                filter === item.value
                  ? "bg-pink-600 text-white shadow-md"
                  : "bg-white text-gray-600 hover:bg-pink-50"
              }`}
            >
              {item.label}
            </button>
          ))}
        </div>

        {/* 3. PRIKAZ KARTICA */}
        <div className="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-3 gap-8">
          {prikazaneRezervacije.map((res) => (
            <RezervacijaCard
              key={res.idRezervacije}
              res={res}
              onOtkazi={handleOtkazi}
              onPotvrdi={handlePotvrdi}
              isAdmin={currentUser?.uloga === "ROLE_ADMIN"}
              // OVO JE TAJ NOVI DEO KOJI TREBA DA DODAS:
              isOwner={res.korisnik.id === currentUser?.id}
            />
          ))}
        </div>

        {/* 4. PORUKA KADA NEMA REZERVACIJA (samo tekst, ne header!) */}
        {prikazaneRezervacije.length === 0 && (
          <div className="text-center py-32 bg-white rounded-3xl border-2 border-dashed border-pink-100">
            <p className="text-pink-900/40 text-lg italic">
              {currentUser?.uloga === "ROLE_ADMIN"
                ? "Trenutno nema rezervacija u sistemu."
                : "Trenutno nemate svojih rezervacija."}
            </p>
          </div>
        )}

        {/* 5. GRAFIKONI (SAMO ZA ADMINA) */}
        {currentUser?.uloga === "ROLE_ADMIN" && (
          <div className="mt-20 pt-10 border-t-2 border-pink-100">
            <div className="text-center mb-8">
              <h2 className="text-2xl font-bold text-gray-800 uppercase tracking-tight">
                Statistički Izveštaj
              </h2>
            </div>
            <StatistikaChart podaci={sveRezervacije} />
          </div>
        )}
      </div>
    </main>
  );
}
