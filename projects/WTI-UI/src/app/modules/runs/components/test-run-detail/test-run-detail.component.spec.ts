import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { TestRunDetailComponent } from './test-run-detail.component';

describe('TestRunDetailComponent', () => {
  let component: TestRunDetailComponent;
  let fixture: ComponentFixture<TestRunDetailComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ TestRunDetailComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(TestRunDetailComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
