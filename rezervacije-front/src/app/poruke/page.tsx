"use client";

import { useState, useEffect, useCallback, useRef } from "react";
import { api } from "@/lib/api";
import { Konverzacija, Korisnik, Poruka } from "@/lib/types";
import Header from "@/components/Header";
import { useObavestenja } from "@/components/Obavestenja";
import { MessageCircle, Send } from "lucide-react";

// na koliko se osvežava otvoreni razgovor
const INTERVAL_MS = 4000;

const formatirajVreme = (vreme: string) => {
  if (!vreme) return "";
  const [datum, sat] = vreme.replace("T", " ").split(" ");
  if (!datum || !sat) return vreme;
  const [g, m, d] = datum.split("-");
  const [h, min] = sat.split(":");
  const danas = new Date();
  const isti =
    Number(g) === danas.getFullYear() &&
    Number(m) === danas.getMonth() + 1 &&
    Number(d) === danas.getDate();
  return isti ? `${h}:${min}` : `${d}.${m}. u ${h}:${min}`;
};

export default function PorukePage() {
  const { greska } = useObavestenja();

  const [korisnik, setKorisnik] = useState<Korisnik | null>(null);
  const [konverzacije, setKonverzacije] = useState<Konverzacija[]>([]);
  const [izabrana, setIzabrana] = useState<Konverzacija | null>(null);
  const [poruke, setPoruke] = useState<Poruka[]>([]);
  const [tekst, setTekst] = useState("");
  const [loading, setLoading] = useState(true);
  const [salje, setSalje] = useState(false);

  const krajListe = useRef<HTMLDivElement>(null);
  const jeAdmin = korisnik?.uloga === "ROLE_ADMIN";

  useEffect(() => {
    const sacuvan = localStorage.getItem("ulogovan_korisnik");
    if (sacuvan) setKorisnik(JSON.parse(sacuvan));
  }, []);

  const ucitajKonverzacije = useCallback(async () => {
    try {
      setKonverzacije(await api.getKonverzacije());
    } catch (e: any) {
      greska(e?.message || "Razgovori nisu učitani.");
    } finally {
      setLoading(false);
    }
  }, [greska]);

  useEffect(() => {
    ucitajKonverzacije();
  }, [ucitajKonverzacije]);

  const ucitajPoruke = useCallback(async (id: number) => {
    try {
      setPoruke(await api.getPoruke(id));
    } catch (e: any) {
      greska(e?.message || "Poruke nisu učitane.");
    }
  }, [greska]);

  // otvoreni razgovor se sam osvežava, pa odgovor stigne bez osvežavanja stranice
  useEffect(() => {
    if (!izabrana) return;
    ucitajPoruke(izabrana.idKonverzacije);
    const interval = setInterval(() => {
      ucitajPoruke(izabrana.idKonverzacije);
      ucitajKonverzacije();
    }, INTERVAL_MS);
    return () => clearInterval(interval);
  }, [izabrana, ucitajPoruke, ucitajKonverzacije]);

  useEffect(() => {
    krajListe.current?.scrollIntoView({ behavior: "smooth" });
  }, [poruke]);

  const posalji = async (e: React.FormEvent) => {
    e.preventDefault();
    if (!izabrana || !tekst.trim() || salje) return;

    setSalje(true);
    try {
      await api.posaljiPoruku(izabrana.idKonverzacije, tekst.trim());
      setTekst("");
      await ucitajPoruke(izabrana.idKonverzacije);
      await ucitajKonverzacije();
    } catch (err: any) {
      greska(err?.message || "Poruka nije poslata.");
    } finally {
      setSalje(false);
    }
  };

  const naslovRazgovora = (k: Konverzacija) =>
    jeAdmin ? `${k.korisnik.ime} ${k.korisnik.prezime}` : k.nazivSale || "Opšte pitanje";

  const podnaslovRazgovora = (k: Konverzacija) =>
    jeAdmin
      ? k.nazivSale
        ? `pita o sali: ${k.nazivSale}`
        : "opšte pitanje"
      : k.nazivSale
        ? "pitanje o sali"
        : "opšte pitanje";

  return (
    <main className="min-h-screen bg-gray-50">
      <Header />

      <div className="max-w-6xl mx-auto p-8">
        <div className="text-center mb-10">
          <h2 className="text-4xl font-light text-pink-950 uppercase tracking-[0.3em]">
            {jeAdmin ? (
              <>
                Sve <span className="font-bold">Poruke</span>
              </>
            ) : (
              <>
                Moje <span className="font-bold">Poruke</span>
              </>
            )}
          </h2>
          <div className="h-1 w-20 bg-pink-200 mx-auto mt-2 rounded-full"></div>
        </div>

        {loading ? (
          <div className="text-center py-20 text-pink-900/40">Učitavanje…</div>
        ) : konverzacije.length === 0 ? (
          <div className="text-center py-32 bg-white rounded-3xl border-2 border-dashed border-pink-100">
            <MessageCircle size={40} className="text-pink-200 mx-auto mb-4" />
            <p className="text-pink-900/40 text-lg italic">
              {jeAdmin
                ? "Trenutno nema poruka od korisnika."
                : "Nemate nijedan razgovor. Pitanje o sali možete poslati sa njene kartice."}
            </p>
          </div>
        ) : (
          <div className="bg-white rounded-3xl shadow-sm border border-pink-100 overflow-hidden flex flex-col md:flex-row min-h-[560px]">
            <aside className="md:w-72 border-b md:border-b-0 md:border-r border-pink-100 md:max-h-[560px] md:overflow-y-auto">
              <div className="px-5 py-4 text-[10px] uppercase tracking-[0.2em] font-black text-pink-900/50">
                Razgovori
              </div>

              {konverzacije.map((k) => {
                const aktivna = izabrana?.idKonverzacije === k.idKonverzacije;
                return (
                  <button
                    key={k.idKonverzacije}
                    onClick={() => setIzabrana(k)}
                    className={`w-full text-left px-5 py-4 border-l-4 transition-colors ${
                      aktivna
                        ? "bg-pink-50 border-pink-600"
                        : "border-transparent hover:bg-gray-50"
                    }`}
                  >
                    <div className="flex justify-between items-start gap-2">
                      <div className="min-w-0">
                        <div className="text-sm font-bold text-pink-950 truncate">
                          {naslovRazgovora(k)}
                        </div>
                        <div className="text-xs text-gray-500 truncate mt-0.5">
                          {k.pregledPoslednjePoruke}
                        </div>
                        <div className="text-[10px] text-gray-400 mt-1">
                          {formatirajVreme(k.poslednjaAktivnost)}
                        </div>
                      </div>
                      {k.brojNeprocitanih > 0 && !aktivna && (
                        <span className="shrink-0 mt-1 bg-pink-600 text-white text-[10px] font-bold rounded-full min-w-[18px] h-[18px] flex items-center justify-center px-1">
                          {k.brojNeprocitanih}
                        </span>
                      )}
                    </div>
                  </button>
                );
              })}
            </aside>

            <section className="flex-1 flex flex-col">
              {!izabrana ? (
                <div className="flex-1 flex items-center justify-center text-pink-900/30 italic text-sm p-10 text-center">
                  Izaberite razgovor sa leve strane.
                </div>
              ) : (
                <>
                  <div className="px-6 py-4 border-b border-pink-100">
                    <div className="text-sm font-bold text-pink-950">
                      {naslovRazgovora(izabrana)}
                    </div>
                    <div className="text-xs text-pink-900/50 mt-0.5">
                      {podnaslovRazgovora(izabrana)}
                    </div>
                  </div>

                  <div className="flex-1 p-6 space-y-4 overflow-y-auto max-h-[400px]">
                    {poruke.map((p) => {
                      const moja = p.autor.id === korisnik?.id;
                      return (
                        <div
                          key={p.idPoruke}
                          className={`flex flex-col ${moja ? "items-end" : "items-start"}`}
                        >
                          <div
                            className={`max-w-[75%] px-4 py-3 text-sm leading-relaxed ${
                              moja
                                ? "bg-pink-600 text-white rounded-2xl rounded-br-sm"
                                : "bg-gray-100 text-gray-800 rounded-2xl rounded-bl-sm"
                            }`}
                          >
                            {p.tekst}
                          </div>
                          <div className="text-[10px] text-gray-400 mt-1 px-1">
                            {moja ? "Vi" : `${p.autor.ime} ${p.autor.prezime}`} ·{" "}
                            {formatirajVreme(p.vreme)}
                          </div>
                        </div>
                      );
                    })}
                    <div ref={krajListe} />
                  </div>

                  <form
                    onSubmit={posalji}
                    className="px-5 py-4 border-t border-pink-100 flex gap-3 items-center"
                  >
                    <input
                      value={tekst}
                      onChange={(e) => setTekst(e.target.value)}
                      placeholder={jeAdmin ? "Napišite odgovor…" : "Napišite poruku…"}
                      maxLength={2000}
                      className="flex-1 px-4 py-3 rounded-2xl border-2 border-pink-50 focus:border-pink-500 outline-none text-sm transition-all"
                    />
                    <button
                      type="submit"
                      disabled={!tekst.trim() || salje}
                      className="bg-pink-600 text-white rounded-2xl px-5 py-3 font-black text-xs uppercase tracking-widest shadow-lg shadow-pink-200 hover:bg-pink-700 disabled:opacity-30 disabled:shadow-none transition-all flex items-center gap-2"
                    >
                      <Send size={14} />
                      Pošalji
                    </button>
                  </form>
                </>
              )}
            </section>
          </div>
        )}
      </div>
    </main>
  );
}
