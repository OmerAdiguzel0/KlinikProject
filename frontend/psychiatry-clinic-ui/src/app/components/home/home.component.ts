import { Component, OnInit, HostListener } from '@angular/core';
import { ScrollService } from '../../shared/services/scroll.service';

@Component({
  selector: 'app-home',
  templateUrl: './home.component.html',
  styleUrls: ['./home.component.scss']
})
export class HomeComponent implements OnInit {
  activeSection: string = 'hero';

  constructor(private scrollService: ScrollService) {}

  ngOnInit() {
    this.checkActiveSection();
  }

  @HostListener('window:scroll', ['$event'])
  onScroll() {
    this.checkActiveSection();
  }

  private checkActiveSection() {
    const sections = ['hero', 'about', 'contact'];
    const navbarHeight = 80;

    for (const section of sections) {
      const element = document.getElementById(section);
      if (element) {
        const rect = element.getBoundingClientRect();
        if (rect.top <= navbarHeight + 50 && rect.bottom > navbarHeight) {
          this.activeSection = section;
          break;
        }
      }
    }
  }
}
