"use client";

import { useState, useEffect } from "react";
import Link from "next/link";
import { usePathname } from "next/navigation";
import { Instagram, Facebook, Youtube, Phone, MessageCircle } from "lucide-react";
import { Korisnik } from "@/lib/types";
import { api } from "@/lib/api";

export default function Header() {
  const [showMenu, setShowMenu] = useState(false);
  const [trenutniKorisnik, setTrenutniKorisnik] = useState<Korisnik | null>(
    null,
  );
  const [neprocitanih, setNeprocitanih] = useState(0);
  const pathname = usePathname();

  // Izmeni useEffect u Header.tsx ovako:
  useEffect(() => {
    const proveriKorisnika = () => {
      const sacuvanKorisnik = localStorage.getItem("ulogovan_korisnik");
      if (sacuvanKorisnik) {
        setTrenutniKorisnik(JSON.parse(sacuvanKorisnik));
      } else {
        setTrenutniKorisnik(null);
      }
    };

    // Proveri odmah pri učitavanju
    proveriKorisnika();

    // Slušaj promene u localStorage-u sa drugih stranica
    window.addEventListener("storage", proveriKorisnika);

    return () => window.removeEventListener("storage", proveriKorisnika);
  }, []);

  useEffect(() => {
    if (!trenutniKorisnik) {
      setNeprocitanih(0);
      return;
    }
    const ucitaj = () =>
      api
        .getBrojNeprocitanih()
        .then((o: any) => setNeprocitanih(o?.broj ?? 0))
        .catch(() => setNeprocitanih(0));

    ucitaj();
    const interval = setInterval(ucitaj, 5000);
    return () => clearInterval(interval);
  }, [trenutniKorisnik, pathname]);

  const handleLogout = () => {
    localStorage.clear();
    setTrenutniKorisnik(null);
    setShowMenu(false);
    window.location.href = "/";
  };

  return (
    <div className="bg-white border-b py-6 px-8 flex justify-between items-center shadow-sm">
      {/* Telefon - LEVA KOLONA */}
      <div className="flex items-center gap-2 text-pink-800 font-semibold group cursor-pointer">
        <div className="bg-pink-100 p-2 rounded-full group-hover:bg-pink-200 transition-colors">
          <Phone size={18} className="text-pink-600" />
        </div>
        <span className="text-sm">+381 66 777 888</span>
      </div>

      {/* Naslov - SREDNJA KOLONA */}
      <div className="text-center">
        <h1 className="text-3xl font-serif text-pink-950 tracking-[0.2em] uppercase font-light">
          Rezervacije Sala
        </h1>
        <div className="h-1 w-20 bg-pink-200 mx-auto mt-1 rounded-full"></div>

        <div className="flex justify-center gap-6 mt-4">
          <Link
            href="/"
            className={`text-[10px] uppercase tracking-[0.2em] font-black transition-all ${
              pathname === "/"
                ? "text-pink-600 border-b border-pink-600"
                : "text-gray-400 hover:text-pink-600"
            }`}
          >
            Pregled sala
          </Link>
          {trenutniKorisnik && (
            <>
              <Link
                href="/rezervacije"
                className={`text-[10px] uppercase tracking-[0.2em] font-black transition-all ${
                  pathname === "/rezervacije"
                    ? "text-pink-600 border-b border-pink-600"
                    : "text-gray-400 hover:text-pink-600"
                }`}
              >
                {trenutniKorisnik.uloga === "ROLE_ADMIN"
                  ? "Sve Rezervacije"
                  : "Moje Rezervacije"}
              </Link>
              {trenutniKorisnik.uloga === "ROLE_ADMIN" && (
                <Link
                  href="/admin/korisnici"
                  className={`text-[10px] uppercase tracking-[0.2em] font-black transition-all ${
                    pathname === "/admin/korisnici"
                      ? "text-pink-600 border-b border-pink-600"
                      : "text-gray-400 hover:text-pink-600"
                  }`}
                >
                  Korisnici
                </Link>
              )}
            </>
          )}
        </div>
      </div>

      {/* Socijalne mreže i Menu - DESNA KOLONA */}
      <div className="flex items-center gap-6">
        <div className="flex gap-4 items-center border-r border-pink-100 pr-6">
          <a href="#" className="group">
            <Instagram
              size={20}
              className="text-pink-800/50 group-hover:text-pink-600 transition-all"
            />
          </a>
          <a href="#" className="group">
            <Facebook
              size={20}
              className="text-pink-800/50 group-hover:text-pink-600 transition-all"
            />
          </a>
          <a href="#" className="group">
            <Youtube
              size={22}
              className="text-pink-800/50 group-hover:text-pink-600 transition-all"
            />
          </a>
        </div>

        {trenutniKorisnik && (
          <Link
            href="/poruke"
            aria-label="Poruke"
            className="relative group"
            title="Poruke"
          >
            <MessageCircle
              size={22}
              className={`transition-all ${
                pathname === "/poruke" || neprocitanih > 0
                  ? "text-pink-600"
                  : "text-pink-800/50 group-hover:text-pink-600"
              }`}
            />
            {neprocitanih > 0 && (
              <span className="absolute -top-1.5 -right-2 bg-pink-600 text-white text-[9px] font-bold rounded-full min-w-[16px] h-[16px] flex items-center justify-center px-1 border-2 border-white">
                {neprocitanih > 9 ? "9+" : neprocitanih}
              </span>
            )}
          </Link>
        )}

        <div className="relative">
          <button
            onClick={() => setShowMenu(!showMenu)}
            className="text-pink-900/40 hover:text-pink-600 text-3xl font-bold px-2 transition-colors leading-none pb-2"
          >
            ⋮
          </button>
          {showMenu && (
            <div className="absolute right-0 mt-3 w-44 bg-white shadow-2xl border border-pink-100 rounded-xl z-50 overflow-hidden">
              {trenutniKorisnik ? (
                <>
                  <div className="px-6 py-4 border-b border-pink-50 bg-pink-50/30 text-xs text-pink-900 font-bold uppercase tracking-wider">
                    {trenutniKorisnik.ime} {trenutniKorisnik.prezime}
                    <div className="text-[9px] text-pink-400 font-medium lowercase italic mt-1">
                      uloga: {trenutniKorisnik.uloga}
                    </div>
                  </div>
                  <button
                    onClick={handleLogout}
                    className="w-full text-left block px-6 py-4 hover:bg-red-50 font-bold text-red-600 text-sm transition-colors"
                  >
                    LOGOUT
                  </button>
                </>
              ) : (
                <>
                  <Link
                    href="/login"
                    className="block px-6 py-4 hover:bg-pink-50 font-bold text-pink-900 text-sm transition-colors"
                  >
                    LOGIN
                  </Link>
                  <Link
                    href="/register"
                    onClick={() => setShowMenu(false)}
                    className="block px-6 py-4 hover:bg-pink-50 font-bold text-pink-600 text-sm transition-colors"
                  >
                    REGISTER
                  </Link>
                </>
              )}
            </div>
          )}
        </div>
      </div>
    </div>
  );
}
