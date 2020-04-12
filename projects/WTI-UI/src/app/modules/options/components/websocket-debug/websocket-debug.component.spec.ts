import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { WebsocketDebugComponent } from './websocket-debug.component';

describe('WebsocketDebugComponent', () => {
  let component: WebsocketDebugComponent;
  let fixture: ComponentFixture<WebsocketDebugComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ WebsocketDebugComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(WebsocketDebugComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
