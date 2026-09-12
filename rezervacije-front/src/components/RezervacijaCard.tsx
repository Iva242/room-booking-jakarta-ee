// import { Rezervacija } from "@/lib/types";

// const formatirajDatum = (datum: string | Date) => {
//   if (!datum) return "";

//   try {
//     // Ako je Date objekat, konvertuj ga u naš format
//     if (datum instanceof Date) {
//       const dan = datum.getDate().toString().padStart(2, "0");
//       const mesec = (datum.getMonth() + 1).toString().padStart(2, "0");
//       const godina = datum.getFullYear();
//       const sati = datum.getHours().toString().padStart(2, "0");
//       const minuti = datum.getMinutes().toString().padStart(2, "0");

//       return `${dan}.${mesec}.${godina}. u ${sati}:${minuti}h`;
//     }

//     // Ako je string, radi kao pre
//     const cistString = datum.replace("T", " ").split(".")[0];
//     const [datumStr, vreme] = cistString.split(" ");
//     const [godina, mesec, dan] = datumStr.split("-");
//     const [sati, minuti] = vreme.split(":");

//     return `${dan}.${mesec}.${godina}. u ${sati}:${minuti}h`;
//   } catch (e) {
//     console.warn("Greška pri formatiranju datuma:", datum, e);
//     return String(datum);
//   }
// };

// export default function RezervacijaCard({
//   res,
//   onOtkazi,
//   onPotvrdi,
//   isAdmin = false,
// }: {
//   res: Rezervacija;
//   onOtkazi: (id: number) => void;
//   onPotvrdi?: (id: number) => void;
//   isAdmin?: boolean;
// }) {
//   // Boje za statuse
//   const statusColor =
//     {
//       potvrdjena: "bg-green-100 text-green-800",
//       na_cekanju: "bg-yellow-100 text-yellow-800",
//       otkazana: "bg-red-100 text-red-800",
//       zavrsena: "bg-gray-100 text-gray-800",
//       u_toku: "bg-pink-100 text-pink-800",
//     }[res.status] || "bg-blue-100 text-blue-800";

//   return (
//     <div className="card card-hover flex flex-col justify-between h-full">
//       <div>
//         <div className="flex justify-between items-start mb-4">
//           <div>
//             <h3 className="text-lg font-bold text-pink-900 dark:text-pink-200">
//               Rezervacija #{res.idRezervacije}
//             </h3>
//             <p className="text-sm text-pink-700 dark:text-pink-400 opacity-70">
//               Sala: {res.sala?.naziv || `ID ${res.sala?.idSale || "N/A"}`}
//             </p>
//           </div>
//           <span
//             className={`px-3 py-1 rounded-full text-[10px] uppercase tracking-wider font-bold ${statusColor}`}
//           >
//             {res.status.replace("_", " ")}
//           </span>
//         </div>

//         <div className="space-y-3 mb-6 text-pink-900 dark:text-pink-100">
//           <p className="text-sm flex items-center gap-2">
//             <span>📅</span>
//             <span>
//               <strong>Početak:</strong> {formatirajDatum(res.pocetak)}
//             </span>
//           </p>
//           <p className="text-sm flex items-center gap-2">
//             <span>🏁</span>
//             <span>
//               <strong>Kraj:</strong> {formatirajDatum(res.kraj)}
//             </span>
//           </p>
//           {/*prikazujemo ime i prezime korisnika adminu*/}
//           {isAdmin && res.korisnik && (
//             <p className="text-sm flex items-center gap-2 pt-2 border-t border-pink-100/50 mt-2 opacity-80">
//               <span>👤</span>
//               <span>
//                 <strong>Korisnik:</strong> {res.korisnik.ime}{" "}
//                 {res.korisnik.prezime}
//               </span>
//             </p>
//           )}
//         </div>
//       </div>

//       {/* DUGMICI*/}
//       <div className="flex gap-2 mt-auto">
//         {/* Dugme POTVRDI - Vidljivo samo adminu i samo ako je na cekanju */}
//         {isAdmin && res.status === "na_cekanju" && (
//           <button
//             onClick={() => onPotvrdi?.(res.idRezervacije)}
//             className="flex-1 bg-green-600 text-white py-2.5 rounded-xl hover:bg-green-700 transition-colors text-sm font-semibold shadow-sm"
//           >
//             Potvrdi
//           </button>
//         )}

//         {/* Dugme OTKAZI */}
//         {(res.status === "na_cekanju" || res.status === "potvrdjena") && (
//           <button
//             onClick={() => onOtkazi(res.idRezervacije)}
//             className={`px-4 py-2.5 border-2 border-pink-200 text-pink-700 rounded-xl 
//                            transition-all duration-300 ease-in-out
//                            hover:bg-red-500 hover:border-red-500 hover:text-white 
//                            text-sm font-medium ${res.status === "potvrdjena" ? "flex-1" : ""}`}
//           >
//             Otkaži
//           </button>
//         )}
//       </div>
//     </div>
//   );
// }



