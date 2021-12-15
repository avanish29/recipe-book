import { Component, OnInit } from '@angular/core';
import { Router } from '@angular/router';
import { HttpErrorResponse } from '@angular/common/http';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { TokenStorageService } from '../../services/token-storage.service';
import { AuthService } from '../../services/auth.service';

@Component({
  selector: 'app-log-in',
  templateUrl: './log-in.component.html',
  styleUrls: ['./log-in.component.css']
})
export class LogInComponent implements OnInit {
  loginForm: FormGroup;
  loading: boolean = false;
  errorMsg: string = "";

  constructor(private formBuilder: FormBuilder, private authService: AuthService, 
    private tokenStorage: TokenStorageService, private router: Router) {
    this.loginForm = this.formBuilder.group({
      'username': ['', [Validators.required, Validators.email]],
      'password': ['', Validators.required],
    });
  }

  ngOnInit(): void {
    if (this.tokenStorage.getToken()) {
      this.router.navigateByUrl('/');
    }
  }

  onSubmit() {
    if(this.loginForm.valid) {
      this.loading = true;
      this.errorMsg = "";
      this.authService.login(this.loginForm.controls["username"].value, this.loginForm.controls["password"].value)
        .subscribe({
          next: (data) => {
            this.tokenStorage.saveToken(data.accessToken);
            this.tokenStorage.saveRefreshToken(data.refreshToken);
            this.tokenStorage.saveUser(data.userInfo);
            this.router.navigateByUrl('/home');
          },
          error: (errorResp: HttpErrorResponse) => {
            this.errorMsg = errorResp.error.message;
            this.loading = false;
          }
        });
    }
  }

  reloadPage(): void {
    window.location.reload();
  }

}
