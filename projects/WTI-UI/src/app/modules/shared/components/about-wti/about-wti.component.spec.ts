import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { AboutWtiComponent } from './about-wti.component';

describe('AboutWtiComponent', () => {
  let component: AboutWtiComponent;
  let fixture: ComponentFixture<AboutWtiComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ AboutWtiComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(AboutWtiComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
