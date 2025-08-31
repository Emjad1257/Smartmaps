import { Component, EventEmitter, Output, Input, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { HttpClient, HttpClientModule } from '@angular/common/http';

interface Client {
  id: string;
  name: string;
  uprole_code: string;
  lat: number;
  lon: number;
}

interface Itinerary {
  itineraryId: string;
  clients: Client[];
}

interface GeoArea {
  geoId: string;
  itineraries: Itinerary[];
}

interface Upgeo {
  upgeoId: string;
  geoareas: GeoArea[];
}

@Component({
  selector: 'app-sidebar',
  standalone: true,
  imports: [CommonModule, FormsModule, HttpClientModule],
  template: `
    <!-- Sidebar Toggle -->
    <button class="sidebar-toggle" (click)="toggleSidebar()" [ngClass]="{ 'active': isOpen }">
      <i class="fas fa-bars"></i>
    </button>

    <!-- Sidebar Overlay -->
    <div class="sidebar-overlay" [ngClass]="{ 'active': isOpen }" (click)="closeSidebar()"></div>

    <!-- Sidebar -->
    <div class="sidebar" [ngClass]="{ 'active': isOpen, 'light-mode': isLightMode }">
      <div class="sidebar-header">
        <h3><i class="fas fa-map-marked-alt me-2"></i>Geo Manager</h3>
        <button class="close-btn" (click)="closeSidebar()">
          <i class="fas fa-times"></i>
        </button>
      </div>

      <div class="sidebar-content">
        <!-- UPGEO List -->
        <div class="depot-list">
          <div *ngFor="let upgeo of data" class="depot-item">
            <!-- UPGEO Header (chevrons supprimés) -->
            <button class="section-btn depot-btn"
                    [ngClass]="{ 'active': selectedUpgeoId === upgeo.upgeoId }"
                    (click)="handleUpgeoClick(upgeo)">
              <div class="section-icon depot-icon"><i class="fas fa-map"></i></div>
              <div class="section-info">
                <div class="section-title">UPGEO {{ upgeo.upgeoId }}</div>
                <div class="section-subtitle">{{ upgeo.geoareas.length }} geoareas</div>
              </div>
            </button>

            <!-- GEOAREA Dropdown -->
            <div class="dropdown-content" [ngClass]="{ 'open': openUpgeos[upgeo.upgeoId] }">
              <div *ngFor="let geo of upgeo.geoareas" class="role-item">
                <button class="section-btn role-btn"
                        [ngClass]="{ 'active': selectedGeoId === geo.geoId }"
                        (click)="handleGeoClick(geo)">
                  <div class="section-icon role-icon"><i class="fas fa-map-pin"></i></div>
                  <div class="section-info">
                    <div class="section-title">GEO {{ geo.geoId }}</div>
                    <div class="section-subtitle">{{ geo.itineraries.length }} itineraries</div>
                  </div>
                </button>

                <!-- Itinerary Dropdown -->
                <div class="dropdown-content" [ngClass]="{ 'open': openGeos[geo.geoId] }">
                  <div *ngFor="let itinerary of geo.itineraries" class="tournee-item">
                    <button class="section-btn tournee-btn"
                            [ngClass]="{ 'active': selectedItineraryId === itinerary.itineraryId }"
                            (click)="handleItineraryClick(itinerary)">
                      <div class="section-icon tournee-icon"><i class="fas fa-route"></i></div>
                      <div class="section-info">
                        <div class="section-title">Itinerary {{ itinerary.itineraryId }}</div>
                        <div class="section-subtitle">{{ itinerary.clients.length }} stops</div>
                      </div>
                    </button>

                    <!-- Clients Dropdown -->
                    <div class="dropdown-content" [ngClass]="{ 'open': openItineraries[itinerary.itineraryId] }">
                      <div class="client-scroll">
                        <div *ngFor="let client of itinerary.clients" class="client-item">
                          <button class="section-btn client-btn"
                                  [ngClass]="{ 'active': selectedClientId === client.id }"
                                  (click)="handleClientClick(client)">
                            <div class="section-icon client-icon"><i class="fas fa-map-marker-alt"></i></div>
                            <div class="section-info">
                              <div class="section-title">{{ client.name }}</div>
                              <div class="section-subtitle">{{ client.lat.toFixed(4) }}, {{ client.lon.toFixed(4) }}</div>
                            </div>
                          </button>
                        </div>
                      </div>
                    </div> <!-- /Clients -->
                  </div>
                </div> <!-- /Itineraries -->
              </div>
            </div> <!-- /Geoareas -->
          </div>
        </div>

        <!-- Actions -->
        <button class="action-btn optimize-btn" (click)="optimizeVisit()">
          <i class="fas fa-magic me-2"></i>
          Optimize Route
        </button>
      </div>
    </div>
  `,
  styleUrls: ['./sidebar.component.scss']
})
export class SidebarComponent implements OnInit {
  @Output() onSelect = new EventEmitter<any>();
  @Input() selection: any;
  @Input() isLightMode: boolean = false;

  data: Upgeo[] = [];

  isOpen = false;
  selectedUpgeoId: string | null = null;
  selectedGeoId: string | null = null;
  selectedItineraryId: string | null = null;
  selectedClientId: string | null = null;

  openUpgeos: Record<string, boolean> = {};
  openGeos: Record<string, boolean> = {};
  openItineraries: Record<string, boolean> = {};

  constructor(private http: HttpClient) {}

  ngOnInit() {
    this.loadSidebarData();
  }

  loadSidebarData() {
    this.http.get<Upgeo[]>('http://localhost:8000/sidebar-data')
      .subscribe({
        next: (response) => { this.data = response; },
        error: (err) => { console.error('Failed to load sidebar data:', err); }
      });
  }

  toggleSidebar() { this.isOpen = !this.isOpen; }
  closeSidebar() { this.isOpen = false; }

  toggleUpgeo(upgeoId: string) {
    this.openUpgeos = { ...this.openUpgeos, [upgeoId]: !this.openUpgeos[upgeoId] };
  }
  toggleGeo(geoId: string) {
    this.openGeos = { ...this.openGeos, [geoId]: !this.openGeos[geoId] };
  }
  toggleItinerary(itineraryId: string) {
    this.openItineraries = { ...this.openItineraries, [itineraryId]: !this.openItineraries[itineraryId] };
  }

  handleUpgeoClick(upgeo: Upgeo) {
    this.toggleUpgeo(upgeo.upgeoId);
    this.selectedUpgeoId = upgeo.upgeoId;
    this.selectedGeoId = null;
    this.selectedItineraryId = null;
    this.selectedClientId = null;

    const clients = upgeo.geoareas.flatMap(geo => geo.itineraries.flatMap(i => i.clients));
    this.onSelect.emit({ clients, upgeo });
  }

  handleGeoClick(geo: GeoArea) {
    this.toggleGeo(geo.geoId);
    this.selectedGeoId = geo.geoId;
    this.selectedItineraryId = null;
    this.selectedClientId = null;

    const clients = geo.itineraries.flatMap(i => i.clients);
    this.onSelect.emit({ clients, upgeoId: this.selectedUpgeoId, geo });
  }

  handleItineraryClick(itinerary: Itinerary) {
    this.toggleItinerary(itinerary.itineraryId);
    this.selectedItineraryId = itinerary.itineraryId;
    this.selectedClientId = null;

    this.onSelect.emit({
      clients: itinerary.clients,
      upgeoId: this.selectedUpgeoId,
      geoId: this.selectedGeoId,
      itinerary
    });
  }

  handleClientClick(client: Client) {
    this.selectedClientId = client.id;
    this.onSelect.emit({
      clients: [client],
      upgeoId: this.selectedUpgeoId,
      geoId: this.selectedGeoId,
      itineraryId: this.selectedItineraryId,
      client
    });
  }

  optimizeVisit() {
    const upgeo = this.data.find(u => u.upgeoId === this.selectedUpgeoId);
    if (!upgeo) return;
    const geo = upgeo.geoareas.find(g => g.geoId === this.selectedGeoId);
    if (!geo) return;
    const itinerary = geo.itineraries.find(i => i.itineraryId === this.selectedItineraryId);
    if (!itinerary || itinerary.clients.length === 0) return;

    const depotCoords = { lat: itinerary.clients[0].lat, lon: itinerary.clients[0].lon };

    const payload = {
      lat: depotCoords.lat,
      lon: depotCoords.lon,
      roles: [{ tournees: [{ clients: itinerary.clients }] }]
    };

    fetch('http://localhost:8000/optimize-visit/', {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify(payload)
    })
      .then(res => res.blob())
      .then(blob => {
        const url = window.URL.createObjectURL(blob);
        const link = document.createElement('a');
        link.href = url;
        link.download = 'visit_plan.xlsx';
        document.body.appendChild(link);
        link.click();
        link.remove();
        window.URL.revokeObjectURL(url);
      })
      .catch(error => console.error('Error optimizing visit:', error));
  }
}
