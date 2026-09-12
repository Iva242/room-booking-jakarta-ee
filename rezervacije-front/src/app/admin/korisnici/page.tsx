"use client";

import { useState, useEffect } from "react";
import { api } from "@/lib/api";
import { Korisnik } from "@/lib/types";
import Header from "@/components/Header";

// Očistili smo interfejs jer nam više ne treba otkazane_count
export default function AdminKorisniciPage() {
  const [korisnici, setKorisnici] = useState<Korisnik[]>([]);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    const ucitajKorisnike = async () => {
      try {
        setLoading(true);
        const data = await api.getKorisnici();
        setKorisnici(data);
      } catch (error) {
        console.error("Greška pri učitavanju korisnika:", error);
      } finally {
        setLoading(false);
      }
    };
    ucitajKorisnike();
  }, []);

  if (loading) return <div className="p-10 text-center">Učitavanje korisnika...</div>;

  return (
    <div className="min-h-screen bg-gray-50">
      <Header />
      <main className="max-w-6xl mx-auto py-12 px-4">
        <h1 className="text-3xl font-black text-pink-950 mb-8 uppercase tracking-widest">
          Pregled korisnika
        </h1>

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
                <tr key={user.id} className="border-b border-gray-100 hover:bg-gray-50">
                  <td className="p-4 font-semibold text-gray-800">
                    {user.ime} {user.prezime}
                  </td>
                  <td className="p-4 text-gray-600">{user.korisnickoIme}</td>
                </tr>
              ))}
            </tbody>
          </table>
        </div>
      </main>
    </div>
  );
}