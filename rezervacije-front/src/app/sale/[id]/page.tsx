"use client";

import { useState, useEffect, use, useMemo } from "react";
import { useParams, notFound } from "next/navigation";
import { api } from "@/lib/api";
import { Sala } from "@/lib/types";
import Link from "next/link";
import Header from "@/components/Header";
import RezervacijaModal from "@/components/RezervacijaModal";
import dynamic from "next/dynamic";

// 1. Promenjeno ime komponente u 'MapaKomponenta' da ne bi bilo konflikta sa JS 'Map' objektom
const MapaKomponenta = dynamic(() => import("@/components/Map"), {
  ssr: false,
  loading: () => (
    <div className="h-[400px] bg-gray-100 animate-pulse rounded-3xl">
      Učitavanje mape...
    </div>
  ),
});

interface PageProps {
  params: Promise<{ id: string }>;
}

export default function SalaDetalji({ params }: PageProps) {
  const resolvedParams = use(params);
  const id = resolvedParams.id;

  const [sala, setSala] = useState<Sala | null>(null);
  const [loading, setLoading] = useState(true);
  const [isAdmin, setIsAdmin] = useState(false);
  const [isLoggedIn, setIsLoggedIn] = useState(false);
  const [isRezervacijaModalOpen, setIsRezervacijaModalOpen] = useState(false);
  const [isEditModalOpen, setIsEditModalOpen] = useState(false);

  const [sveKarakteristike, setSveKarakteristike] = useState<any[]>([]);
  const [sviTipovi, setSviTipovi] = useState<any[]>([]);
  const [selectedKarakteristike, setSelectedKarakteristike] = useState<
    number[]
  >([]);
  const [selectedTipovi, setSelectedTipovi] = useState<number[]>([]);

  // 2. Koristimo 'mapaTipova' umesto 'tipoviMap' (da izbegnemo konflikt)
  const mapaTipova = useMemo(() => {
    const m = new Map();
    sviTipovi.forEach((t) => m.set(Number(t.idTipDogadjaja), t.naziv));
    return m;
  }, [sviTipovi]);

  const mapaKarakteristika = useMemo(() => {
    const m = new Map();
    sveKarakteristike.forEach((k) =>
      m.set(Number(k.idKarakteristike), k.naziv),
    );
    return m;
  }, [sveKarakteristike]);

  const getImageUrl = (imgName: string | null | undefined) => {
    if (!imgName) return "/slike/placeholder.jpg";
    if (imgName.startsWith("http")) return imgName;
    return `/slike/${imgName}`;
  };

  useEffect(() => {
    const userJson = localStorage.getItem("ulogovan_korisnik");
    if (userJson) {
      setIsLoggedIn(true);
      try {
        const user = JSON.parse(userJson);
        if (
          user.uloga &&
          (user.uloga === "ADMIN" || user.uloga === "ROLE_ADMIN")
        ) {
          setIsAdmin(true);
        }
      } catch (e) {
        console.error(e);
      }
    }

    const fetchData = async () => {
      try {
        setLoading(true);
        const [salaData, karData, tipData] = await Promise.all([
          api.getSalaById(Number(id)),
          fetch("http://localhost:8080/api/karakteristike").then((r) =>
            r.json(),
          ),
          fetch("http://localhost:8080/api/tipovi-dogadjaja").then((r) =>
            r.json(),
          ),
        ]);

        // OVO DODAJ U FETCH FUNKCIJU
        console.log("ŠTA TAČNO STIGNE KAO TIPOVIDOGADJAJA:", tipData);
        console.log("ŠTA IMA SALA U sebi:", salaData.tipoviDogadjaja);

        setSala(salaData);
        setSveKarakteristike(karData);
        setSviTipovi(tipData);

        setSelectedKarakteristike(
          salaData.karakteristike?.map((k: any) =>
            Number(k.idKarakteristike),
          ) || [],
        );
        setSelectedTipovi(
          salaData.tipoviDogadjaja?.map((t: any) => Number(t.idTipDogadjaja)) ||
            [],
        );
      } catch (err) {
        console.error(err);
      } finally {
        setLoading(false);
      }
    };
    fetchData();
  }, [id]);

  const handleSave = async (e: React.FormEvent) => {
    e.preventDefault();
    const form = e.target as HTMLFormElement;
    const updatedData = {
      naziv: (form.elements.namedItem("naziv") as HTMLInputElement).value,
      kapacitet: parseInt(
        (form.elements.namedItem("kapacitet") as HTMLInputElement).value,
      ),
      opis: (form.elements.namedItem("opis") as HTMLTextAreaElement).value,
      lokacija: sala?.lokacija,
      latitude: sala?.latitude,
      longitude: sala?.longitude,
      slika: sala?.slika,
      karakteristike: selectedKarakteristike.map((id) => ({
        idKarakteristike: id,
      })),
      tipoviDogadjaja: selectedTipovi.map((id) => ({ idTipDogadjaja: id })),
    };

    try {
      const token = localStorage.getItem("token");
      const res = await fetch(
        `http://localhost:8080/api/sale/${sala?.idSale}`,
        {
          method: "PUT",
          headers: {
            "Content-Type": "application/json",
            Authorization: `Bearer ${token}`,
          },
          body: JSON.stringify(updatedData),
        },
      );

      if (res.ok) {
        const novaSala = await res.json();
        setSala(novaSala);
        setIsEditModalOpen(false);
        alert("Sala uspešno ažurirana!");
      } else {
        alert("Greška pri izmeni.");
      }
    } catch (err) {
      console.error(err);
    }
  };

  if (loading)
    return (
      <div className="min-h-screen flex items-center justify-center">
        Učitavanje...
      </div>
    );
  if (!sala) return notFound();

  return (
    <div className="min-h-screen bg-gray-50">
      <Header />
      <nav className="bg-white border-b px-8 py-4">
        <div className="max-w-6xl mx-auto flex justify-between">
          <Link href="/" className="text-pink-600 font-bold">
            ← NAZAD
          </Link>
        </div>
      </nav>

      <main className="max-w-6xl mx-auto p-8">
        <div className="grid grid-cols-1 lg:grid-cols-3 gap-12">
          <div className="lg:col-span-2 space-y-8">
            <img
              src={getImageUrl(sala.slika)}
              alt={sala.naziv}
              className="w-full h-80 object-cover rounded-3xl shadow-lg"
            />

            <div>
              <h1 className="text-4xl font-black uppercase">{sala.naziv}</h1>
              <p className="text-gray-600 mt-2 font-medium">
                📍 {sala.lokacija}
              </p>
              <p className="mt-6 text-gray-700 leading-relaxed">{sala.opis}</p>
            </div>

            <div>
              <h3 className="font-bold text-lg mb-4">Karakteristike:</h3>
              <ul className="list-disc pl-5 text-gray-700">
                {sala.karakteristike?.map((k: any) => {
                  const naz = mapaKarakteristika.get(
                    Number(k.idKarakteristike),
                  );
                  return naz ? <li key={k.idKarakteristike}>{naz}</li> : null;
                })}
              </ul>
            </div>

            <div>
              <h3 className="font-bold text-lg mb-4">Tipovi događaja:</h3>
              <ul className="list-disc pl-5 text-gray-700">
                {sala.tipoviDogadjaja?.map((t: any) => {
                  const naz = mapaTipova.get(Number(t.idTipDogadjaja));
                  // 3. Ovde se filtriraju null nazivi
                  return naz ? <li key={t.idTipDogadjaja}>{naz}</li> : null;
                })}
              </ul>
            </div>

            {sala.latitude && sala.longitude && (
              <MapaKomponenta
                lat={Number(sala.latitude)}
                lng={Number(sala.longitude)}
                naziv={sala.naziv}
              />
            )}
          </div>

          <div className="space-y-6">
            <div className="bg-white p-8 rounded-3xl shadow-sm border">
              <h3 className="text-sm font-bold uppercase text-gray-400 mb-4">
                Detalji
              </h3>
              <div className="flex justify-between mb-6">
                <span>Kapacitet</span>
                <span className="font-bold">{sala.kapacitet} osoba</span>
              </div>
              {isAdmin && (
                <button
                  onClick={() => setIsEditModalOpen(true)}
                  className="w-full bg-blue-600 text-white py-4 rounded-2xl font-bold hover:bg-blue-700 transition mb-4"
                >
                  IZMENI SALU
                </button>
              )}
              {isLoggedIn && !isAdmin && (
                <button
                  onClick={() => setIsRezervacijaModalOpen(true)}
                  className="w-full bg-pink-600 text-white py-4 rounded-2xl font-bold hover:bg-pink-700 transition"
                >
                  REZERVIŠI TERMIN
                </button>
              )}
            </div>
          </div>
        </div>
      </main>

      {/* Modal i ostalo ostaje isto... */}
      {isEditModalOpen && (
        <div className="fixed inset-0 bg-black/60 z-50 flex items-center justify-center p-4">
          <form
            onSubmit={handleSave}
            className="bg-white p-8 rounded-3xl max-w-lg w-full space-y-4 max-h-[80vh] overflow-y-auto"
          >
            <h2 className="text-2xl font-bold">Izmeni salu</h2>

            <div>
              <label className="block text-sm font-bold text-gray-700 mb-1">
                Naziv
              </label>
              <input
                name="naziv"
                defaultValue={sala.naziv}
                className="w-full border p-3 rounded-xl"
                required
              />
            </div>

            <div>
              <label className="block text-sm font-bold text-gray-700 mb-1">
                Kapacitet
              </label>
              <input
                name="kapacitet"
                type="number"
                defaultValue={sala.kapacitet}
                className="w-full border p-3 rounded-xl"
                required
              />
            </div>

            <div>
              <label className="block text-sm font-bold text-gray-700 mb-1">
                Opis
              </label>
              <textarea
                name="opis"
                defaultValue={sala.opis}
                className="w-full border p-3 rounded-xl"
              />
            </div>
            <div className="font-bold text-sm">Karakteristike:</div>
            <div className="grid grid-cols-2 gap-2 text-sm">
              {sveKarakteristike.map((k) => (
                <label
                  key={`kar-${k.idKarakteristike}`}
                  className="flex items-center gap-2 cursor-pointer"
                >
                  <input
                    type="checkbox"
                    checked={selectedKarakteristike.includes(
                      Number(k.idKarakteristike),
                    )}
                    onChange={(e) => {
                      const val = Number(k.idKarakteristike);
                      setSelectedKarakteristike((prev) =>
                        e.target.checked
                          ? [...prev, val]
                          : prev.filter((id) => id !== val),
                      );
                    }}
                  />
                  {k.naziv}
                </label>
              ))}
            </div>

            <div className="font-bold text-sm">Tipovi događaja:</div>
            <div className="grid grid-cols-2 gap-2 text-sm">
              {sviTipovi.map((t) => (
                <label
                  key={`tip-${t.idTipDogadjaja}`}
                  className="flex items-center gap-2 cursor-pointer"
                >
                  <input
                    type="checkbox"
                    checked={selectedTipovi.includes(Number(t.idTipDogadjaja))}
                    onChange={(e) => {
                      const val = Number(t.idTipDogadjaja);
                      setSelectedTipovi((prev) =>
                        e.target.checked
                          ? [...prev, val]
                          : prev.filter((id) => id !== val),
                      );
                    }}
                  />
                  {t.naziv}
                </label>
              ))}
            </div>

            <div className="flex gap-4 pt-4">
              <button
                type="button"
                onClick={() => setIsEditModalOpen(false)}
                className="flex-1 bg-gray-200 py-3 rounded-xl"
              >
                Odustani
              </button>
              <button
                type="submit"
                className="flex-1 bg-pink-600 text-white py-3 rounded-xl"
              >
                Sačuvaj
              </button>
            </div>
          </form>
        </div>
      )}
      {/* {isRezervacijaModalOpen && (
        <RezervacijaModal sala={sala} tipoviDogadjaja={sala.tipoviDogadjaja} onClose={() => setIsRezervacijaModalOpen(false)} onConfirm={() => {}} />
      )} */}
      {isRezervacijaModalOpen && (
        <RezervacijaModal
          sala={sala}
          tipoviDogadjaja={sala.tipoviDogadjaja}
          onClose={() => setIsRezervacijaModalOpen(false)}
          onConfirm={async (podaci) => {
            const token = localStorage.getItem("token");

            try {
              const res = await fetch("http://localhost:8080/api/rezervacije", {
                method: "POST",
                headers: {
                  "Content-Type": "application/json",
                  Authorization: `Bearer ${token}`,
                },
                body: JSON.stringify(podaci),
              });

              // Uvek čitamo tekstualni odgovor, jer backend vraća poruku greške kao string
              const responseText = await res.text();

              if (res.ok) {
                alert("Rezervacija uspešno poslata!");
                setIsRezervacijaModalOpen(false);
              } else {
                // Ako nije OK, samo izbaci taj tekst kao alert
                // Ovo pokriva "Sala je zauzeta..." i sve druge poruke koje backend pošalje
                alert(responseText || "Došlo je do greške pri rezervaciji.");
              }
            } catch (error) {
              console.error("Greška pri mreži:", error);
              alert("Došlo je do problema sa konekcijom.");
            }
          }}
        />
      )}
    </div>
  );
}
