import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { NewClarificationComponent } from './new-clarification.component';

describe('NewClarificationComponent', () => {
  let component: NewClarificationComponent;
  let fixture: ComponentFixture<NewClarificationComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ NewClarificationComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(NewClarificationComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
