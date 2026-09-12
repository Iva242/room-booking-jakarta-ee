"use client";
import { 
  PieChart, Pie, Cell, ResponsiveContainer, Tooltip, Legend, 
  BarChart, Bar, XAxis, YAxis, CartesianGrid, Label 
} from 'recharts';

const COLORS = ['#db2777', '#f472b6', '#fbcfe8', '#9ca3af', '#ef4444'];

export default function StatistikaChart({ podaci }: { podaci: any[] }) {
  
  // 1. Grupisanje podataka za Kružni grafikon (Statusi)
  const statusPodaci = [
    { name: 'Potvrđeno', value: podaci.filter(r => r.status === 'potvrdjena').length },
    { name: 'Na čekanju', value: podaci.filter(r => r.status === 'na_cekanju').length },
    { name: 'Otkazano', value: podaci.filter(r => r.status === 'otkazana').length },
    { name: 'Završeno', value: podaci.filter(r => r.status === 'zavrsena').length },
  ];

  // 2. Logika za grupisanje rezervacija po salama (sabiramo koliko koja sala ima ukupno rezervacija)
  const saleMap = podaci.reduce((acc: any, curr: any) => {
    const idSale = curr.idSale;
    acc[idSale] = (acc[idSale] || 0) + 1;
    return acc;
  }, {});

  // Pretvaramo mapu u niz koji Recharts razume
  const barPodaci = Object.keys(saleMap).map(id => ({
    nazivSale: `Sala #${id}`,
    brojRezervacija: saleMap[id]
  }));

  return (
    <div className="grid grid-cols-1 md:grid-cols-2 gap-10 mt-10">
      
      {/* KRUŽNI GRAFIKON - Statusi */}
      <div className="bg-white p-6 rounded-[30px] shadow-sm border border-pink-50">
        <h3 className="text-lg font-bold text-pink-900 mb-4 uppercase tracking-tight">Statusi Rezervacija</h3>
        <div className="h-[300px] w-full">
          <ResponsiveContainer width="100%" height="100%">
            <PieChart>
              <Pie
                data={statusPodaci}
                cx="50%"
                cy="50%"
                innerRadius={60}
                outerRadius={80}
                paddingAngle={5}
                dataKey="value"
              >
                {statusPodaci.map((entry, index) => (
                  <Cell key={`cell-${index}`} fill={COLORS[index % COLORS.length]} />
                ))}
              </Pie>
              <Tooltip />
              <Legend />
            </PieChart>
          </ResponsiveContainer>
        </div>
      </div>

      {/* 2. STUBIČASTI GRAFIKON - POPULARNOST SALA */}
      <div className="bg-white p-8 rounded-[40px] shadow-sm border border-pink-50">
        <h3 className="text-lg font-bold text-pink-900 mb-4 uppercase tracking-tight">Popularnost sala</h3>
        <div className="h-[350px] w-full">
          <ResponsiveContainer width="100%" height="100%">
            <BarChart data={barPodaci} margin={{ top: 20, right: 30, left: 30, bottom: 60 }}>
              <CartesianGrid strokeDasharray="3 3" vertical={false} stroke="#f0f0f0" />
              
              {/* X OSA - IDENTIFIKATORI */}
              <XAxis dataKey="nazivSale" stroke="#9ca3af" fontSize={12} tickLine={false} axisLine={false} dy={10}>
                <Label value="IDENTIFIKATOR SALE" offset={-45} position="insideBottom" fill="#be185d" fontSize={12} fontWeight="bold" />
              </XAxis>
              
              {/* Y OSA - BROJ REZERVACIJA */}
              <YAxis stroke="#9ca3af" fontSize={12} tickLine={false} axisLine={false}>
                <Label value="BROJ REZERVACIJA" angle={-90} position="insideLeft" style={{ textAnchor: 'middle' }} fill="#be185d" fontSize={12} fontWeight="bold" />
              </YAxis>
              
              {/*PRIKAZ NA HOVER */}
              <Tooltip 
                cursor={{ fill: '#fff1f2', radius: 10 }}
                contentStyle={{ borderRadius: '15px', border: 'none', boxShadow: '0 10px 15px -3px rgba(0,0,0,0.1)' }}
                itemStyle={{ color: '#be185d', fontWeight: 'bold' }}
                labelStyle={{ marginBottom: '5px', color: '#1f2937' }}
                formatter={(value) => [value, "Rezervacija"]}
              />
              
              <Bar 
                dataKey="brojRezervacija" 
                fill="#db2777" 
                radius={[10, 10, 0, 0]} 
                barSize={40}
              />
            </BarChart>
          </ResponsiveContainer>
        </div>
      </div>

    </div>
  );
}