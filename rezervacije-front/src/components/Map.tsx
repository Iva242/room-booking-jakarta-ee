'use client'; // OBAVEZNO za Next.js jer mapa koristi browser funkcije

import { MapContainer, TileLayer, Marker, Popup } from 'react-leaflet';
// @ts-ignore
import L from 'leaflet';
import 'leaflet/dist/leaflet.css';

// Fix za ikone
import markerIcon from 'leaflet/dist/images/marker-icon.png';
import markerShadow from 'leaflet/dist/images/marker-shadow.png';


let DefaultIcon = L.icon({
iconUrl: 'https://unpkg.com/leaflet@1.9.4/dist/images/marker-icon.png',
    shadowUrl: 'https://unpkg.com/leaflet@1.9.4/dist/images/marker-shadow.png',
    iconSize: [25, 41],
    iconAnchor: [12, 41]
});
L.Marker.prototype.options.icon = DefaultIcon;

const MapComponent = ({ lat, lng, naziv }: { lat: number, lng: number, naziv: string }) => {
  return (
    <div style={{ height: "400px", width: "100%", borderRadius: "10px", overflow: "hidden" }}>
      <MapContainer center={[lat, lng]} zoom={15} style={{ height: '100%', width: '100%' }}>
        <TileLayer 
        attribution='&copy; <a href="https://www.openstreetmap.org/copyright">OpenStreetMap</a> contributors'
        url="https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png" />
        <Marker position={[lat, lng]}>
          <Popup>{naziv}</Popup>
        </Marker>
      </MapContainer>
    </div>
  );
};

export default MapComponent;