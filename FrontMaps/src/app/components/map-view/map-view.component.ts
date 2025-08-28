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

@Component({
  selector: 'app-map-view',
  standalone: true,
  encapsulation: ViewEncapsulation.None,
  template: `<div id="map" class="map-container" [class.light-mode]="isLightMode"></div>`,
  styleUrls: ['./map-view.component.scss']
})

export class MapViewComponent implements AfterViewInit, OnChanges {
  @Input() selection: any;
  @Input() isLightMode: boolean = false;
  isBrowser = false;
  private map: any;
  private L: any;
  private markers: any[] = [];
  private polyline: any;
  private drawControl: any;

  constructor(@Inject(PLATFORM_ID) private platformId: Object) {
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

    // Create map with dark theme
    this.map = this.L.map(container, {
      zoomControl: false,
      attributionControl: false
    }).setView([33.5731, -7.5898], 13);

    // Add custom zoom control
    this.L.control.zoom({
      position: 'bottomright'
    }).addTo(this.map);

    // Dark theme tile layer
    this.L.tileLayer('https://tiles.stadiamaps.com/tiles/alidade_smooth_dark/{z}/{x}/{y}{r}.png', {
      attribution: '&copy; <a href="https://stadiamaps.com/">Stadia Maps</a>',
      maxZoom: 20
    }).addTo(this.map);

    this.map.invalidateSize();

    // Enhanced draw control
    this.drawControl = new this.L.Control.Draw({
      position: 'topright',
      draw: {
        rectangle: {
          shapeOptions: {
            color: '#3b82f6',
            weight: 2,
            fillOpacity: 0.1
          }
        },
        polygon: {
          shapeOptions: {
            color: '#10b981',
            weight: 2,
            fillOpacity: 0.1
          }
        },
        circle: {
          shapeOptions: {
            color: '#f59e0b',
            weight: 2,
            fillOpacity: 0.1
          }
        },
        marker: false,
        polyline: false
      }
    });
    this.map.addControl(this.drawControl);

    this.map.on(this.L.Draw.Event.CREATED, (e: any) => {
      const layer = e.layer;
      const type = e.layerType;
      console.log('✏️ Draw Created:', type, layer.toGeoJSON());
      layer.addTo(this.map);
    });

    this.renderSelection();
  }

  ngOnChanges(changes: SimpleChanges): void {
    if (changes['selection'] && this.selection && this.map) {
      this.renderSelection();
    }
    
    // Handle theme changes
    if (changes['isLightMode'] && this.map) {
      this.updateMapTheme();
    }
  }

  private updateMapTheme(): void {
    // Remove existing tile layers
    this.map.eachLayer((layer: any) => {
      if (layer instanceof this.L.TileLayer) {
        this.map.removeLayer(layer);
      }
    });

    // Add new tile layer based on current theme
    if (this.isLightMode) {
      this.L.tileLayer('https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png', {
        attribution: '&copy; <a href="https://www.openstreetmap.org/copyright">OpenStreetMap</a> contributors',
        maxZoom: 20
      }).addTo(this.map);
    } else {
      this.L.tileLayer('https://tiles.stadiamaps.com/tiles/alidade_smooth_dark/{z}/{x}/{y}.png', {
        attribution: '&copy; <a href="https://stadiamaps.com/">Stadia Maps</a>',
        maxZoom: 20
      }).addTo(this.map);
    }
  }

  private renderSelection(): void {
    this.markers.forEach(m => this.map.removeLayer(m));
    this.markers = [];
    if (this.polyline) {
      this.map.removeLayer(this.polyline);
      this.polyline = null;
    }

    // Check if showing all depots
    if (this.selection?.allDepots) {
      this.renderAllDepots();
      return;
    }

    // Original client rendering logic
    const clientsWithDepot = this.getClientsFromSelection();
    if (clientsWithDepot.length === 0) return;

    const latlngs = clientsWithDepot.map(c => [c.client.lat, c.client.lon]);

    clientsWithDepot.forEach(({ client, depotId }) => {
      const color = this.getDepotColor(depotId);
      
      // Enhanced marker with pulse effect
      const icon = this.L.divIcon({
        html: `
          <div class="custom-marker ${depotId}">
            <div class="marker-pulse"></div>
            <div class="marker-pin">
              <i class="fas fa-map-marker-alt"></i>
            </div>
          </div>
        `,
        className: 'custom-fa-marker',
        iconSize: [40, 40],
        iconAnchor: [20, 40]
      });

      // Enhanced popup
      const popupContent = `
        <div class="custom-popup">
          <div class="popup-header" style="border-left: 4px solid ${color}">
            <h4><i class="fas fa-user-circle me-2"></i>${client.name}</h4>
          </div>
          <div class="popup-body">
            <p><i class="fas fa-building me-2"></i><strong>Depot:</strong> ${depotId.replace('depot', 'Depot ')}</p>
            <p><i class="fas fa-map-marker-alt me-2"></i><strong>Location:</strong> ${client.lat.toFixed(4)}, ${client.lon.toFixed(4)}</p>
          </div>
        </div>
      `;

      const marker = this.L.marker([client.lat, client.lon], { icon })
        .bindPopup(popupContent, {
          className: 'custom-leaflet-popup',
          maxWidth: 300,
          closeButton: true
        })
        .addTo(this.map);
      this.markers.push(marker);
    });

    // Enhanced polyline
    if (clientsWithDepot.length > 1) {
      this.polyline = this.L.polyline(latlngs, {
        color: '#8b5cf6',
        weight: 4,
        opacity: 0.8,
        dashArray: '10, 10',
        lineCap: 'round',
        lineJoin: 'round'
      }).addTo(this.map);

      // Add animated arrow
      this.addAnimatedArrows(this.polyline);
    }

    const bounds = this.L.latLngBounds(latlngs);
    this.map.fitBounds(bounds, { padding: [50, 50] });
  }

