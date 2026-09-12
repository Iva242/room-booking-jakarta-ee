"use client";

import { useState, useEffect } from "react";
import DatePicker from "react-datepicker";
import "react-datepicker/dist/react-datepicker.css";
import { Sala, TipDogadjaja } from "@/lib/types";

interface Props {
  sala: Sala;
  tipoviDogadjaja: TipDogadjaja[];
  onClose: () => void;
  onConfirm: (data: any) => void;
}

export default function RezervacijaModal({
  sala,
  tipoviDogadjaja,
  onClose,
  onConfirm,
}: Props) {
  const [startDate, setStartDate] = useState<Date | null>(new Date());
  const [endDate, setEndDate] = useState<Date | null>(
    new Date(new Date().getTime() + 60 * 60 * 1000),
  );

  const [selectedTip, setSelectedTip] = useState(
    tipoviDogadjaja[0]?.idTipDogadjaja || "",
  );
  const [loading] = useState(false); // Možeš da ukloniš i ovo ako ga nigde drugde ne koristiš

  useEffect(() => {
    if (startDate instanceof Date && !isNaN(startDate.getTime())) {
      setEndDate(new Date(startDate.getTime() + 60 * 60 * 1000));
    }
  }, [startDate]);

  // const handleSave = () => {
  //   console.log("Kliknuto na dugme POTVRDI!"); // DODAJ OVO
  //   if (!(startDate instanceof Date) || !(endDate instanceof Date)) {
  //     alert("Molimo izaberite ispravne datume.");
  //     return;
  //   }
  //   if (endDate <= startDate) {
  //     alert("Kraj mora biti posle početka.");
  //     return;
  //   }

  //   const formatZaBazu = (d: Date) => {
  //     const pad = (n: number) => n.toString().padStart(2, "0");
  //     return `${d.getFullYear()}-${pad(d.getMonth() + 1)}-${pad(d.getDate())} ${pad(d.getHours())}:${pad(d.getMinutes())}:00`;
  //   };

  //   try {
  //     const formattedPocetak = formatZaBazu(startDate);
  //     const formattedKraj = formatZaBazu(endDate);

  //     const podaciZaSlanje = {
  //       idSale: sala.idSale,
  //       tipDogadjaja: { idTipDogadjaja: Number(selectedTip) },
  //       pocetak: formattedPocetak,
  //       kraj: formattedKraj,
  //       status: "NA_CEKANJU",
  //     };

  //     onConfirm(podaciZaSlanje);
  //   } catch (error) {
  //     alert("Došlo je do greške pri formatiranju datuma.");
  //   }
  // };

  const handleSave = () => {
    // 1. Dobavi ulogovanog korisnika iz localStorage
    const userJson = localStorage.getItem("ulogovan_korisnik");
    if (!userJson) {
      alert("Niste ulogovani, ne možete rezervisati.");
      return;
    }
    const user = JSON.parse(userJson);

    // 2. Tvoje postojeće validacije
    if (!(startDate instanceof Date) || !(endDate instanceof Date)) {
      alert("Molimo izaberite ispravne datume.");
      return;
    }
    if (endDate <= startDate) {
      alert("Kraj mora biti posle početka.");
      return;
    }

    const formatZaBazu = (d: Date) => {
      const pad = (n: number) => n.toString().padStart(2, "0");
      return `${d.getFullYear()}-${pad(d.getMonth() + 1)}-${pad(d.getDate())} ${pad(d.getHours())}:${pad(d.getMinutes())}:00`;
    };

    try {
      const formattedPocetak = formatZaBazu(startDate);
      const formattedKraj = formatZaBazu(endDate);

      // 3. OVDE PAKUJEŠ KORISNIKA
      // Proveri u konzoli/logu da li tvoj user objekat ima polje "idKorisnika" ili samo "id"
      const podaciZaSlanje = {
        idSale: sala.idSale,
        tipDogadjaja: { idTipDogadjaja: Number(selectedTip) },
        korisnik: { id: user.id }, // <--- OVO JE KLJUČNA IZMENA
        pocetak: formattedPocetak,
        kraj: formattedKraj,
        status: "na_cekanju",
      };

      onConfirm(podaciZaSlanje);
    } catch (error) {
      alert("Došlo je do greške pri formatiranju datuma.");
    }
  };

  return (
    <div className="fixed inset-0 z-[100] flex items-center justify-center bg-black/60 backdrop-blur-sm p-4">
      <div className="bg-white rounded-3xl shadow-2xl w-full max-w-md max-h-[90vh] overflow-hidden flex flex-col">
        <div className="p-6 bg-pink-600 text-white text-center shrink-0">
          <h2 className="text-2xl font-bold uppercase tracking-wider">
            Rezervacija
          </h2>
          <p className="opacity-90">{sala.naziv}</p>
        </div>

        <div className="p-8 space-y-5 overflow-y-auto">
          <div>
            <label className="block text-xs font-bold text-gray-400 uppercase mb-2">
              Tip Događaja
            </label>
            <select
              className="w-full p-3 border-2 border-gray-100 rounded-xl outline-none"
              value={selectedTip}
              onChange={(e) => setSelectedTip(e.target.value)}
            >
              {tipoviDogadjaja.map((t) => (
                <option key={t.idTipDogadjaja} value={t.idTipDogadjaja}>
                  {t.naziv}
                </option>
              ))}
            </select>
          </div>

          <div>
            <label className="block text-xs font-bold text-pink-600 uppercase mb-2">
              Početak
            </label>
            <DatePicker
              selected={startDate}
              onChange={(date: Date | null) => setStartDate(date)}
              showTimeSelect
              timeIntervals={15}
              dateFormat="dd.MM.yyyy. HH:mm"
              minDate={new Date()}
              className="w-full p-3 border-2 border-gray-100 rounded-xl outline-none"
            />
          </div>

          <div>
            <label className="block text-xs font-bold text-gray-400 uppercase mb-2">
              Kraj
            </label>
            <DatePicker
              selected={endDate}
              onChange={(date: Date | null) => setEndDate(date)}
              showTimeSelect
              timeIntervals={15}
              dateFormat="dd.MM.yyyy. HH:mm"
              minDate={startDate || new Date()}
              className="w-full p-3 border-2 border-gray-100 rounded-xl outline-none"
            />
          </div>
        </div>

        <div className="p-6 bg-gray-50 flex gap-3 shrink-0">
          <button
            onClick={onClose}
            className="flex-1 py-3 bg-white border border-gray-200 rounded-xl font-bold text-gray-500"
          >
            ODUSTANI
          </button>
          <button
            onClick={handleSave}
            className="flex-1 py-3 rounded-xl font-bold text-white bg-pink-600 shadow-lg"
          >
            POTVRDI
          </button>
        </div>
      </div>
    </div>
  );
}
