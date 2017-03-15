import { ElementRef } from '@angular/core';

const SELECTION_RADIUS_MIN = 64;
const PROFILE_PIC_DIMENSION = 256;

const MAX_SELECTED_IMG_WIDTH: number = document.documentElement.clientWidth * 0.65;
const MAX_SELECTED_IMG_HEIGHT: number = document.documentElement.clientHeight * 0.85;

let selectionInstance: PictureSelection;

class MousePos {
  public x: number;
  public y: number;

  constructor(x: number, y: number) {
    this.x = x;
    this.y = y;
  }

  public copy(): MousePos {
    return new MousePos(this.x, this.y);
  }
}

export class Circle {
  public x: number;
  public y: number;
  public r: number;

  constructor(x: number, y: number, r: number) {
    this.x = x;
    this.y = y;
    this.r = r;
  }

  public copy(): Circle {
    return new Circle(this.x, this.y, this.r);
  }

  public getImageData(ctx: CanvasRenderingContext2D): ImageData {
    return ctx.getImageData(this.x - this.r, this.y - this.r, 2 * this.r, 2 * this.r);
  }
}

export class PictureSelection {
  public selection: Circle;
  private previousSelection: Circle;
  private startPos: MousePos;

  private active: boolean;
  private resize: boolean;

  private canvasRef: HTMLCanvasElement;
  private ctx: CanvasRenderingContext2D;

  private baseImageReady: boolean;
  private baseImage: HTMLImageElement = new Image();

  constructor() {
    this.selection = new Circle(0, 0, 0);
    this.previousSelection = new Circle(0, 0, 0);
    this.startPos = new MousePos(0, 0);
    this.active = false;
    this.resize = false;
    this.baseImageReady = false;
    const selection: PictureSelection = this;

    this.baseImage.onload = function() {
      selection.baseImageReady = true;
      const widthRatio: number = MAX_SELECTED_IMG_WIDTH / selection.baseImage.width;
      const heightRatio: number = MAX_SELECTED_IMG_HEIGHT / selection.baseImage.height;
      const scaleFactor = Math.min(1, Math.min(widthRatio, heightRatio));
      selection.canvasRef.width = selection.baseImage.width * scaleFactor;
      selection.canvasRef.height = selection.baseImage.height * scaleFactor;
      selection.selection = new Circle(selection.canvasRef.width / 2, selection.canvasRef.height / 2,
          Math.min(selection.canvasRef.width, selection.canvasRef.height) / 4);
      selection.drawSelection(selection.canvasRef);
    };
    selectionInstance = this;
  }

  public setCanvasRef(canvasRef: ElementRef) {
    this.ctx = canvasRef.nativeElement.getContext('2d');
    this.canvasRef = (<HTMLCanvasElement> canvasRef.nativeElement);
    const selection: PictureSelection = this;
    this.canvasRef.addEventListener('mousedown', selection.startSelectionHelper, false);
    this.canvasRef.addEventListener('mouseup', selection.endSelectionHelper, false);
    this.canvasRef.addEventListener('mousemove', selection.mutateSelectionHelper, false);
    this.canvasRef.addEventListener('mouseout', selection.endSelectionHelper, false);
  }

  public loadImage(input) {
    if (input.files && input.files[0]) {
      const reader: FileReader = new FileReader();
      const selection: PictureSelection = this;

      reader.onload = function(e: any) {
        selection.baseImage.src = e.target.result;
      };
      reader.readAsDataURL(input.files[0]);
      input.value = null;
    }
  }

  public drawSelection(canvasRef: HTMLCanvasElement) {
    this.drawImage(canvasRef);
    const ctx: CanvasRenderingContext2D = canvasRef.getContext('2d');
    ctx.beginPath();
    ctx.arc(this.selection.x, this.selection.y, this.selection.r, 0, 2 * Math.PI);
    ctx.lineWidth = 2;
    ctx.strokeStyle = 'rgba(50,50,50,0.7)';
    ctx.fillStyle = 'rgba(210,210,210,0.5)';
    ctx.fill();
    ctx.stroke();
  }

  private getMousePos(canvas, evt): MousePos {
    const rect = canvas.getBoundingClientRect();
    return new MousePos(evt.clientX - rect.left, evt.clientY - rect.top);
  }

  private startSelectionHelper(e) {
    selectionInstance.startSelection(e);
  }

  private startSelection(e) {
    const mousePos: MousePos = this.getMousePos(this.canvasRef, e);
    this.startPos = mousePos.copy();
    const dx: number = this.startPos.x - this.selection.x;
    const dy: number = this.startPos.y - this.selection.y;
    this.previousSelection = this.selection.copy();

    // If we are inside the circle.
    if (dx * dx + dy * dy <= this.selection.r * this.selection.r) {
      this.active = true;
      this.resize = false;
    } else {
      this.active = true;
      this.resize = true;
    }
  }

