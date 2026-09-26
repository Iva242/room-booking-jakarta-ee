import { useState } from "react";
import { Sala } from "@/lib/types";

export default function DeleteSalaModal({
  sale,
  onClose,
  onConfirm,
}: {
  sale: Sala[];
  onClose: () => void;
  onConfirm: (id: number) => void;
}) {
  const [selectedId, setSelectedId] = useState("");
  const [error, setError] = useState("");
  const [success, setSuccess] = useState(false);

  const handleConfirm = async () => {
    setError(""); // Resetuj grešku pre klika

    try {
      //  onConfirm mora da bude async funkcija koja poziva axios
      await onConfirm(Number(selectedId));
      setSuccess(true);
      setTimeout(() => {
        onClose();
      }, 1500);
    } catch (err: any) {
      // 4xx: server zna sta nije u redu, npr. da sala ima aktivne rezervacije
      // 5xx ili nema odgovora: razlog korisnika ne zanima
      setError(
        err?.status && err.status < 500
          ? err.message
          : "Sistem ne može da obriše salu.",
      );
    }
  };

  return (
    <div className="fixed inset-0 bg-black/50 z-[100] flex items-center justify-center p-4">
      <div className="bg-white rounded-3xl p-8 w-full max-w-sm shadow-2xl">
        {success ? (
          // LEPA PORUKA ZA USPEH
          <div className="text-center py-8 animate-in zoom-in">
            <div className="w-16 h-16 bg-green-100 text-green-600 rounded-full flex items-center justify-center mx-auto mb-4 text-2xl">
              ✓
            </div>
            <h3 className="text-xl font-bold text-gray-800">
              Sala je obrisana!
            </h3>
            <p className="text-gray-500 text-sm">Lista sala je osvežena.</p>
          </div>
        ) : (
          <>
            <h3 className="text-xl font-bold text-pink-950 mb-6 uppercase tracking-widest">
              Obriši salu
            </h3>
            <p className="text-gray-500 text-sm mb-6">
              Izaberite salu koju želite trajno da uklonite.
            </p>

            <select
              className={`w-full p-4 border ${error ? "border-red-300" : "border-gray-200"} rounded-2xl mb-4 outline-none focus:border-pink-500 transition-all`}
              value={selectedId}
              onChange={(e) => {
                setSelectedId(e.target.value);
                setError("");
              }}
            >
              <option value="">Izaberi salu...</option>
              {sale.map((s: any) => (
                // OVDE PROMENI: key i value treba da budu s.idSale
                <option key={s.idSale} value={s.idSale}>
                  {s.naziv}
                </option>
              ))}
            </select>

            {error && (
              <div className="mb-6 p-4 bg-red-50 border border-red-100 rounded-2xl flex items-start gap-3 animate-shake">
                <span className="text-red-600 text-lg">⚠️</span>
                <p className="text-red-700 text-xs font-bold leading-tight uppercase tracking-tight">
                  {error}
                </p>
              </div>
            )}

            <div className="flex gap-4">
              <button
                onClick={onClose}
                className="flex-1 py-4 text-gray-400 font-black text-xs uppercase tracking-widest hover:text-gray-600 transition-all"
              >
                Otkaži
              </button>
              <button
                onClick={handleConfirm}
                disabled={!selectedId}
                className="flex-1 bg-red-600 text-white py-4 rounded-2xl font-black text-xs uppercase tracking-widest disabled:opacity-30 shadow-lg shadow-red-200 hover:bg-red-700 transition-all"
              >
                Obriši
              </button>
            </div>
          </>
        )}
      </div>
    </div>
  );
}
