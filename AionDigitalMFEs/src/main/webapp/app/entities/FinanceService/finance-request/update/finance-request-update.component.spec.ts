import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HttpResponse } from '@angular/common/http';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { FormBuilder } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';
import { of, Subject, from } from 'rxjs';

import { FinanceRequestFormService } from './finance-request-form.service';
import { FinanceRequestService } from '../service/finance-request.service';
import { IFinanceRequest } from '../finance-request.model';

import { FinanceRequestUpdateComponent } from './finance-request-update.component';

describe('FinanceRequest Management Update Component', () => {
  let comp: FinanceRequestUpdateComponent;
  let fixture: ComponentFixture<FinanceRequestUpdateComponent>;
  let activatedRoute: ActivatedRoute;
  let financeRequestFormService: FinanceRequestFormService;
  let financeRequestService: FinanceRequestService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule, RouterTestingModule.withRoutes([])],
      declarations: [FinanceRequestUpdateComponent],
      providers: [
        FormBuilder,
        {
          provide: ActivatedRoute,
          useValue: {
            params: from([{}]),
          },
        },
      ],
    })
      .overrideTemplate(FinanceRequestUpdateComponent, '')
      .compileComponents();

    fixture = TestBed.createComponent(FinanceRequestUpdateComponent);
    activatedRoute = TestBed.inject(ActivatedRoute);
    financeRequestFormService = TestBed.inject(FinanceRequestFormService);
    financeRequestService = TestBed.inject(FinanceRequestService);

    comp = fixture.componentInstance;
  });

  describe('ngOnInit', () => {
    it('Should update editForm', () => {
      const financeRequest: IFinanceRequest = { id: 'CBA' };

      activatedRoute.data = of({ financeRequest });
      comp.ngOnInit();

      expect(comp.financeRequest).toEqual(financeRequest);
    });
  });

  describe('save', () => {
    it('Should call update service on save for existing entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<IFinanceRequest>>();
      const financeRequest = { id: 'ABC' };
      jest.spyOn(financeRequestFormService, 'getFinanceRequest').mockReturnValue(financeRequest);
      jest.spyOn(financeRequestService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ financeRequest });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: financeRequest }));
      saveSubject.complete();

      // THEN
      expect(financeRequestFormService.getFinanceRequest).toHaveBeenCalled();
      expect(comp.previousState).toHaveBeenCalled();
      expect(financeRequestService.update).toHaveBeenCalledWith(expect.objectContaining(financeRequest));
      expect(comp.isSaving).toEqual(false);
    });

    it('Should call create service on save for new entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<IFinanceRequest>>();
      const financeRequest = { id: 'ABC' };
      jest.spyOn(financeRequestFormService, 'getFinanceRequest').mockReturnValue({ id: null });
      jest.spyOn(financeRequestService, 'create').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ financeRequest: null });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: financeRequest }));
      saveSubject.complete();

      // THEN
      expect(financeRequestFormService.getFinanceRequest).toHaveBeenCalled();
      expect(financeRequestService.create).toHaveBeenCalled();
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).toHaveBeenCalled();
    });

    it('Should set isSaving to false on error', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<IFinanceRequest>>();
      const financeRequest = { id: 'ABC' };
      jest.spyOn(financeRequestService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ financeRequest });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.error('This is an error!');

      // THEN
      expect(financeRequestService.update).toHaveBeenCalled();
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).not.toHaveBeenCalled();
    });
  });
});
