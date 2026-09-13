"use client";

import { useState, useEffect } from "react";
import { api } from "@/lib/api";
import { Korisnik } from "@/lib/types";
import Header from "@/components/Header";
import { useObavestenja } from "@/components/Obavestenja";

// koliko se ceka posle kucanja pre nego sto se posalje zahtev
const PAUZA_MS = 300;

export default function AdminKorisniciPage() {
  const { greska } = useObavestenja();
  const [korisnici, setKorisnici] = useState<Korisnik[]>([]);
  const [pretraga, setPretraga] = useState("");
  const [loading, setLoading] = useState(true);

  // zahtev se salje tek kad se prestane sa kucanjem
  useEffect(() => {
    const odloziPoziv = setTimeout(async () => {
      try {
        setLoading(true);
        const podaci = pretraga.trim()
          ? await api.pretraziKorisnike(pretraga.trim())
          : await api.getKorisnici();
        setKorisnici(podaci);
      } catch (e: any) {
        greska(e?.message || "Korisnici nisu učitani.");
      } finally {
        setLoading(false);
      }
    }, PAUZA_MS);

    return () => clearTimeout(odloziPoziv);
  }, [pretraga, greska]);

  return (
    <div className="min-h-screen bg-gray-50">
      <Header />
      <main className="max-w-6xl mx-auto py-12 px-4">
        <h1 className="text-3xl font-black text-pink-950 mb-8 uppercase tracking-widest">
          Pregled korisnika
        </h1>

        <div className="bg-white p-6 rounded-2xl shadow-sm mb-8 border border-pink-50">
          <input
            type="text"
            placeholder="Pretraži po imenu, prezimenu ili korisničkom imenu…"
            value={pretraga}
            onChange={(e) => setPretraga(e.target.value)}
            className="w-full p-3 rounded-xl border border-gray-100 bg-gray-50 focus:bg-white outline-none text-sm"
          />
        </div>

        <div className="bg-white rounded-2xl shadow-sm overflow-hidden border border-pink-100">
          <table className="w-full text-left border-collapse">
            <thead>
              <tr className="bg-pink-50 text-pink-900 uppercase text-xs font-bold tracking-wider">
                <th className="p-4">Ime i Prezime</th>
                <th className="p-4">Korisničko ime</th>
              </tr>
            </thead>
            <tbody>
              {korisnici.map((user) => (
                <tr
                  key={user.id}
                  className="border-b border-gray-100 hover:bg-gray-50"
                >
                  <td className="p-4 font-semibold text-gray-800">
                    {user.ime} {user.prezime}
                  </td>
                  <td className="p-4 text-gray-600">{user.korisnickoIme}</td>
                </tr>
              ))}
            </tbody>
          </table>

          {loading && (
            <div className="p-10 text-center text-pink-900/40 text-sm">
              Učitavanje…
            </div>
          )}

          {!loading && korisnici.length === 0 && (
            <div className="p-10 text-center text-pink-900/40 text-sm italic">
              {pretraga.trim()
                ? `Nema korisnika za „${pretraga.trim()}”.`
                : "Trenutno nema registrovanih korisnika."}
            </div>
          )}
        </div>

        {!loading && korisnici.length > 0 && (
          <p className="text-xs text-pink-900/40 mt-4 text-right">
            Prikazano: {korisnici.length}
          </p>
        )}
      </main>
    </div>
  );
}
