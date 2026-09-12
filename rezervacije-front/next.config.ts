import type { NextConfig } from "next";

const nextConfig: NextConfig = {
  /* config options here */
  reactStrictMode: false,

  typescript: {
    // Ovo dozvoljava build čak i ako postoje TypeScript greške
    ignoreBuildErrors: true,
  },

} as any; 


export default nextConfig;