import { Rezervacija } from "@/lib/types";

// 1. Definišemo interfejs za bolju preglednost i tipizaciju
interface RezervacijaCardProps {
  res: Rezervacija;
  onOtkazi: (id: number) => void;
  onPotvrdi?: (id: number) => void;
  isAdmin?: boolean;
  isOwner?: boolean; // Dodato radi kontrole otkazivanja
}

// Helper funkcija ostaje ista
const formatirajDatum = (datum: string | Date) => {
  if (!datum) return "";
  try {
    if (datum instanceof Date) {
      const dan = datum.getDate().toString().padStart(2, "0");
      const mesec = (datum.getMonth() + 1).toString().padStart(2, "0");
      const godina = datum.getFullYear();
      const sati = datum.getHours().toString().padStart(2, "0");
      const minuti = datum.getMinutes().toString().padStart(2, "0");
      return `${dan}.${mesec}.${godina}. u ${sati}:${minuti}h`;
    }
    const cistString = datum.replace("T", " ").split(".")[0];
    const [datumStr, vreme] = cistString.split(" ");
    const [godina, mesec, dan] = datumStr.split("-");
    const [sati, minuti] = vreme.split(":");
    return `${dan}.${mesec}.${godina}. u ${sati}:${minuti}h`;
  } catch (e) {
    return String(datum);
  }
};

export default function RezervacijaCard({
  res,
  onOtkazi,
  onPotvrdi,
  isAdmin = false,
  isOwner = false,
}: RezervacijaCardProps) {
  console.log("Rezervacija podaci:", res); // Dodaj ovo!
  // Logika za boje
  const statusColor = {
    potvrdjena: "bg-green-100 text-green-800",
    na_cekanju: "bg-yellow-100 text-yellow-800",
    otkazana: "bg-red-100 text-red-800",
    zavrsena: "bg-gray-100 text-gray-800",
    u_toku: "bg-pink-100 text-pink-800",
  }[res.status] || "bg-blue-100 text-blue-800";

  // 2. Čista logika za dugmiće
  const mozeDaSeOtkaze = (isOwner || isAdmin) && (res.status === "na_cekanju" || res.status === "potvrdjena");

  return (
    <div className="card card-hover flex flex-col justify-between h-full">
      <div>
        <div className="flex justify-between items-start mb-4">
          <div>
            <h3 className="text-lg font-bold text-pink-900 dark:text-pink-200">
              Rezervacija #{res.idRezervacije}
            </h3>
            <p className="text-sm text-pink-700 dark:text-pink-400 opacity-70">
              Sala: {res.sala?.naziv || `ID ${res.sala?.idSale || "N/A"}`}
            </p>
          </div>
          <span className={`px-3 py-1 rounded-full text-[10px] uppercase tracking-wider font-bold ${statusColor}`}>
            {res.status.replace("_", " ")}
          </span>
        </div>

        <div className="space-y-3 mb-6 text-pink-900 dark:text-pink-100">
          <p className="text-sm flex items-center gap-2">
            <span>📅</span>
            <span><strong>Početak:</strong> {formatirajDatum(res.pocetak)}</span>
          </p>
          <p className="text-sm flex items-center gap-2">
            <span>🏁</span>
            <span><strong>Kraj:</strong> {formatirajDatum(res.kraj)}</span>
          </p>
          
          {/* Prikaz korisnika samo adminu */}
          {isAdmin && res.korisnik && (
            <p className="text-sm flex items-center gap-2 pt-2 border-t border-pink-100/50 mt-2 opacity-80">
              <span>👤</span>
              <span><strong>Korisnik:</strong> {res.korisnik.ime} {res.korisnik.prezime}</span>
            </p>
          )}
        </div>
      </div>

      {/* DUGMICI */}
      <div className="flex gap-2 mt-auto">
        {/* Admin potvrđuje */}
        {isAdmin && res.status === "na_cekanju" && (
          <button
            onClick={() => onPotvrdi?.(res.idRezervacije)}
            className="flex-1 bg-green-600 text-white py-2.5 rounded-xl hover:bg-green-700 transition-colors text-sm font-semibold shadow-sm"
          >
            Potvrdi
          </button>
        )}

        {/* Admin ili Vlasnik otkazuje (koristimo našu čistu logiku) */}
        {mozeDaSeOtkaze && (
          <button
            onClick={() => onOtkazi(res.idRezervacije)}
            className="flex-1 px-4 py-2.5 border-2 border-pink-200 text-pink-700 rounded-xl transition-all duration-300 hover:bg-red-500 hover:border-red-500 hover:text-white text-sm font-medium"
          >
            Otkaži
          </button>
        )}
      </div>
    </div>
  );
}