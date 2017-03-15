import { Component, ElementRef, OnInit, ViewChild } from '@angular/core';
import { Router } from '@angular/router';

import { ProfileService } from '../profile.service';
import { UserService } from '../user.service';

import { PictureSelection, Circle } from '../models/profile-picture';
import { UserData } from '../models/user';


const UPLOAD_STAGE_SELECT_FILE = 0;
const UPLOAD_STAGE_DRAW_SELECTION = 1;
const UPLOAD_STAGE_CONFIRM_SELECTION = 2;
const UPLOAD_STAGE_FINISHED = 3;
const UPLOAD_STAGE_ERROR = 4;

const SELECTION_RADIUS_MIN = 64;

const MAX_SELECTED_IMG_WIDTH: number = document.documentElement.clientWidth * 0.65;
const MAX_SELECTED_IMG_HEIGHT: number = document.documentElement.clientHeight * 0.85;

@Component({
  selector: 'app-edit-profile-picture',
  templateUrl: './edit-profile-picture.component.html',
  styleUrls: ['./edit-profile-picture.component.css']
})
export class EditProfilePictureComponent implements OnInit {

  @ViewChild('imageSelectionCanvas') canvasRef: ElementRef;
  @ViewChild('confirmSelectionImg') imgRef: ElementRef;

  UPLOAD_STAGE_SELECT_FILE = UPLOAD_STAGE_SELECT_FILE;
  UPLOAD_STAGE_START = UPLOAD_STAGE_SELECT_FILE;
  UPLOAD_STAGE_DRAW_SELECTION = UPLOAD_STAGE_DRAW_SELECTION;
  UPLOAD_STAGE_CONFIRM_SELECTION = UPLOAD_STAGE_CONFIRM_SELECTION;
  UPLOAD_STAGE_FINISHED = UPLOAD_STAGE_FINISHED;
  UPLOAD_STAGE_ERROR = UPLOAD_STAGE_ERROR;

  private MAX_SELECTED_IMG_WIDTH: number;
  private MAX_SELECTED_IMG_HEIGHT: number;

  private ctx: CanvasRenderingContext2D;
  private canvas: HTMLCanvasElement;
  private previewImg: HTMLImageElement;

  private baseImageReady = false;
  private baseImage: HTMLImageElement = new Image();
  public uploadStage = this.UPLOAD_STAGE_START;

  private selection: PictureSelection = new PictureSelection();

  private user: UserData;

  constructor(private _userService: UserService,
              private _profileService: ProfileService,
              private _router: Router) {

    this._userService.userData$.subscribe(data => {
      this.user = data;
      if (!this.user.loggedIn) {
        this._router.navigate(['/']);
      }
    });
  }

  ngOnInit() {
    this.user = this._userService.getUserData();
    this.ctx = this.canvasRef.nativeElement.getContext('2d');
    this.selection.selection = new Circle(300, 700, SELECTION_RADIUS_MIN);
    this.selection.setCanvasRef(this.canvasRef);
    this.uploadStage = this.UPLOAD_STAGE_START;
    this.previewImg = <HTMLImageElement> this.imgRef.nativeElement;
  }

  // Tie - Ins that allow the view to change upload stages.
  returnToFileSelect() {
    this.uploadStage = UPLOAD_STAGE_SELECT_FILE;
  }

  returnToSelectRegion() {
    this.uploadStage = UPLOAD_STAGE_DRAW_SELECTION;
  }

  confirmSelection() {
    const data = this.previewImg.src.replace(/^data:image\/(png|jpg);base64,/, '');
    this._profileService.uploadUserPicture(data, this.user.username).then( response => {
      if (response.success) {
        this.uploadStage = UPLOAD_STAGE_FINISHED;
        this._router.navigate(['/profile']);
      }
    });
  }

  clipSelection() {
    this.selection.clipSelection(this.previewImg);
    this.uploadStage = UPLOAD_STAGE_CONFIRM_SELECTION;
  }

  loadImage(input) {
    this.selection.loadImage(input.srcElement);
    this.uploadStage = UPLOAD_STAGE_DRAW_SELECTION;
  }

}
