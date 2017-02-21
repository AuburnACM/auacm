/* tslint:disable:no-unused-variable */
import { async, ComponentFixture, TestBed } from '@angular/core/testing';
import { By } from '@angular/platform-browser';
import { DebugElement } from '@angular/core';

import { EditProblemComponent } from './edit-problem.component';

describe('EditProblemComponent', () => {
  let component: EditProblemComponent;
  let fixture: ComponentFixture<EditProblemComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ EditProblemComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(EditProblemComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
