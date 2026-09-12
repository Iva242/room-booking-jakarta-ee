"use client";

import { useState, useEffect, useMemo } from "react";
import { api, BASE_URL, procitajGresku } from "@/lib/api";
import SalaCard from "@/components/SalaCard";
import Link from "next/link";
import { Korisnik, Sala } from "@/lib/types";
import Header from "../components/Header";
import RezervacijaModal from "@/components/RezervacijaModal";
import DeleteSalaModal from "@/components/DeleteSalaModal";
import { useObavestenja } from "@/components/Obavestenja";

export default function HomePage() {
  const { uspeh, greska } = useObavestenja();
  const [showDeleteModal, setShowDeleteModal] = useState(false);
  const [saleIzBaze, setSaleIzBaze] = useState<Sala[]>([]);
  const [loading, setLoading] = useState(true);

  const [searchQuery, setSearchQuery] = useState("");
  const [sortOrder, setSortOrder] = useState("default");
  const [currentUser, setCurrentUser] = useState<Korisnik | null>(null);
  const [selectedSala, setSelectedSala] = useState<Sala | null>(null);

  // 2. SORTIRANJE
  const sortiraneSale = useMemo(() => {
    let temp = [...saleIzBaze];
    if (sortOrder === "az") {
      return temp.sort((a, b) => a.naziv.localeCompare(b.naziv));
    } else if (sortOrder === "za") {
      return temp.sort((a, b) => b.naziv.localeCompare(a.naziv));
    }
    return temp;
  }, [saleIzBaze, sortOrder]);

  // Učitavanje korisnika
  useEffect(() => {
    const storedUser = localStorage.getItem("ulogovan_korisnik");
    if (storedUser) {
      try {
        setCurrentUser(JSON.parse(storedUser));
      } catch (e) {
        console.error("Greška pri parsiranju korisnika");
      }
    }
  }, []);

  // 3. PRETRAGA
  useEffect(() => {
    setLoading(true);
    if (searchQuery.trim() !== "") {
      api
        .pretraziSale(searchQuery)
        .then(setSaleIzBaze)
        .catch((err) => console.error("Greška pretrage:", err))
        .finally(() => setLoading(false));
    } else {
      api
        .getAllSale()
        .then(setSaleIzBaze)
        .catch((err) => console.error("Greška učitavanja:", err))
        .finally(() => setLoading(false));
    }
  }, [searchQuery]);

  return (
    <main className="min-h-screen bg-gray-50">
      <Header />

      <div className="max-w-7xl mx-auto p-8">
        {/* PRETRAGA I SORTIRANJE */}
        <div className="bg-white p-6 rounded-2xl shadow-sm mb-12 flex items-center gap-6 border border-pink-50">
          <input
            type="text"
            placeholder="Pretraži sale..."
            value={searchQuery}
            onChange={(e) => setSearchQuery(e.target.value)}
            className="flex-1 p-3 rounded-xl border border-gray-100 bg-gray-50 focus:bg-white outline-none text-sm"
          />

          <select
            value={sortOrder}
            onChange={(e) => setSortOrder(e.target.value)}
            className="bg-gray-50 p-3 rounded-xl border border-gray-100 text-sm font-bold text-pink-950 outline-none"
          >
            <option value="default">Podrazumevano</option>
            <option value="az">A - Z</option>
            <option value="za">Z - A</option>
          </select>
        </div>

        {/* NASLOV I ADMIN DUGMIĆI */}
        <div className="flex flex-col md:flex-row items-center justify-between gap-6 mb-12 w-full">
          {/* Naslov - dodali smo text-center za mobilni, a md:text-left za desktop */}
          <div className="text-center md:text-left">
            <h2 className="text-4xl font-light text-pink-950 uppercase tracking-[0.3em]">
              Prostori za sve prilike
            </h2>
          </div>

          {/* Admin dugmići - više nisu 'absolute', već ravnopravni članovi flex-a */}
          {currentUser?.uloga === "ROLE_ADMIN" && (
            <div className="flex items-center gap-3">
              <button
                onClick={() => setShowDeleteModal(true)}
                className="bg-red-50 hover:bg-red-100 text-red-600 px-6 py-3 rounded-xl font-bold transition-all text-xs uppercase tracking-widest border border-red-100"
              >
                Obriši
              </button>
              <Link
                href="/dodaj"
                className="bg-pink-600 hover:bg-pink-700 text-white px-6 py-3 rounded-xl font-bold transition-all text-xs uppercase tracking-widest flex items-center gap-2"
              >
                + Dodaj salu
              </Link>
            </div>
          )}
        </div>
        {/* GRID */}
        <section className="py-16">
          {loading ? (
            <div className="text-center py-20">Učitavanje...</div>
          ) : (
            <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-10">
              {sortiraneSale.map((sala) => (
                <SalaCard
                  key={sala.idSale}
                  sala={sala}
                  // FIX: Rezerviši se vidi samo ako je ulogovan I NIJE ADMIN
                  isKorisnik={
                    !!currentUser && currentUser.uloga !== "ROLE_ADMIN"
                  }
                  onRezervisi={() => setSelectedSala(sala)}
                />
              ))}
            </div>
          )}
        </section>

        {/* MODALI */}
        {/* {selectedSala && (
          <RezervacijaModal
            sala={selectedSala}
            tipoviDogadjaja={selectedSala.tipoviDogadjaja}
            onClose={() => setSelectedSala(null)}
            onConfirm={() => {
              setSelectedSala(null);
            }}
          />
        )} */}
        {selectedSala && (
          <RezervacijaModal
            sala={selectedSala}
            tipoviDogadjaja={selectedSala.tipoviDogadjaja}
            onClose={() => setSelectedSala(null)}
            onConfirm={async (podaci) => {
              try {
                const token = localStorage.getItem("token");
                const res = await fetch(`${BASE_URL}/rezervacije`, {
                  method: "POST",
                  headers: {
                    "Content-Type": "application/json",
                    Authorization: `Bearer ${token}`,
                  },
                  body: JSON.stringify(podaci),
                });

                if (res.ok) {
                  uspeh("Rezervacija je poslata.");
                  setSelectedSala(null); // Zatvara modal tek nakon uspešnog slanja
                } else {
                  greska(await procitajGresku(res));
                }
              } catch (err) {
                console.error("Greška:", err);
                greska("Došlo je do greške sa serverom.");
              }
            }}
          />
        )}

        {/* {showDeleteModal && (
          <DeleteSalaModal
            sale={saleIzBaze}
            onClose={() => setShowDeleteModal(false)}
            onConfirm={(id) => {
              setSaleIzBaze((prev) => prev.filter((s) => s.idSale !== id));
              setShowDeleteModal(false);
            }}
          />
        )} */}
        {showDeleteModal && (
          <DeleteSalaModal
            sale={saleIzBaze}
            onClose={() => setShowDeleteModal(false)}
            onConfirm={async (id) => {
              // 1. Pozivamo pravi API
              await api.deleteSala(id);

              // 2. Ako je API poziv prošao bez greške (nema Exception-a),
              // tek onda ažuriramo listu na ekranu
              setSaleIzBaze((prev) => prev.filter((s) => s.idSale !== id));
            }}
          />
        )}
      </div>
    </main>
  );
}
