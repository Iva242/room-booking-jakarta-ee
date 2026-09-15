"use client";

import { useState } from "react";
import { useRouter } from "next/navigation";
import { api } from "@/lib/api";
import Link from "next/link";

export default function RegisterStranica() {
  const [formData, setFormData] = useState({
    ime: "",
    prezime: "",
    korisnickoIme: "", // DODATO
    email: "",
    lozinka: "",
  });
  const [error, setError] = useState("");
  const [loading, setLoading] = useState(false);
  const [success, setSuccess] = useState(false);

  const router = useRouter();

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    setError("");
    setLoading(true);

    try {
      // Šaljemo podatke u obliku koji odgovara Spring DTO-u
      const registerPayload = {
        ime: formData.ime,
        prezime: formData.prezime,
        korisnickoIme: formData.korisnickoIme, // DODATO
        email: formData.email,
        lozinka: formData.lozinka,
      };

      await api.register(registerPayload);

      setSuccess(true);
      setTimeout(() => {
        router.push("/login");
      }, 3000);
    } catch (err: any) {
      setError(err?.message || "Greška pri registraciji. Proverite podatke.");
    } finally {
      setLoading(false);
    }
  };

  return (
    <main className="min-h-screen bg-pink-50 flex items-center justify-center p-6 font-sans">
      <div className="bg-white p-10 rounded-[30px] shadow-2xl shadow-pink-200 w-full max-w-md border border-pink-100">
        <h1 className="text-4xl font-black text-pink-900 mb-2 text-center tracking-tight">
          Registracija
        </h1>
        <p className="text-pink-600 mb-8 text-center font-medium opacity-70">
          Kreirajte nalog za rezervaciju sala
        </p>

        {success ? (
          <div className="bg-green-50 text-green-700 p-6 rounded-2xl border border-green-100 text-center animate-bounce">
            <span className="text-3xl block mb-2">🎉</span>
            <p className="font-bold">Uspešna registracija!</p>
            <p className="text-sm opacity-80">
              Proverite vaš email (Mailtrap) za potvrdu pre prijave.
            </p>
          </div>
        ) : (
          <>
            {error && (
              <div className="bg-red-50 text-red-600 p-4 rounded-xl mb-6 text-sm font-bold border border-red-100 text-center">
                {error}
              </div>
            )}

            <form onSubmit={handleSubmit} className="space-y-4">
              <div className="grid grid-cols-2 gap-4">
                <div>
                  <label className="block text-sm font-bold text-pink-800 mb-2 ml-1">
                    Ime
                  </label>
                  <input
                    type="text"
                    required
                    className="w-full p-4 rounded-2xl border-2 border-pink-50 focus:border-pink-500 focus:outline-none transition-all outline-none text-gray-800"
                    onChange={(e) =>
                      setFormData({ ...formData, ime: e.target.value })
                    }
                  />
                </div>
                <div>
                  <label className="block text-sm font-bold text-pink-800 mb-2 ml-1">
                    Prezime
                  </label>
                  <input
                    type="text"
                    required
                    className="w-full p-4 rounded-2xl border-2 border-pink-50 focus:border-pink-500 focus:outline-none transition-all outline-none text-gray-800"
                    onChange={(e) =>
                      setFormData({ ...formData, prezime: e.target.value })
                    }
                  />
                </div>
              </div>

              {/* NOVO POLJE ZA KORISNICKO IME */}
              <div>
                <label className="block text-sm font-bold text-pink-800 mb-2 ml-1">
                  Korisničko ime
                </label>
                <input
                  type="text"
                  required
                  className="w-full p-4 rounded-2xl border-2 border-pink-50 focus:border-pink-500 focus:outline-none transition-all outline-none text-gray-800"
                  placeholder="npr. ana1234"
                  onChange={(e) =>
                    setFormData({ ...formData, korisnickoIme: e.target.value })
                  }
                />
              </div>

              <div>
                <label className="block text-sm font-bold text-pink-800 mb-2 ml-1">
                  Email
                </label>
                <input
                  type="email"
                  required
                  className="w-full p-4 rounded-2xl border-2 border-pink-50 focus:border-pink-500 focus:outline-none transition-all outline-none text-gray-800"
                  placeholder="vas@email.com"
                  onChange={(e) =>
                    setFormData({ ...formData, email: e.target.value })
                  }
                />
              </div>

              <div>
                <label className="block text-sm font-bold text-pink-800 mb-2 ml-1">
                  Lozinka
                </label>
                <input
                  type="password"
                  required
                  className="w-full p-4 rounded-2xl border-2 border-pink-50 focus:border-pink-500 focus:outline-none transition-all outline-none text-gray-800"
                  placeholder="••••••••"
                  onChange={(e) =>
                    setFormData({ ...formData, lozinka: e.target.value })
                  }
                />
              </div>

              <button
                disabled={loading}
                type="submit"
                className="w-full bg-pink-600 text-white py-4 rounded-2xl font-black text-lg hover:bg-pink-700 transition-all shadow-lg shadow-pink-200 active:scale-95 mt-4 disabled:bg-gray-300"
              >
                {loading ? "KREIRANJE..." : "NAPRAVI NALOG"}
              </button>

              <p className="text-center text-sm text-gray-500 mt-4">
                Već imate nalog?{" "}
                <Link
                  href="/login"
                  className="text-pink-600 font-bold hover:underline"
                >
                  Prijavite se
                </Link>
              </p>
            </form>
          </>
        )}
      </div>
    </main>
  );
}