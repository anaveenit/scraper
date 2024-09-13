- name: Build and Push Docker Image
  run: |
    docker build -t ghcr.io/${{ github.repository_owner }}/web-scraper:latest -f ./docker/Dockerfile .
    docker push ghcr.io/${{ github.repository_owner }}/web-scraper:latest
