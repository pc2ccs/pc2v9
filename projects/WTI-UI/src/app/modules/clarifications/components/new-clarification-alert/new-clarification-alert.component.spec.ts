import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { NewClarificationAlertComponent } from './new-clarification-alert.component';

describe('NewClarificationAlertComponent', () => {
  let component: NewClarificationAlertComponent;
  let fixture: ComponentFixture<NewClarificationAlertComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ NewClarificationAlertComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(NewClarificationAlertComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
