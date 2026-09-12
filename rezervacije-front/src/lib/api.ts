const BASE_URL = "http://localhost:8080/api";

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

// Dodajemo pomoćnu funkciju da ne pišemo isti kod za greške 10 puta
const handleResponse = async (res: Response) => {
  if (!res.ok) {
    let errorData;
    try {
      const text = await res.text();
      errorData = text ? JSON.parse(text) : { message: "Greška na serveru" };
    } catch (e) {
      errorData = { message: "Greška pri obradi odgovora" };
    }
    throw { response: { status: res.status, data: errorData } };
  }
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
    if (!res.ok) {
      const err = await res.text();
      throw new Error(err || "Greška pri registraciji");
    }
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

    if (!res.ok) {
      // Pokušaj da pročitaš poruku koju je backend poslao
      const errorMessage = await res.text();
      // Ako je stigla poruka (npr. "Ne možeš obrisati salu..."), baci je,
      // inače koristi generičku
      throw new Error(errorMessage || "Nije moguće obrisati salu");
    }
    return { success: true };
  },

  getSalaById: async (id: number) => {
    const res = await fetch(`http://localhost:8080/api/sale/${id}`, {
      method: "GET",
      headers: {
        "Content-Type": "application/json",
        // Ako ti treba token za pregled detalja, dodaj ga ovde:
        // "Authorization": `Bearer ${localStorage.getItem("token")}`
      },
    });

    if (!res.ok) {
      throw new Error("Nije moguće dohvatiti salu");
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
    if (!res.ok) throw new Error(await res.text());
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
    if (!res.ok) throw new Error("Greška pri otkazivanju");
    return { success: true }; // Spring vraća tekst, ne JSON, pa vraćamo uspeh
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
    if (!res.ok) {
      const errorText = await res.text(); // Često je korisno pročitati poruku greške
      throw new Error(errorText || "Greška pri potvrdi");
    }

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
