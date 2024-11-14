import { Component, ViewChild, ElementRef } from '@angular/core';
import { HttpClient, HttpEventType, HttpResponse } from '@angular/common/http';
import { NgForm } from '@angular/forms';

@Component({
  selector: 'app-pdf-upload',
  templateUrl: './pdf-upload.component.html',
  styleUrls: ['./pdf-upload.component.css']
})
export class PdfUploadComponent {
  @ViewChild('fileInput') fileInput!: ElementRef;
  @ViewChild('uploadForm') uploadForm!: NgForm;

  selectedFile: File | null = null;
  uploadStatus: string = '';
  uploadProgress: number = 0;
  selectedCategory: string = '';
  selectedType: string = '';

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

  onFileSelected(event: any) {
    this.selectedFile = event.target.files[0];
  }

  onUpload() {
    if (this.selectedFile && this.selectedCategory && this.selectedType) {
      const formData = new FormData();
      formData.append('file', this.selectedFile);
      formData.append('category', this.selectedCategory);
      formData.append('type', this.selectedType);
      
      this.http.post('http://localhost:8080/api/pdf/upload', formData, {
        reportProgress: true,
        observe: 'events',
        responseType: 'text'
      }).subscribe(
        event => {
          if (event.type === HttpEventType.UploadProgress) {
            this.uploadProgress = Math.round(100 * event.loaded / (event.total || 1));
          } else if (event instanceof HttpResponse) {
            console.log('Upload successful');
            this.uploadStatus = event.body || 'Upload successful';
            this.uploadProgress = 0;
            this.clearForm();
          }
        },
        error => {
          console.error('Upload failed', error);
          this.uploadStatus = 'Upload failed: ' + (error.error || 'Unknown error');
          this.uploadProgress = 0;
          this.clearForm(); // Clear form even when upload fails
        }
      );
    } else {
      this.uploadStatus = 'Please select a file, category, and type before uploading.';
    }
  }

  clearForm() {
    this.selectedFile = null;
    this.selectedCategory = '';
    this.selectedType = '';
    this.fileInput.nativeElement.value = '';
    this.uploadForm.resetForm();
  }
}