import { defineConfig } from 'vite'
import react from '@vitejs/plugin-react'

export default defineConfig({
  plugins: [react()],
  server: {
    port: 3000,
    proxy: {
      '/api': {
        target: 'http://localhost:8080',
        changeOrigin: true,
      },
      '/user': {
        target: 'http://localhost:8080',
        changeOrigin: true,
      },
      '/movie': {
        target: 'http://localhost:8080',
        changeOrigin: true,
      },
      '/theater': {
        target: 'http://localhost:8080',
        changeOrigin: true,
      },
      '/show': {
        target: 'http://localhost:8080',
        changeOrigin: true,
      },
      '/ticket': {
        target: 'http://localhost:8080',
        changeOrigin: true,
      },
      '/admin': {
        target: 'http://localhost:8080',
        changeOrigin: true,
      }
    }
  },
  build: {
    outDir: 'dist',
    sourcemap: true
  }
})
