
"use client";
import { useState, useEffect } from "react";
import { useRouter } from "next/navigation";
import { api } from "@/lib/api";
import { Karakteristika, TipDogadjaja } from "@/lib/types";
import { useObavestenja } from "@/components/Obavestenja";

export default function DodajSalu() {
  const { uspeh, greska } = useObavestenja();
  const router = useRouter();
  const [podaci, setPodaci] = useState<{
    karakteristike: Karakteristika[];
    tipovi: TipDogadjaja[];
  }>({ karakteristike: [], tipovi: [] });
  const [errors, setErrors] = useState<{ [key: string]: string }>({});

  const [formData, setFormData] = useState({
    naziv: "",
    kapacitet: "",
    opis: "",
    lokacija: "",
    latitude: "",
    longitude: "",
    slika: "", // OVO JE NOVO
    izabraneKarakteristike: [] as number[],
    izabraniTipovi: [] as number[],
  });

  useEffect(() => {
    const ucitajPodatke = async () => {
      try {
        const kar = await api.getKarakteristike();
        const tip = await api.getTipovi();
        setPodaci({ karakteristike: kar, tipovi: tip });
      } catch (err) {
        console.error("Greška pri učitavanju:", err);
      }
    };
    ucitajPodatke();
  }, []);

  const validate = () => {
    let tempErrors: { [key: string]: string } = {};
    if (!formData.naziv) tempErrors.naziv = "Naziv je obavezan";
    if (!formData.kapacitet || Number(formData.kapacitet) < 1)
      tempErrors.kapacitet = "Kapacitet mora biti broj veći od 0";
    if (!formData.lokacija) tempErrors.lokacija = "Lokacija je obavezna";
    if (!formData.latitude) tempErrors.latitude = "Geografska širina je obavezna";
    if (!formData.longitude) tempErrors.longitude = "Geografska dužina je obavezna";
    if (!formData.slika) tempErrors.slika = "Naziv slike je obavezan"; // NOVO
    if (formData.izabraniTipovi.length === 0)
      tempErrors.tipovi = "Morate izabrati bar jedan tip događaja";

    setErrors(tempErrors);
    return Object.keys(tempErrors).length === 0;
  };

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    if (!validate()) return;

    const payload = {
      naziv: formData.naziv,
      kapacitet: parseInt(formData.kapacitet),
      opis: formData.opis,
      lokacija: formData.lokacija,
      latitude: parseFloat(formData.latitude),
      longitude: parseFloat(formData.longitude),
      slika: formData.slika, // Šaljemo ono što smo uneli u polje
      karakteristike: formData.izabraneKarakteristike.map(id => ({ idKarakteristike: id })),
      tipoviDogadjaja: formData.izabraniTipovi.map(id => ({ idTipDogadjaja: id })),
    };

    try {
      await api.createSala(payload);
      uspeh("Sala je dodata.");
      router.push("/");
    } catch (err: any) {
      console.error("Greška pri slanju:", err);
      greska(err?.message || "Sala nije sačuvana. Pokušajte ponovo.");
    }
  };

  const ErrorMsg = ({ name }: { name: string }) =>
    errors[name] ? (
      <p className="text-red-500 text-[10px] mt-[-12px] mb-3 ml-2 font-bold uppercase">
        {errors[name]}
      </p>
    ) : null;

  return (
    <div className="min-h-screen flex items-center justify-center bg-gray-50 p-4">
      <form
        onSubmit={handleSubmit}
        className="w-full max-w-lg p-8 bg-[#fdf2f2] border-2 border-pink-100 rounded-2xl shadow-sm"
      >
        <h1 className="text-2xl font-black mb-6 text-pink-950 uppercase tracking-widest border-b border-pink-200 pb-4">
          Dodaj novu salu
        </h1>

        <input
          type="text"
          placeholder="Naziv sale *"
          className={`w-full p-3 border ${errors.naziv ? "border-red-500" : "border-pink-200"} mb-4 rounded-xl outline-none`}
          onChange={(e) => {
            setFormData({ ...formData, naziv: e.target.value });
            if (errors.naziv) setErrors({ ...errors, naziv: "" });
          }}
        />
        <ErrorMsg name="naziv" />

        <input
          type="number"
          min="1"
          placeholder="Kapacitet *"
          className={`w-full p-3 border ${errors.kapacitet ? "border-red-500" : "border-pink-200"} mb-4 rounded-xl outline-none`}
          onChange={(e) => {
            setFormData({ ...formData, kapacitet: e.target.value });
            if (errors.kapacitet) setErrors({ ...errors, kapacitet: "" });
          }}
        />
        <ErrorMsg name="kapacitet" />

        <textarea
          placeholder="Opis sale"
          rows={4}
          className="w-full p-3 border border-pink-200 mb-4 rounded-xl outline-none"
          onChange={(e) => setFormData({ ...formData, opis: e.target.value })}
        />

        <input
          type="text"
          placeholder="Lokacija *"
          className="w-full p-3 border border-pink-200 mb-4 rounded-xl outline-none"
          onChange={(e) => {
            setFormData({ ...formData, lokacija: e.target.value });
            if (errors.lokacija) setErrors({ ...errors, lokacija: "" });
          }}
        />
        <ErrorMsg name="lokacija" />

        <input
          type="number"
          step="any"
          placeholder="Latitude *"
          className="w-full p-3 border border-pink-200 mb-4 rounded-xl outline-none"
          onChange={(e) => setFormData({ ...formData, latitude: e.target.value })}
        />
        <input
          type="number"
          step="any"
          placeholder="Longitude *"
          className="w-full p-3 border border-pink-200 mb-4 rounded-xl outline-none"
          onChange={(e) => setFormData({ ...formData, longitude: e.target.value })}
        />

        {/* INPUT ZA SLIKU - OVO JE SADA TEKSTUALNO POLJE */}
        <input
          type="text"
          placeholder="Naziv slike (npr. sala1.jpg) *"
          className={`w-full p-3 border ${errors.slika ? "border-red-500" : "border-pink-200"} mb-4 rounded-xl outline-none`}
          onChange={(e) => {
            setFormData({ ...formData, slika: e.target.value });
            if (errors.slika) setErrors({ ...errors, slika: "" });
          }}
        />
        <ErrorMsg name="slika" />

        <div className="mb-6 border border-pink-100 p-4 rounded-xl bg-white/50">
          <label className="block text-[10px] font-black uppercase tracking-widest text-pink-400 mb-3">
            Karakteristike
          </label>
          {podaci.karakteristike.map((k) => (
            <label key={k.idKarakteristike} className="flex items-center gap-2 mb-1 text-sm text-gray-700">
              <input
                type="checkbox"
                className="accent-pink-600"
                onChange={(e) => {
                  const novi = e.target.checked
                    ? [...formData.izabraneKarakteristike, k.idKarakteristike]
                    : formData.izabraneKarakteristike.filter((id) => id !== k.idKarakteristike);
                  setFormData({ ...formData, izabraneKarakteristike: novi });
                }}
              />
              {k.naziv}
            </label>
          ))}
        </div>

        <div className={`mb-6 border ${errors.tipovi ? "border-red-500" : "border-pink-100"} p-4 rounded-xl bg-white/50`}>
          <label className="block text-[10px] font-black uppercase tracking-widest text-pink-400 mb-3">
            Tipovi događaja *
          </label>
          {podaci.tipovi.map((t) => (
            <label key={t.idTipDogadjaja} className="flex items-center gap-2 mb-1 text-sm text-gray-700">
              <input
                type="checkbox"
                className="accent-pink-600"
                onChange={(e) => {
                  const novi = e.target.checked
                    ? [...formData.izabraniTipovi, t.idTipDogadjaja]
                    : formData.izabraniTipovi.filter((id) => id !== t.idTipDogadjaja);
                  setFormData({ ...formData, izabraniTipovi: novi });
                  if (errors.tipovi) setErrors({ ...errors, tipovi: "" });
                }}
              />
              {t.naziv}
            </label>
          ))}
        </div>

        <button
          type="submit"
          className="w-full bg-pink-950 text-white py-4 rounded-xl font-black text-xs uppercase tracking-widest hover:bg-pink-900 transition-all shadow-lg"
        >
          Sačuvaj salu
        </button>
      </form>
    </div>
  );
}