import { beforeEach, describe, expect, it, vi } from 'vitest';
import { provideHttpClientTesting } from '@angular/common/http/testing';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute } from '@angular/router';

import { provideTranslateService } from '@ngx-translate/core';
import { Subject, from, of } from 'rxjs';

import { ConfirmService } from 'app/shared/confirm';

import { IEtablissementSante } from '../etablissement-sante.model';
import { EtablissementSanteService } from '../service/etablissement-sante.service';

import { EtablissementSanteFormService } from './etablissement-sante-form.service';
import { EtablissementSanteUpdate } from './etablissement-sante-update';

describe('EtablissementSante Management Update Component', () => {
  let comp: EtablissementSanteUpdate;
  let fixture: ComponentFixture<EtablissementSanteUpdate>;
  let activatedRoute: ActivatedRoute;
  let etablissementSanteFormService: EtablissementSanteFormService;
  let etablissementSanteService: EtablissementSanteService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [
        // La confirmation est une exigence d'interface : ici elle répond « oui » d'office,
        // pour que le spec teste l'enregistrement et non le dialogue.
        { provide: ConfirmService, useValue: { confirmer: () => of(undefined), confirmerEnregistrement: () => of(undefined) } },
        provideTranslateService(),
        provideHttpClientTesting(),
        {
          provide: ActivatedRoute,
          useValue: {
            params: from([{}]),
          },
        },
      ],
    });

    fixture = TestBed.createComponent(EtablissementSanteUpdate);
    activatedRoute = TestBed.inject(ActivatedRoute);
    etablissementSanteFormService = TestBed.inject(EtablissementSanteFormService);
    etablissementSanteService = TestBed.inject(EtablissementSanteService);

    comp = fixture.componentInstance;
  });

  describe('ngOnInit', () => {
    it('should update editForm', () => {
      const etablissementSante: IEtablissementSante = { id: 20730 };

      activatedRoute.data = of({ etablissementSante });
      comp.ngOnInit();

      expect(comp.etablissementSante).toEqual(etablissementSante);
    });
  });

  describe('save', () => {
    it('should call update service on save for existing entity', () => {
      // GIVEN
      const saveSubject = new Subject<IEtablissementSante>();
      const etablissementSante = { id: 2850 };
      vi.spyOn(etablissementSanteFormService, 'getEtablissementSante').mockReturnValue(etablissementSante);
      vi.spyOn(etablissementSanteService, 'update').mockReturnValue(saveSubject);
      vi.spyOn(comp, 'previousState');
      activatedRoute.data = of({ etablissementSante });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving()).toEqual(true);
      saveSubject.next(etablissementSante);
      saveSubject.complete();

      // THEN
      expect(etablissementSanteFormService.getEtablissementSante).toHaveBeenCalled();
      expect(comp.previousState).toHaveBeenCalled();
      expect(etablissementSanteService.update).toHaveBeenCalledWith(expect.objectContaining(etablissementSante));
      expect(comp.isSaving()).toEqual(false);
    });

    it('should call create service on save for new entity', () => {
      // GIVEN
      const saveSubject = new Subject<IEtablissementSante>();
      const etablissementSante = { id: 2850 };
      vi.spyOn(etablissementSanteFormService, 'getEtablissementSante').mockReturnValue({ id: null });
      vi.spyOn(etablissementSanteService, 'create').mockReturnValue(saveSubject);
      vi.spyOn(comp, 'previousState');
      activatedRoute.data = of({ etablissementSante: null });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving()).toEqual(true);
      saveSubject.next(etablissementSante);
      saveSubject.complete();

      // THEN
      expect(etablissementSanteFormService.getEtablissementSante).toHaveBeenCalled();
      expect(etablissementSanteService.create).toHaveBeenCalled();
      expect(comp.isSaving()).toEqual(false);
      expect(comp.previousState).toHaveBeenCalled();
    });

    it('should set isSaving to false on error', () => {
      // GIVEN
      const saveSubject = new Subject<IEtablissementSante>();
      const etablissementSante = { id: 2850 };
      vi.spyOn(etablissementSanteService, 'update').mockReturnValue(saveSubject);
      vi.spyOn(comp, 'previousState');
      activatedRoute.data = of({ etablissementSante });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving()).toEqual(true);
      saveSubject.error('This is an error!');

      // THEN
      expect(etablissementSanteService.update).toHaveBeenCalled();
      expect(comp.isSaving()).toEqual(false);
      expect(comp.previousState).not.toHaveBeenCalled();
    });
  });
});
