import { Component, OnInit } from '@angular/core';
import { HttpClient } from '@angular/common/http';

interface PdfDocument {
  id: number;
  name: string;
  category: string;
  type: string;
}

interface PaginatedResponse {
  content: PdfDocument[];
  totalElements: number;
  totalPages: number;
  number: number;
  size: number;
}

@Component({
  selector: 'app-library',
  templateUrl: './library.component.html',
  styleUrls: ['./library.component.css']
})
export class LibraryComponent implements OnInit {
  pdfs: PdfDocument[] = [];
  searchTerm: string = '';
  isLoading: boolean = false;
  hasSearched: boolean = false;
  selectedCategory: string = '';
  selectedType: string = '';
  currentPage: number = 0;
  pageSize: number = 15;
  totalPages: number = 0;
  totalElements: number = 0;
  showAllResults: boolean = false;

  categories = [
    'CHEMICAL_ENGINEERING_TECHNOLOGY',
    'CIVIL_ENGINEERING_TECHNOLOGY',
    'ELECTRICAL_ENGINEERING_TECHNOLOGY',
    'ELECTRONIC_AND_TELECOMMUNICATION_ENGINEERING_TECHNOLOGY',
    'INFORMATION_TECHNOLOGY',
    'MECHANICAL_ENGINEERING_TECHNOLOGY',
    'MARINE_TECHNOLOGY',
    'NAUTICAL_STUDIES',
    'POLYMER_TECHNOLOGY',
    'TEXTILE_AND_CLOTHING_TECHNOLOGY'
  ];

  types = ['PAST_PAPERS', 'SHORT_NOTES'];

  constructor(private http: HttpClient) {}

  ngOnInit() {
    this.loadPaginatedPdfs();
  }

  loadPaginatedPdfs() {
    this.isLoading = true;
    this.showAllResults = false;
    this.http.get<PaginatedResponse>(`http://localhost:8080/api/pdf/paginated?page=${this.currentPage}&size=${this.pageSize}`).subscribe(
      data => {
        this.pdfs = data.content;
        this.totalPages = data.totalPages;
        this.totalElements = data.totalElements;
        this.isLoading = false;
      },
      error => {
        console.error('Error loading PDFs', error);
        this.isLoading = false;
      }
    );
  }

  loadAllPdfs() {
    this.isLoading = true;
    this.showAllResults = true;
    this.http.get<PdfDocument[]>('http://localhost:8080/api/pdf/all').subscribe(
      data => {
        this.pdfs = data;
        this.isLoading = false;
      },
      error => {
        console.error('Error loading all PDFs', error);
        this.isLoading = false;
      }
    );
  }

  onSearch() {
    this.isLoading = true;
    this.hasSearched = true;
    this.showAllResults = false;
    const lowercaseQuery = this.searchTerm.toLowerCase();
    let url = `http://localhost:8080/api/pdf/search?query=${encodeURIComponent(lowercaseQuery)}&page=${this.currentPage}&size=${this.pageSize}`;
    
    if (this.selectedCategory) {
      url += `&category=${encodeURIComponent(this.selectedCategory)}`;
    }
    if (this.selectedType) {
      url += `&type=${encodeURIComponent(this.selectedType)}`;
    }

    this.http.get<PaginatedResponse>(url).subscribe(
      data => {
        this.pdfs = data.content;
        this.totalPages = data.totalPages;
        this.totalElements = data.totalElements;
        this.isLoading = false;
      },
      error => {
        console.error('Error searching PDFs', error);
        this.isLoading = false;
      }
    );
  }

  onFilter() {
    this.searchTerm = '';
    this.currentPage = 0;
    this.onSearch();
  }

  clearSearch() {
    this.selectedCategory = '';
    this.selectedType = '';
    this.searchTerm = '';
    this.currentPage = 0;
    this.hasSearched = false;
    this.loadPaginatedPdfs();
  }

  downloadPdf(id: number, name: string) {
    this.http.get(`http://localhost:8080/api/pdf/download/${id}`, { responseType: 'blob' })
      .subscribe(
        (data: Blob) => {
          const blob = new Blob([data], { type: 'application/pdf' });
          const url = window.URL.createObjectURL(blob);
          const link = document.createElement('a');
          link.href = url;
          link.download = name;
          link.click();
          window.URL.revokeObjectURL(url);
        },
        error => console.error('Error downloading PDF', error)
      );
  }

  nextPage() {
    if (this.currentPage < this.totalPages - 1) {
      this.currentPage++;
      this.hasSearched ? this.onSearch() : this.loadPaginatedPdfs();
    }
  }

  prevPage() {
    if (this.currentPage > 0) {
      this.currentPage--;
      this.hasSearched ? this.onSearch() : this.loadPaginatedPdfs();
    }
  }

  toggleShowAllResults() {
    if (this.showAllResults) {
      this.loadPaginatedPdfs();
    } else {
      this.loadAllPdfs();
    }
  }
}