  private endSelectionHelper(e) {
    selectionInstance.endSelection(e);
  }

  private endSelection(e) {
    selectionInstance.resize = false;
    selectionInstance.active = false;
  }

  private mutateSelectionHelper(e) {
    selectionInstance.mutateSelection(e);
  }

  private resizeSelection(e) {
    this.selection.r = this.sanitizeNewRadius(this.getNewRadius(e));
    this.drawSelection(this.canvasRef);
  }

  private mutateSelection(e) {
    if (this.active && !this.resize) {
      this.transposeSelection(e);
    } else if (this.active && this.resize) {
      this.resizeSelection(e);
    }
  }

  public clipSelection(img: HTMLImageElement) {
    console.log(img);
    const ctx: CanvasRenderingContext2D = this.canvasRef.getContext('2d');
    ctx.save();
    this.drawImage(this.canvasRef);
    // Create canvas to save to.
    const writeCanvas: HTMLCanvasElement = document.createElement('canvas');
    writeCanvas.width = 2 * this.selection.r;
    writeCanvas.height = 2 * this.selection.r;
    writeCanvas.getContext('2d').putImageData(this.selection.getImageData(ctx), 0, 0);
    // Write image data.
    this.resizeImg(writeCanvas, img);
    ctx.restore();
    this.drawSelection(this.canvasRef);
  }

  resizeImg(originalCanvas, img) {
    const newImage: HTMLImageElement = new Image();

    newImage.onload = function() {
      const resizeCanvas: HTMLCanvasElement = document.createElement('canvas');
      resizeCanvas.width = PROFILE_PIC_DIMENSION;
      resizeCanvas.height = PROFILE_PIC_DIMENSION;
      resizeCanvas.getContext('2d').drawImage(newImage, 0, 0, PROFILE_PIC_DIMENSION, PROFILE_PIC_DIMENSION);
      img.src = resizeCanvas.toDataURL('image/png');
    };
    newImage.src = originalCanvas.toDataURL('image/png');
  }

  private drawImage(cnv) {
    if (this.baseImageReady) {
      cnv.getContext('2d').drawImage(this.baseImage, 0, 0, cnv.width, cnv.height);
    }
  }

  private keepInRange(lo, hi, rad, val) {
    if (val - rad < lo) {
      return lo + rad;
    } else if (val + rad > hi) {
      return hi - rad;
    }
    return val;
  }

  private transposeSelection(e) {
    const mousePos: MousePos = this.getMousePos(this.canvasRef, e);
    const dx: number = mousePos.x - this.startPos.x;
    const dy: number = mousePos.y - this.startPos.y;
    this.selection.x = this.keepInRange(0, this.canvasRef.width, this.selection.r,
        this.previousSelection.x + dx);
    this.selection.y = this.keepInRange(0, this.canvasRef.height, this.selection.r,
        this.previousSelection.y + dy);
    this.drawSelection(this.canvasRef);
  }

  private getNewRadius(e) {
    const mousePos: MousePos = this.getMousePos(this.canvasRef, e);
    const dx: number = mousePos.x - this.startPos.x;
    const dy: number = mousePos.y - this.startPos.y;

    let start_dx: number = this.previousSelection.x - this.startPos.x;
    let start_dy: number = this.previousSelection.y - this.startPos.y;
    const origDist: number = Math.sqrt(start_dx * start_dx + start_dy * start_dy);

    start_dx = start_dx / origDist;
    start_dy = start_dy / origDist;

    const dotProduct: number = (dx * start_dx + dy * start_dy);
    return this.previousSelection.r - dotProduct;
  }

  private sanitizeNewRadius(newRadius) {
    this.selection.x = this.previousSelection.x;
    this.selection.y = this.previousSelection.y;

    if (2 * newRadius > this.canvasRef.height ||
        2 * newRadius > this.canvasRef.width) {

      newRadius = Math.min(this.canvasRef.height, this.canvasRef.width) / 2;

    } else if (newRadius < SELECTION_RADIUS_MIN) {
      newRadius = SELECTION_RADIUS_MIN;
    }

    if (this.selection.y - newRadius < 0) {
      this.selection.y = newRadius;
    } else if (this.selection.y + newRadius > this.canvasRef.height) {
      this.selection.y = this.canvasRef.height - newRadius;
    }

    if (this.selection.x - newRadius < 0) {
      this.selection.x = newRadius;
    } else if (this.selection.x + newRadius > this.canvasRef.width) {
      this.selection.x = this.canvasRef.width - newRadius;
    }
    return newRadius;
  }
}
