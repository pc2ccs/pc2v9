import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { NewAnnouncementAlertComponent } from './new-announcement-alert.component';

describe('NewClarificationAlertComponent', () => {
  let component: NewAnnouncementAlertComponent;
  let fixture: ComponentFixture<NewAnnouncementAlertComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ NewAnnouncementAlertComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(NewAnnouncementAlertComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
