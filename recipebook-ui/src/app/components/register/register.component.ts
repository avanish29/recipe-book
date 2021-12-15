import { HttpErrorResponse } from '@angular/common/http';
import { Component, OnInit } from '@angular/core';
import { AbstractControl, FormBuilder, FormGroup, Validators } from '@angular/forms';
import { Router } from '@angular/router';
import { AuthService } from 'src/app/services/auth.service';
import Validation from 'src/app/utils/validation';

@Component({
  selector: 'app-register',
  templateUrl: './register.component.html',
  styleUrls: ['./register.component.css']
})
export class RegisterComponent implements OnInit {
  registrationForm: FormGroup;
  loading: boolean = false;
  errorMsg: string = "";

  constructor(private formBuilder: FormBuilder, private authService: AuthService, private router: Router) { 
    this.registrationForm = this.formBuilder.group({
      'firstName': ['', Validators.required],
      'lastName': ['', Validators.required],
      'email': ['', [Validators.required, Validators.email]],
      'password': ['', [
        Validators.required,
        Validators.minLength(6),
        Validators.maxLength(40)
      ]],
      'confirmPassword': ['', Validators.required],
    },
    {
      validators: [Validation.match('password', 'confirmPassword')]
    });
  }

  ngOnInit(): void {
  }

  public checkError = (controlName: string, errorName: string) => {
    return this.registrationForm.controls[controlName].hasError(errorName);
  }

  onSubmit(): void {
    if(this.registrationForm.valid) {
      this.loading = true;
      this.errorMsg = "";
      this.authService.register(this.registrationForm.controls["firstName"].value, 
      this.registrationForm.controls["lastName"].value,
      this.registrationForm.controls["email"].value,
      this.registrationForm.controls["password"].value).subscribe({
        next: (data) => {
          console.log(data);
          this.router.navigateByUrl('/');
        },
        error: (errorResp: HttpErrorResponse) => {
          this.errorMsg = errorResp.error.message;
            this.loading = false;
        }
      });
    }
  }

}
