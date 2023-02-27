import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute } from '@angular/router';
import { of } from 'rxjs';

import { FinanceRequestDetailComponent } from './finance-request-detail.component';

describe('FinanceRequest Management Detail Component', () => {
  let comp: FinanceRequestDetailComponent;
  let fixture: ComponentFixture<FinanceRequestDetailComponent>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      declarations: [FinanceRequestDetailComponent],
      providers: [
        {
          provide: ActivatedRoute,
          useValue: { data: of({ financeRequest: { id: 'ABC' } }) },
        },
      ],
    })
      .overrideTemplate(FinanceRequestDetailComponent, '')
      .compileComponents();
    fixture = TestBed.createComponent(FinanceRequestDetailComponent);
    comp = fixture.componentInstance;
  });

  describe('OnInit', () => {
    it('Should load financeRequest on init', () => {
      // WHEN
      comp.ngOnInit();

      // THEN
      expect(comp.financeRequest).toEqual(expect.objectContaining({ id: 'ABC' }));
    });
  });
});
