import { AfterViewInit, Component, ElementRef, OnInit, inject, signal, viewChild } from '@angular/core';
import { FormControl, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { Router } from '@angular/router';

import { AccountService } from 'app/core/auth';
import { LoginService } from 'app/login/login.service';

@Component({
  selector: 'jhi-login',
  // L'ecran de connexion est en francais administratif, sans bascule de langue : l'agent n'a
  // pas encore de preference enregistree a ce stade.
  imports: [ReactiveFormsModule],
  templateUrl: './login.html',
})
export default class Login implements OnInit, AfterViewInit {
  readonly username = viewChild.required<ElementRef>('username');

  readonly authenticationError = signal(false);

  /** Un mot de passe saisi sur un poste partagé doit pouvoir être relu avant d'être envoyé. */
  readonly motDePasseVisible = signal(false);

  loginForm = new FormGroup({
    username: new FormControl('', { nonNullable: true, validators: [Validators.required] }),
    password: new FormControl('', { nonNullable: true, validators: [Validators.required] }),
    rememberMe: new FormControl(false, { nonNullable: true, validators: [Validators.required] }),
  });

  private readonly accountService = inject(AccountService);
  private readonly loginService = inject(LoginService);
  private readonly router = inject(Router);

  ngOnInit(): void {
    // if already authenticated then navigate to home page
    this.accountService.identity().subscribe(() => {
      if (this.accountService.isAuthenticated()) {
        this.router.navigate(['']);
      }
    });
  }

  ngAfterViewInit(): void {
    this.username().nativeElement.focus();
  }

  basculerMotDePasse(): void {
    this.motDePasseVisible.update(visible => !visible);
  }

  login(): void {
    this.loginService.login(this.loginForm.getRawValue()).subscribe({
      next: () => {
        this.authenticationError.set(false);
        if (!this.router.currentNavigation()) {
          // There were no routing during login (eg from navigationToStoredUrl)
          this.router.navigate(['']);
        }
      },
      error: () => this.authenticationError.set(true),
    });
  }
}