  private renderAllDepots(): void {
    if (!this.selection?.depots) return;

    const depots = this.selection.depots;
    const latlngs = depots.map((depot: any) => [depot.lat, depot.lon]);

    depots.forEach((depot: any) => {
      const color = this.getDepotColor(depot.depotId);
      
      // Depot building icon with glow effect
      const icon = this.L.divIcon({
        html: `
          <div class="depot-marker ${depot.depotId}">
            <div class="depot-glow"></div>
            <div class="depot-building">
              <i class="fas fa-building"></i>
            </div>
            <div class="depot-label">${depot.depotName}</div>
          </div>
        `,
        className: 'custom-depot-marker',
        iconSize: [60, 80],
        iconAnchor: [30, 70]
      });

      // Count total clients for this depot
      const totalClients = depot.roles.reduce((total: number, role: any) => {
        return total + role.tournees.reduce((roleTotal: number, tournee: any) => {
          return roleTotal + (tournee.clients?.length || 0);
        }, 0);
      }, 0);

      const popupContent = `
        <div class="custom-popup depot-popup">
          <div class="popup-header" style="border-left: 4px solid ${color}">
            <h4><i class="fas fa-building me-2"></i>${depot.depotName}</h4>
          </div>
          <div class="popup-body">
            <div class="depot-stats">
              <div class="stat-item">
                <i class="fas fa-hashtag"></i>
                <span class="stat-label">ID:</span>
                <span class="stat-value">${depot.depotId}</span>
              </div>
              <div class="stat-item">
                <i class="fas fa-users"></i>
                <span class="stat-label">Roles:</span>
                <span class="stat-value">${depot.roles.length}</span>
              </div>
              <div class="stat-item">
                <i class="fas fa-map-marker-alt"></i>
                <span class="stat-label">Clients:</span>
                <span class="stat-value">${totalClients}</span>
              </div>
              <div class="stat-item">
                <i class="fas fa-map-pin"></i>
                <span class="stat-label">Location:</span>
                <span class="stat-value">${depot.lat.toFixed(4)}, ${depot.lon.toFixed(4)}</span>
              </div>
            </div>
          </div>
        </div>
      `;

      const marker = this.L.marker([depot.lat, depot.lon], { icon })
        .bindPopup(popupContent, {
          className: 'custom-leaflet-popup depot-popup-wrapper',
          maxWidth: 350,
          closeButton: true
        })
        .addTo(this.map);
      this.markers.push(marker);
    });

    // Fit map to show all depots
    if (latlngs.length > 0) {
      const bounds = this.L.latLngBounds(latlngs);
      this.map.fitBounds(bounds, { padding: [80, 80] });
    }
  }

  private addAnimatedArrows(polyline: any): void {
    const arrowHead = this.L.polylineDecorator(polyline, {
      patterns: [
        {
          offset: '100%',
          repeat: 0,
          symbol: this.L.Symbol.arrowHead({
            pixelSize: 15,
            polygon: false,
            pathOptions: {
              stroke: true,
              weight: 3,
              color: '#8b5cf6'
            }
          })
        }
      ]
    });
    arrowHead.addTo(this.map);
    this.markers.push(arrowHead);
  }

  private getClientsFromSelection(): { client: any, depotId: string }[] {
    if (!this.selection) return [];

    if (this.selection.client) {
      return [{ client: this.selection.client, depotId: this.selection.depot.depotId }];
    }

    if (this.selection.tournee) {
      const depotId = this.selection.depot.depotId;
      return (this.selection.tournee.clients || []).map((client: any) => ({ client, depotId }));
    }

    if (this.selection.role) {
      const depotId = this.selection.depot.depotId;
      return this.selection.role.tournees
        .flatMap((t: any) => (t.clients || []).map((client: any) => ({ client, depotId })));
    }

    if (this.selection.depot) {
      const depotId = this.selection.depot.depotId;
      return this.selection.depot.roles
        .flatMap((r: any) => r.tournees.flatMap((t: any) => (t.clients || []).map((client: any) => ({ client, depotId }))));
    }

    return [];
  }

  private getDepotColor(depotId: string): string {
    const colorMap: Record<string, string> = {
      'depot01': '#10b981', // green
      'depot02': '#ff1493', // hot pink
      'depot03': '#ffc0cb', // pink
      'depot04': '#3b82f6'  // blue
    };

    return colorMap[depotId] || '#ef4444';
  }
}