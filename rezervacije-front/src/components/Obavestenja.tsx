"use client";

import {
  createContext,
  useCallback,
  useContext,
  useEffect,
  useState,
} from "react";
import { CheckCircle2, XCircle, Info, AlertTriangle } from "lucide-react";

type TipToasta = "uspeh" | "greska" | "info";

interface Toast {
  id: number;
  poruka: string;
  tip: TipToasta;
}

interface OpcijePotvrde {
  naslov: string;
  opis?: string;
  tekstPotvrde?: string;
}

interface Potvrda extends OpcijePotvrde {
  odgovori: (prihvaceno: boolean) => void;
}

interface Obavestenja {
  uspeh: (poruka: string) => void;
  greska: (poruka: string) => void;
  info: (poruka: string) => void;
  potvrdi: (opcije: OpcijePotvrde) => Promise<boolean>;
}

const TRAJANJE_MS = 3000;

const ObavestenjaContext = createContext<Obavestenja | null>(null);

export function useObavestenja() {
  const kontekst = useContext(ObavestenjaContext);
  if (!kontekst) {
    throw new Error(
      "useObavestenja radi samo unutar <ObavestenjaProvider> (postavljen je u layout.tsx)",
    );
  }
  return kontekst;
}

const stilToasta: Record<
  TipToasta,
  { okvir: string; tekst: string; ikona: string; Ikona: typeof CheckCircle2 }
> = {
  uspeh: {
    okvir: "bg-green-50 border-green-100 shadow-green-100",
    tekst: "text-green-700",
    ikona: "text-green-600",
    Ikona: CheckCircle2,
  },
  greska: {
    okvir: "bg-red-50 border-red-100 shadow-red-100",
    tekst: "text-red-700",
    ikona: "text-red-600",
    Ikona: XCircle,
  },
  info: {
    okvir: "bg-pink-50 border-pink-100 shadow-pink-100",
    tekst: "text-pink-900",
    ikona: "text-pink-600",
    Ikona: Info,
  },
};

export function ObavestenjaProvider({
  children,
}: {
  children: React.ReactNode;
}) {
  const [toastovi, setToastovi] = useState<Toast[]>([]);
  const [potvrda, setPotvrda] = useState<Potvrda | null>(null);

  const dodaj = useCallback((poruka: string, tip: TipToasta) => {
    const id = Date.now() + Math.random();
    setToastovi((prethodni) => [...prethodni, { id, poruka, tip }]);
    setTimeout(
      () => setToastovi((prethodni) => prethodni.filter((t) => t.id !== id)),
      TRAJANJE_MS,
    );
  }, []);

  const uspeh = useCallback((p: string) => dodaj(p, "uspeh"), [dodaj]);
  const greska = useCallback((p: string) => dodaj(p, "greska"), [dodaj]);
  const info = useCallback((p: string) => dodaj(p, "info"), [dodaj]);

  const potvrdi = useCallback(
    (opcije: OpcijePotvrde) =>
      new Promise<boolean>((resolve) => {
        setPotvrda({ ...opcije, odgovori: resolve });
      }),
    [],
  );

  const zatvoriPotvrdu = (prihvaceno: boolean) => {
    potvrda?.odgovori(prihvaceno);
    setPotvrda(null);
  };

  useEffect(() => {
    if (!potvrda) return;
    const naTaster = (e: KeyboardEvent) => {
      if (e.key === "Escape") zatvoriPotvrdu(false);
    };
    window.addEventListener("keydown", naTaster);
    return () => window.removeEventListener("keydown", naTaster);
  });

  return (
    <ObavestenjaContext.Provider value={{ uspeh, greska, info, potvrdi }}>
      {children}

      <div
        className="fixed top-6 right-6 z-[120] flex flex-col gap-3 pointer-events-none"
        role="status"
        aria-live="polite"
      >
        {toastovi.map(({ id, poruka, tip }) => {
          const { okvir, tekst, ikona, Ikona } = stilToasta[tip];
          return (
            <div
              key={id}
              className={`pointer-events-auto flex items-start gap-3 p-4 pr-6 border rounded-2xl shadow-lg max-w-sm ${okvir} animate-[toastUlaz_0.2s_ease-out]`}
            >
              <Ikona size={20} className={`${ikona} shrink-0 mt-0.5`} />
              <p className={`${tekst} text-xs font-bold leading-tight`}>
                {poruka}
              </p>
            </div>
          );
        })}
      </div>

      {potvrda && (
        <div
          className="fixed inset-0 bg-black/50 z-[110] flex items-center justify-center p-4"
          onClick={() => zatvoriPotvrdu(false)}
        >
          <div
            role="dialog"
            aria-modal="true"
            aria-labelledby="naslov-potvrde"
            className="bg-white rounded-3xl p-8 w-full max-w-sm shadow-2xl text-center"
            onClick={(e) => e.stopPropagation()}
          >
            <div className="w-16 h-16 bg-red-100 text-red-600 rounded-full flex items-center justify-center mx-auto mb-4">
              <AlertTriangle size={28} />
            </div>

            <h3
              id="naslov-potvrde"
              className="text-xl font-bold text-pink-950 mb-3 uppercase tracking-widest"
            >
              {potvrda.naslov}
            </h3>

            {potvrda.opis && (
              <p className="text-gray-500 text-sm mb-6">{potvrda.opis}</p>
            )}

            <div className="flex gap-4">
              <button
                autoFocus
                onClick={() => zatvoriPotvrdu(false)}
                className="flex-1 py-4 text-gray-400 font-black text-xs uppercase tracking-widest hover:text-gray-600 transition-all"
              >
                Odustani
              </button>
              <button
                onClick={() => zatvoriPotvrdu(true)}
                className="flex-1 bg-red-600 text-white py-4 rounded-2xl font-black text-xs uppercase tracking-widest shadow-lg shadow-red-200 hover:bg-red-700 transition-all"
              >
                {potvrda.tekstPotvrde ?? "Potvrdi"}
              </button>
            </div>
          </div>
        </div>
      )}
    </ObavestenjaContext.Provider>
  );
}
