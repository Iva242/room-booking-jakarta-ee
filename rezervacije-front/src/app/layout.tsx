import 'leaflet/dist/leaflet.css';
import type { Metadata } from "next";
import { Geist, Geist_Mono } from "next/font/google";
import "./globals.css";
import { ObavestenjaProvider } from "@/components/Obavestenja";


const geistSans = Geist({
  variable: "--font-geist-sans",
  subsets: ["latin"],
});

const geistMono = Geist_Mono({
  variable: "--font-geist-mono",
  subsets: ["latin"],
});

export const metadata: Metadata = {
  title: "Rezervacije sala",
  description: "Sistem za rezervaciju sala i upravljanje terminima",
};

export default function RootLayout({
  children,
}: Readonly<{
  children: React.ReactNode;
}>) {
  return (
    <html lang="sr">
      <body
        className={`${geistSans.variable} ${geistMono.variable} antialiased`}
      >
        <ObavestenjaProvider>{children}</ObavestenjaProvider>
      </body>
    </html>
  );
}
