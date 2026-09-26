"use client";

import { useState } from "react";
import { api } from "@/lib/api";
import { useObavestenja } from "@/components/Obavestenja";
import { MessageCircle } from "lucide-react";

interface Props {
  idSale: number | null;
  nazivSale: string | null;
  onClose: () => void;
}

export default function PitanjeModal({ idSale, nazivSale, onClose }: Props) {
  const { uspeh, greska } = useObavestenja();
  const [tekst, setTekst] = useState("");
  const [salje, setSalje] = useState(false);

  const posalji = async (e: React.FormEvent) => {
    e.preventDefault();
    if (salje) return;

    if (!tekst.trim()) {
      greska("Sistem ne može da pošalje pitanje jer tekst pitanja nije unet.");
      return;
    }

    setSalje(true);
    try {
      await api.zapocniKonverzaciju(idSale, tekst.trim());
      uspeh("Pitanje je poslato. Odgovor stiže u vaše poruke.");
      onClose();
    } catch (err: any) {
      // 4xx: server zna sta nije u redu; 5xx ili nema odgovora: razlog korisnika ne zanima
      greska(
        err?.status && err.status < 500
          ? err.message
          : "Sistem ne može da pošalje pitanje.",
      );
    } finally {
      setSalje(false);
    }
  };

  return (
    <div
      className="fixed inset-0 bg-black/50 z-[100] flex items-center justify-center p-4"
      onClick={onClose}
    >
      <div
        className="bg-white rounded-3xl p-8 w-full max-w-md shadow-2xl"
        onClick={(e) => e.stopPropagation()}
      >
        <div className="w-16 h-16 bg-pink-100 text-pink-600 rounded-full flex items-center justify-center mx-auto mb-4">
          <MessageCircle size={28} />
        </div>

        <h3 className="text-xl font-bold text-pink-950 mb-2 uppercase tracking-widest text-center">
          Pitajte za cenu
        </h3>
        <p className="text-gray-500 text-sm mb-6 text-center">
          {nazivSale
            ? `Pitanje o sali ${nazivSale}. Odgovor stiže u vaše poruke.`
            : "Vaše pitanje stiže administratoru."}
        </p>

        <form onSubmit={posalji}>
          <textarea
            autoFocus
            value={tekst}
            onChange={(e) => setTekst(e.target.value)}
            rows={4}
            maxLength={2000}
            placeholder="Na primer: koja je cena zakupa za subotu uveče?"
            className="w-full p-4 rounded-2xl border-2 border-pink-50 focus:border-pink-500 outline-none text-sm resize-none transition-all"
          />

          <div className="flex gap-4 mt-6">
            <button
              type="button"
              onClick={onClose}
              className="flex-1 py-4 text-gray-400 font-black text-xs uppercase tracking-widest hover:text-gray-600 transition-all"
            >
              Odustani
            </button>
            <button
              type="submit"
              disabled={salje}
              className="flex-1 bg-pink-600 text-white py-4 rounded-2xl font-black text-xs uppercase tracking-widest shadow-lg shadow-pink-200 hover:bg-pink-700 disabled:opacity-30 disabled:shadow-none transition-all"
            >
              {salje ? "Šalje se…" : "Pošalji"}
            </button>
          </div>
        </form>
      </div>
    </div>
  );
}
