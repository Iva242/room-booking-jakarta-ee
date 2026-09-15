"use client";

import { useState } from "react";
import { useRouter } from "next/navigation"; // Ovo nam treba za prebacivanje stranica
import { api } from "@/lib/api"; // Uvozimo naš API

export default function LoginStranica() {
  // const [email, setEmail] = useState("");
  const [username, setUsername] = useState("");
  const [password, setPassword] = useState("");
  const [error, setError] = useState(""); // Za poruku ako pogrešiš podatke
  const [loading, setLoading] = useState(false); // Da onemogućimo dugme dok traje provera

  const router = useRouter();

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    setError("");
    setLoading(true);

    try {
      // 1. Poziv API-ja
      const response = await api.login({
        korisnickoIme: username,
        lozinka: password,
      });

      // 2. Provera odgovora - sada tražimo 'korisnik' umesto 'korisnikDto'
      if (!response || !response.korisnik) {
        setError("Greška u komunikaciji sa serverom.");
        return;
      }

      const { token, korisnik } = response;

      // 3. Čuvanje podataka
      localStorage.setItem("ulogovan_korisnik", JSON.stringify(korisnik));
      localStorage.setItem("token", token);

      // 4. Preusmeravanje na osnovu uloge
      // Proveravamo ulogu unutar 'korisnik' objekta
      if (korisnik.uloga === "ROLE_ADMIN") {
        router.push("/rezervacije");
      } else {
        router.push("/");
      }
    } catch (err: any) {
      setError(
        err?.message || "Neuspešna prijava. Proverite korisničko ime i lozinku.",
      );
    } finally {
      setLoading(false);
    }
  };

  return (
    <main className="min-h-screen bg-pink-50 flex items-center justify-center p-6 font-sans">
      <div className="bg-white p-10 rounded-[30px] shadow-2xl shadow-pink-200 w-full max-w-md border border-pink-100">
        <h1 className="text-4xl font-black text-pink-900 mb-2 text-center tracking-tight">
          Prijava
        </h1>
        {/*<p className="text-pink-600 mb-8 text-center font-medium opacity-70">Admin pristup sistemu</p>/*}
        
        {/* PORUKA O GREŠCI */}
        {error && (
          <div className="bg-red-50 text-red-600 p-4 rounded-xl mb-6 text-sm font-bold border border-red-100 text-center animate-shake">
            {error}
          </div>
        )}

        <form onSubmit={handleSubmit} className="space-y-6">
          <div>
            <label className="block text-sm font-bold text-pink-800 mb-2 ml-1">
              Korisničko ime
            </label>
            <input
              type="text"
              required
              value={username}
              onChange={(e) => setUsername(e.target.value)}
              className="w-full p-4 rounded-2xl border-2 border-pink-50 focus:border-pink-500 focus:outline-none transition-all outline-none text-gray-800"
              placeholder="Unesite korisničko ime"
            />
          </div>
          <div>
            <label className="block text-sm font-bold text-pink-800 mb-2 ml-1">
              Lozinka
            </label>
            <input
              type="password"
              required
              value={password}
              onChange={(e) => setPassword(e.target.value)}
              className="w-full p-4 rounded-2xl border-2 border-pink-50 focus:border-pink-500 focus:outline-none transition-all outline-none text-gray-800"
              placeholder="••••••••"
            />
          </div>
          <button
            type="submit"
            className="w-full bg-pink-600 text-white py-4 rounded-2xl font-black text-lg hover:bg-pink-700 transition-all shadow-lg shadow-pink-200 active:scale-95 mt-4"
          >
            UĐI U SISTEM
          </button>
        </form>
      </div>
    </main>
  );
}
