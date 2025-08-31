import {
  Component,
  Input,
  AfterViewInit,
  Inject,
  PLATFORM_ID,
  ViewEncapsulation,
  OnChanges,
  SimpleChanges
} from '@angular/core';
import { isPlatformBrowser } from '@angular/common';
import { HttpClient, HttpClientModule } from '@angular/common/http';

@Component({
  selector: 'app-map-view',
  standalone: true,
  imports: [HttpClientModule],
  encapsulation: ViewEncapsulation.None,
  template: `<div id="map" class="map-container" [class.light-mode]="isLightMode"></div>`,
  styleUrls: ['./map-view.component.scss']
})
export class MapViewComponent implements AfterViewInit, OnChanges {
  @Input() selection: any;
  @Input() isLightMode: boolean = false;

  private isBrowser = false;
  private map: any;
  private L: any;
  private markers: any[] = [];

  // adapte si tu utilises un environment.ts
  private readonly API_BASE = 'http://localhost:8000';

  constructor(
    @Inject(PLATFORM_ID) private platformId: Object,
    private http: HttpClient
  ) {
    this.isBrowser = isPlatformBrowser(this.platformId);
  }

  async ngAfterViewInit(): Promise<void> {
    if (!this.isBrowser) return;

    this.L = await import('leaflet');
    await import('leaflet-draw');

    const container = document.getElementById('map');
    if (!container) {
      console.error('❌ Map container not found');
      return;
    }

    // Init map
    this.map = this.L.map(container, {
      zoomControl: false,
      attributionControl: false
    }).setView([33.5731, -7.5898], 13);

    // Zoom control
    this.L.control.zoom({ position: 'bottomright' }).addTo(this.map);

    // Tiles selon thème
    this.updateMapTheme();
    this.map.invalidateSize();

    // Outils de dessin (facultatifs)
    const drawControl = new this.L.Control.Draw({
      position: 'topright',
      draw: {
        rectangle: { shapeOptions: { color: '#3b82f6', weight: 2, fillOpacity: 0.1 } },
        polygon:   { shapeOptions: { color: '#10b981', weight: 2, fillOpacity: 0.1 } },
        circle:    { shapeOptions: { color: '#f59e0b', weight: 2, fillOpacity: 0.1 } },
        marker: false,
        polyline: false
      }
    });
    this.map.addControl(drawControl);

    this.map.on(this.L.Draw.Event.CREATED, (e: any) => {
      const layer = e.layer;
      layer.addTo(this.map);
    });

    this.renderSelection();
  }

  ngOnChanges(changes: SimpleChanges): void {
    if (changes['selection'] && this.selection && this.map) {
      this.renderSelection();
    }
    if (changes['isLightMode'] && this.map) {
      this.updateMapTheme();
    }
  }

  private updateMapTheme(): void {
    // enlever les tuiles existantes
    this.map.eachLayer((layer: any) => {
      if (layer instanceof this.L.TileLayer) this.map.removeLayer(layer);
    });

    if (this.isLightMode) {
      this.L.tileLayer('https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png', {
        attribution: '&copy; OpenStreetMap contributors',
        maxZoom: 20
      }).addTo(this.map);
    } else {
      this.L.tileLayer('https://tiles.stadiamaps.com/tiles/alidade_smooth_dark/{z}/{x}/{y}{r}.png', {
        attribution: '&copy; Stadia Maps',
        maxZoom: 20
      }).addTo(this.map);
    }
  }

  private renderSelection(): void {
    // nettoyer anciens éléments
    this.markers.forEach(m => this.map.removeLayer(m));
    this.markers = [];

    const clients = this.selection?.clients || [];
    if (clients.length === 0) return;

    const latlngs = clients.map((c: any) => [c.lat, c.lon]);

    // Ajouter uniquement les points (AUCUNE LIGNE)
    clients.forEach((client: any) => {
      const icon = this.L.divIcon({
        html: `
          <div class="custom-marker">
            <div class="marker-pulse"></div>
            <div class="marker-pin"><i class="fas fa-map-marker-alt"></i></div>
          </div>
        `,
        className: 'custom-fa-marker',
        iconSize: [40, 40],
        iconAnchor: [20, 40]
      });

      // marqueur draggable
      const marker = this.L.marker([client.lat, client.lon], {
        icon,
        draggable: true
      })
      .bindPopup(
        `
        <div class="custom-popup">
          <div class="popup-header">
            <h4><i class="fas fa-user-circle me-2"></i>${client.name}</h4>
          </div>
          <div class="popup-body">
            <p><i class="fas fa-map-marker-alt me-2"></i>
              <strong>Location:</strong> ${client.lat.toFixed(6)}, ${client.lon.toFixed(6)}
            </p>
            <p style="margin:0"><em>Astuce:</em> glisse ce point pour corriger sa position.</p>
          </div>
        </div>
        `,
        { className: 'custom-leaflet-popup', maxWidth: 320, closeButton: true }
      )
      .addTo(this.map);

      // on dragend => MAJ API + popup info
      marker.on('dragend', () => {
        const newPos = marker.getLatLng();
        this.updateClientLocation(client.id, newPos.lat, newPos.lng)
          .then(() => {
            client.lat = newPos.lat;
            client.lon = newPos.lng;
            marker.setPopupContent(`
              <div class="custom-popup">
                <div class="popup-header">
                  <h4><i class="fas fa-user-circle me-2"></i>${client.name}</h4>
                </div>
                <div class="popup-body">
                  <p><i class="fas fa-map-marker-alt me-2"></i>
                    <strong>Location:</strong> ${newPos.lat.toFixed(6)}, ${newPos.lng.toFixed(6)}
                  </p>
                  <p style="color:#10b981;margin:0"><strong>✔ Position sauvegardée</strong></p>
                </div>
              </div>
            `);
            marker.openPopup();
          })
          .catch(err => {
            // rollback si erreur
            marker.setLatLng([client.lat, client.lon]);
            alert('Erreur de sauvegarde: ' + (err?.error?.detail || err?.message || 'inconnue'));
          });
      });

      this.markers.push(marker);
    });

    // Ajuster la vue aux points
    const bounds = this.L.latLngBounds(latlngs);
    this.map.fitBounds(bounds, { padding: [50, 50] });
  }

  /** Appelle l'API pour mettre à jour la localisation d’un client */
  private async updateClientLocation(id: string, lat: number, lon: number): Promise<void> {
    await this.http.put(`${this.API_BASE}/clients/${encodeURIComponent(id)}/location`, { lat, lon }).toPromise();
  }
}
