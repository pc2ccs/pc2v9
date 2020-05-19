import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { NewRunAlertComponent } from './new-run-alert.component';

describe('NewRunAlertComponent', () => {
  let component: NewRunAlertComponent;
  let fixture: ComponentFixture<NewRunAlertComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ NewRunAlertComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(NewRunAlertComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
