import { Component, ElementRef, OnInit, ViewChild } from '@angular/core';
import { Router } from '@angular/router';

import { ProfileService } from '../profile.service';
import { UserService } from '../user.service';

import { PictureSelection, Circle } from '../models/profile-picture';
import { UserData } from '../models/user';

enum UploadStage {
  SelectFile = 0,
  DrawSelection,
  ConfirmSelection,
  Finished,
  Error
}

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

  public UploadStage = UploadStage;

  private MAX_SELECTED_IMG_WIDTH: number;
  private MAX_SELECTED_IMG_HEIGHT: number;

  private ctx: CanvasRenderingContext2D;
  private canvas: HTMLCanvasElement;
  private previewImg: HTMLImageElement;

  private baseImageReady = false;
  private baseImage: HTMLImageElement = new Image();
  public currentStage: UploadStage = UploadStage.SelectFile;

  private selection: PictureSelection;

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
    this.selection = new PictureSelection(this.canvasRef);
    this.currentStage = UploadStage.SelectFile;
    this.previewImg = <HTMLImageElement> this.imgRef.nativeElement;
  }

  // Tie - Ins that allow the view to change upload stages.
  returnToFileSelect() {
    this.currentStage = UploadStage.SelectFile;
  }

  returnToSelectRegion() {
    this.currentStage = UploadStage.DrawSelection;
  }

  confirmSelection() {
    const data = this.previewImg.src.replace(/^data:image\/(png|jpg);base64,/, '');
    this._profileService.uploadUserPicture(data, this.user.username).then( response => {
      if (response.success) {
        this.currentStage = UploadStage.Finished;
        this._router.navigate(['/profile']);
      }
    });
  }

  clipSelection() {
    this.selection.clipSelection(this.previewImg);
    this.currentStage = UploadStage.ConfirmSelection;
  }

  loadImage(input) {
    this.selection.loadImage(input.srcElement);
    this.currentStage = UploadStage.DrawSelection;
  }

}
