import { Component, inject } from '@angular/core';
import { isPlatformBrowser, NgClass } from '@angular/common';
import { PLATFORM_ID } from '@angular/core';
import { SidebarComponent } from './components/sidebar/sidebar.component';
import { MapViewComponent } from './components/map-view/map-view.component';




@Component({
  selector: 'app-root',
  standalone: true,
  imports: [SidebarComponent, MapViewComponent, NgClass],
  template: `
    <div class="app-container" [ngClass]="{'light-mode': isLightMode}">
      <button 
        class="theme-toggle" 
        (click)="toggleTheme()"
        [title]="isLightMode ? 'Switch to Dark Mode' : 'Switch to Light Mode'">
        <i class="fas" [ngClass]="isLightMode ? 'fa-moon' : 'fa-sun'"></i>
      </button>
      <app-sidebar 
        (onSelect)="onSelectionChange($event)"
        [isLightMode]="isLightMode">
      </app-sidebar>
      <app-map-view 
        [selection]="selection"
        [isLightMode]="isLightMode">
      </app-map-view>
    </div>
  `,
  styleUrls: ['./app.scss']
})
export class AppComponent {
  selection: any = null;
  isLightMode: boolean = false;

  private platformId = inject(PLATFORM_ID);

  constructor() {
    if (isPlatformBrowser(this.platformId)) {
      const savedTheme = localStorage.getItem('theme-preference');
      this.isLightMode = savedTheme === 'light';
      this.updateBodyClass();
    }
  }

  toggleTheme() {
    this.isLightMode = !this.isLightMode;

    if (isPlatformBrowser(this.platformId)) {
      localStorage.setItem('theme-preference', this.isLightMode ? 'light' : 'dark');
      this.updateBodyClass();

      const button = document.querySelector('.theme-toggle');
      if (button) {
        button.classList.add('clicked');
        setTimeout(() => button.classList.remove('clicked'), 200);
      }
    }

    console.log('Theme changed to:', this.isLightMode ? 'Light' : 'Dark');
  }

  onSelectionChange(selection: any) {
    this.selection = selection;
    console.log('Selection changed:', selection);
  }

  private updateBodyClass() {
    if (isPlatformBrowser(this.platformId)) {
      document.body.classList.toggle('light-mode', this.isLightMode);
      document.body.classList.toggle('dark-mode', !this.isLightMode);
    }
  }
}