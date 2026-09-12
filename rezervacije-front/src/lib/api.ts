export const BASE_URL =
  process.env.NEXT_PUBLIC_API_URL ?? "http://localhost:8080/api";

// Helper za headere
const getHeaders = (isAuth = true) => {
  const headers: any = {
    "Content-Type": "application/json",
    Accept: "application/json",
  };
  if (isAuth) {
    const token = localStorage.getItem("token");
    if (token) headers["Authorization"] = `Bearer ${token}`;
  }
  return headers;
};

// Backend greske vraca kao { status, poruka, putanja, vreme }.
// Izuzetak je /api/auth/verify, koji i dalje salje obican tekst jer ga
// korisnik otvara u pregledacu.
export const procitajGresku = async (res: Response): Promise<string> => {
  const tekst = await res.text();
  if (!tekst) return `Greška ${res.status}`;
  try {
    const podaci = JSON.parse(tekst);
    return podaci?.poruka || tekst;
  } catch {
    return tekst;
  }
};

// Dodajemo pomoćnu funkciju da ne pišemo isti kod za greške 10 puta
const handleResponse = async (res: Response) => {
  if (!res.ok) throw new Error(await procitajGresku(res));
  if (res.status === 204) return null;
  return res.json();
};

export const api = {
  // --- AUTH ---

  login: async (podaci: { korisnickoIme: string; lozinka: string }) => {
    const res = await fetch(`${BASE_URL}/auth/login`, {
      method: "POST",
      headers: getHeaders(false),
      body: JSON.stringify(podaci),
    });

    if (!res.ok) throw new Error("Pogrešno korisničko ime ili lozinka");

    return res.json();
  },

  register: async (podaci: any) => {
    const res = await fetch(`${BASE_URL}/auth/register`, {
      method: "POST",
      headers: getHeaders(false),
      body: JSON.stringify(podaci),
    });
    if (!res.ok) throw new Error(await procitajGresku(res));
    return res.json();
  },

  // --- SALE ---
  getSale: async (page: number = 1, params: any = {}) => {
    const springPage = page > 0 ? page - 1 : 0;
    let queryString = `?page=${springPage}`;
    Object.keys(params).forEach((key) => {
      if (params[key] && params[key] !== "sve")
        queryString += `&${key}=${params[key]}`;
    });
    const res = await fetch(`${BASE_URL}/sale${queryString}`, {
      headers: getHeaders(false),
    });
    return res.json();
  },

  getAllSale: async () => {
    const res = await fetch(`${BASE_URL}/sale`, { headers: getHeaders(false) });
    return res.json();
  },

  // --- SALE (Dodaj ovo u api objekat) ---
  deleteSala: async (id: number) => {
    const res = await fetch(`${BASE_URL}/sale/${id}`, {
      method: "DELETE",
      headers: getHeaders(true),
    });

    if (!res.ok) throw new Error(await procitajGresku(res));
    return { success: true };
  },

  getSalaById: async (id: number) => {
    const res = await fetch(`${BASE_URL}/sale/${id}`, {
      method: "GET",
      headers: getHeaders(false),
    });

    if (!res.ok) {
      throw new Error(await procitajGresku(res));
    }

    return res.json();
  },

  // --- SALE (Dodaj ovo) ---
  createSala: async (salaDto: any) => {
    const res = await fetch(`${BASE_URL}/sale`, {
      method: "POST",
      headers: getHeaders(true), // Koristi ispravne headere sa tokenom
      body: JSON.stringify(salaDto), // Šaljemo čist JSON
    });

    return handleResponse(res);
  },

  // --- REZERVACIJE ---
  // Backend očekuje RezervacijaDto sa ugnježdenim objektima: { korisnik: { id: 1 }, sala: { idSale: 2 }, ... }
  createRezervacija: async (podaci: any) => {
    const res = await fetch(`${BASE_URL}/rezervacije`, {
      method: "POST",
      headers: getHeaders(true),
      body: JSON.stringify(podaci),
    });
    if (!res.ok) throw new Error(await procitajGresku(res));
    return res.json();
  },

  // --- REZERVACIJE ---

  // 1. OVO DODAJ: Nova metoda koja ne traži ID
  getMojeRezervacije: async () => {
    const res = await fetch(`${BASE_URL}/rezervacije/moje`, {
      method: "GET",
      headers: getHeaders(true), // Obavezno 'true' da bi poslao JWT token!
    });
    return handleResponse(res); // Koristimo tvoj handleResponse helper koji si već napisala
  },

  // --- REZERVACIJE (ADMIN) ---

  // Ovo je za Admina da vidi sve
  getAllRezervacije: async () => {
    const res = await fetch(`${BASE_URL}/rezervacije`, {
      method: "GET",
      headers: getHeaders(true), // Šaljemo token da bi backend znao da si Admin
    });
    return handleResponse(res);
  },

  otkaziRezervaciju: async (id: number) => {
    const res = await fetch(`${BASE_URL}/rezervacije/${id}`, {
      method: "DELETE",
      headers: getHeaders(true),
    });
    if (!res.ok) throw new Error(await procitajGresku(res));
    return { success: true };
  },

  potvrdiRezervaciju: async (idRezervacije: number) => {
    const res = await fetch(
      `${BASE_URL}/rezervacije/potvrdi/${idRezervacije}`,
      {
        method: "PUT",
        headers: getHeaders(true),
      },
    );

    // Ako backend vrati grešku (status 400 ili 500), bacaš izuzetak
    if (!res.ok) throw new Error(await procitajGresku(res));

    // Pošto vraćaš običan String, ne treba ti res.json().
    // Samo vrati uspeh da znaš da je sve prošlo.
    return { success: true };
  },

  // U api.ts
  getKarakteristike: async () => {
    // Mora biti tačno kako piše u KarakteristikaController (@RequestMapping)
    const res = await fetch(`${BASE_URL}/karakteristike`, {
      headers: getHeaders(true),
    });
    if (!res.ok) throw new Error(`Greška: ${res.status}`);
    return res.json();
  },

  getTipovi: async () => {
    // Mora imati crtice, baš kao u TipDogadjajaController
    const res = await fetch(`${BASE_URL}/tipovi-dogadjaja`, {
      headers: getHeaders(true),
    });
    if (!res.ok) throw new Error(`Greška: ${res.status}`);
    return res.json();
  },

  // --- KORISNICI ---
  getKorisnici: async () => {
    const res = await fetch(`${BASE_URL}/korisnici`, {
      headers: getHeaders(true),
    });

    if (!res.ok) {
      throw new Error("Greška pri dohvatanju korisnika");
    }

    return res.json();
  },

  // Dodaj ovo u api objekat u api.ts
  pretraziSale: async (term: string) => {
    const res = await fetch(
      `${BASE_URL}/sale/pretraga?term=${encodeURIComponent(term)}`,
      {
        headers: { Accept: "application/json" },
      },
    );
    if (!res.ok) throw new Error("Greška pri pretrazi");
    return res.json();
  },

  // U api objektu u api.ts
  updateSala: async (id: number, salaDto: any) => {
    const res = await fetch(`${BASE_URL}/sale/${id}`, {
      method: "PUT",
      headers: {
        ...getHeaders(true),
        "Content-Type": "application/json",
      },
      body: JSON.stringify(salaDto),
    });
    return handleResponse(res);
  },
};
