/** @type {import('next').NextConfig} */
const nextConfig = {
  reactStrictMode: true,
  output: 'standalone',
  async rewrites() {
    return [
      {
        source: '/api/core/:path*',
        destination: 'http://localhost:8080/api/v1/:path*',
      },
      {
        source: '/api/ai/:path*',
        destination: 'http://localhost:8000/api/v1/ai/:path*',
      },
    ];
  },
};

export default nextConfig;
