import { fileURLToPath, URL } from 'node:url';
import { defineConfig } from 'vite';
import vue from '@vitejs/plugin-vue';

export default defineConfig({
  plugins: [
    vue(), // Vite plugin to handle Vue files
  ],
  resolve: {
    alias: {
      // Aliases the '@' symbol to the 'src' directory for easier imports
      '@': fileURLToPath(new URL('./src', import.meta.url)),
    },
  },
  server: {
    host: '0.0.0.0',  // Listens on all IPv4 addresses, including localhost
    port: 80,          // Frontend will run on port 80
    proxy: {
      '/api': {
        target: 'http://localhost:8080',  // backend runs on localhost:8080
        changeOrigin: true,  // Changes the origin of the host header to match the target
        secure: false,  // Disables SSL/TLS verification (useful if backend doesn't have a valid SSL cert)
        rewrite: (path) => path.replace(/^\/api/, ''),  // Removes "/api" from the path before forwarding
      },
    },
  },
});
